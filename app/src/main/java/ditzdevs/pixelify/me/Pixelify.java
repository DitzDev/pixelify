package ditzdevs.pixelify.me;
import android.app.Application;
import ditzdevs.pixelify.me.handler.CrashHandler;
import ditzdevs.pixelify.me.utils.SettingsUtils;

public class Pixelify extends Application {
   @Override
   public void onCreate() {
       super.onCreate();
       CrashHandler.initialize(this);
       SettingsUtils.initializeAppSettings(this);
   }
}
