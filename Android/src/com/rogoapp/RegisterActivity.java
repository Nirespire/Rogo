package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity{
    
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
                // Open main screen for user
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
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }
    
}
