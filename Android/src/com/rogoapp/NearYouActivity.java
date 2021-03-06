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
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;


public class NearYouActivity extends SherlockListActivity {

    static final String NEARBY_FILE = "nearby";

    Button goToMapButton;
    ListView nearbyUsersList;
    LocationManager loc;

    ArrayList<NearbyUser> users;
    
    NearbyUserAdapter listAdapter;

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

        JSONObject jObj = ServerClient.genericPostRequest("availability", nameValuePairs);

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
        
        
        nearbyUsersList = this.getListView();

        users = new ArrayList<NearbyUser>();
        getNearbyUsers();

        if(!users.isEmpty()){
            /*ArrayAdapter<String> arrayAdapter =      
                    new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, users);
            nearbyUsersList.setAdapter(arrayAdapter);
            */
        	this.listAdapter = new NearbyUserAdapter(this,R.layout.nearby_user_item,users);
        	this.setListAdapter(this.listAdapter);
        }


        nearbyUsersList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                NearbyUser user = (NearbyUser) parent.getItemAtPosition(position);

                System.out.printf("(%s) %s",user.uid,user.username);

                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);

                intent.putExtra("user", Integer.toString(user.uid));
                
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
            JSONObject json = ServerClient.genericPostRequest("nearby", Collections.<NameValuePair>emptyList());
            if(json != null)
                parseJ(json, NEARBY_FILE);
            return json != null;
        }
        return false;
    }

    public void parseJ(JSONObject jObject, String filename){
        JSONArray jArray = new JSONArray();
        try {
            jArray = jObject.getJSONArray("data");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        for (int i=0; i < jArray.length(); i++)
        {
        	NearbyUser n = new NearbyUser();
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                
                n.username = oneObject.getString("username");
                n.uid = oneObject.getInt("uid");
                n.distance = oneObject.getDouble("distance");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            users.add(n);
        }

    }
    
    private class NearbyUser{
    	public String username;
    	public int uid;
    	public double distance;
    	
    	public String getDistance(){
    		return String.format("%6s",this.distance);
    	}
    }

    private class NearbyUserAdapter extends ArrayAdapter<NearbyUser> {

        private ArrayList<NearbyUser> users;

        public NearbyUserAdapter(Context context, int textViewResourceId, ArrayList<NearbyUser> users) {
                super(context, textViewResourceId, users);
                this.users = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.nearby_user_item, null);
                }
                NearbyUser u = users.get(position);
                if (u != null) {
                        TextView tt = (TextView) v.findViewById(R.id.nearby_user_username);
                        TextView bt = (TextView) v.findViewById(R.id.nearby_user_distance);
                        if (tt != null) {
                              tt.setText(u.username);                            
                        }
                        if(bt != null){
                              bt.setText(u.getDistance());
                        }
                }
                return v;
        }
}

}

