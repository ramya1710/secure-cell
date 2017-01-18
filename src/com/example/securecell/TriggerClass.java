package com.example.securecell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TriggerClass extends Activity implements LocationListener,OnClickListener{
	
	LocationManager locationmanager;
	Location location;
	MySQLiteHelper mysqlitehelper = null;
	
	Preview preview;
	
	private MediaRecorder mediarecorder = null;
	String audioFileName = null;
	String picFileName = null;
	
	private int intentCount = 0;
	
	private static int audioFinalCounter = 0;
	private static int picFinalCounter = 0;
	
	PendingIntent pendingintent = null;

	AlarmManager alarmmanager = null;
	
	PowerManager powermanager = null;
	PowerManager.WakeLock wakelock = null;
	
	Ringtone ringtone = null;
	
	Vibrator vibrator = null;

	File directory;
	File internalFile;
	File audioFileFolder;
	File picFileFolder;
	File audioFileMain;
	File picFileMain;
	
	MailSender mailsender;
	
	Camera camera;
	SurfaceView surfaceview;
	SurfaceHolder surfaceholder;
	
	startTimerRecording starttimerRecording;
	startTimerEmail starttimerEmail;
	startTimerPic starttimerpic;
	
	int audioCounter;
	
	Context context;
	
	double latitude;
	double longitude;
	boolean recordingsStatus = false;
	boolean checkStatus = true;
	//private boolean safeToTakePicture = false;
	private boolean safeToSendEmail = false;	
	
	String queryResultPhone = null;
	String queryResultEmail = null;
	
	TextView locDisplay;	
	Button backButton;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_alarm_activity); 
        mysqlitehelper = new MySQLiteHelper(this);  
        locDisplay = (TextView) findViewById(R.id.textView1);
        backButton = (Button) findViewById(R.id.button1);
        
        backButton.setOnClickListener(this);
        
        ///Tasks to be done Start
        
        ///send alert message to the registered cell number
        ///send Email with the picture and location
        ///record audio sample for 30 seconds
        ///run alarm for a period which is predefined by user
        
        ///Tasks to be done End
        
        recordAudioForApp();  
        
        preview = new Preview(this);
        
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);	
        
        starttimerpic = new startTimerPic(1000, 1000);
        starttimerpic.start();
        
        starttimerRecording = new startTimerRecording(2000, 1000);
        starttimerRecording.start();        
        
        starttimerEmail = new startTimerEmail(60000, 1000);
        starttimerEmail.start();
        
        Intent intent = new Intent(this,AlarmActivity.class); 
        intentCount = intentCount + 1;
        pendingintent = PendingIntent.getActivity(this, 0, intent, 0);	 		
 		
 		alarmmanager = (AlarmManager) getSystemService(ALARM_SERVICE);		 		
 		alarmmanager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 31000, pendingintent);
 		
        sendSMSTrigger();    
        
    	latitude = location.getLatitude();
        longitude = location.getLongitude();
        
        String infoText = "Your phone is mishandled by someone at location"
        					+"\n\nLatitude: "+location.getLatitude()
        					+"\nLongitude: "+location.getLongitude()
        					+"\n\nPress back button to go back or stop Alarm\n";
        locDisplay.setText(infoText);
	}
	
	public void onClick(View view){
		switch(view.getId()){
			case R.id.button1:
				Intent SecureCellMainIntent = new Intent(this,SecureCellMain.class);
		    	SecureCellMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
				startActivity(SecureCellMainIntent);   
			break;
		}
	}
	
	public class startTimerRecording extends CountDownTimer{
		
		public startTimerRecording(long startTime, long interval){
			super(startTime, interval);
		}	
		
			
		@Override
		public void onTick(long millisUntilFinished) {
			if(recordingsStatus == false){
				Log.d("Start Recording","onTick");				
			}
			else if(recordingsStatus == true){
				Log.d("Stop Recording","onTick");
			}
			
		}
		
		@Override
		public void onFinish() {
			Log.d("Timer","Finished");
			
			if(recordingsStatus == false){
				startRecordingAudio();				
			}
			else if(recordingsStatus == true){
				stopRecordingAudio();	
			}	
			
		}
	}
	
	public class startTimerEmail extends CountDownTimer{
		
		public startTimerEmail(long startTime, long interval){
			super(startTime, interval);
		}		
			
		@Override
		public void onTick(long millisUntilFinished) {
			if(safeToSendEmail == true){
				Log.d("Start Email","onTick");				
			}	
		}
		
		@Override
		public void onFinish() {
			Log.d("Start Email","Finished");
			
			if(safeToSendEmail == true){
				emailSetup();	
			}
			
			
		}
	}
	
	public class startTimerPic extends CountDownTimer{
		
		public startTimerPic(long startTime, long interval){
			super(startTime, interval);
		}		
			
		@Override
		public void onTick(long millisUntilFinished) {
			Log.d("Start Pic","onTick");				
		}
		
		@Override
		public void onFinish() {
			Log.d("Start Pic","Finished");
			
			preview.camera.takePicture(null, null, jpegCallback);
		}
	}
		
	///methods for location service Start
	@Override
	public void onLocationChanged(Location location) {
		//locDisplay = (TextView) findViewById(R.id.infoText);
		//locDisplay.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
		//Log.d("Location:",""+location.getLatitude()+","+location.getLongitude());
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		//Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		//Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//Log.d("Latitude","status");
	}
	///methods for location service End
	
	void sendSMS(Location location){
		
		queryResultPhone = mysqlitehelper.getEntry("Phone"); 
		
		latitude = location.getLatitude();
        longitude = location.getLongitude();
        
        String myUrl1 = String.valueOf(latitude);
		String myUrl2 = String.valueOf(longitude);
		
		String url = myUrl1+","+myUrl2;
		
		String locRef = "http://maps.google.com/maps?q=" + latitude+","+longitude;//https://www.google.com/maps?saddr=
        //String locRef = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        SmsManager smsmanager = SmsManager.getDefault();
        StringBuffer smsContent = new StringBuffer();
        smsContent.append("Your friend's phone is being tried to unlock by unknown persons. \nFor Location of device tap below link\n\n");
        smsContent.append(Uri.parse(locRef));        
        smsmanager.sendTextMessage(queryResultPhone, null, smsContent.toString(), null, null);
        
        //Log.d("Location:",""+latitude+","+longitude);
        Log.d("Location:",url);
	}

	void sendSMSTrigger(){
		locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
		location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if(location == null){
        	location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        
        Log.d("Location", ""+location);
        
        if(location != null){
        	onLocationChanged(location);
        }
        else{
        	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        	startActivity(intent);
        }       
        
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
        
        sendSMS(location);     
	}

	void recordAudioForApp(){
		
		audioFileMain = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureCell");
		
		if(!audioFileMain.exists()){
			audioFileMain.mkdirs();
			Log.d("File Made",""+audioFileMain);
		}
		
		
		audioFileFolder = new File(audioFileMain.getAbsolutePath()+"/Audio");	
		
		
		if(!audioFileFolder.exists()){
			audioFileFolder.mkdirs();
			Log.d("File Made",""+audioFileFolder);
		}
		
		
		audioFileName = audioFileFolder+"/myAudioRecording"+audioFinalCounter+".3gp";
		
		audioFinalCounter = audioFinalCounter + 1;
		
		mediarecorder = new MediaRecorder();
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediarecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		mediarecorder.setOutputFile(audioFileName);
	}
	
	void startRecordingAudio(){
		Log.d("Started","Recording");
		try {
			mediarecorder.prepare();
			mediarecorder.start();
			
			recordingsStatus = true;		
			
			starttimerRecording = new startTimerRecording(30000, 1000);
			starttimerRecording.start();		

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void stopRecordingAudio(){
		
		if(mediarecorder != null){
			mediarecorder.stop();
			mediarecorder.reset();			
			mediarecorder.release();			
			mediarecorder = null;			
			
			recordingsStatus = false;
			
			safeToSendEmail = true;
			
			Log.d("Stopped","Recording");
			
			Toast.makeText(getApplicationContext(), "Stop recording...", Toast.LENGTH_LONG).show();
		}
	}

	void emailSetup(){
		
		String senderEmail = "team.securecell@gmail.com";
		String senderpassword = "securecell541";
		
		mailsender = new MailSender();
		
		queryResultEmail = mysqlitehelper.getEntry("Email");
		
		mailsender.initializer(senderEmail,senderpassword);
		
		String[] toArr = {queryResultEmail,senderEmail};
		
		mailsender.setTo(toArr);
		mailsender.setFrom(senderEmail);
		mailsender.setSubject("Security Alert");
		mailsender.setLatitude(latitude);
		mailsender.setLongitude(longitude);
		
		mailsender.setBody("Your phone is being tried to unlock by unknown persons. \nFor Location on device tap below link\n\n http://maps.google.com/maps?q=" + latitude+","+longitude);
		
		audioFileMain = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureCell");
		picFileMain = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureCell");
		
		audioFileFolder = new File(audioFileMain.getAbsolutePath()+"/Audio");
		picFileFolder = new File(picFileMain.getAbsolutePath()+"/Pictures");
		
		audioFileName = audioFileFolder+"/myAudioRecording"+(audioFinalCounter-1)+".3gp";
		picFileName = picFileFolder+"/myPic"+(picFinalCounter-1)+".jpg";
		
		Log.d("Audio File Name",audioFileName);
		Log.d("Pic File Name",picFileName);
				
		try {			
			if(mailsender.send(audioFileName,picFileName)){
				Log.d("Email", "Was Sent Successfully");
			}
			else{
				Log.d("Email", "Was not Sent Successfully");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				picFileMain = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureCell");
				
				if(!picFileMain.exists()){
					picFileMain.mkdirs();
					Log.d("File Made",""+picFileMain);
				}
				
				picFileFolder = new File(picFileMain.getAbsolutePath()+"/Pictures");
				
				if(!picFileFolder.exists()){
					picFileFolder.mkdirs();
					Log.d("File Made",""+picFileFolder);
				}
				
				FileOutputStream  outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureCell"+"/Pictures"+"/myPic"+picFinalCounter+".jpg");
				
				picFinalCounter = picFinalCounter + 1;
				
				outStream.write(data);
				outStream.close();			
				
				camera.stopPreview();							
				camera.release();				
				camera = null;		
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}			
		}
	};
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(camera != null){
			camera.stopPreview();							
			camera.release();				
			camera = null;
		}
		
		if(mediarecorder != null){
			mediarecorder.stop();
			mediarecorder.reset();			
			mediarecorder.release();			
			mediarecorder = null;	
		}
	}


}
