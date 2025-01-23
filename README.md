# Cordova Plugin AlwaysTop

This plugin allows you to keep an application always visible on top of the screen, even when the device is in sleep mode or after a reboot. It also provides functionalities to manage permissions, check the service status, and control battery optimization behavior.

---

## Installation

To install the plugin, run the following command:

```bash
cordova plugin add cordova-plugin-alwaystop
```

---

## Methods

### `enable(cb, err)`

Enables the service that keeps the application always visible. This method starts a foreground service to ensure the application remains on top of the screen.

**Example:**

```javascript
cordova.plugins.AlwaysTop.enable(
  function(success) {
    console.log('Service enabled successfully');
  },
  function(error) {
    console.error('Error enabling service:', error);
  }
);
```

---

### `disable(cb, err)`

Disables the service that keeps the application always visible. This method stops the foreground service.

**Example:**

```javascript
cordova.plugins.AlwaysTop.disable(
  function(success) {
    console.log('Service disabled successfully');
  },
  function(error) {
    console.error('Error disabling service:', error);
  }
);
```

---

### `isEnable(cb, err)`

Checks if the service that keeps the application always visible is currently running.

**Example:**

```javascript
cordova.plugins.AlwaysTop.isEnable(
  function(isRunning) {
    if (isRunning) {
      console.log('Service is running');
    } else {
      console.log('Service is not running');
    }
  },
  function(error) {
    console.error('Error checking service status:', error);
  }
);
```

---

### `hasPermission(cb, err)`

Checks if the application has the necessary permissions to run the service.

**Example:**

```javascript
cordova.plugins.AlwaysTop.hasPermission(
  function(hasPermission) {
    if (hasPermission) {
      console.log('Application has the necessary permissions');
    } else {
      console.log('Application does not have the necessary permissions');
    }
  },
  function(error) {
    console.error('Error checking permissions:', error);
  }
);
```

---

### `requestPermission()`

Requests the necessary permissions to run the service. This method opens the application settings screen for the user to grant the required permissions.

**Example:**

```javascript
cordova.plugins.AlwaysTop.requestPermission();
```

---

### `appSettings()`

Opens the application settings screen. This can be useful for the user to manually grant permissions or adjust application settings.

**Example:**

```javascript
cordova.plugins.AlwaysTop.appSettings();
```

---

### `bateryNotOptimized(cb, err)`

Checks if the application is excluded from battery optimizations. If the application is optimized, the service may be stopped by the system to save battery.

**Example:**

```javascript
cordova.plugins.AlwaysTop.bateryNotOptimized(
  function(isOptimized) {
    if (isOptimized) {
      console.log('Application is optimized for battery');
    } else {
      console.log('Application is not optimized for battery');
    }
  },
  function(error) {
    console.error('Error checking battery optimization:', error);
  }
);
```

---

### `drawOverlays(cb, err)`

Checks if the application has permission to draw over other apps. This permission is required to keep the application always visible.

**Example:**

```javascript
cordova.plugins.AlwaysTop.drawOverlays(
  function(canDrawOverlays) {
    if (canDrawOverlays) {
      console.log('Application can draw over other apps');
    } else {
      console.log('Application cannot draw over other apps');
    }
  },
  function(error) {
    console.error('Error checking overlay permission:', error);
  }
);
```

---

### `keepAwake(cb, err)`

Keeps the device screen awake. This is useful to prevent the device from entering sleep mode while the application is in use.

**Example:**

```javascript
cordova.plugins.AlwaysTop.keepAwake(
  function(success) {
    console.log('Screen kept awake');
  },
  function(error) {
    console.error('Error keeping screen awake:', error);
  }
);
```

---

### `allowSleepAgain(cb, err)`

Allows the device to enter sleep mode again. This method reverses the effect of `keepAwake`.

**Example:**

```javascript
cordova.plugins.AlwaysTop.allowSleepAgain(
  function(success) {
    console.log('Device can enter sleep mode again');
  },
  function(error) {
    console.error('Error allowing sleep mode:', error);
  }
);
```

---

## Permissions

The plugin requires the following permissions in the `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

---

## Notes

- The foreground service (`ServiceLauncher`) starts automatically after a device reboot if the service was enabled before the reboot.
- The plugin is designed to work on devices with Android 5.0 (API level 21) or higher.
- Ensure that the necessary permissions are granted before attempting to enable the service.

---

## License

This plugin is licensed under the Apache 2.0 License. See the `LICENSE` file for more details.

---

## Plugin Structure

### `plugin.xml`

The plugin configuration file defines the plugin's ID, version, and platform-specific settings. It also includes the necessary permissions and Java source files.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plugin xmlns="http://www.phonegap.com/ns/plugins/1.0"
    xmlns:android="http://schemas.android.com/apk/res/android"
    id="cordova-plugin-alwaystop"
    version="1.0.0">
    <!-- Plugin details -->
</plugin>
```

### JavaScript Interface

The JavaScript interface (`www/always-top.js`) exposes the plugin's methods to the Cordova application.

```javascript
const exec = require('cordova/exec');

const AlwaysTop = {
    enable: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'enable', []);
    },
    disable: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'disable', []);
    },
    isEnable: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'is_service_running', []);
    },
    hasPermission: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'check_permission', []);
    },
    requestPermission: function () {
        exec(null, null, 'AlwaysTop', 'request_permission', []);
    },
    appSettings: function () {
        exec(null, null, 'AlwaysTop', 'app_settings', []);
    },
    bateryNotOptimized: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'batery_not_optimized', []);
    },
    drawOverlays: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'draw_overlays', []);
    },
    keepAwake: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'keep_awake', []);
    },
    allowSleepAgain: function (cb, err) {
        exec(cb, err, 'AlwaysTop', 'allow_sleep_again', []);
    }
};

module.exports = AlwaysTop;
```

### Java Implementation

The Java implementation (`src/android/AlwaysTop.java`) handles the plugin's core functionality, including service management, permission handling, and lifecycle events.

```java
package com.vayapedal.alwaystop;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class AlwaysTop extends CordovaPlugin {
    // Implementation details
}
```

### BootReceiver

The `BootReceiver` class listens for the `BOOT_COMPLETED` event and starts the foreground service after a device reboot.

```java
package com.vayapedal.alwaystop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ServiceLauncher.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
```

### ServiceLauncher

The `ServiceLauncher` class manages the foreground service and ensures the application remains visible.

```java
package com.vayapedal.alwaystop;

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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceLauncher extends Service {
    // Implementation details
}
```

### LifecycleHandler

The `LifecycleHandler` class tracks the current activity and ensures the application remains visible when resumed.

```java
package com.vayapedal.alwaystop;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class LifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // Implementation details
}
```

---

This documentation provides a comprehensive guide to using the `cordova-plugin-alwaystop` plugin, including detailed explanations of each method, examples, and implementation details.