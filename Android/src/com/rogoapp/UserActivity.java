package com.rogoapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class UserActivity extends Activity {
    
    TextView username;
    TextView email;
    TextView points;
    TextView interests;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iu_profile);
		
		username = (TextView)findViewById(R.id.username_value);
		email = (TextView)findViewById(R.id.email_value);
		points = (TextView)findViewById(R.id.points_value);
		interests = (TextView)findViewById(R.id.interests_value);
		
		//TODO REQUEST FOR USER INFO TO POPULATE TEXTVIEWS
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
}
