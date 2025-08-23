package ditzdevs.pixelify.me.models;

public final class WindowManagerConstants {
    private WindowManagerConstants() {}

    public static final class WindowManagerGlobal {
        public static final String CLASS_NAME = "android.view.WindowManagerGlobal";
        public static final String GET_WINDOW_MANAGER_SERVICE = "getWindowManagerService";

        private WindowManagerGlobal() {}
    }

    public static final class IWindowManager {
        public static final String CLASS_NAME = "android.view.IWindowManager";
        public static final String SET_FORCED_DISPLAY_SIZE = "setForcedDisplaySize";
        public static final String CLEAR_FORCED_DISPLAY_SIZE = "clearForcedDisplaySize";
        public static final String SET_FORCED_DISPLAY_DENSITY = "setForcedDisplayDensity";
        public static final String SET_FORCED_DISPLAY_DENSITY_FOR_USER = "setForcedDisplayDensityForUser";
        public static final String CLEAR_FORCED_DISPLAY_DENSITY = "clearForcedDisplayDensity";
        public static final String CLEAR_FORCED_DISPLAY_DENSITY_FOR_USER = "clearForcedDisplayDensityForUser";
        public static final String GET_INITIAL_DISPLAY_SIZE = "getInitialDisplaySize";
        public static final String GET_INITIAL_DISPLAY_DENSITY = "getInitialDisplayDensity";

        private IWindowManager() {}
    }
}