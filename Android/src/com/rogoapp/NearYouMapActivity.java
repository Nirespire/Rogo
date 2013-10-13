package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NearYouMapActivity extends Activity {
	
	Button goToListButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_you_map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	public void addListenerOnButton1() {

        goToListButton = (Button) findViewById(R.id.near_you_list);

        goToListButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openNearYouScreen(arg0);
            }

        });

    }
	
	public void openNearYouScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, NearYouActivity.class);
        startActivity(intent);
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
