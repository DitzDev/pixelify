package ditzdevs.pixelify.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ditzdevs.pixelify.me.BuildConfig;
import ditzdevs.pixelify.me.R;
import ditzdevs.pixelify.me.utils.BasicUtils;
import ditzdevs.pixelify.me.utils.PermissionsHelper;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;
    private static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        TextView tvTextVersion = findViewById(R.id.textview_version);
        String versionText = getString(R.string.app_version_format,
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        tvTextVersion.setText(versionText);

        BasicUtils utils = new BasicUtils();

        new Handler().postDelayed(() -> {
            Class<?> nextActivity;
            
            boolean hasBasicPermission = utils.checkPermission(getApplicationContext());
            boolean isShizukuReady = isShizukuReady();
            
            Log.d(TAG, "Permission check - Basic: " + hasBasicPermission + ", Shizuku: " + isShizukuReady);
            
            if (hasBasicPermission || isShizukuReady) {
                nextActivity = Main.class;
                Log.d(TAG, "Navigating to Main - At least one permission is available");
            } else {
                nextActivity = Setup.class;
                Log.d(TAG, "Navigating to Setup - No permissions available");
            }
            
            startActivity(new Intent(getApplicationContext(), nextActivity));
            finish();
        }, SPLASH_DELAY);
    }
    
    private boolean isShizukuReady() {
        try {
            return PermissionsHelper.isShizukuInstalled(this) && 
                   PermissionsHelper.isShizukuRunning() && 
                   PermissionsHelper.hasShizukuPermission();
        } catch (Exception e) {
            Log.e(TAG, "Error checking Shizuku status", e);
            return false;
        }
    }
}