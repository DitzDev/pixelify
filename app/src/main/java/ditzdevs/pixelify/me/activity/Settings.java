package ditzdevs.pixelify.me.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import ditzdevs.pixelify.me.R;
import ditzdevs.pixelify.me.models.SettingsData;
import ditzdevs.pixelify.me.utils.SettingsUtils;

public class Settings extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView themeSummary;
    private TextView languageSummary;
    private MaterialSwitch smartAlertSwitch;
    private MaterialSwitch controlPanelResolutionSwitch;

    private SettingsData settingsData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        
        initViews();
        setupToolbar();
        loadSettings();
        setupClickListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        themeSummary = findViewById(R.id.theme_summary);
        languageSummary = findViewById(R.id.language_summary);
        smartAlertSwitch = findViewById(R.id.smart_alert_switch);
        controlPanelResolutionSwitch = findViewById(R.id.control_panel_resolution_switch);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    
    private void loadSettings() {
        settingsData = SettingsUtils.getSettings(this);
        updateUI();
    }
    
    private void updateUI() {
        themeSummary.setText(settingsData.getThemeModeString());
        languageSummary.setText(settingsData.getLanguageString());
        smartAlertSwitch.setChecked(settingsData.isSmartAlert());
        controlPanelResolutionSwitch.setChecked(settingsData.isEnableControlPanelResolution());
    }
    
    private void setupClickListeners() {
        findViewById(R.id.theme_setting).setOnClickListener(v -> showThemeDialog());
        findViewById(R.id.language_setting).setOnClickListener(v -> showLanguageDialog());
  
        smartAlertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsData.setSmartAlert(isChecked);
            SettingsUtils.setSmartAlert(this, isChecked);
        });

        controlPanelResolutionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsData.setEnableControlPanelResolution(isChecked);
            SettingsUtils.setEnableControlPanelResolution(this, isChecked);
        });
        
        findViewById(R.id.reset_settings).setOnClickListener(v -> showResetDialog());
    }
    
    private void showThemeDialog() {
        String[] themeOptions = {"Light", "Dark", "Follow System"};
        int currentSelection = settingsData.getThemeMode();
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Theme")
                .setSingleChoiceItems(themeOptions, currentSelection, (dialog, which) -> {
                    settingsData.setThemeMode(which);
                    SettingsUtils.setThemeMode(this, which);
                    themeSummary.setText(settingsData.getThemeModeString());
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showLanguageDialog() {
        String[] languageOptions = {"Follow System", "English", "Indonesia"};
        int currentSelection = settingsData.getLanguage();
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Language")
                .setSingleChoiceItems(languageOptions, currentSelection, (dialog, which) -> {
                    settingsData.setLanguage(which);
                    SettingsUtils.setLanguage(this, which);
                    SettingsUtils.applyLanguage(this, which);
                    languageSummary.setText(settingsData.getLanguageString());
                    dialog.dismiss();
  
                    if (which != SettingsData.LANGUAGE_FOLLOW_SYSTEM) {
                        showRestartDialog();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showResetDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Settings")
                .setMessage("Are you sure you want to reset all settings to default? This action cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    SettingsUtils.resetToDefault(this);
                    loadSettings(); // Reload settings after reset
                    showRestartDialog();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showRestartDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Restart Required")
                .setMessage("Please restart the app to apply the changes.")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}