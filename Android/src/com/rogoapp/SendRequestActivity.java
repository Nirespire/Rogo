package com.rogoapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SendRequestActivity extends Activity {

	Button sendRequestButton;
	String userID;
	String trait;
	String location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_request, menu);
		return true;
	}

	
	
	public void addListenerOnButton1() {

		sendRequestButton = (Button) findViewById(R.id.send_meet_request_button);

		sendRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				//TODO NEED TO UPDATE FOR MEETUP REQUEST
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user_id1", ""));
				nameValuePairs.add(new BasicNameValuePair("user1_trait", ""));
				nameValuePairs.add(new BasicNameValuePair("location", ""));
				
				
				ServerClient sc = new ServerClient();
				JSONObject jObj = sc.genericPostRequest("", nameValuePairs);
				String uid = null;
				String status = null;
				try{
					//uid = sc.getLastResponse().getString("uid");
					status = jObj.getString("status");
				}catch(JSONException e){
					System.err.print(e);
				}
				System.out.println("status = " + status + ", uid = " + uid);
			}

		});

	}

}
