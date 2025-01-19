package com.vayapedal.startservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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
  public int onStartCommand(Intent intent, int flags, int startId) {
    task(this, intent);
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
      NotificationManager.IMPORTANCE_LOW
    );
    NotificationManager manager = getSystemService(NotificationManager.class);
    manager.createNotificationChannel(channel);
  }

  private Notification createNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "boot_service_channel")
      .setContentTitle("Servicio en ejecución")
      .setContentText("El servicio está activo")
      .setSmallIcon(R.drawable.ic_cdv_splashscreen) //TODO añadir assset al plugin
      .setPriority(NotificationCompat.PRIORITY_LOW);
    return builder.build();
  }

  public void task(Context context, Intent intent) {
    if (timer == null) {
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            Log.i("ServiceLauncher", "task.run()");
            AppLauncher appLauncher = new AppLauncher();
            appLauncher.run(context);
          } catch (Exception e) {
            Log.e("ServiceLauncher", Objects.requireNonNull(e.getMessage()));
          }
        }
      }, 0, 10000);
    }
  }


}
