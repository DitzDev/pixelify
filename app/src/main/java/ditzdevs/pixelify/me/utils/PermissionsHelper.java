package ditzdevs.pixelify.me.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import rikka.shizuku.Shizuku;

public class PermissionsHelper {
    
    public static boolean hasWriteSecureSettings(Context context) {
        return ContextCompat.checkSelfPermission(context, 
            android.Manifest.permission.WRITE_SECURE_SETTINGS)
            == PackageManager.PERMISSION_GRANTED;
    }
    
    public static boolean isShizukuInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("moe.shizuku.privileged.api", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            try {
                context.getPackageManager().getPackageInfo("moe.shizuku.manager", 0);
                return true;
            } catch (PackageManager.NameNotFoundException e2) {
                return false;
            }
        }
    }
    
    public static boolean isShizukuRunning() {
        try {
            return Shizuku.pingBinder();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean hasShizukuPermission() {
        try {
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }
}