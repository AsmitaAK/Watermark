package com.sanda.plugins.WatermarkDetector;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;



public class WatermarkDetector extends CordovaPlugin {

  private static final String ACTION_SHOW_EVENT = "start";



  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_SHOW_EVENT.equals(action)) {

      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
        
        	Context context = cordova.getActivity().getApplicationContext();
          	Intent i = new Intent(context,WatermarkDetectorApp.class);
            cordova.getActivity().startActivity(i);
        	

          callbackContext.success();
        }
      });

      return true;
    } else {
      callbackContext.error("Recording." + action + " is not a supported function. Did you mean '" + ACTION_SHOW_EVENT + "'?");
      return false;
    }
  }
}
