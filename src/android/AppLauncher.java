package com.vayapedal.startservice;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppLauncher {
 
  public void run(Context context) {
    try {
      Log.d("AppLauncher", "STARTING APP...");
      SharedPreferences sp = context.createDeviceProtectedStorageContext().getSharedPreferences(StartService.PREFS, Context.MODE_PRIVATE);
      String packageName = context.getPackageName();
      String activityClassName = sp.getString(StartService.ACTIVITY_CLASS_NAME, "");
      if (!activityClassName.isEmpty()) {
        Log.d("AppLauncher packageName", packageName + "." + activityClassName);
        Intent activityIntent = new Intent().setClassName(packageName, packageName + "." + activityClassName);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(activityIntent);
      }
    } catch (Exception e) {
      Log.e("AppLauncher.run", String.valueOf(e));
    }
  }
}
