package com.example.securecell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends Activity implements OnClickListener{

	PowerManager powermanager = null;
	PowerManager.WakeLock wakelock = null;
	MediaPlayer mediaplayer = null;
	Ringtone ringtone = null;
	
	Vibrator vibrator = null;
	
	Button stopAlarm = null;
	
	LocationManager locationmanager;
	Location location;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.activity_alarm_activity);
		TextView locDisplay = (TextView) findViewById(R.id.textView1);
		
		
		locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		
		location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if(location == null){
			location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		String infoText = "Your phone is mishandled by someone at location"
				+"\n\nLatitude: "+location.getLatitude()
				+"\nLongitude: "+location.getLongitude()
				+"\n\nPress back button to go back or stop Alarm\n";
		locDisplay.setText(infoText);
		
		stopAlarm = (Button) findViewById(R.id.button1);		
		stopAlarm.setOnClickListener(this);	
		
		startAlarm();
		
	}
	
	@Override
	public void onClick(View view) {
		if(ringtone != null && ringtone.isPlaying()){
			ringtone.stop();
			
			if(wakelock != null && wakelock.isHeld()){
				wakelock.release();
				wakelock = null;
			}
		}
		
		Intent SecureCellMainIntent = new Intent(this,SecureCellMain.class);
    	SecureCellMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
		startActivity(SecureCellMainIntent);    
	}
	
	public void startAlarm(){
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		
		if(alert == null){
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if(alert == null){
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			}			
		}
		else{
			
		}
		
		//Get the wake lock to keep the phone awake till the timer expire
		powermanager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		wakelock = powermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Wake tag");
		
		ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
		
		//Register for the vibrator Broadcast
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		wakelock.acquire();
		ringtone.play();
		
	}
}
