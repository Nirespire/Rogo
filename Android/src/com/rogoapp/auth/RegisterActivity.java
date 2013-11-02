package com.rogoapp.auth;

import java.util.ArrayList;
//for ServerClient class
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rogoapp.R;
import com.rogoapp.ServerClient;

public class RegisterActivity extends AccountAuthenticatorActivity{
    
    Button registerButton;
    EditText lastName;
    EditText firstName;
    EditText email;
    EditText password;
    
    TextView loginLink;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        addListenerOnButton1();
        
        Button button = (Button) this.findViewById(R.id.link_to_login);
        button.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }
    
    public void addListenerOnButton1() {

        registerButton = (Button) findViewById(R.id.btnRegister);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO send authentication for registration
                //TODO add validation of entered strings
                
                EditText username = (EditText) findViewById(R.id.reg_username);
                EditText first = (EditText) findViewById(R.id.reg_firstname);
                EditText email = (EditText) findViewById(R.id.reg_email);
                EditText password = (EditText) findViewById(R.id.reg_password);
                
            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        		nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
        		nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
        		nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
        		
                ServerClient sc = new ServerClient();
                JSONObject jObj = sc.genericPostRequest("register", nameValuePairs);
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
    public void addListenerOnButton2() {

        registerButton = (Button) findViewById(R.id.link_to_login);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openLoginScreen(arg0);
            }

        });

    }
    
    public void openLoginScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, RogoAuthenticatorActivity.class);
        startActivity(intent);
    }
    
    
    
}
