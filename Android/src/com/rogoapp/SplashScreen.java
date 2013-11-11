package com.rogoapp;

import com.rogoapp.auth.RogoAuthenticatorActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;	// Wait for specified time before going to main screen

public class SplashScreen extends Activity {

	static int SPLASH_TIME_OUT = 1500;	// Splash screen timer
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {
			/* Showing splash screen with timer */
			
			public void run() {
				/* Start main activity after splash screen over */
				//AccountManager am = AccountManager.get(getBaseContext());
				//android.accounts.Account[] accounts = am.getAccountsByType(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
	            //String authToken = am.peekAuthToken(accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);

	            Intent i;
	            //if(authToken == null){
	           // 	i = new Intent(SplashScreen.this, MainScreenActivity.class);
	           // }
	           // else{
	            	i = new Intent(SplashScreen.this,RogoAuthenticatorActivity.class);
	           // }
				startActivity(i);
				
				// close the activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
