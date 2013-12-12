package com.rogoapp;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		username = (TextView)findViewById(R.id.username_value);
		email = (TextView)findViewById(R.id.email_value);
		points = (TextView)findViewById(R.id.points_value);
		interests = (TextView)findViewById(R.id.interests_value);
		
		String interest = sharedPrefs.getString("user_interests", "No Interests!");
		
		username.setText(sharedPrefs.getString("prefUsername","No Username Set!"));
		interests.setText(interest);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
}
