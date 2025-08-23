package ditzdevs.pixelify.me.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import ditzdevs.pixelify.me.databinding.FragmentAdbBinding;
import ditzdevs.pixelify.me.utils.PermissionsHelper;

public class AdbFragment extends Fragment {
    
    private FragmentAdbBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable permissionChecker = this::checkPermissions;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdbBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        checkPermissions();
        startPeriodicCheck();
    }
    
    private void setupViews() {
        binding.copyCommand.setOnClickListener(v -> copyCommandToClipboard());
        binding.continueButton.setOnClickListener(v -> navigateToMain());
    }
    
    private void copyCommandToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) requireContext()
            .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ADB Command", binding.adbCommand.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "Command copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    
    private void checkPermissions() {
        if (!isAdded()) return;
        
        boolean hasPermission = PermissionsHelper.hasWriteSecureSettings(requireContext());
        
        binding.permissionStatus.setText(hasPermission ? "✓ Permission granted" : "✗ Permission not granted");
        binding.permissionStatus.setTextColor(getResources().getColor(
            hasPermission ? android.R.color.holo_green_dark : android.R.color.holo_red_dark, null));
        
        binding.continueButton.setEnabled(hasPermission);
        
        if (hasPermission) {
            navigateToMain();
        }
    }
    
    private void startPeriodicCheck() {
        handler.postDelayed(permissionChecker, 2000);
    }
    
    private void stopPeriodicCheck() {
        handler.removeCallbacks(permissionChecker);
    }
    
    private void navigateToMain() {
        startActivity(new Intent(requireContext(), Main.class));
        requireActivity().finish();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
        startPeriodicCheck();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicCheck();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPeriodicCheck();
        binding = null;
    }
}