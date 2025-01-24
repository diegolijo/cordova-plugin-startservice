package com.cocodin.alwaystop;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class LifecycleHandler implements Application.ActivityLifecycleCallbacks {

  private static String currentActivity = "";

  @Override
  public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(@NonNull Activity activity) {
  }

  @Override
  public void onActivityResumed(Activity activity) {
    currentActivity = activity.getClass().getName();
  }

  @Override
  public void onActivityPaused(@NonNull Activity activity) {
    currentActivity = "";
  }

  @Override
  public void onActivityStopped(@NonNull Activity activity) {
    currentActivity = "";
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {
    currentActivity = "";
  }

  public static String getCurrentActivity() {
    return currentActivity;
  }
}
