package com.example.securecell;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Preview extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mHolder;
    public Camera camera;

	Preview(Context context) {
        super(context);
        
        mHolder = getHolder();
        mHolder.addCallback(this);       
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open(1);
        try {
			camera.setPreviewDisplay(holder);				
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        //camera.stopPreview();
        //camera = null;
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
    	
    	Camera.Parameters parameters = camera.getParameters();
    	

    	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
    	parameters.set("rotation", 270);
    	camera.setParameters(parameters);
    	
        camera.startPreview();
    }
}
