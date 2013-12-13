
package com.rogoapp;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, OnInfoWindowClickListener  {

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private static final float DEFAULTZOOM = 17;
    @SuppressWarnings("unused")
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
    GoogleMap mMap;
    LocationClient mLocationClient;

    private double userLat;
    private double userLong;

    private List<User> otherUsers;
    private List<Marker> markers;

    @SuppressWarnings("unused")
    private static final String LOGTAG = "Maps";

    @SuppressWarnings("unused")
    private static final double GVILLE_LAT = 29.666576,
    GVILLE_LNG = -82.319977,
    GVILLE_HEIGHT = 42.000000;
    
    LocationManager loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markers = new ArrayList<Marker>();

        if (servicesOK()) {
            setContentView(R.layout.near_you_map);	

            if(initMap()) {
                Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
                //	goToLocation(GVILLE_LAT, GVILLE_LNG, DEFAULTZOOM);
                //	mMap.setMyLocationEnabled(true);
                // code for the current location
                mLocationClient = new LocationClient(this, this, this);
                mLocationClient.connect();
                //	goToCurrentLocation();

                /***************************************************/

                //first update avaliability
                //force location to be florida gym
                //userLat = 29.649674; //florida gym
                //userLong = -82.347224; //florida gym
                //WHY WOULD YOU DO THIS?!
                
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                
                loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            	Location locate = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            	System.out.println("THE CURRENT LOCATION IS....  "+locate);
            	
            	if(locate != null){
            		nameValuePairs.add(new BasicNameValuePair("location_lat",String.format("%s", locate.getLatitude())));
            		nameValuePairs.add(new BasicNameValuePair("location_lon",String.format("%s", locate.getLongitude())));
            		System.out.println(String.format("%s", locate.getLatitude()));
            		System.out.println(String.format("%s", locate.getLongitude()));
            	}
                else{
                	nameValuePairs.add(new BasicNameValuePair("location_lat","0.000000")); //Maybe I'm a bad person, but
                	nameValuePairs.add(new BasicNameValuePair("location_lon","0.000000")); //But the server requires a minimum of 5 decimal places
                	
                	//System.out.println("Location not available");
                }
            	
                //nameValuePairs.add(new BasicNameValuePair("availability", "available"));
                //nameValuePairs.add(new BasicNameValuePair("radius", "1"));
            	
            	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            	System.out.println(sharedPrefs.getString("radius","1"));
                String sharedRadius = sharedPrefs.getString("radius", "1");
                Boolean sharedBool = sharedPrefs.getBoolean("availability", false);
                String sharedAvail;
                if(sharedBool){
                	sharedAvail = "available";
                }
                else{
                	sharedAvail = "busy";
                }
                nameValuePairs.add(new BasicNameValuePair("availability",sharedAvail));
                nameValuePairs.add(new BasicNameValuePair("radius",sharedRadius));

                JSONObject jObj = ServerClient.genericPostRequest("availability", nameValuePairs, this.getApplicationContext());

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
                jObj = ServerClient.genericPostRequest("nearby", nameValuePairs, this.getApplicationContext());
                //sort jObj into list of users
                otherUsers = new ArrayList<User>();

                JSONArray others = null;
                try {
                    others = jObj.getJSONArray("data");
                    for(int i = 0; i < others.length(); i++){
                        JSONObject oneUser = others.getJSONObject(i);

                        String userIDString = oneUser.getString("uid");
                        int userID = Integer.parseInt(userIDString);
                        System.out.println(userID);
                        
                        String label = oneUser.getString("location_label");
                        System.out.println(label);

                        String latString = oneUser.getString("location_latitude");
                        double lat = Double.parseDouble(latString);
                        System.out.println(lat);

                        String lonString = oneUser.getString("location_longitude");
                        double lon = Double.parseDouble(lonString);
                        System.out.println(lon);

                        String distanceString = oneUser.getString("distance");
                        double distance = Double.parseDouble(distanceString);
                        System.out.println(distance);

                        String updated = oneUser.getString("updated");
                        System.out.println(updated);

                        String recentness = oneUser.getString("recentness");
                        System.out.println(recentness);

                        User currUser = new User(userID, lat, lon, label, distance, updated, recentness);
                        otherUsers.add(currUser);

                        /* now, otherUsers should be full of all nearby users
                         * put these users on the map!
                         */

                        for (int k = 0; k < otherUsers.size(); k++) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(otherUsers.get(k).getLat(), otherUsers.get(k).getLon()))
                            .title(otherUsers.get(k).getUID() + " " + otherUsers.get(k).getLabel()));
                            markers.add(marker);
                        }
                    
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }        
                /**************************************************/
            }
            else {
                Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "uhhh...", Toast.LENGTH_SHORT).show();
        }	
        
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            	String[] uidString = marker.getTitle().split(" ");
               Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
               intent.putExtra("user", uidString[0]);
               startActivity(intent);
            }
        });
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        System.out.println("MARKER CLICK");
        Intent intent = new Intent(getApplicationContext(), SendRequestActivity.class);

        intent.putExtra("user", marker.getTitle());
        startActivity(intent);
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

 /*   private void goToLocation(double lat, double lng, float zoom) {
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
            LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
            mMap.animateCamera(update);
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
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(30000);	// should be 60000
        request.setFastestInterval(10000);  // should be 10000
        mLocationClient.requestLocationUpdates(request, this);
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

