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

public class ProfileActivity extends Activity {
    
    TextView username;
    TextView status;
    TextView interests;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		username = (TextView)findViewById(R.id.username_value);
        status = (TextView)findViewById(R.id.status_value);
        interests = (TextView)findViewById(R.id.interests_value);
		
		String targetID = (String) getIntent().getSerializableExtra("user");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("person_id", targetID));
		
		JSONObject userInfo = ServerClient.genericPostRequest("userdata", nameValuePairs, this.getApplicationContext());
		
		username.setText(targetID);
		
		try{
            JSONObject jArray = userInfo.getJSONObject("data");
            JSONArray user = jArray.getJSONArray("user");
            //interests.setText(user.getJSONObject(0).getString("interests"));
            username.setText(user.getJSONObject(0).getString("username"));
            status.setText(user.getJSONObject(0).getString("status"));
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	public void sendRequest(View v){
	    final Context context = this;
        Intent intent = new Intent(context, SendRequestActivity.class);
	    intent.putExtra("user",username.getText().toString());
        startActivity(intent);
	}

}
