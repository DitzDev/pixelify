package ditzdevs.pixelify.me.handler;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import androidx.core.content.FileProvider;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ditzdevs.pixelify.me.activity.Crash;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    // Default system uncaught exception handler
    private final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private final Context context;

    private static volatile CrashHandler instance = null;

    private CrashHandler(Context context) {
        this.context = context;
    }

    /**
     * Initializes the CrashHandler and sets it as the default exception handler.
     *
     * @param application The Application context
     */
    public static void initialize(Application application) {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler(application);
                    Thread.setDefaultUncaughtExceptionHandler(instance);
                }
            }
        }
    }

    /**
     * Handles uncaught exceptions by generating logs, saving crash data to files,
     * and launching CrashActivity to notify the user.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            String crashLog = generateCrashLog(throwable);
            File crashFile = saveCrashToFile(crashLog);
            File crashHtmlFile = saveCrashToHtmlFile(throwable);

            context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("last_crash_file", crashFile.getAbsolutePath())
                    .putString("last_crash_html_file", crashHtmlFile.getAbsolutePath())
                    .apply();

            new Thread(() -> {
                Looper.prepare();

                Intent intent = new Intent(context, Crash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Crash.EXTRA_CRASH_INFO, crashLog);
                intent.putExtra(Crash.EXTRA_CRASH_HTML_PATH, crashHtmlFile.getAbsolutePath());

                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Looper.loop();
            }).start();

            Thread.sleep(1000);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

        } catch (Exception e) {
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        }
    }

    /**
     * Generates a plain text crash log containing device and stack trace information.
     */
    private String generateCrashLog(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");
        sb.append("========== DEVICE INFORMATION =========\n");
        sb.append("Brand: ").append(Build.BRAND).append("\n");
        sb.append("Device: ").append(Build.DEVICE).append("\n");
        sb.append("Model: ").append(Build.MODEL).append("\n");
        sb.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("SDK: ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("========== END OF DEVICE INFORMATION =========\n\n");
        sb.append("========== START STACK TRACE =========\n\n");
        sb.append(Log.getStackTraceString(throwable));
        sb.append("========== END OF STACK TRACE =========\n\n");
        return sb.toString();
    }

    /**
     * Saves the crash log as a plain text file in external files directory.
     */
    private File saveCrashToFile(String crashLog) throws IOException {
        String fileName = "crash_" + System.currentTimeMillis() + ".txt";
        File file = new File(context.getExternalFilesDir(null), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(crashLog.getBytes());
        }
        return file;
    }

    /**
     * Saves the crash information as a structured HTML file, including assets and JSON data.
     */
    private File saveCrashToHtmlFile(Throwable throwable) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String stackTrace = Log.getStackTraceString(throwable);

        JSONObject jsonData = new JSONObject();
        try {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("brand", Build.BRAND);
            deviceJson.put("device", Build.DEVICE);
            deviceJson.put("model", Build.MODEL);
            deviceJson.put("androidVersion", Build.VERSION.RELEASE);
            deviceJson.put("sdk", Build.VERSION.SDK_INT);

            jsonData.put("time", timestamp);
            jsonData.put("device", deviceJson);
            jsonData.put("stackTrace", stackTrace);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File crashReportDir = new File(context.getExternalFilesDir(null), "crash_reports");
        if (!crashReportDir.exists()) {
            crashReportDir.mkdirs();
        }

        ensureAssetCopied("crash_template.html", crashReportDir);
        ensureAssetCopied("crash_styles.css", crashReportDir);
        ensureAssetCopied("crash_script.js", crashReportDir);

        String htmlFileName = "crash_" + System.currentTimeMillis() + ".html";
        File htmlFile = new File(crashReportDir, htmlFileName);

        File templateFile = new File(crashReportDir, "crash_template.html");
        String htmlTemplate = new String(java.nio.file.Files.readAllBytes(templateFile.toPath()));

        String injectedScript = "<script type=\"text/javascript\">\n" +
                "    window.crashData = " + jsonData.toString() + ";\n" +
                "</script>";

        String modifiedHtml = htmlTemplate.replace("</body>", injectedScript + "\n</body>");

        try (FileOutputStream fos = new FileOutputStream(htmlFile)) {
            fos.write(modifiedHtml.getBytes());
        }

        return htmlFile;
    }

    /**
     * Copies an asset file to the given destination directory if it does not already exist.
     */
    private void ensureAssetCopied(String assetName, File destinationDir) {
        File destFile = new File(destinationDir, assetName);

        if (!destFile.exists()) {
            try (InputStream input = context.getAssets().open(assetName);
                 FileOutputStream output = new FileOutputStream(destFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

            } catch (IOException e) {
                Log.e("CrashHandler", "Failed to copy asset: " + assetName, e);
            }
        }
    }
}