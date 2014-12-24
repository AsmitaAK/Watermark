
	
	var exec 	var exec = require('cordova/exec') ; 
	module.exports = {
    start: function(successCallback, errorCallback) {
        //exec(successCallback, errorCallback, "ContactVcardPicker", "getContactVcard", []);
        exec(successCallback, errorCallback, 'WatermarkDetector', 'start', []);
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