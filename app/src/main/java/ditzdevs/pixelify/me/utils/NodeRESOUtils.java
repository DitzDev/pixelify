package ditzdevs.pixelify.me.utils;

import android.graphics.Point;
import ditzdevs.pixelify.me.models.Resolution;
import ditzdevs.pixelify.me.wm.WM;

public class NodeRESOUtils {
    private final Resolution realResolution;
    private final double resolutionDivisor;

    public NodeRESOUtils(WM windowManager) {
        try {
            Point realPoint = windowManager.getRealResolution();
            this.realResolution = new Resolution(realPoint.x, realPoint.y);
            this.resolutionDivisor = getDiagonalPixels(realResolution) / windowManager.getRealDensity();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize NodeRESOUtils", e);
        }
    }

    /**
     * Calculate diagonal pixel width
     */
    private double getDiagonalPixels(Resolution resolution) {
        return Math.sqrt(
                Math.pow(resolution.getWidth(), 2) +
                        Math.pow(resolution.getHeight(), 2)
        );
    }

    /**
     * Calculate DPI for a given resolution
     */
    public int getDPI(Resolution resolution) {
        double diagonalPixels = getDiagonalPixels(resolution);
        return (int) Math.round(diagonalPixels / resolutionDivisor);
    }

    /**
     * Scale resolution by percentage
     */
    public Resolution scaleResolution(Resolution resolution, float percent) {
        float scale = percent / 100f;
        return new Resolution(
                Math.round(resolution.getWidth() * scale),
                Math.round(resolution.getHeight() * scale)
        );
    }

    /**
     * Get the real resolution
     */
    public Resolution getRealResolution() {
        return realResolution;
    }
}