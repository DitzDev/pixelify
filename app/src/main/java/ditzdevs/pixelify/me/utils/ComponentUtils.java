package ditzdevs.pixelify.me.utils;

import android.content.Context;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import ditzdevs.pixelify.me.R;
import java.util.concurrent.Callable;

public class ComponentUtils {
   public void showDialogPosNeg(
         Context ctx, 
         String title, 
         String message, 
         Runnable onPositive,
         Runnable onNegative
     ) {
       new MaterialAlertDialogBuilder(ctx)
       .setTitle(title)
       .setMessage(message)
       .setPositiveButton(R.string.action_ok, (dialog, which) -> {
           if (onPositive != null) {
               onPositive.run();
           }    
       })
       .setNegativeButton(R.string.action_cancel, (dialog, which) -> {
           if (onNegative != null) {
               onNegative.run();
           }    
       })
       .show();
   }
}