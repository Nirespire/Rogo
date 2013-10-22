package com.rogoapp.auth;

import android.app.Service;  
import android.content.Intent;  
import android.os.IBinder; 

public class AuthenticationService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// Returns IBinder. IBinder binds an activity to a service (let's them communicate)
		//According to documentation, can't make an AccAuth without a service to return the IBinder
		return new AccountAuthenticator(this).getIBinder();
	}

}
