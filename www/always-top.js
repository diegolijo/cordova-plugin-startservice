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
}

module.exports = AlwaysTop;