package com.rogoapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;


public class NearYouActivity extends SherlockActivity {

    static final String NEARBY_FILE = "nearby";

    Button goToMapButton;
    ListView nearbyUsersList;
    LocationManager loc;

    ArrayList<String> users;

    CacheClient cache = new CacheClient(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_you);

        goToMapButton = (Button) findViewById(R.id.near_you_map);
        goToMapButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                startActivity(new Intent(NearYouActivity.this, NearYouMapActivity.class));

            }
        });

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
        }
    	
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
        
        
        nearbyUsersList = (ListView) findViewById(R.id.nearby_users_list);

        users = new ArrayList<String>();
        getNearbyUsers();

        if(!users.isEmpty()){
            ArrayAdapter<String> arrayAdapter =      
                    new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, users);
            nearbyUsersList.setAdapter(arrayAdapter);
        }


        nearbyUsersList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                String user = (String) parent.getItemAtPosition(position);

                int i = user.indexOf("'");
                int j = user.lastIndexOf("'");

                user = user.substring(i + 1 , j);

                System.out.println(user);

                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);

                intent.putExtra("user", user);
                startActivity(intent);
            }


        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("User")
        .setOnMenuItemClickListener(this.UserClickListener)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Settings")
        .setOnMenuItemClickListener(this.SettingsClickListener)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    public void openUserSettings(View v){
        final Context context = this;
        Intent intent = new Intent(context, UserSettingsActivity.class);
        startActivity(intent);
    }
    OnMenuItemClickListener SettingsClickListener = new OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem item){
            Intent intent = new Intent(NearYouActivity.this, UserSettingsActivity.class);
            startActivity(intent);
            return false;
        }
    };
    OnMenuItemClickListener UserClickListener = new OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem item){
            Intent intent = new Intent(NearYouActivity.this, UserActivity.class);
            startActivity(intent);
            return false;
        }
    };


    public boolean getNearbyUsers() {
        if(ServerClient.isNetworkAvailable()){
            JSONObject json = ServerClient.genericPostRequest("nearby", Collections.<NameValuePair>emptyList(), this.getApplicationContext());
            if(json != null)
                parseJ(json, NEARBY_FILE);
            return json != null;
        }
        return false;
    }

    public void parseJ(JSONObject jObject, String filename){
        JSONArray jArray = new JSONArray();
        StringBuffer out;
        try {
            jArray = jObject.getJSONArray("data");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        for (int i=0; i < jArray.length(); i++)
        {
            out = new StringBuffer();
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                out.append("User: " +  "'" + oneObject.getString("uid") + "'" + 
                        " Distance: " + oneObject.getString("distance"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            users.add(out.toString());
        }

    }



}

