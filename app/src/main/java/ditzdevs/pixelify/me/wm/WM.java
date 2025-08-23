package ditzdevs.pixelify.me.wm;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import ditzdevs.pixelify.me.models.WindowManagerConstants;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class WM {
    private static final int USER_ID = -3;

    // Settings keys to unblock hidden APIs
    private static final String[] GLOBAL_SETTINGS_BLACKLIST_KEYS = {
            "hidden_api_policy",
            "hidden_api_policy_pre_p_apps",
            "hidden_api_policy_p_apps"
    };

    private final Object iWindowManager;

    /**
     * Constructor that initializes access to IWindowManager and unblocks private APIs.
     *
     * @param contentResolver The ContentResolver to modify global settings
     * @throws Exception if initialization fails
     */
    public WM(ContentResolver contentResolver) throws Exception {
        for (String key : GLOBAL_SETTINGS_BLACKLIST_KEYS) {
            Settings.Global.putInt(contentResolver, key, 1);
        }

        Class<?> windowManagerGlobalClass = Class.forName(WindowManagerConstants.WindowManagerGlobal.CLASS_NAME);
        Method getWindowManagerServiceMethod = windowManagerGlobalClass.getMethod(
                WindowManagerConstants.WindowManagerGlobal.GET_WINDOW_MANAGER_SERVICE
        );
        iWindowManager = getWindowManagerServiceMethod.invoke(null);
        
        if (iWindowManager == null) {
            throw new RuntimeException("Failed to get IWindowManager instance");
        }
    }

    /**
     * Sets a custom resolution for the display.
     *
     * @param x Width in pixels
     * @param y Height in pixels
     * @throws Exception if method access fails
     */
    public void setResolution(int x, int y) throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method setForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_SIZE,
                int.class, int.class, int.class
        );
        setForcedDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, x, y);
    }

    /**
     * Gets the real resolution of the display from system defaults.
     *
     * @return A Point containing width and height
     * @throws Exception if method access fails
     */
    public Point getRealResolution() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method getInitialDisplaySizeMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_SIZE,
                int.class, Point.class
        );
        Point point = new Point();
        getInitialDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, point);
        return point;
    }

    /**
     * Clears the custom resolution and restores default resolution.
     *
     * @throws Exception if method access fails
     */
    public void clearResolution() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method clearForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_SIZE,
                int.class
        );
        clearForcedDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY);
    }

    /**
     * Sets a custom display density (DPI).
     *
     * @param density The desired DPI value
     * @throws Exception if method access fails
     */
    public void setDisplayDensity(int density) throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            Method setForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                    WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY,
                    int.class, int.class
            );
            setForcedDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, density);
        } else {
            Method setForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                    WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY_FOR_USER,
                    int.class, int.class, int.class
            );
            setForcedDisplayDensityForUserMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, density, USER_ID);
        }
    }

    /**
     * Clears the custom display density and restores default density.
     *
     * @throws Exception if method access fails
     */
    public void clearDisplayDensity() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            Method clearForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                    WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY,
                    int.class
            );
            clearForcedDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY);
        } else {
            Method clearForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                    WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY_FOR_USER,
                    int.class, int.class
            );
            clearForcedDisplayDensityForUserMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, USER_ID);
        }
    }

    /**
     * Retrieves the real (default) display density of the system.
     *
     * @return The density in DPI
     * @throws Exception if method access fails
     */
    public int getRealDensity() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method getInitialDisplayDensityMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_DENSITY,
                int.class
        );
        return (Integer) getInitialDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY);
    }
}