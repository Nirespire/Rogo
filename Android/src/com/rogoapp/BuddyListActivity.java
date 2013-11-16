package com.rogoapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class BuddyListActivity extends Activity {
	
	private ListView lv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_list);

        lv = (ListView) findViewById(R.id.buddylist);
        // Instantiating an array list (you don't need to do this, you already have yours)
   //     ArrayList<Friend> recentlyMet = User.getFriends();
        // This is the array adapter, it takes the context of the activity as a first // parameter, 0the type of list view as a second parameter and your array as a third parameter
   //     ArrayAdapter<Friend> arrayAdapter =      
   //     new ArrayAdapter<Friend>(this,android.R.layout.simple_list_item_1, recentlyMet);
    //    lv.setAdapter(arrayAdapter); 
    }  
}
