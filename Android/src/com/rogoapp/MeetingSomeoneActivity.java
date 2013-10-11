package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MeetingSomeoneActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_someone);
	}

	public void openSettingsScreen(View v){
		final Context context = this;
		Intent intent = new Intent(context, SettingsActivity.class);
		startActivity(intent);
	}
}
