package com.example.securecell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsPage extends Activity implements OnClickListener{
	
	MySQLiteHelper mysqlitehelper = null;
	
	EditText phoneNumber = null;
	EditText emailId = null;
	
	Button saveButton = null;
	Button backButton = null;
	
	String phoneNumberToSave = null;
	String emailIdToSave = null;
	
	String queryResultPhone = null;
	String queryResultEmail = null;
	
	boolean phoneSuccess = false;
	boolean emailSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);      
        
        mysqlitehelper = new MySQLiteHelper(this);  
        queryResultPhone = mysqlitehelper.getEntry("Phone");
        
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        
        if(queryResultPhone.compareTo("-1") == 0){ 
        	phoneNumber.setText("");   
        }
        else if(queryResultPhone.compareTo("-1") != 0){
        	phoneNumber.setText(queryResultPhone);    //id = 1 for phone number
        } 
        
        emailId = (EditText) findViewById(R.id.emailId);
        queryResultEmail = mysqlitehelper.getEntry("Email");
        
        if(queryResultEmail.compareTo("-1") == 0){   
        	emailId.setText("");
        }
        
        else if(queryResultEmail.compareTo("-1") != 0){   
        	emailId.setText(queryResultEmail);
        }
        
        saveButton = (Button) findViewById(R.id.save);
        backButton = (Button) findViewById(R.id.back);
        
        saveButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        
    }
	
	public void onClick(View view){
		switch(view.getId()){
			case R.id.save:
				phoneNumberToSave = phoneNumber.getText().toString();
				emailIdToSave = emailId.getText().toString();
				
				if(phoneNumberToSave.length() == 0){
					Toast.makeText(this, "Please Enter the Phone Number", Toast.LENGTH_LONG).show();
				}								
				else if(phoneNumberToSave.length() != 0){
					queryResultPhone = mysqlitehelper.getEntry("Phone");
					
					if(queryResultPhone.compareTo("-1") == 0){
						mysqlitehelper.addEntry("Phone",phoneNumberToSave);
			        }
			        else{
			        	mysqlitehelper.updateEntry("Phone",phoneNumberToSave);    //id = 1 for phone number
			        } 	
					
					phoneSuccess = true;
				}
				
				if(emailIdToSave.length() == 0){
					Toast.makeText(this, "Please Enter the Email Id", Toast.LENGTH_LONG).show();
				}				
				else if(emailIdToSave.length() != 0){
					queryResultEmail = mysqlitehelper.getEntry("Email");
					
					if(queryResultEmail.compareTo("-1") == 0){
						mysqlitehelper.addEntry("Email", emailIdToSave);
					}
					else{
						mysqlitehelper.updateEntry("Email", emailIdToSave);
					}
					
					emailSuccess = true;
				}
				
				if(phoneSuccess == true && emailSuccess == true){
					Intent MainPageIntent = new Intent(this,SecureCellMain.class);
					MainPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
	    			startActivity(MainPageIntent);    
				}			
			break;
			
			case R.id.back:
				Intent MainPageIntent = new Intent(this,SecureCellMain.class);
				MainPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
    			startActivity(MainPageIntent);
			break;
		}
			
		
	}

}



















