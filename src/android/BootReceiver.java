package com.vayapedal.startservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i("BootReceiver", "onReceive");
    if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Log.i("BootReceiver", "ACTION_BOOT_COMPLETED");
      Intent serviceIntent = new Intent(context, ServiceLauncher.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.i("BootReceiver", "startForegroundService");
        context.startForegroundService(serviceIntent);
      } else {
        context.startService(serviceIntent);
      }
    }
  }

}
