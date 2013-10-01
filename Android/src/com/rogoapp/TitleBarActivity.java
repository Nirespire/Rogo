package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class TitleBarActivity extends Activity{
	
	public void openSettingsScreen(View v){
		final Context context = this;
		Intent intent = new Intent(context, SettingsActivity.class);
		startActivity(intent);
	}
}
