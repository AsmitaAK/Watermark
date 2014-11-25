package com.sanda.plugins.WatermarkDetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Gravity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class WatermarkDetector extends CordovaPlugin  {
	CodeReceiver code;
	public WatermarkDetector(){
	 code = new CodeReceiver();
	}

  private static final String ACTION_SHOW_EVENT = "start";


public static CallbackContext callback;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_SHOW_EVENT.equals(action)) {

    	
    	callback = callbackContext;
    	cordova.getActivity().registerReceiver(code, new IntentFilter("android.Watermark"));
    	 PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
         pluginResult.setKeepCallback(true);
         callback.sendPluginResult(pluginResult);
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
        
        	Context context = cordova.getActivity().getApplicationContext();
          	Intent i = new Intent(context,WatermarkDetectorApp.class);
            cordova.getActivity().startService(i);
        	
            
//          callbackContext.success();
            
//          callbackContext.success("");        // callbackContext.
//            PluginResult result;
//            result = new PluginResult(PluginResult.Status.OK,
//                    "Wifi Connected");
//            result.setKeepCallback(true);
//           // result.setKeepCallback(false);
//            JSONObject json = new JSONObject();
//            try {
//				json.put("foo", "bar");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
           // callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "plugin result"));
            //result.setKeepCallback(false);
//            if (callbackContext != null) {
//                //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,"hello world"));
//                
//                //callbackContext = null;
//            }
            
//            PluginResult copy_ret = new PluginResult(PluginResult.Status.OK, "sanda plugin");
//            callbackContext.sendPluginResult(copy_ret);
//            callbackContext.success();
        }
        
      });

      return true;
    } else {
      callbackContext.error("Recording." + action + " is not a supported function. Did you mean '" + ACTION_SHOW_EVENT + "'?");
      return false;
    }
    
    
    
  }


public class CodeReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();

            PluginResult result;
            if (intent.getStringExtra("value") != null) {
             
                result = new PluginResult(PluginResult.Status.OK,intent.getStringExtra("value"));
            } else {
               
                result = new PluginResult(PluginResult.Status.ERROR,
                        "Error Fetching Code");
            }

            result.setKeepCallback(true);
            if (callback != null) {
            	callback.sendPluginResult(result); 
            
        }
	}
	
}


}
