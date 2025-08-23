package ditzdevs.pixelify.me.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import ditzdevs.pixelify.me.models.SettingsData;
import java.util.Locale;
import android.content.res.Configuration;
import android.os.Build;

public class SettingsUtils {
    
    private static final String PREFS_NAME = "pixelify_settings";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }
    }
  
    public static SettingsData getSettings(Context context) {
        init(context);
        
        int themeMode = prefs.getInt(SettingsData.KEY_THEME_MODE, SettingsData.DEFAULT_THEME_MODE);
        int language = prefs.getInt(SettingsData.KEY_LANGUAGE, SettingsData.DEFAULT_LANGUAGE);
        boolean smartAlert = prefs.getBoolean(SettingsData.KEY_SMART_ALERT, SettingsData.DEFAULT_SMART_ALERT);
        boolean enableControlPanelResolution = prefs.getBoolean(SettingsData.KEY_ENABLE_CONTROL_PANEL_RESOLUTION, SettingsData.DEFAULT_ENABLE_CONTROL_PANEL_RESOLUTION);
        
        return new SettingsData(themeMode, language, smartAlert, enableControlPanelResolution);
    }

    public static void saveSettings(Context context, SettingsData settingsData) {
        init(context);
        
        editor.putInt(SettingsData.KEY_THEME_MODE, settingsData.getThemeMode());
        editor.putInt(SettingsData.KEY_LANGUAGE, settingsData.getLanguage());
        editor.putBoolean(SettingsData.KEY_SMART_ALERT, settingsData.isSmartAlert());
        editor.putBoolean(SettingsData.KEY_ENABLE_CONTROL_PANEL_RESOLUTION, settingsData.isEnableControlPanelResolution());
        editor.apply();
    }
  
    public static void setThemeMode(Context context, int themeMode) {
        init(context);
        editor.putInt(SettingsData.KEY_THEME_MODE, themeMode);
        editor.apply();
        applyTheme(themeMode);
    }
    
    public static void setLanguage(Context context, int language) {
        init(context);
        editor.putInt(SettingsData.KEY_LANGUAGE, language);
        editor.apply();
    }
    
    public static void setSmartAlert(Context context, boolean smartAlert) {
        init(context);
        editor.putBoolean(SettingsData.KEY_SMART_ALERT, smartAlert);
        editor.apply();
    }
    
    public static void setEnableControlPanelResolution(Context context, boolean enable) {
        init(context);
        editor.putBoolean(SettingsData.KEY_ENABLE_CONTROL_PANEL_RESOLUTION, enable);
        editor.apply();
    }
 
    public static int getThemeMode(Context context) {
        init(context);
        return prefs.getInt(SettingsData.KEY_THEME_MODE, SettingsData.DEFAULT_THEME_MODE);
    }
    
    public static int getLanguage(Context context) {
        init(context);
        return prefs.getInt(SettingsData.KEY_LANGUAGE, SettingsData.DEFAULT_LANGUAGE);
    }
    
    public static boolean isSmartAlert(Context context) {
        init(context);
        return prefs.getBoolean(SettingsData.KEY_SMART_ALERT, SettingsData.DEFAULT_SMART_ALERT);
    }
    
    public static boolean isEnableControlPanelResolution(Context context) {
        init(context);
        return prefs.getBoolean(SettingsData.KEY_ENABLE_CONTROL_PANEL_RESOLUTION, SettingsData.DEFAULT_ENABLE_CONTROL_PANEL_RESOLUTION);
    }

    public static void applyTheme(int themeMode) {
        switch (themeMode) {
            case SettingsData.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case SettingsData.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case SettingsData.THEME_FOLLOW_SYSTEM:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
        }
    }

    public static void applyLanguage(Context context, int language) {
        Locale locale;
        switch (language) {
            case SettingsData.LANGUAGE_ENGLISH:
                locale = new Locale("en");
                break;
            case SettingsData.LANGUAGE_INDONESIA:
                locale = new Locale("id");
                break;
            case SettingsData.LANGUAGE_FOLLOW_SYSTEM:
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = context.getResources().getConfiguration().getLocales().get(0);
                } else {
                    locale = context.getResources().getConfiguration().locale;
                }
                break;
        }
        
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
    
    public static void initializeAppSettings(Context context) {
        int themeMode = getThemeMode(context);
        int language = getLanguage(context);
        
        applyTheme(themeMode);
        applyLanguage(context, language);
    }
    
    public static void resetToDefault(Context context) {
        init(context);
        editor.clear();
        editor.apply();
 
        applyTheme(SettingsData.DEFAULT_THEME_MODE);
    }
}