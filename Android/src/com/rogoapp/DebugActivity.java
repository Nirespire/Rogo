package com.rogoapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.rogoapp.auth.RogoAuthenticatorActivity;

public class DebugActivity extends Activity {
    
    Button serverButton;
    Button registerButton;
    Button loginButton;
    Button meetingSomeoneButton;
 	Button buddyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	 public void addListenerOnButton1() {

	        registerButton = (Button) findViewById(R.id.registration_debug_button);

	        registerButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	            	openRegistrationScreen(arg0);
	            }

	        });

	 }
	 
	 public void addListenerOnButton2() {

	        registerButton = (Button) findViewById(R.id.login_debug_button);

	        registerButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	            	openLoginScreen(arg0);
	            }

	        });

	 }
	 
	 public void addListenerOnButton3() {

	        registerButton = (Button) findViewById(R.id.meeting_someone_debug_button);

	        registerButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	            	openMeetingSomeoneScreen(arg0);
	            }

	        });

	 }
	 
	 public void addListenerOnButton4() {

	        registerButton = (Button) findViewById(R.id.location_debug_button);

	        registerButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	            	String loc = getLocation(arg0);
	            }

	        });

	 }

	 public void addListenerOnBuddyListButton() {

         	//registerButton = (Button) findViewById(R.id.buddy_list_button);

         	registerButton.setOnClickListener(new OnClickListener() {

         		@Override
         		public void onClick(View arg0) {
	                     //openBuddyListScreen(arg0);
	                 }

	         	});
	     }
	 

	 
	 public void openRegistrationScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, RegisterActivity.class);
	        startActivity(intent);
	 }
	 
	 public void openLoginScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, RogoAuthenticatorActivity.class);
	        startActivity(intent);
	 }
	 
	 /*
	 public void openBuddyListScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, BuddyListActivity.class);
	        startActivity(intent);
	}
	    */
	
	 public void openMeetingSomeoneScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, MeetingSomeoneActivity.class);
	        startActivity(intent);
	 }
	 
	 public String getLocation(View v){

			final Context context = this;
			String bestProvider;
			List<Address> user = null;
			double lat;
			double lng;
			Geocoder geocoder;
			String out = "";

			LocationManager loc = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			bestProvider = loc.getBestProvider(criteria, false);
			Location location = loc.getLastKnownLocation(bestProvider);

			if (location == null){
				Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
				out = "Location Not found";
			}else{
				geocoder = new Geocoder(this);
				try {
					user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
					lat=(double)user.get(0).getLatitude();
					lng=(double)user.get(0).getLongitude();
					Toast.makeText(this," DDD lat: " +lat+",  longitude: "+lng, Toast.LENGTH_LONG).show();
					System.out.println(" DDD lat: " +lat+",  longitude: "+lng);
					out = " DDD lat: " +lat+",  longitude: "+lng;

				}catch (Exception e) {
					e.printStackTrace();
				}

			}
			return out;
		}
	 
	 //TODO
		public void onCacheRead(View v) throws IOException{
			String FILENAME = "Test";
			String txt = "Store this";
			String newVal = "";
			final Context context = this;
			File cacheDir = context.getCacheDir();
			File file = new File(cacheDir,FILENAME);
			
			Button name = (Button) this.findViewById(R.id.cache_read);
			
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(txt.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				FileInputStream fis = new FileInputStream(file);
				int content;
				while((content = fis.read()) != -1){
					newVal += (char) content;
				}
				fis.close();
				name.setText(newVal);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	

}
