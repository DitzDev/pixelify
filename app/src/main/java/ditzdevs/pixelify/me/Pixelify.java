package ditzdevs.pixelify.me;

import android.app.Application;
import android.content.res.Configuration;
import ditzdevs.pixelify.me.handler.CrashHandler;
import ditzdevs.pixelify.me.models.SettingsData;
import ditzdevs.pixelify.me.utils.SettingsUtils;

public class Pixelify extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.initialize(this);
        SettingsUtils.initializeAppSettings(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentLanguage = SettingsUtils.getLanguage(this);
        if (currentLanguage == SettingsData.LANGUAGE_FOLLOW_SYSTEM) {
            SettingsUtils.applyLanguage(this, currentLanguage);
        }
    }
}
