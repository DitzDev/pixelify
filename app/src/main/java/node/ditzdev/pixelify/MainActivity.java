package node.ditzdev.pixelify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import node.ditzdev.pixelify.models.Resolution;
import node.ditzdev.pixelify.models.ResolutionTemplate;
import node.ditzdev.pixelify.utils.NodeRESOUtils;
import node.ditzdev.pixelify.utils.WM;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private TextInputEditText heightInput, widthInput, dpiInput;
  private Button btnReset, btnApply;
  private LinearLayout selectTemplateBtn;
  private Resolution defaultResolution;
  private WM windowManager;
  private NodeRESOUtils resolutionUtils;
  private MaterialToolbar toolbar;   

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ErrorHandlerActivity.setDefaultUncaughtExceptionHandler(this);

    try {
      windowManager = new WM(getContentResolver());
      resolutionUtils = new NodeRESOUtils(windowManager);
      defaultResolution = resolutionUtils.getRealResolution();
      initializeViews();
      setupListeners();

    } catch (Exception e) {
      showErrorDialog(
          "Initialization Error", "Failed to initialize window manager: " + e.getMessage());
    }
  }

  private void initializeViews() {
    heightInput = findViewById(R.id.heightInput);
    widthInput = findViewById(R.id.widthInput);
    dpiInput = findViewById(R.id.dpiInput);
    btnReset = findViewById(R.id.resetBtn);
    btnApply = findViewById(R.id.applyChangeBtn);
    toolbar = findViewById(R.id.toolbar);
    selectTemplateBtn = findViewById(R.id.selectTemplateBtn);
    widthInput.setText(String.valueOf(defaultResolution.getWidth()));
    heightInput.setText(String.valueOf(defaultResolution.getHeight()));
    toolbar.setOnMenuItemClickListener(item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.action_report_issues) {
            String issuesUrl = "https://github.com/DitzDev/pixelify/issues";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(issuesUrl));
            startActivity(browserIntent);
            return true;
        } else if (itemId == R.id.action_source_code) {
            String sourceCodeUrl = "https://github.com/DitzDev/pixelify";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceCodeUrl));
            startActivity(browserIntent);
            return true;
        } else if (itemId == R.id.action_license) {
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            return true;
        }
        
        return false;
    });    
    try {
      dpiInput.setText(String.valueOf(windowManager.getRealDensity()));
    } catch (Exception e) {
      showErrorDialog("DPI Error", "Could not fetch default DPI");
    }
  }

  private void setupListeners() {
    btnApply.setOnClickListener(
        v -> {
          try {
            int newWidth = Integer.parseInt(widthInput.getText().toString());
            int newHeight = Integer.parseInt(heightInput.getText().toString());
            int newDpi = Integer.parseInt(dpiInput.getText().toString());
            if (isResolutionDangerous(newWidth, newHeight, newDpi)) {
              showDangerousResolutionWarning(newWidth, newHeight, newDpi);
            } else {
              applyResolutionChange(newWidth, newHeight, newDpi);
            }
          } catch (NumberFormatException e) {
            showErrorDialog(
                "Invalid input", "Please enter valid numbers for width, height, and DPI.");
          }
        });

    btnReset.setOnClickListener(v -> resetToDefaultResolution());

    selectTemplateBtn.setOnClickListener(v -> showResolutionTemplates());
  }

  private boolean isResolutionDangerous(int newWidth, int newHeight, int newDpi) {
    return Math.abs(newWidth - defaultResolution.getWidth()) > defaultResolution.getWidth() * 0.5
        || Math.abs(newHeight - defaultResolution.getHeight())
            > defaultResolution.getHeight() * 0.5;
  }

  private void showDangerousResolutionWarning(int width, int height, int dpi) {
    new MaterialAlertDialogBuilder(this)
        .setTitle("Dangerous Resolution Warning")
        .setMessage(
            "The resolution you've chosen is significantly different from your device's default. "
                + "This might cause system instability or UI issues.\n\n"
                + "Current Default: "
                + defaultResolution.getWidth()
                + "x"
                + defaultResolution.getHeight()
                + "\nNew Resolution: "
                + width
                + "x"
                + height
                + " @ "
                + dpi
                + "dpi\n\n"
                + "Are you sure you want to proceed?")
        .setPositiveButton(
            "Yes, I Understand the Risks",
            (dialog, which) -> applyResolutionChange(width, height, dpi))
        .setNegativeButton("Cancel", null)
        .show();
  }

  private void applyResolutionChange(int width, int height, int dpi) {
    try {
      windowManager.setResolution(width, height);
      windowManager.setDisplayDensity(dpi);

      showErrorDialog("Success", "Resolution and DPI changed successfully.");
    } catch (Exception e) {
      showErrorDialog("Resolution Change Failed", "Could not change resolution: " + e.getMessage());
    }
  }

  private void resetToDefaultResolution() {
    try {
      windowManager.clearResolution();
      windowManager.clearDisplayDensity();
      widthInput.setText(String.valueOf(defaultResolution.getWidth()));
      heightInput.setText(String.valueOf(defaultResolution.getHeight()));
      dpiInput.setText(String.valueOf(windowManager.getRealDensity()));

      showErrorDialog("Reset Successful", "Display settings restored to default.");
    } catch (Exception e) {
      showErrorDialog("Reset Failed", "Could not reset display settings: " + e.getMessage());
    }
  }

  private void showResolutionTemplates() {
    List<ResolutionTemplate> templates = createResolutionTemplates();
    String[] templateNames =
        templates.stream().map(ResolutionTemplate::getDeviceName).toArray(String[]::new);

    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
    builder.setTitle("Select Resolution Template");
    ListView listView = new ListView(this);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, templateNames);
    listView.setAdapter(adapter);
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    builder.setView(listView);
    builder.setPositiveButton(
        "OK",
        (dialog, which) -> {
          int selectedPosition = listView.getCheckedItemPosition();
          if (selectedPosition != -1) {
            ResolutionTemplate selected = templates.get(selectedPosition);
            widthInput.setText(String.valueOf(selected.getLogicalWidth()));
            heightInput.setText(String.valueOf(selected.getLogicalHeight()));
            dpiInput.setText(String.valueOf(selected.getPpi()));
          }
        });

    builder.setNegativeButton("Cancel", null);
    AlertDialog dialog = builder.create();
    dialog.show();
    if (templates.size() > 6) {
      dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(600));
    }
  }

  private int dpToPx(int dp) {
    float density = getResources().getDisplayMetrics().density;
    return Math.round(dp * density);
  }

  private List<ResolutionTemplate> createResolutionTemplates() {
    List<ResolutionTemplate> templates = new ArrayList<>();
    templates.add(new ResolutionTemplate("iPad View", 1536, 2080, 384, 2.0f, "8.3\""));
    templates.add(new ResolutionTemplate("iPad (2021)", 1620, 2160, 264, 2.0f, "10.2\""));
    templates.add(new ResolutionTemplate("iPhone 13 Pro max (2021)", 1284, 2778, 340, 3.0f, "6.68\""));
    templates.add(new ResolutionTemplate("iPhone 8 Plus (2017)", 1080, 1920, 401, 3.0f, "5.5\""));
    return templates;
  }

  private void showErrorDialog(String title, String message) {
    new MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show();
  }
}
