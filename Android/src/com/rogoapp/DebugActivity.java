package com.rogoapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.rogoapp.auth.RegisterActivity;
import com.rogoapp.auth.RogoAuthenticatorActivity;

public class DebugActivity extends Activity implements LocationListener {

    Button serverButton;
    Button registerButton;
    Button loginButton;
    Button meetingSomeoneButton;
    Button buddyList;
    private LocationManager loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_screen);

        loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    public void addListenerOnSendRequestButton(){
        registerButton = (Button) findViewById(R.id.send_meet_request_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0){
                openSendRequestScreen(arg0);
            }
        });
    }

    public void addListenerOnButton4() {

        registerButton = (Button) findViewById(R.id.location_debug_button);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String loc = getLocation(arg0);
                postLocation(loc);
            }

        });

    }

    public void addListenerOnBuddyListButton() {

        registerButton = (Button) findViewById(R.id.recently_met);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openRecentlyMetScreen(arg0);
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

    
	 public void openRecentlyMetScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, BuddyListActivity.class);
	        startActivity(intent);
	}
     

    public void openMeetingSomeoneScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, MeetingSomeoneActivity.class);
		intent.putExtra("id", "tempid");
		intent.putExtra("name", "tempname");
		context.startActivity(intent);
    }

    public void openSendRequestScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, SendRequestActivity.class);
        startActivity(intent);
    }
    
    public void openUserScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, UserActivity.class);
        startActivity(intent);
    }

    public String getLocation(View v){

        List<Address> user = null;
        double lat;
        double lng;
        Geocoder geocoder;
        String out = "";
        String provider = "";
        
        Location location = null;



        if ( !loc.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,this);
            provider = "Network";
            location = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else{
            loc.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            provider = "GPS";
            location = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


        if (location == null){
            Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
            out = "Location Not found";
        }else{
            geocoder = new Geocoder(this);
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                lat=(double)user.get(0).getLatitude();
                lng=(double)user.get(0).getLongitude();
                Toast.makeText(this,provider + " lat: " +lat+",  longitude: "+lng, Toast.LENGTH_LONG).show();
                System.out.println(provider + " lat: " +lat+",  longitude: "+lng);
                out = lat+ "," + lng;

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        return out;
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void postLocation(String location){
        String[] latLon = location.split(",");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        
        if(latLon.length == 2){
        	
        	nameValuePairs.add(new BasicNameValuePair("location_lat",latLon[0]));
        	nameValuePairs.add(new BasicNameValuePair("location_lon",latLon[1]));
        	
        	System.out.println("Latitude: " + latLon[1]);
        	System.out.println("Longitude: " + latLon[2]);
        }
        else{
        	nameValuePairs.add(new BasicNameValuePair("location_lat","-1"));
        	nameValuePairs.add(new BasicNameValuePair("location_lon","-1"));
        	
        	System.out.println("Location not available");
        }
        
        //TODO NEED TO PULL USER INFO
        nameValuePairs.add(new BasicNameValuePair("availability","available"));
        nameValuePairs.add(new BasicNameValuePair("radius","1")); //1 mile
        
        ServerClient.genericPostRequest("availability", nameValuePairs);
        


    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    };
    
    public void checkRequests(View v){
        ServerClient sc = new ServerClient();
        //TODO need info for how to query requests
    }



}
