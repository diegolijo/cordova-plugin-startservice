const exec = require('cordova/exec');

const AlwaysTop = {
    enable: function () {
        exec(null, null, 'AlwaysTop', 'enable', []);
    },
    disable: function () {
        exec(null, null, 'AlwaysTop', 'disable', []);
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

}

module.exports = AlwaysTop;