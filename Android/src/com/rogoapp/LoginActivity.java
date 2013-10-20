package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	EditText email;
	EditText password;
	Button loginButton;
	Button cancelButton;
	TextView registerButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
	
	public void onClick(View v) {
        // Switch to main activity
        Intent i = new Intent(getApplicationContext(),MainScreenActivity.class);
        startActivity(i);
    }
	
	public void addListenerOnButton1() {

        registerButton = (Button) findViewById(R.id.link_to_register);

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