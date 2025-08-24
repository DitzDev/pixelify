package ditzdevs.pixelify.me;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ditzdevs.pixelify.me.models.SettingsData;
import ditzdevs.pixelify.me.utils.SettingsUtils;

public class BasePixelifyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsUtils.applyLanguage(this, SettingsUtils.getLanguage(this));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        int language = SettingsUtils.getLanguage(newBase);

        if (language == SettingsData.LANGUAGE_FOLLOW_SYSTEM) {
            super.attachBaseContext(newBase);
        } else {
            Context localizedContext = SettingsUtils.getLocalizedContext(newBase);
            super.attachBaseContext(localizedContext);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SettingsUtils.applyLanguage(this, SettingsUtils.getLanguage(this));
    }
}
