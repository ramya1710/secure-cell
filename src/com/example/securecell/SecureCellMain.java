package com.example.securecell;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecureCellMain extends Activity implements OnClickListener{

	MySQLiteHelper mysqlitehelper = null;
	
	Button startApp = null;
	Button settingsPage = null;
	Button about = null;
	
	TextView infoText = null;
	
	String queryResultPhone = null;
	String queryResultEmail = null;
	
	boolean setContentVar = false;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_cell_main);
        
        mysqlitehelper = new MySQLiteHelper(this);
        
        infoText = (TextView) findViewById(R.id.infoText);
        
        queryResultPhone = mysqlitehelper.getEntry("Phone");   
		queryResultEmail = mysqlitehelper.getEntry("Email");
        
		if((queryResultPhone.compareTo("-1") == 0) || (queryResultEmail.compareTo("-1") == 0)){
			
			String noPhoneEmail = "Welcome to Secure Cell\n\n"
					+"Please click \"Settings\" and enter:\n"
					+"Phone Number of a Friend\n"
					+"Email ID to get the mail\n\n";
        	infoText.setText(noPhoneEmail);
        }
		else if(setContentVar == false && (queryResultPhone.compareTo("-1") != 0) && (queryResultEmail.compareTo("-1") != 0)){
        	String initialMsg = "Welcome to Secure Cell\n\n"
        			+"You Can Start Monitoring the Phone by Clicking \"Monitor\" Button";
        	
        	infoText.setText(initialMsg);
        }
        else if(setContentVar == true){
        	String finalMsg = "Your Phone is now being monitored for Security Alert\n\n";
        	infoText.setText(finalMsg);
        }      
        
        startApp = (Button) findViewById(R.id.startApp);
        settingsPage = (Button) findViewById(R.id.settings);
        about = (Button) findViewById(R.id.about);
        
        startApp.setOnClickListener(this);
        settingsPage.setOnClickListener(this);
        about.setOnClickListener(this);
    
    }
    
    public void onClick(View view){
    	switch(view.getId()){
    		case R.id.startApp:
    			
    			setContentVar = true; 
    			
    			/*queryResultPhone = mysqlitehelper.getEntry("Phone");   
    			queryResultEmail = mysqlitehelper.getEntry("Email");*/
    			
    			if((queryResultPhone.compareTo("-1") == 0) || (queryResultEmail.compareTo("-1") == 0)){
    				Intent settingsIntent = new Intent(this,SettingsPage.class);        			
        			startActivity(settingsIntent);
    	        }
    			else if((queryResultPhone.compareTo("-1") != 0) && (queryResultEmail.compareTo("-1") != 0)){
    				
	    	      	ComponentName componentname = new ComponentName(this, AdminReceiver.class);    	        
	        	        
        	    	Intent AdminReceiverintent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        	    	
        	    	AdminReceiverintent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentname);
        	    	AdminReceiverintent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_explanation));
        	    	
        	    	startActivity(AdminReceiverintent);
        	    	
        	    	Intent SecureCellMainIntent = new Intent(this,SecureCellMain.class);
        	    	SecureCellMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
        			startActivity(SecureCellMainIntent);  
    			}    	            	    	
    	    break;
    	    
    		case R.id.settings:
    			Intent settingsIntent = new Intent(this,SettingsPage.class);
    			
    			startActivity(settingsIntent);
    		break;
    		
    		case R.id.about:
    			Intent aboutIntent = new Intent(this,AboutClass.class);
    			
    			startActivity(aboutIntent);
    		break;
    	}
    }
    
}
