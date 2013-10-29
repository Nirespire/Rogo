package com.rogoapp;

import com.rogoapp.auth.RegisterActivity;

import android.app.Activity;
import android.accounts.AccountAuthenticatorActivity;
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
	
	public static final String PARAM_AUTHTOKEN_TYPE = "auth.token";  
    public static final String PARAM_CREATE = "create";  
  
    public static final int REQ_CODE_CREATE = 1;  
    public static final int REQ_CODE_UPDATE = 2;  
  
    public static final String EXTRA_REQUEST_CODE = "req.code";  
  
    public static final int RESP_CODE_SUCCESS = 0;  
    public static final int RESP_CODE_ERROR = 1;  
    public static final int RESP_CODE_CANCEL = 2;  
  
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
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
	 
	 public void onCancelClick(View V){
		 System.exit(0);
	 }
	 
	 public void addListenerOnButtonCancel(){
		 cancelButton = (Button) findViewById(R.id.on_Cancel_Click);
		 cancelButton.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View arg0){
				 openRegistrationScreen(arg0);
			 }
		 });
	 }
	
}