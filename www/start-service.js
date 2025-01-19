const exec = require('cordova/exec');

const startService = {
    enable: function () {
        exec(null, null, 'StartService', 'enable', []);
    },
    enableService: function (id) {
        exec(null, null, 'StartService', 'enableService', [id]);
    },
    disable: function () {
        exec(null, null, 'StartService', 'disable', []);
    }
}

module.exports = startService;