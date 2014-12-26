
	
	 	var exec = require('cordova/exec') ; 
	module.exports = {
    start: function(successCallback, errorCallback) {
        //exec(successCallback, errorCallback, "ContactVcardPicker", "getContactVcard", []);
        exec(successCallback, errorCallback, 'WatermarkDetector', 'start', []);
    },
	stop: function(successCallback, errorCallback) {
		//exec(successCallback, errorCallback, "ContactVcardPicker", "getContactVcard", []);
		console.log("inside stop");
		exec(successCallback, errorCallback, 'WatermarkDetector', 'stop', []);
}
	};
function success(data)
{
alert("success"+data); 
}

 function failure(data)
{
alert("failure");
}