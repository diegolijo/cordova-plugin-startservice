package com.vayapedal.alwaystop;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import org.json.JSONObject;

public class AlwaysTop extends CordovaPlugin {
  CallbackContext callbackContext = null;
  public static final int REQUEST_CODE = 1001;
  public static final String PREFS = "autostart";
  public static final String ACTIVITY_CLASS_NAME = "class";

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    cordova.getActivity().getApplication().registerActivityLifecycleCallbacks(new LifecycleHandler());
  }

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callback)
    throws JSONException {
    if (action.equalsIgnoreCase("enable")) {
      callbackContext = callback;
      enableAutoStart(cordova.getActivity().getLocalClassName());
      return true;
    } else if (action.equalsIgnoreCase("disable")) {
      callbackContext = callback;
      disableAutoStart();
      return true;
    } else if (action.equalsIgnoreCase("check_permission")) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, checkPermission());
      callback.sendPluginResult(result);
      return true;
    } else if (action.equalsIgnoreCase("request_permission")) {
      callbackContext = callback;
      requestNotificationPermission();
      return true;
    } else if (action.equalsIgnoreCase("app_settings")) {
      openAppSettings();
      return true;
    } else if (action.equalsIgnoreCase("is_service_running")) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, isServiceRunning());
      callback.sendPluginResult(result);
      return true;
    } else if (action.equalsIgnoreCase("batery_not_optimized")) {
      callbackContext = callback;
      batteryNotOptimized();
      return true;
    } else if (action.equalsIgnoreCase("draw_overlays")) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, canDrawOverlays());
      callback.sendPluginResult(result);
      return true;
    }
    return false;
  }

  private void enableAutoStart(final String className) {
    try {
      boolean b = checkPermission();
      if (!b) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "No permission");
        callbackContext.sendPluginResult(result);
        return;
      }
      if (className != null) {
        setAutoStart(className, true);
      }
      Intent serviceIntent = new Intent(cordova.getActivity(), ServiceLauncher.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        cordova.getActivity().getApplicationContext().startForegroundService(serviceIntent);
      } else {
        cordova.getActivity().getApplicationContext().startService(serviceIntent); // todo probar
      }
      PluginResult result = new PluginResult(PluginResult.Status.OK, true);
      callbackContext.sendPluginResult(result);
    } catch (Exception e) {
      PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
      callbackContext.sendPluginResult(result);
    }
  }

  private void disableAutoStart() {
    setAutoStart(null, false);
    if (isServiceRunning()) {
      Intent serviceIntent = new Intent(cordova.getActivity().getApplicationContext(), ServiceLauncher.class);
      cordova.getActivity().getApplicationContext().stopService(serviceIntent);
    }
    PluginResult result = new PluginResult(PluginResult.Status.OK, true);
    callbackContext.sendPluginResult(result);
  }

  private void setAutoStart(final String className, boolean enabled) {
    try {
      Context context = cordova.getActivity().getApplicationContext().createDeviceProtectedStorageContext();
      int componentState;
      SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sp.edit();
      if (enabled) {
        componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        editor.putString(ACTIVITY_CLASS_NAME, className);
      } else {
        componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        editor.remove(ACTIVITY_CLASS_NAME);
      }
      editor.apply();
      // Enable or Disable BootReceiver
      ComponentName BootReceiver = new ComponentName(context, BootReceiver.class);
      PackageManager pm = context.getPackageManager();
      pm.setComponentEnabledSetting(BootReceiver, componentState, PackageManager.DONT_KILL_APP);
    } catch (Exception e) {
      Log.e("AutoStart setAutoStart", Objects.requireNonNull(e.getMessage()));
    }
  }

  private boolean isServiceRunning() {
    ActivityManager manager = (ActivityManager) cordova.getActivity().getApplicationContext()
      .getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (ServiceLauncher.class.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  /* * * * * * * * * * * * * * * PERMISSIONS * * * * * * * * * * * * * * * */
  private boolean checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      return ContextCompat.checkSelfPermission(cordova.getActivity(),
        Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
    // versión de Android anterior a Tiramisu, asumimos el permiso concedido
    return true;
  }

  private void requestNotificationPermission() {
    if (checkPermission()) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, true);
      callbackContext.sendPluginResult(result);
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.requestPermissions(
        cordova.getActivity(),
        new String[]{Manifest.permission.POST_NOTIFICATIONS},
        REQUEST_CODE);
    }
  }

  @Override // TODO no se dispara nunca!
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CODE) {
      boolean b = false;
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        b = true;
      }
      PluginResult result = new PluginResult(PluginResult.Status.OK, b);
      callbackContext.sendPluginResult(result);
    }
  }

  /**
   * Verifica restricciones de la batería.
   */
  public void batteryNotOptimized() {
    Context context = cordova.getActivity().getApplicationContext();
    boolean batteryNotOptimized = false;
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if (powerManager != null) {
      batteryNotOptimized = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }
    PluginResult result = new PluginResult(PluginResult.Status.OK, batteryNotOptimized);
    callbackContext.sendPluginResult(result);
  }


  /**
   * Verifica si la aplicación tiene el permiso "Mostrar encima de otras
   * aplicaciones".
   */
  public boolean canDrawOverlays() {
    Context context = cordova.getActivity().getApplicationContext();
    return Settings.canDrawOverlays(context);
  }

  public void openAppSettings() {
    try {
      Intent intent = new Intent();
      Context context = cordova.getActivity().getApplicationContext();
      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      String packageName = context.getPackageName();
      Uri uri = Uri.fromParts("package", packageName, null);
      intent.setData(uri);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(intent);
    } catch (Exception e) {
      Log.e("AutoStart openAppSettings", Objects.requireNonNull(e.getMessage()));
    }
  }

}
