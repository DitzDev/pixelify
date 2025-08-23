package ditzdevs.pixelify.me.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import ditzdevs.pixelify.me.R;

public class Crash extends AppCompatActivity {

    public static final String EXTRA_CRASH_INFO = "extra_crash_info";
    public static final String EXTRA_CRASH_HTML_PATH = "extra_crash_html_path";

    private String crashHtmlPath;
    private String crashInfo;
    private File crashHtmlDir;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);

        crashInfo = getIntent().getStringExtra(EXTRA_CRASH_INFO);
        if (crashInfo == null) crashInfo = "Unknown error occurred";

        crashHtmlPath = getIntent().getStringExtra(EXTRA_CRASH_HTML_PATH);
        crashHtmlDir = new File(getExternalFilesDir(null), "crash_reports");

        TextView tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvErrorMessage.setText(crashInfo);

        WebView webView = findViewById(R.id.webViewCrash);

        if (crashHtmlPath != null && new File(crashHtmlPath).exists()) {
            webView.setVisibility(View.VISIBLE);
            tvErrorMessage.setVisibility(View.GONE);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);

            webView.addJavascriptInterface(new CrashJSInterface(this), "AndroidCrashInterface");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();

                    if (url.contains("crash_styles.css") || url.contains("crash_script.js")) {
                        try {
                            String fileName = url.contains("crash_styles.css") ? "crash_styles.css" : "crash_script.js";
                            File file = new File(crashHtmlDir, fileName);

                            String mimeType = fileName.endsWith(".css") ? "text/css" : "application/javascript";
                            FileInputStream inputStream = new FileInputStream(file);

                            return new WebResourceResponse(mimeType, "UTF-8", inputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return super.shouldInterceptRequest(view, request);
                }
            });

            File htmlFile = new File(crashHtmlPath);
            String htmlContent = "";

            try {
                htmlContent = new String(java.nio.file.Files.readAllBytes(htmlFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String baseUrl = "file://" + crashHtmlDir.getAbsolutePath() + "/";
            webView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);

        } else {
            webView.setVisibility(View.GONE);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnRestartApp).setOnClickListener(v -> restartApp());
        findViewById(R.id.btnShareLog).setOnClickListener(v -> shareCrashLog());
    }

    private void restartApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    private void shareCrashLog() {
        try {
            String lastCrashFile = getSharedPreferences("crash_prefs", MODE_PRIVATE)
                    .getString("last_crash_file", null);

            if (lastCrashFile != null) {
                File crashFile = new File(lastCrashFile);
                if (crashFile.exists()) {
                    Uri fileUri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".fileprovider",
                            crashFile
                    );

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(shareIntent, "Share Crash Log"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CrashJSInterface {
        private final Context context;

        public CrashJSInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String getCrashData() {
            String crashLog = crashInfo != null ? crashInfo : "Unknown error occurred";

            String timestamp = extractInfo(crashLog, "Time: ", "\n\n");
            String brand = extractInfo(crashLog, "Brand: ", "\n");
            String device = extractInfo(crashLog, "Device: ", "\n");
            String model = extractInfo(crashLog, "Model: ", "\n");
            String androidVersion = extractInfo(crashLog, "Android Version: ", "\n");
            String sdk = extractInfo(crashLog, "SDK: ", "\n");

            String stackTrace;
            if (crashLog.contains("========== START STACK TRACE =========")) {
                stackTrace = crashLog.substring(
                        crashLog.indexOf("========== START STACK TRACE =========\n\n") + "========== START STACK TRACE =========\n\n".length(),
                        crashLog.indexOf("========== END OF STACK TRACE =========")
                );
            } else {
                stackTrace = crashLog;
            }

            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("time", timestamp);

                JSONObject deviceJson = new JSONObject();
                deviceJson.put("brand", brand);
                deviceJson.put("device", device);
                deviceJson.put("model", model);
                deviceJson.put("androidVersion", androidVersion);
                deviceJson.put("sdk", sdk);

                jsonData.put("device", deviceJson);
                jsonData.put("stackTrace", stackTrace);

                return jsonData.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "{}";
            }
        }

        @JavascriptInterface
        public void restartApp() {
            runOnUiThread(() -> Crash.this.restartApp());
        }

        @JavascriptInterface
        public void shareCrashLog() {
            runOnUiThread(() -> Crash.this.shareCrashLog());
        }

        private String extractInfo(String text, String startMarker, String endMarker) {
            try {
                int start = text.indexOf(startMarker);
                if (start == -1) return "Unknown";
                start += startMarker.length();
                int end = text.indexOf(endMarker, start);
                if (end == -1) return "Unknown";
                return text.substring(start, end);
            } catch (Exception e) {
                return "Unknown";
            }
        }
    }
}