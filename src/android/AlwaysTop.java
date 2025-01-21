package com.vayapedal.alwaystop;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class AlwaysTop extends CordovaPlugin {
  public static final int REQUEST_CODE = 100;
  public static final String PREFS = "autostart";
  public static final String ACTIVITY_CLASS_NAME = "class";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callback)
    throws JSONException {
    if (action.equalsIgnoreCase("enable")) {
      enableAutoStart(cordova.getActivity().getLocalClassName(), callback);
      return true;
    } else if (action.equalsIgnoreCase("disable")) {
      disableAutoStart(callback);
      return true;
    } else if (action.equalsIgnoreCase("check_permission")) {
      boolean b = checkPermission();
      PluginResult result = new PluginResult(PluginResult.Status.OK, b);
      callback.sendPluginResult(result);
      return true;
    } else if (action.equalsIgnoreCase("request_permission")) {
      requestNotificationPermission();
      return true;
    } else if (action.equalsIgnoreCase("app_settings")) {
      openAppSettings();
      return true;
    }
    return false;
  }

  private void enableAutoStart(final String className, CallbackContext callback) {
    try {
      boolean b = checkPermission();
      if (!b) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "No permission");
        callback.sendPluginResult(result);
        return;
      }
      if (className != null) {
        setAutoStart(className, true);
      }
      Intent serviceIntent = new Intent(cordova.getActivity(), ServiceLauncher.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        cordova.getActivity().getApplicationContext().startForegroundService(serviceIntent);
      }else{
        cordova.getActivity().getApplicationContext().startService(serviceIntent); // todo probar
      }
      PluginResult result = new PluginResult(PluginResult.Status.ERROR, b);
      result.setKeepCallback(false);
      callback.sendPluginResult(result);
    } catch (Exception e) {
      PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
      result.setKeepCallback(false);
      callback.sendPluginResult(result);
    }
  }

  private void disableAutoStart(CallbackContext callback) {
    setAutoStart(null, false);
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
      Log.i("setAutoStart setComponentEnabledSetting", "*********** BootReceiver ********");
    } catch (Exception e) {
      Log.e("AutoStart setAutoStart", Objects.requireNonNull(e.getMessage()));
    }
  }

  private boolean checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      return ContextCompat.checkSelfPermission(cordova.getActivity(),
        Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
    // versión de Android anterior a Tiramisu, asumimos el permiso concedido
    return true;
  }

  private void requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.requestPermissions(cordova.getActivity(),
        new String[]{Manifest.permission.POST_NOTIFICATIONS},
        REQUEST_CODE);
    }
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
