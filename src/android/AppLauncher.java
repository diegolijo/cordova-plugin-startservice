package com.vayapedal.alwaystop;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppLauncher {

  public void run(Context context) {
    try {
      Log.d("AppLauncher", "STARTING APP...");
      SharedPreferences sp = context.createDeviceProtectedStorageContext().getSharedPreferences(AlwaysTop.PREFS, Context.MODE_PRIVATE);
      String packageName = context.getPackageName();
      String activityClassName = sp.getString(AlwaysTop.ACTIVITY_CLASS_NAME, "");
      if (!activityClassName.isEmpty()) {
        String classPath = String.format("%s.%s", packageName, activityClassName);
        Log.d("AppLauncher packageName",classPath);
        Intent activityIntent = new Intent().setClassName(packageName, classPath);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(activityIntent);
      }
    } catch (Exception e) {
      Log.e("AppLauncher.run", String.valueOf(e));
    }
  }
}
