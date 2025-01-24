package com.cocodin.alwaystop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.cocodin.cocopricepharma.MainActivity;
import com.cocodin.cocopricepharma.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceLauncher extends Service {

  private Timer timer;

  @Override
  public void onCreate() {
    super.onCreate();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificationChannel();
      startForeground(1, createNotification());
    }
  }

  @Override
  public void onDestroy() {
    timer.cancel();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
      stopSelf();
      return START_NOT_STICKY;
    }
    task(this);
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void createNotificationChannel() {
    NotificationChannel channel = new NotificationChannel(
      "boot_service_channel",
      "Boot Service Channel",
      NotificationManager.IMPORTANCE_LOW);
    NotificationManager manager = getSystemService(NotificationManager.class);
    manager.createNotificationChannel(channel);
  }

  private Notification createNotification() {
    // Intent para abrir la aplicación
    Intent openAppIntent = new Intent(this, MainActivity.class);
    PendingIntent openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    // Intent para detener el servicio
    Intent stopServiceIntent = new Intent(this, ServiceLauncher.class);
    stopServiceIntent.setAction("STOP_SERVICE");
    PendingIntent stopServicePendingIntent = PendingIntent.getService(this, 1, stopServiceIntent,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "boot_service_channel")
      .setContentTitle("AlwaysTop")
      .setContentText("Mantendrá la aplicación siempre visible")
      .setSmallIcon(R.drawable.service_icon)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setContentIntent(openAppPendingIntent)
      .addAction(R.drawable.service_icon, "detener", stopServicePendingIntent);
    return builder.build();
  }

  public void task(Context context) {
    if (timer == null) {
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            SharedPreferences sp = context.createDeviceProtectedStorageContext()
              .getSharedPreferences(AlwaysTop.PREFS, Context.MODE_PRIVATE);
            String packageName = context.getPackageName();
            String activityClassName = sp.getString(AlwaysTop.ACTIVITY_CLASS_NAME, "");
            String classPath = String.format("%s.%s", packageName, activityClassName);
            if (!activityClassName.isEmpty() && !classPath.equals(LifecycleHandler.getCurrentActivity())) {
              Log.i("ServiceLauncher", "TRY STARTING APP...");
              Intent activityIntent = new Intent().setClassName(packageName, classPath);
              activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              context.startActivity(activityIntent);
            }
          } catch (Exception e) {
            Log.e("ServiceLauncher", Objects.requireNonNull(e.getMessage()));
          }
        }
      }, 0, 1000);
    }
  }

}
