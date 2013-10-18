package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DebugActivity extends Activity {
    
    Button serverButton;
    Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug, menu);
		return true;
	}
	
	 public void addListenerOnButton1() {

	        registerButton = (Button) findViewById(R.id.registration_debug_button);

	        registerButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	                openRegistrationScreen(arg0);
	            }

	        });

	 }
	 
	 public void openRegistrationScreen(View v){
	        final Context context = this;
	        Intent intent = new Intent(context, RegisterActivity.class);
	        startActivity(intent);
	 }
	
	

}
