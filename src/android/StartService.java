/*
 Author: Toni Korin

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */
package com.vayapedal.startservice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
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

public class StartService extends CordovaPlugin {
  
  public static final int REQUEST_CODE = 0x0ba7c;
  public static final String PREFS = "autostart";
  public static final String ACTIVITY_CLASS_NAME = "class";
  public static final String SERVICE_CLASS_NAME = "service";

  @Override
  public boolean execute(String action, JSONArray args,
                         CallbackContext callback) throws JSONException {
    if (action.equalsIgnoreCase("enable")) {
      enableAutoStart(cordova.getActivity().getLocalClassName(), false);
      return true;
    } else if (action.equalsIgnoreCase("enableService")) {
      final String serviceClassName = args.getString(0);
      enableAutoStart(serviceClassName, true);
      return true;
    } else if (action.equalsIgnoreCase("disable")) {
      disableAutoStart();
      return true;
    }
    return false;
  }

  private void enableAutoStart(final String className, boolean isService) {
    checkPermission();
    if (className != null) {
      setAutoStart(className, true, isService);
    }
    Intent serviceIntent = new Intent(cordova.getActivity(), ServiceLauncher.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       cordova.getActivity().getApplicationContext().startForegroundService(serviceIntent);
    }
  }
  private void disableAutoStart() {
    setAutoStart(null, false, false);
  }

  private void setAutoStart(final String className, boolean enabled, boolean isService) {
    try {
      Context context = cordova.getActivity().getApplicationContext().createDeviceProtectedStorageContext();
      int componentState;
      SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sp.edit();
      if (enabled) {
        componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
         final String preferenceKey = isService ? SERVICE_CLASS_NAME : ACTIVITY_CLASS_NAME;
        editor.putString(preferenceKey, className);
      } else {
        componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        editor.remove(ACTIVITY_CLASS_NAME);
        editor.remove(SERVICE_CLASS_NAME);
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
  private void checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(cordova.getActivity(),
          new String[]{Manifest.permission.POST_NOTIFICATIONS},
          REQUEST_CODE);
      }
    }
  }
  public void openBatteryUsageSettings() {
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
      Log.e("AutoStart openBatteryUsageSettings", Objects.requireNonNull(e.getMessage()));
    }
  }


}
