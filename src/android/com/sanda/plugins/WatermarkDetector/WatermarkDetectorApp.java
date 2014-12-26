package com.sanda.plugins.WatermarkDetector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
//import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.cordova.PluginResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
//import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.fraunhofer.sit.watermarking.algorithmmanager.AlgorithmParameter;
import de.fraunhofer.sit.watermarking.algorithmmanager.WatermarkMessage;
import de.fraunhofer.sit.watermarking.algorithmmanager.detector.StreamWatermarkDetector;
import de.fraunhofer.sit.watermarking.algorithmmanager.exception.WatermarkException;
import de.fraunhofer.sit.watermarking.sitmark.audio.SITMarkAudioAnnotationDetector;

public class WatermarkDetectorApp extends Service {
//	private static final String SITMARK_AUDIO_ANNOTATION_ALGO_NAME = "SITMarkAudio2M";
	private static final String AUDIOANN_MESSAGE_LENGTH_PARAM = "NetMessageLength";
	public static final String AUDIOANN_FREQ_MIN_PARAM = "FreqMin";
	public static final String AUDIOANN_FREQ_MAX_PARAM = "FreqMax";
	public static final String AUDIOANN_ECC_TYPE_PARAM = "ErrorCorrectionType";
	public static final String AUDIOANN_WM_REDUNDANCY_PARAM = "WMRedundancy";
//	private static final File RESOURCE_FOLDER = null;
	private static final String MARKED_WHITE_NOISE_WAV = "markedWhiteNoise.wav";
	private static final String MESSAGE_IN_WHITENOISE = "000000001111111110101010";
	
	private Button mAudioStartBtn;
	private Button mAudioStopBtn;
//	private File mRecAudioFile; 
//	private File mRecAudioPath; 
//	private WatermarkDetectorApp mMediaRecorder;
//	private String strTempFile = "recaudio_";
	private AudioRecord mAudioRecord;
	boolean mStatus = true;
	
	private static int   sampleRateInHz = 44100;
	// AudioRecord 
	private int mBufSize;
	
	private byte[] bytes_pkg;
	
	private boolean isPaly = true;
//	private ArrayList<Byte[]> que = new ArrayList<Byte[]>();
	
	private TextView mResultTextView;
	private String outputMessage;
	int numberOfFoundMessage = 0 ;
	
	
	SITMarkAudioAnnotationDetector detector;
	@SuppressWarnings("deprecation")

	
	private void testDirectly(InputStream stream) {
		try {
			if (detector == null) {
				System.out.println("Testing SITMarkAudio annotation detector *without* AlgorithmManager");
				detector = new SITMarkAudioAnnotationDetector();
				System.out.println("Initializing parameters...");
				detector.initWithoutAlgoman();

				AlgorithmParameter messageLength = detector.getParameter(AUDIOANN_MESSAGE_LENGTH_PARAM);
				AlgorithmParameter minFreq= detector.getParameter(AUDIOANN_FREQ_MIN_PARAM);
				AlgorithmParameter maxFreq= detector.getParameter(AUDIOANN_FREQ_MAX_PARAM);
				AlgorithmParameter ECCMode= detector.getParameter(AUDIOANN_ECC_TYPE_PARAM);
				AlgorithmParameter WatermarkRedundancy= detector.getParameter(AUDIOANN_WM_REDUNDANCY_PARAM);
				
				messageLength.setValue("24");
				minFreq.setValue("2000");
				maxFreq.setValue("10000");
				ECCMode.setValue("1");
				WatermarkRedundancy.setValue("2");
				
				detector.setParameter(messageLength);
				detector.setParameter(minFreq);
				detector.setParameter(maxFreq);
				detector.setParameter(ECCMode);
				detector.setParameter(WatermarkRedundancy);
				detector.reinitialize();

				System.out.println("Starting detection from white noise file...");
			}
			
//			WatermarkMessage expectedMessage = new WatermarkMessage(MESSAGE_IN_WHITENOISE, WatermarkMessage.BINARY_MESSAGE);
//			InputStream markedWhiteNoise = getAssets().open(MARKED_WHITE_NOISE_WAV);
//			detectFromStream(detector, null/*expectedMessage*/, markedWhiteNoise);
			detectFromStream(detector, null, stream);
			System.out.println("Finished testing SITMarkAudio annotation detector. All is well.");
			
		} catch (WatermarkException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void detectFromStream(StreamWatermarkDetector detector,
			WatermarkMessage expectedMessage, InputStream markedWhiteNoise)
			throws WatermarkException {
		List<WatermarkMessage> detectedMessages;
		boolean foundMessage = false;
		
		Set<String> distinctFoundMessages = new TreeSet<String>();
		
		do {
			detectedMessages = detector.detect(markedWhiteNoise);
			foundMessage = (detectedMessages.size() > 0);
			if (foundMessage) {
				Log.i("Mark", foundMessage + "");
				WatermarkMessage detectedMessage = detectedMessages.get(0);
				double confidence = Double.parseDouble(detectedMessage.getMetaDate(WatermarkMessage.SCORE_METADATA));
				
				if (confidence > 0.1) {
					distinctFoundMessages.add(detectedMessage.toString());
					numberOfFoundMessage++;
					System.out.println("aaa ++++ " + detectedMessage.toString());
					//Asmita
//					if(numberOfFoundMessage >=1){
//						this.stopSelf();
//					}
//					listener.sendCode(detectedMessage.toString());
					sendCode(detectedMessage.toString(),"19.118962479677364","72.87003725767136");
					
					String result = detectedMessage.toString(); 
					
					Intent intent = new Intent("android.Watermark");
				//	WatermarkObject objMyobject = new WatermarkObject();
	                //objMyobject.mData = result;
	                intent.putExtra("value", result);
	               // startActivity(intent);
	                
	                sendBroadcast(intent);
	                
					Toast toast = Toast.makeText(getApplicationContext(), detectedMessage.toString(), Toast.LENGTH_LONG);
					toast.show();
					outputMessage += String.format("%s: %s\n", String.format("%03d",numberOfFoundMessage), detectedMessage.toString());
					

				}
			}
		} while (foundMessage);
		Log.i("Mark", String.format("Found %d messages (%d distinct): %s", numberOfFoundMessage, distinctFoundMessages.size(), distinctFoundMessages));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
//	@Override
//	protected void onStop() {
//		super.onStop();
//		mAudioRecord.release();
//		mAudioRecord = null;
//	}
	
	public class RecoderThread extends Thread {

		public AudioRecord mAudioRecord;
		public RecoderThread(AudioRecord mAudioRecord){
			this.mAudioRecord = mAudioRecord;
		}
		
		@Override
		public void run() {
			super.run();
			mAudioRecord.startRecording();
		
			while (isPaly)
	        {
				byte[] bytes = new byte[mBufSize];
				mAudioRecord.read(bytes, 0, mBufSize);
				System.out.println("write date to bytes...");
	            bytes_pkg = bytes.clone();
	            //Log.i("Mark", "........recordSound bytes_pkg==" + bytes_pkg.length);
	            //Log.i("Mark", Arrays.toString(bytes_pkg));
	            ByteArrayInputStream stream = new ByteArrayInputStream(bytes_pkg);
	            testDirectly(stream);
	        }
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		

//		
		mBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, 
		AudioFormat.ENCODING_PCM_16BIT);
		System.out.println("mBufSize = " + mBufSize);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
		sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
		AudioFormat.ENCODING_PCM_16BIT, mBufSize);

	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		start();
		//Asmita
		//this.stopSelf();
		return super.onStartCommand(intent, flags, startId);

	}
	 
	public void start() {
		// TODO Auto-generated method stub
		try {
			isPaly = true;
			outputMessage = "Start detecting watermarks...\n";
			System.out.println(outputMessage);
//			mResultTextView.setText(outputMessage);
			new RecoderThread(mAudioRecord).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//this.stopSelf();
		 isPaly = false;
		mAudioRecord.stop();
		WatermarkDetectorApp.RecoderThread.interrupted();
		//mAudioRecord = null;
		
	}
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
//Dec23 - Asmita
	public void sendCode(String code,String lat,String lon){
		//mAudioRecord.stop();
		Log.e("Inside SendCode Method", "stop Called on service");
		String url = "http://yourwellness.com/wemet/api/getting_started.php";
		String tag[] = {"request_type","code","lat","lon"};
		String value[] = {"get_banner_using_code"/*"wavemark"*/,code, lat , lon};

		String api = addParameter(url/*url[0]*/, tag, value);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(api);

		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			InputStream stream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
			String data = null;
			String result = null;
			while((data = reader.readLine()) != null){
				if(result == null){
					result = data;
				}else{
					result +=data;
				}
			}

			System.out.println(" response in result is " + result);
			//				UtilFunctions.getInstance().invokeRequest(_url);
				parseJSON(result);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	///Dec23 - Asmita
	public static String addParameter(String url,String[] tag,String[] value){
		if(!url.endsWith("?"))
			url += "?";

		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		//		params.add(new BasicNameValuePair("request_type","wavemark"));
		for(int i=0;i<tag.length;i++)
		{
			params.add(new BasicNameValuePair(tag[i],value[i]));
		}
		String paramString = URLEncodedUtils.format(params, "UTF-8");
		url += paramString;

		return url;		
	}
	public void parseJSON(String json){
		try {
			JSONObject jsonObjectRequest = new JSONObject(json);
			JSONObject jsonObject1 = jsonObjectRequest.getJSONObject("ResponseHeader");
			String request = jsonObject1.getString("request_type");
			Log.e("---------------request---------------------", request);
			
			JSONObject jsonObject = new JSONObject(json);
			JSONObject responsed = jsonObject.getJSONObject("ResponseDetail");
			String project_id = responsed.getString("project_id");
			String project_title =responsed.getString("project_title");
			String project_desc =responsed.getString("project_desc");
			String campaign_id =responsed.getString("campaign_id");
			String test_count =responsed.getString("test_count");
			String project_start_data =responsed.getString("project_start_data");
			String project_end_data =responsed.getString("project_end_data");
			String project_start_time =responsed.getString("project_start_time");
			String project_end_time =responsed.getString("project_end_time");
			String expiry_msg =responsed.getString("expiry_msg");

			if(responsed.has("ErrorCode")){
				String errorCode = responsed.getString("ErrorCode");

				if(errorCode.equalsIgnoreCase("1"))
					//return null;
					Log.e("Error in parsing data", "Error");
			}
			if(responsed.has("banner"))
			{

				Object baner = responsed.get("banner");

				if (baner instanceof JSONArray) {
					JSONArray banner = (JSONArray)baner;
					
				
					for(int i=0;i<banner.length();i++)
					{
						
						JSONObject jsonO = banner.getJSONObject(i);
						String id = (String)jsonO.getString("id");
						String title = (String) jsonO.getString("Title");
						String imageUrl = (String)jsonO.getString("Image_Url");
						String url = (String)jsonO.getString("Image_Link");
						String desc = (String)jsonO.getString("Description");
						Log.e("ID", Long.valueOf(id).toString());
						Log.e("Title", title);
						Log.e("imageUrl", imageUrl);
						Log.e("url", url);
						Log.e("desc", desc);
						Log.e("project_id", project_id);
						Log.e("project_start_data", project_start_data);
						Log.e("project_end_data", project_end_data);
						Log.e("project_end_data", project_end_data);
						Log.e("project_start_time", project_start_time);
						Log.e("expiry_msg", expiry_msg);
						

						
					}
					
				}else{
					JSONObject jsonO = (JSONObject)baner;
					
					String id = (String)jsonO.getString("id");
					String title = (String) jsonO.getString("Title");
					String imageUrl = (String)jsonO.getString("Image_Url");
					String url = (String)jsonO.getString("Image_Link");
					String desc = (String)jsonO.getString("Description");
					Log.e("ID", Long.valueOf(id).toString());
					Log.e("Title", title);
					Log.e("imageUrl", imageUrl);
					Log.e("url", url);
					Log.e("desc", desc);
					Log.e("project_id", project_id);
					Log.e("project_start_data", project_start_data);
					Log.e("project_end_data", project_end_data);
					Log.e("project_end_data", project_end_data);
					Log.e("project_start_time", project_start_time);
					Log.e("expiry_msg", expiry_msg);
					

					
				}
			}
			if(request.equals("success")){
			WatermarkDetector.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, responsed.toString()));
			System.out.println(" responsed.toString() is " + responsed.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
