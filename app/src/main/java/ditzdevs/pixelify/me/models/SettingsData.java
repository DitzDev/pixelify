package ditzdevs.pixelify.me.models;

import android.content.Context;
import ditzdevs.pixelify.me.R;

public class SettingsData {
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_FOLLOW_SYSTEM = 2;
    
    // Language constants
    // NOTE (DitzDev): For Contributors, Don't forget to add
    // Data Constants, Read CONTRIBUTING.md for more info
    public static final int LANGUAGE_FOLLOW_SYSTEM = 0;
    public static final int LANGUAGE_ENGLISH = 1;
    public static final int LANGUAGE_INDONESIA = 2;
    
    // Settings keys
    public static final String KEY_THEME_MODE = "theme_mode";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_SMART_ALERT = "smart_alert";
    public static final String KEY_ENABLE_CONTROL_PANEL_RESOLUTION = "enable_control_panel_resolution";
    
    // Default values
    public static final int DEFAULT_THEME_MODE = THEME_FOLLOW_SYSTEM;
    public static final int DEFAULT_LANGUAGE = LANGUAGE_FOLLOW_SYSTEM;
    public static final boolean DEFAULT_SMART_ALERT = true;
    public static final boolean DEFAULT_ENABLE_CONTROL_PANEL_RESOLUTION = false;
    
    private int themeMode;
    private int language;
    private boolean smartAlert;
    private boolean enableControlPanelResolution;
    
    public SettingsData() {
        this.themeMode = DEFAULT_THEME_MODE;
        this.language = DEFAULT_LANGUAGE;
        this.smartAlert = DEFAULT_SMART_ALERT;
        this.enableControlPanelResolution = DEFAULT_ENABLE_CONTROL_PANEL_RESOLUTION;
    }
    
    public SettingsData(int themeMode, int language, boolean smartAlert, boolean enableControlPanelResolution) {
        this.themeMode = themeMode;
        this.language = language;
        this.smartAlert = smartAlert;
        this.enableControlPanelResolution = enableControlPanelResolution;
    }
    
    // Getters
    public int getThemeMode() {
        return themeMode;
    }
    
    public int getLanguage() {
        return language;
    }
    
    public boolean isSmartAlert() {
        return smartAlert;
    }
    
    public boolean isEnableControlPanelResolution() {
        return enableControlPanelResolution;
    }
    
    // Setters
    public void setThemeMode(int themeMode) {
        this.themeMode = themeMode;
    }
    
    public void setLanguage(int language) {
        this.language = language;
    }
    
    public void setSmartAlert(boolean smartAlert) {
        this.smartAlert = smartAlert;
    }
    
    public void setEnableControlPanelResolution(boolean enableControlPanelResolution) {
        this.enableControlPanelResolution = enableControlPanelResolution;
    }
 
    public String getThemeModeString(Context ctx) {
        switch (themeMode) {
            case THEME_LIGHT:
                return ctx.getString(R.string.dialog_items_light);
            case THEME_DARK:
                return ctx.getString(R.string.dialog_items_night);
            case THEME_FOLLOW_SYSTEM:
                return ctx.getString(R.string.follow_system);
            default:
                return ctx.getString(R.string.follow_system);
        }
    }
    
    // TODO: For Contributions 
    // Add your language as shown.
    // Read CONTRIBUTING.md for more informations.
    public String getLanguageString(Context ctx) {
        switch (language) {
            case LANGUAGE_ENGLISH:
                return "English";
            case LANGUAGE_INDONESIA:
                return "Bahasa Indonesia";
            case LANGUAGE_FOLLOW_SYSTEM:
                return ctx.getString(R.string.follow_system);
            default:
                return ctx.getString(R.string.follow_system);
        }
    }
    
    @Override
    public String toString() {
        return "SettingsData{" +
                "themeMode=" + themeMode +
                ", language=" + language +
                ", smartAlert=" + smartAlert +
                ", enableControlPanelResolution=" + enableControlPanelResolution +
                '}';
    }
}