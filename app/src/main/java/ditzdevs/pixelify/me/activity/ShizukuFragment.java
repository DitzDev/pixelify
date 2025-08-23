package ditzdevs.pixelify.me.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import ditzdevs.pixelify.me.databinding.FragmentShizukuBinding;
import ditzdevs.pixelify.me.utils.PermissionsHelper;
import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class ShizukuFragment extends Fragment {
    
    private static final String TAG = "ShizukuFragment";
    private static final int REQUEST_CODE_SHIZUKU = 100;
    
    private FragmentShizukuBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable statusChecker = this::updateStatus;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShizukuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        updateStatus();
        startPeriodicCheck();
    }
    
    private void setupViews() {
        binding.installShizuku.setOnClickListener(v -> installShizuku());
       
        binding.connectShizuku.setOnClickListener(v -> connectShizuku());
   
        binding.continueShizuku.setOnClickListener(v -> {
            if (isShizukuReady()) {
                navigateToMain();
            } else {
                Toast.makeText(requireContext(), 
                    "Shizuku is not ready. Please complete the setup.", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean isShizukuReady() {
        return PermissionsHelper.isShizukuInstalled(requireContext()) && 
               PermissionsHelper.isShizukuRunning() && 
               PermissionsHelper.hasShizukuPermission();
    }
    
    private void installShizuku() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://github.com/RikkaApps/Shizuku/releases/latest"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Cannot open browser", e);
            Toast.makeText(requireContext(), "Cannot open browser", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void connectShizuku() {
        Log.d(TAG, "Attempting to connect to Shizuku");
     
        if (!PermissionsHelper.isShizukuInstalled(requireContext())) {
            Toast.makeText(requireContext(), "Shizuku not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!PermissionsHelper.isShizukuRunning()) {
            Toast.makeText(requireContext(), 
                "Shizuku service not found. Please start Shizuku first.", 
                Toast.LENGTH_LONG).show();
            return;
        }
        
        if (PermissionsHelper.hasShizukuPermission()) {
            Log.d(TAG, "Shizuku already connected");
            Toast.makeText(requireContext(), "Shizuku is already connected!", Toast.LENGTH_SHORT).show();
            // Tidak langsung navigasi, hanya update status
            updateStatus();
        } else {
            try {
                Shizuku.requestPermission(REQUEST_CODE_SHIZUKU);
            } catch (Exception e) {
                Log.e(TAG, "Error requesting Shizuku permission", e);
                Toast.makeText(requireContext(), "Failed to request permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateStatus() {
        if (!isAdded()) return;
        
        try {
            boolean isInstalled = PermissionsHelper.isShizukuInstalled(requireContext());
            boolean isRunning = isInstalled && PermissionsHelper.isShizukuRunning();
            boolean hasShizukuPermission = isRunning && PermissionsHelper.hasShizukuPermission();
            boolean shizukuReady = isShizukuReady();
            
            Log.d(TAG, String.format("Status - Installed: %b, Running: %b, ShizukuPerm: %b, Ready: %b",
                isInstalled, isRunning, hasShizukuPermission, shizukuReady));
        
            binding.installStatus.setText(isInstalled ? "✓ Installed" : "✗ Not installed");
            binding.installStatus.setTextColor(getResources().getColor(
                isInstalled ? android.R.color.holo_green_dark : android.R.color.holo_red_dark, null));
            
            binding.serviceStatus.setText(isRunning ? "✓ Running" : "✗ Not running");
            binding.serviceStatus.setTextColor(getResources().getColor(
                isRunning ? android.R.color.holo_green_dark : android.R.color.holo_red_dark, null));
      
            String permissionText;
            if (hasShizukuPermission) {
                permissionText = "✓ Shizuku permission granted";
            } else {
                permissionText = "✗ No permission";
            }
            
            binding.permissionStatusShizuku.setText(permissionText);
            binding.permissionStatusShizuku.setTextColor(getResources().getColor(
                hasShizukuPermission ? android.R.color.holo_green_dark : android.R.color.holo_red_dark, null));
       
            binding.installShizuku.setVisibility(isInstalled ? View.GONE : View.VISIBLE);
            binding.connectShizuku.setEnabled(isRunning && !hasShizukuPermission);

            binding.continueShizuku.setEnabled(shizukuReady);
       
            if (!isInstalled) {
                binding.connectShizuku.setText("Install Shizuku First");
            } else if (!isRunning) {
                binding.connectShizuku.setText("Start Shizuku Service");
            } else if (!hasShizukuPermission) {
                binding.connectShizuku.setText("Grant Shizuku Permission");
            } else {
                binding.connectShizuku.setText("Shizuku Connected");
                binding.connectShizuku.setEnabled(false);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating status", e);
        }
    }
    
    private void startPeriodicCheck() {
        handler.removeCallbacks(statusChecker);
        handler.postDelayed(statusChecker, 2000);
    }
    
    private void stopPeriodicCheck() {
        handler.removeCallbacks(statusChecker);
    }
    
    private void navigateToMain() {
        try {
            Intent intent = new Intent(requireContext(), Main.class);
            startActivity(intent);
            requireActivity().finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to main", e);
        }
    }
   
    private final Shizuku.OnRequestPermissionResultListener permissionResultListener = 
        (requestCode, grantResult) -> {
            Log.d(TAG, "Permission result - Code: " + requestCode + ", Result: " + grantResult);
            
            if (requestCode == REQUEST_CODE_SHIZUKU) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Shizuku permission granted");
                    Toast.makeText(requireContext(), 
                        "Shizuku permission granted! You can now continue.", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), 
                        "Shizuku permission denied", Toast.LENGTH_SHORT).show();
                }
                updateStatus();
            }
        };
    
    @Override
    public void onStart() {
        super.onStart();
        try {
            Shizuku.addRequestPermissionResultListener(permissionResultListener);
            Log.d(TAG, "Permission listener added");
        } catch (Exception e) {
            Log.e(TAG, "Error adding permission listener", e);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateStatus();
        startPeriodicCheck();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicCheck();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Shizuku.removeRequestPermissionResultListener(permissionResultListener);
            Log.d(TAG, "Permission listener removed");
        } catch (Exception e) {
            Log.e(TAG, "Error removing permission listener", e);
        }
        stopPeriodicCheck();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}