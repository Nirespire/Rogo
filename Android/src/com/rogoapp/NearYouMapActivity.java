
package com.rogoapp;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rogoapp.auth.AccountAuthenticator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;



public class NearYouMapActivity extends FragmentActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener  {


	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final float DEFAULTZOOM = 17;
	@SuppressWarnings("unused")
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
	GoogleMap mMap;
	LocationClient mLocationClient;
	
	private double userLat;
	private double userLong;
	
	private List<User> otherUsers;
	
	@SuppressWarnings("unused")
	private static final String LOGTAG = "Maps";
	
	@SuppressWarnings("unused")
	private static final double GVILLE_LAT = 29.666576,
		GVILLE_LNG = -82.319977;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (servicesOK()) {
			setContentView(R.layout.near_you_map);	
	
			if(initMap()) {
				Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
				goToLocation(GVILLE_LAT, GVILLE_LNG, DEFAULTZOOM);
				mMap.setMyLocationEnabled(true);
				// code for the current location
				mLocationClient = new LocationClient(this, this, this);
				mLocationClient.connect();
			//	goToCurrentLocation();
				
				// post request, have it be marked on map, other users
				// getString and name of data field
				// here are some sample locations of "users"
				// create a serverclient objecy here
				// and create generic 
				// look at the register button
				// use nearby.php
				// Joey and Jamie could help if necessary
				
				/***************************************************/
				// Mess around with this and tailor it
				
				//first update avaliability
				//force location to be florida gym
				userLat = 29.649674; //florida gym
				userLong = -82.347224; //florida gym
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("location_lat", String.valueOf(userLat)));
				nameValuePairs.add(new BasicNameValuePair("location_lon", String.valueOf(userLong)));
				nameValuePairs.add(new BasicNameValuePair("availability", "available"));
				nameValuePairs.add(new BasicNameValuePair("radius", ".0946"));
				
				ServerClient sc = new ServerClient();
				JSONObject jObj = sc.genericPostRequest("availability", nameValuePairs);
				
				try{
					String status = jObj.getString("status");
					if(status.equals("success")){
						System.out.println("updated succesfully");
					}
					else{
						System.out.println("not updated!");
					}
				}catch(JSONException e){
					System.err.println("IN MAP: " + e);
				}catch(NullPointerException e){
					System.err.println("IN MAP: " + e);
				}
				
				//now that this user's availability is updated, we must get nearby users
				nameValuePairs = new ArrayList<NameValuePair>(2);
				sc = new ServerClient();
				jObj = sc.genericPostRequest("nearby", nameValuePairs);
				//sort jObj into list of users
				otherUsers = new ArrayList<User>();
				JSONArray others = null;
				try {
					others = jObj.getJSONArray("data");
					for(int i = 0; i < others.length(); i++){
						//User currUser
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
		        

		        /**************************************************/
			}
		
		
			/* So far, I am connected to location services, so how do I use that service to make
			 * this map go to the user's current location upon start up?
			 */
			
			else {
				Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
			}
		
			
		}
		else {
			setContentView(R.layout.activity_main);
			Toast.makeText(this, "uhhh...", Toast.LENGTH_SHORT).show();
		}
		
	}

	// Now that we have the actionbar, I doubt this is needed.  I will erase this when its confirmed.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		}
		else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		}
		else {
			Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	
	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mMap = mMapFrag.getMap();
		}
		return (mMap != null);
	}
	
	private void goToLocation(double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
		mMap.animateCamera(update);
		
	}
	
	private void goToLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.animateCamera(update);
//mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
		
	}
	
	// This method is called to make the map move (animated) to the actual current location of the user
	
		protected void goToCurrentLocation() {
			Location currentLocation = mLocationClient.getLastLocation();
			if (currentLocation == null) {
				Toast.makeText(this, "Current location is not available", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(this, "Current location is available", Toast.LENGTH_SHORT).show();
			/*	LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
				CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
				mMap.animateCamera(update); */
			}
		} 
	
	/* The next 3 methods are to establish implementation of CurrentLocation
	 * which comes from GooglePlayServicesClient (2 implemented classes)
	 */
	

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
/*		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.setFastestInterval(1000);
		mLocationClient.requestLocationUpdates(request, this); */
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	// This method is for LocationListener
	@Override
	public void onLocationChanged(Location location) {
		String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
}

