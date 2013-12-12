package com.rogoapp;

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
		
		username.setText(targetID);
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
