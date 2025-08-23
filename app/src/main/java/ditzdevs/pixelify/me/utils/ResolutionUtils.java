package ditzdevs.pixelify.me.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public final class ResolutionUtils {
    
    private ResolutionUtils() {}

    public static class DeviceResolution {
        private final int width;
        private final int height;
        private final int dpi;
        private final float scaleFactor;

        public DeviceResolution(int width, int height, int dpi, float scaleFactor) {
            this.width = width;
            this.height = height;
            this.dpi = dpi;
            this.scaleFactor = scaleFactor;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getDpi() {
            return dpi;
        }

        public float getScaleFactor() {
            return scaleFactor;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DeviceResolution that = (DeviceResolution) obj;
            return width == that.width && 
                   height == that.height && 
                   dpi == that.dpi && 
                   Float.compare(that.scaleFactor, scaleFactor) == 0;
        }

        @Override
        public int hashCode() {
            return width * 31 + height * 31 + dpi * 31 + Float.hashCode(scaleFactor);
        }

        @Override
        public String toString() {
            return "DeviceResolution{" +
                    "width=" + width +
                    ", height=" + height +
                    ", dpi=" + dpi +
                    ", scaleFactor=" + scaleFactor +
                    '}';
        }
    }

    public static DeviceResolution getDefaultResolution(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return new DeviceResolution(
                displayMetrics.widthPixels,
                displayMetrics.heightPixels,
                displayMetrics.densityDpi,
                displayMetrics.density
        );
    }

    public static boolean isResolutionDangerous(DeviceResolution defaultRes, int newWidth, int newHeight, int newDpi) {
        double widthDiff = Math.abs((double) newWidth / defaultRes.getWidth() - 1) * 100;
        double heightDiff = Math.abs((double) newHeight / defaultRes.getHeight() - 1) * 100;
        double dpiDiff = Math.abs((double) newDpi / defaultRes.getDpi() - 1) * 100;

        return widthDiff > 50 || heightDiff > 50 || dpiDiff > 50;
    }
}