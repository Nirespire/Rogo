package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TitleBarActivity extends Activity{
    
    Button userButton;
    Button settingsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.customtitlebar);
    }
    
    
    public void addListenerOnButton1() {

        settingsButton = (Button) findViewById(R.id.titlebarSettingsButton);

        settingsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                 openSettingsScreen(arg0);
            }

        });

    }
    
    public void addListenerOnButton2() {

        userButton = (Button) findViewById(R.id.titlebarUserButton);

        userButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                 openUserScreen(arg0);
            }

        });

    }
    
	
	public void openSettingsScreen(View v){
		final Context context = this;
		Intent intent = new Intent(context, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void openUserScreen(View v){
	    final Context context = this;
        Intent intent = new Intent(context, UserActivity.class);
        startActivity(intent);
	}
}
