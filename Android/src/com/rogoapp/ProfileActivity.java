package com.rogoapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ProfileActivity extends SherlockActivity{
    
    TextView username;
    TextView status;
    TextView interests;
    
    
    String uid;
    String usrnm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		username = (TextView)findViewById(R.id.username_value);
        status = (TextView)findViewById(R.id.status_value);
        interests = (TextView)findViewById(R.id.interests_value);
		
		String targetID = (String) getIntent().getSerializableExtra("user");
		this.uid = targetID;
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("person_id", targetID));
		
		JSONObject userInfo = ServerClient.genericPostRequest("userdata", nameValuePairs);
		
		username.setText(targetID);
		
		try{
            JSONObject jArray = userInfo.getJSONObject("data");
            JSONArray user = jArray.getJSONArray("user");
            //interests.setText(user.getJSONObject(0).getString("interests"));
            this.usrnm = user.getJSONObject(0).getString("username");
            username.setText(this.usrnm);
            status.setText(user.getJSONObject(0).getString("status"));
            
            this.getSupportActionBar().setTitle(usrnm);
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}*/
	
	public void sendRequest(View v){
	    final Context context = this;
	    if (ServerClient.isNetworkAvailable()) {
	        Intent intent = new Intent(context, SendRequestActivity.class);
		    intent.putExtra("user",this.uid);
		    intent.putExtra("username",this.usrnm);
	        startActivity(intent);
	    }
	}

}
