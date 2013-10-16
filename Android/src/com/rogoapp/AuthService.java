package com.rogoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new Auth(this).getIBinder();
	}

}
