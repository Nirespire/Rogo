package com.rogoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;	// Wait for specified time before going to main screen

public class SplashScreen extends Activity {

	static int SPLASH_TIME_OUT = 3000;	// Splash screen timer
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {
			/* Showing splash screen with timer */
			
			public void run() {
				/* Start main activity after splash screen over */
				Intent i = new Intent(SplashScreen.this,MainScreenActivity.class);
				startActivity(i);
				
				// close the activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
