package com.rogoapp;

import android.app.Application;
import android.content.Context;

public class Rogoapp extends Application{
	private static Context context;
	
	public void onCreate(){
		super.onCreate();
		Rogoapp.context = getApplicationContext();
		
	}
	
	public static Context getAppContext(){
		return Rogoapp.context;
	}
}
