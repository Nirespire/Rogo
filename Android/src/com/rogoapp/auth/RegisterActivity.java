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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rogoapp.MainScreenActivity;
import com.rogoapp.R;
import com.rogoapp.ServerClient;

import java.security.MessageDigest;

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
        
        Button button = (Button) this.findViewById(R.id.link_to_login);
        button.setBackgroundColor(Color.TRANSPARENT);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }
    


    public void onRegister(View v) {
        EditText username = (EditText) findViewById(R.id.reg_username);
        EditText first = (EditText) findViewById(R.id.reg_firstname);
        EditText email = (EditText) findViewById(R.id.reg_email);
        EditText password = (EditText) findViewById(R.id.reg_password);
        
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
		
		String pass = password.getText().toString();
		
		System.out.println(AccountAuthenticator.hashPassword(pass));
		
		nameValuePairs.add(new BasicNameValuePair("password", AccountAuthenticator.hashPassword(pass)));
		
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
        
        final Context context = this;
        if(status.equals("success")){
            final Intent start = new Intent(context, MainScreenActivity.class);
            start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(start);
        }
        
    }
    
    public void openLoginScreen(View v){
        final Context context = this;
        this.finish();
        Intent intent = new Intent(context, RogoAuthenticatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("reset", true);
        startActivity(intent);
    }
    
    
    
}
