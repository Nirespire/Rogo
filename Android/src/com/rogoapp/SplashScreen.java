package com.rogoapp;

import com.rogoapp.auth.RogoAuthenticatorActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;	// Wait for specified time before going to main screen

public class SplashScreen extends Activity {

    static int SPLASH_TIME_OUT = 1500;	// Splash screen timer
    
	private static Context mcontext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        SplashScreen.mcontext = getApplicationContext();


        new Handler().postDelayed(new Runnable() {
            /* Showing splash screen with timer */

            public void run() {
                /* Start main activity after splash screen over */
                AccountManager am = AccountManager.get(getBaseContext());
                android.accounts.Account[] accounts = am.getAccountsByType(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);


                //				AccountAuthenticator auth = new AccountAuthenticator(MyApplication.getAppContext());
                //				
                //				Bundle token = null;
                //				try {
                //					token = auth.getAuthToken(response, accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, null);
                //				} catch (NetworkErrorException e) {
                //					token = null;
                //					e.printStackTrace();
                //				}
                
                String token = "";

                if(accounts.length != 0)
                        token = am.peekAuthToken(accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
                Intent i;
                if(token != null && token != ""){
                    i = new Intent(SplashScreen.this, MainScreenActivity.class);
                }
                else{
                    i = new Intent(SplashScreen.this,RogoAuthenticatorActivity.class);
                    i.putExtra(RogoAuthenticatorActivity.OPEN_MAIN, true);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                
                startActivity(i);

                // close the activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    
    public static Context showContext() {
        return SplashScreen.mcontext;
    }

}
