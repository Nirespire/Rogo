package com.rogoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BuddyListActivity extends Activity {
	Button addFriend;
	
	public void openBuddyListScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, BuddyListActivity.class);
        startActivity(intent);
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_list);

        addFriend = (Button) findViewById(R.id.add_friend);

        addFriend.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View arg0) {
                    
                }

        	});
    }
}
