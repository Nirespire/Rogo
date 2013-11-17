package com.rogoapp;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
        
        nearbyUsersList = (ListView) findViewById(R.id.nearby_users_list);
        
        users = new ArrayList<String>();
        getNearbyUsers();
        
        if(!users.isEmpty()){
        	ArrayAdapter<String> arrayAdapter =      
        	         new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, users);
        	         nearbyUsersList.setAdapter(arrayAdapter); 
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("User")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
                out.append("User: " +  oneObject.getString("uid") + 
                		" Distance: " + oneObject.getString("distance"));
               
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            users.add(out.toString());
        }

    }



}

