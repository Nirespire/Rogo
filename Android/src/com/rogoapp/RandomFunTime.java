package com.rogoapp;

import android.app.Application;
import android.content.Context;


public class RandomFunTime extends Application{
	
	private static Context mcontext;
	
	public void onCreate(){
        super.onCreate();
        RandomFunTime.mcontext = getApplicationContext();
    }

    public static Context showContext() {
        return RandomFunTime.mcontext;
    }

}
