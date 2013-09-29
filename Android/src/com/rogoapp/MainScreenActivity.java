package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainScreenActivity extends Activity {

	Button nearYouButton;
	Button meetRandomButton;
	Button tipsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_screen);

		// creates view of custom title bar -> now included in main_screen.xml

		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}


	public void addListenerOnButton() {

		nearYouButton = (Button) findViewById(R.id.near_you_button);

		nearYouButton.setOnClickListener(new OnClickListener() {

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

}		
