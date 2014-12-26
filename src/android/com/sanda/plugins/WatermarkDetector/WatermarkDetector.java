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
	Context context;
	Intent i;
	public WatermarkDetector(){
	 code = new CodeReceiver();
	}

  private static final String ACTION_SHOW_EVENT = "start";
  private static final String ACTION_STOP_EVENT = "stop";

public static CallbackContext callback;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_SHOW_EVENT.equals(action)) {

    	
    	callback = callbackContext;
    	cordova.getActivity().registerReceiver(code, new IntentFilter("android.Watermark"));
    	 PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
         pluginResult.setKeepCallback(true);
         callback.sendPluginResult(pluginResult);
         context = cordova.getActivity().getApplicationContext();
       	 i = new Intent(context,WatermarkDetectorApp.class);
       	Log.e("Service Started", "startService Called");
       	cordova.getActivity().startService(i);
       	
     
      return true;
    }else  if (ACTION_STOP_EVENT.equals(action)){
    	//WatermarkDetectorApp.getThread().interrupt();
    	try {
			WatermarkDetectorApp.RecoderThread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
				Log.e("Stop Service ", "inside Stop Event");
				// TODO Auto-generated method stub
				context = cordova.getActivity().getApplicationContext();
		     	i = new Intent(context,WatermarkDetectorApp.class);
		        cordova.getActivity().stopService(i);
		        Log.e("Service Stoped", "stopService Called");
		        
		      
		
    	
        return true;
    	
    }
    else {
      callbackContext.error("Recording." + action + " is not a supported function. Did you mean '" + ACTION_SHOW_EVENT + "'?");
      return false;
    }
	
    
    
    
  }

@Override
public void onPause(boolean multitasking) {
	// TODO Auto-generated method stub
	super.onPause(multitasking);
	//Intent i = new Intent(context,WatermarkDetectorApp.class);
    //cordova.getActivity().stopService(i);
    context.stopService(i);
  
    //stopSelf();
    Log.e("Service Stopped", "Service Stopped");
}
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	// stopService(new Intent(getApplicationContext(), WatermarkDetectorApp.class));
	 context.stopService(i);
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
