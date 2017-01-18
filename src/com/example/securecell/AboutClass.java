package com.example.securecell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutClass extends Activity implements OnClickListener{

	TextView aboutText = null;
	
	Button backButton = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_about_class);
     
        aboutText = (TextView) findViewById(R.id.textView1);
        
        String AboutInfo = "Hello Welcome to the Secure Cell \n\n"
        					+"You can monitor your phone from the persons\n"
        					+"who try to access your phone without your permission\n"
        					+"\n\n"
        					+"Before you start monitoring please enter\n"
        					+ "\t\tMobile Number\n"
        					+"\t\tEmail ID\n"
        					+"in the Settings Page"
        					+"\n\n"
        					+"Once the person fails to unlock your device in 3 attempts you will get alerted\n\n"
        					+"\n\n"
        					+"Developers:\n"
        					+"\t\t Chanakya Pallapolu\n"
        					+"\t\t Ramya Deepa Palle\n";
        
        aboutText.setText(AboutInfo);
        
        backButton = (Button) findViewById(R.id.button1);
        
        backButton.setOnClickListener(this);
        
	}
	
	public void onClick(View view){
		switch(view.getId()){
			case R.id.button1:
				Intent homeIntent = new Intent(this,SecureCellMain.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);				
    			startActivity(homeIntent);
			break;
		}
	}	
}
