package com.sanda.plugins.WatermarkDetector;

import java.io.ByteArrayInputStream;
//import java.io.File;
import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
//import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.fraunhofer.sit.watermarking.algorithmmanager.AlgorithmParameter;
import de.fraunhofer.sit.watermarking.algorithmmanager.WatermarkMessage;
import de.fraunhofer.sit.watermarking.algorithmmanager.detector.StreamWatermarkDetector;
import de.fraunhofer.sit.watermarking.algorithmmanager.exception.WatermarkException;
import de.fraunhofer.sit.watermarking.sitmark.audio.SITMarkAudioAnnotationDetector;

public class WatermarkDetectorApp extends Activity {
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		
		
		mBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		System.out.println("mBufSize = " + mBufSize);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
				sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, mBufSize);
		

		mAudioStartBtn = (Button) findViewById(R.id.mediarecorder1_AudioStartBtn);
		mAudioStopBtn = (Button) findViewById(R.id.mediarecorder1_AudioStopBtn);
		mResultTextView = (TextView)findViewById(R.id.resultTextView);
		mResultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		mResultTextView.setText("Ready");

		
		mAudioStartBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					isPaly = true;
					outputMessage = "Start detecting watermarks...\n";
					mResultTextView.setText(outputMessage);
					new RecoderThread().start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		mAudioStopBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAudioRecord.startRecording();
				isPaly = false;
				mResultTextView.setText("Ready");
				numberOfFoundMessage = 0;
				detector = null;
			}
		}); 
	}
	
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
			
			WatermarkMessage expectedMessage = new WatermarkMessage(MESSAGE_IN_WHITENOISE, WatermarkMessage.BINARY_MESSAGE);
			InputStream markedWhiteNoise = getAssets().open(MARKED_WHITE_NOISE_WAV);
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
					System.out.println("++++ " + detectedMessage.toString());
					outputMessage += String.format("%s: %s\n", String.format("%03d",numberOfFoundMessage), detectedMessage.toString());
					mResultTextView.post(new Runnable(){
					    @Override
					    public void run() {
					    	mResultTextView.setText(outputMessage);
					    }
					});
				}
			}
		} while (foundMessage);
		Log.i("Mark", String.format("Found %d messages (%d distinct): %s", numberOfFoundMessage, distinctFoundMessages.size(), distinctFoundMessages));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		mAudioRecord.release();
		mAudioRecord = null;
	}
	
	public class RecoderThread extends Thread {

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
}
