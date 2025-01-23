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

## Notes

- The foreground service (`ServiceLauncher`) starts automatically after a device reboot if the service was enabled before the reboot.
- The plugin is designed to work on devices with Android 5.0 (API level 21) or higher.
- Ensure that the necessary permissions are granted before attempting to enable the service: The calls to hasPermission, batteryNotOptimized, and batteryNotOptimized must return true.

---

## License

This plugin is licensed under the Apache 2.0 License. See the `LICENSE` file for more details.
