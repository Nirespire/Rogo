
package com.rogoapp;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.MenuItem;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rogoapp.auth.AccountAuthenticator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

/*
 * Now That I have the location listener up and running, the next thing needed is to make the location listener update
 * the user's current location as he/she walks around.  This means updating their availability and
 * pulling of nearby users periodically, as often as I check to see if their location has changed.
 * 
 * Just make the code that updates availability and pulls nearby users its own method called as
 * often as the location listener updates location.
 */


public class NearYouMapActivity extends FragmentActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener  {


	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final float DEFAULTZOOM = 17;
	@SuppressWarnings("unused")
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
	GoogleMap mMap;
	LocationClient mLocationClient;
	
	double userLat;
	double userLong;
	String label;
	String latString;
	double lat;
	String lonString;
	double lon;
	String distanceString;
	double distance;
	String updated;
	String recentness;
	
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
				// code for the current location
				mLocationClient = new LocationClient(this, this, this);
				mLocationClient.connect();				
			}
			else {
				Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			setContentView(R.layout.activity_main);
			Toast.makeText(this, "uhhh...", Toast.LENGTH_SHORT).show();
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.goToCurrentLocation:
			goToCurrentLocation();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
	
/*	private void goToLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.animateCamera(update);		
	} */
	
	// This method is called to make the map move (animated) to the actual current location of the user

	protected void goToCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location is not available", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "Current location is available", Toast.LENGTH_SHORT).show();
			userLat = currentLocation.getLatitude();
			userLong = currentLocation.getLongitude();
			LatLng ll = new LatLng(userLat, userLong);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
			mMap.animateCamera(update);
			updateAvailability(userLat, userLong);
		}
	}
	
	protected void updateAvailability(double lat, double lng) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("location_lat", String.valueOf(lat)));
		nameValuePairs.add(new BasicNameValuePair("location_lon", String.valueOf(lng)));
		nameValuePairs.add(new BasicNameValuePair("availability", "available"));
		nameValuePairs.add(new BasicNameValuePair("radius", "1"));
		
		JSONObject jObj = ServerClient.genericPostRequest("availability", nameValuePairs, this.getApplicationContext());
		
		try{
			String status = jObj.getString("status");
			if(status.equals("success")){						/******************** WHY CAN'T I GET SUCCESS???? ********************/
				System.out.println("updated succesfully");
				Toast.makeText(this, "Availability updated successfully", Toast.LENGTH_SHORT).show();
			//	putUsersOnMap(nameValuePairs);
			}
			else{
				System.out.println("not updated!");
			}
		}catch(JSONException e){
			System.err.println("IN MAP: " + e);
		}catch(NullPointerException e){
			System.err.println("IN MAP: " + e);
		}
	}
	
	protected void putUsersOnMap(List<NameValuePair> nameValuePairs) {
		//now that this user's availability is updated, we must get nearby users
		nameValuePairs = new ArrayList<NameValuePair>(2);
		JSONObject jObj = ServerClient.genericPostRequest("nearby", nameValuePairs, this.getApplicationContext());
		//sort jObj into list of users
		List<User> otherUsers = new ArrayList<User>();
		
		JSONArray others = null;
			try {
			others = jObj.getJSONArray("data");
			for(int i = 0; i < others.length(); i++){
				JSONObject oneUser = others.getJSONObject(i);
				
				label = oneUser.getString("location_label");
				System.out.println(label);
	
				latString = oneUser.getString("location_latitude");
				lat = Double.parseDouble(latString);
				System.out.println(lat);
				
				lonString = oneUser.getString("location_longitude");
				lon = Double.parseDouble(lonString);
				System.out.println(lon);
				
				distanceString = oneUser.getString("distance");
				distance = Double.parseDouble(distanceString);
				System.out.println(distance);
		
				updated = oneUser.getString("updated");
				System.out.println(updated);
				
				recentness = oneUser.getString("recentness");
				System.out.println(recentness);
				
				User currUser = new User(lat, lon, label, distance, updated, recentness);
				otherUsers.add(currUser);
			
				/* now, otherUsers should be full of all nearby users
				 * put these users on the map!
				 */
				
				for (int k = 0; k < otherUsers.size(); k++) {
					Marker marker = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(otherUsers.get(k).getLat(), otherUsers.get(k).getLon()))
					.title(otherUsers.get(k).getLabel())
						);
				}	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	/*	LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(30000);	// should be 60000
		request.setFastestInterval(10000);  // should be 10000
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
	//	updateAvailability(location);
	}
}

