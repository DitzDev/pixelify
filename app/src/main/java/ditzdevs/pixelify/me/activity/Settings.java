package ditzdevs.pixelify.me.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import ditzdevs.pixelify.me.BasePixelifyActivity;
import ditzdevs.pixelify.me.R;
import ditzdevs.pixelify.me.models.SettingsData;
import ditzdevs.pixelify.me.utils.SettingsUtils;

public class Settings extends BasePixelifyActivity {

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
        themeSummary.setText(settingsData.getThemeModeString(this));
        languageSummary.setText(settingsData.getLanguageString(this));
        smartAlertSwitch.setChecked(settingsData.isSmartAlert());
        controlPanelResolutionSwitch.setChecked(settingsData.isEnableControlPanelResolution());
    }

    private void setupClickListeners() {
        findViewById(R.id.theme_setting).setOnClickListener(v -> showThemeDialog());
        findViewById(R.id.language_setting).setOnClickListener(v -> showLanguageDialog());

        smartAlertSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    settingsData.setSmartAlert(isChecked);
                    SettingsUtils.setSmartAlert(this, isChecked);
                });

        controlPanelResolutionSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    settingsData.setEnableControlPanelResolution(isChecked);
                    SettingsUtils.setEnableControlPanelResolution(this, isChecked);
                });

        findViewById(R.id.reset_settings).setOnClickListener(v -> showResetDialog());
    }

    private void showThemeDialog() {
        String[] themeOptions = {getString(R.string.dialog_items_light), getString(R.string.dialog_items_night), getString(R.string.follow_system)};
        int currentSelection = settingsData.getThemeMode();

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_set_theme))
                .setSingleChoiceItems(
                        themeOptions,
                        currentSelection,
                        (dialog, which) -> {
                            settingsData.setThemeMode(which);
                            SettingsUtils.setThemeMode(this, which);
                            themeSummary.setText(settingsData.getThemeModeString(this));
                            dialog.dismiss();
                        })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
    
    // TODO: For Contributors
    // Enter the language name into the array, 
    // read CONTRIBUTING.MD for more informations
    private void showLanguageDialog() {
        String[] languageOptions = {
           getString(R.string.follow_system), 
           "English",
           "Bahasa Indonesia"
        };
        int currentSelection = settingsData.getLanguage();

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_language_title))
                .setSingleChoiceItems(
                        languageOptions,
                        currentSelection,
                        (dialog, which) -> {
                            settingsData.setLanguage(which);
                            SettingsUtils.setLanguage(this, which);
                            SettingsUtils.applyLanguage(this, which);
                            languageSummary.setText(settingsData.getLanguageString(this));
                            dialog.dismiss();
                        })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void showResetDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.reset_settings))
                .setMessage(getString(R.string.reset_settings_long_desc))
                .setPositiveButton(
                        getString(R.string.action_ok),
                        (dialog, which) -> {
                            SettingsUtils.resetToDefault(this);
                            loadSettings();
                        })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
