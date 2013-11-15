package com.rogoapp;

import com.rogoapp.auth.AccountAuthenticator;
import com.rogoapp.auth.RogoAuthenticatorActivity;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
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
                AccountManager am = AccountManager.get(getBaseContext());
                android.accounts.Account[] accounts = am.getAccountsByType(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
                RogoAuthenticatorActivity raa = new RogoAuthenticatorActivity();
//                raa.getAuthToken(accounts[0]);
//                am.getAuthToken(accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, null, RogoAuthenticatorActivity.class, null, null);
//                am.getAuthToken(account, authTokenType, options, notifyAuthFailure, callback, handler)
//                am.getAuthToken(account, authTokenType, notifyAuthFailure, callback, handler);
//                am.getAuthToken(account, authTokenType, options, activity, callback, handler)
//                //				AccountAuthenticatorResponse response = new AccountAuthenticatorResponse(null);
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
                try{
                    if(accounts != null)
                        token = am.peekAuthToken(accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
                }catch(Exception e){
                    e.printStackTrace();
                }

                Intent i;
                if(token != null && token != ""){
                    i = new Intent(SplashScreen.this, MainScreenActivity.class);
                }
                else{
                    i = new Intent(SplashScreen.this,RogoAuthenticatorActivity.class);
                }
                
                i.putExtra(RogoAuthenticatorActivity.OPEN_MAIN, true);
                startActivity(i);

                // close the activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
