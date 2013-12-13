package com.rogoapp;

import java.util.Collections;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.TextView;

public class UserActivity extends Activity {

    TextView username;
    TextView email;
    TextView status;
    TextView interests;
    TextView points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iu_profile);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        username = (TextView)findViewById(R.id.username_value);
        email = (TextView)findViewById(R.id.email_value);
        status = (TextView)findViewById(R.id.status_value);
        interests = (TextView)findViewById(R.id.interests_value);
        points = (TextView)findViewById(R.id.points_value);
        

        username.setText(sharedPrefs.getString("prefUsername","No Username Set!"));
        interests.setText(sharedPrefs.getString("user_interests", "No Interests!"));
        
       
        JSONObject userInfo = ServerClient.genericPostRequest("status", Collections.<NameValuePair>emptyList());        
        
        try{
            JSONObject jArray = userInfo.getJSONObject("data");
            JSONArray user = jArray.getJSONArray("user");
            email.setText(user.getJSONObject(0).getString("email"));
            username.setText(user.getJSONObject(0).getString("username"));
            status.setText(user.getJSONObject(0).getString("status"));
            
            int pointsVal = user.getJSONObject(0).getInt("points");
            String pointsStr = (pointsVal != 1)?"Points":"Point";
            String pointsLabel = String.format("%d %s",pointsVal,pointsStr);
            
            points.setText(pointsLabel);
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }
}
