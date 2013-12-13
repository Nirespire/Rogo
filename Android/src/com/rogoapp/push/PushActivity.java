package com.rogoapp.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rogoapp.ServerClient;



public class PushActivity {

    //For GCM Services:
    public static final String EXTRA_MESSAGE = "message";	//i have no idea what this is for
    public static final String PROPERTY_REG_ID = "registration_id";		//for shared prefs
    private static final String PROPERTY_APP_VERSION = "appVersion";	//for shared prefs
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static String SENDER_ID = "104532562629";	//represents the project id of Rogo on GCC
    static final String TAG = "GCM";	//for debugging the GCM
    static GoogleCloudMessaging gcm;	//the gcm object, to handle all gcm services
	
    public static void register(Context context, SharedPreferences sharedPrefs){
    	
        if( checkPlayServices(context) ){
        	gcm = GoogleCloudMessaging.getInstance(context);	//get instance of gcm
        	String regid = getRegistrationId(context, sharedPrefs);
        		if( regid.equals("") ){	//if we've never registered
        			registerInBackground(context);	//register in the background
        		}
        }
        else{
        	Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }
    
	
	//this method checks that we have access to the play services, which are required for GCM
    private static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
    
    //this method gets the regid from shared preferences
    //if we have never stored it before, it will retrieve and return an empty string with an error tag
    //it will also return an empty string if the app has been updated (every new version requires a new regid)
    //else, it just returns the regid
    private static String getRegistrationId(Context context, SharedPreferences prefs){
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if ( registrationId.equals("") ) { 
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context); 
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    //simple method to get our app version
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    
    /*
     * registerInBackground
     * This method uses AsyncTask to perform registration on a separate thread
     * Push notification will not work until this thread finishes executing
     * If registration is successful, a pop-up will display notifying the user that Push Notification are active
     * If registration fails, a pop-up with an error will display
     * 
     */
    
    private static void registerInBackground(final Context context) {
    	new AsyncTask<Void, Void, String>() {
    		
    		protected String doInBackground(Void... params){
    			String msg = "";
    			String newRegid = "";
    			try {
    	            if (gcm == null) {
    	                gcm = GoogleCloudMessaging.getInstance(context);
    	            }
    	            newRegid = gcm.register(SENDER_ID);	//this gcm method executes our registration
    	            msg = "Device registered, registration ID=" + newRegid + "\nPush Notifications Enabled";

    	            
    	            //TODO: Write a request for the server to handle storing the regid
    	            sendRegistrationIdToBackend(newRegid);

    	            // Persist the regID - no need to register again.
    	            storeRegistrationId(context, newRegid);
    	        } catch (IOException ex) {
    	            msg = "Error :" + ex.getMessage();
    	            //If there is an error, don't just keep trying to register.
    	            //Display this error in a pop-up 
    	        }
    	        return msg;
    	    }

    	};
    }
    
    
    /*
     * sendRegistrationIdToBackend
     * This method uses ServerClient to send a post request with the regid to our server
     * TODO: Write a request to handle storing regid
     */
    private static void sendRegistrationIdToBackend(String regid){
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("regid", regid));
		String request = ""; //TODO: Implement request for server, write request name here
		JSONObject jObj = ServerClient.genericPostRequest(request, nameValuePairs);
		String status = "";
		try{
        	status = jObj.getString("status");
        	if(status.equals("error")){
    			System.out.println(jObj.getString("data"));
    		}else{
    			System.out.println("ID sent to backend");
    		}
        }catch(JSONException e){
        	System.err.print(e);
        }
		
    }
    
    //save the regid to preferences
    private static void storeRegistrationId(Context context, String regid){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
    	int appVersion = getAppVersion(context);
    	Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
}
