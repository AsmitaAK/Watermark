var exec = require('cordova/exec');

/**
 * Provides access to the vibration mechanism on the device.
 */

module.exports = {

    /**
     * Causes the device to vibrate.
     *
     * @param {Integer} mills       The number of milliseconds to vibrate for.
     */
     
    start: function(successCallback, errorCallback) {
        //exec(successCallback, errorCallback, "ContactVcardPicker", "getContactVcard", []);
        exec(successCallback, errorCallback, 'WatermarkDetector', 'start', []);
    },
};
