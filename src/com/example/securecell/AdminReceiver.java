package com.example.securecell;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AdminReceiver extends DeviceAdminReceiver{
	
	int passwordFailureCount;
	DevicePolicyManager devicepolicymanager;
	
	@Override
	public void onEnabled(Context context, Intent intent){
		
	}
	
	@Override
	public void onPasswordFailed(Context context, Intent intent){
		
		devicepolicymanager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		passwordFailureCount = devicepolicymanager.getCurrentFailedPasswordAttempts();
		Log.d("Entering: ","Password Fail");		
		
		if(passwordFailureCount == 3){
			Intent TriggerIntent = new Intent("com.example.securecell.TRIGGER_CLASS");	
			TriggerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
			context.startActivity(TriggerIntent);
		}
	}
	
	@Override
	public void onPasswordSucceeded(Context context, Intent intent){
		passwordFailureCount = 0;
	}
	
	@Override
    public void onDisabled(Context context, Intent intent) {
		
    }
}
