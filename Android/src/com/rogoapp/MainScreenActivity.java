package com.rogoapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainScreenActivity extends SherlockActivity {

		String TIPS_FILE = "tips";
	
    Button nearYouButton;
    Button meetRandomButton;
    Button tipsButton;
    Button userButton;
    
    Button debugButton; //TODO REMOVE
    
    List<String> tips;
    List<String> meetRandom;

    CacheClient cache = new CacheClient(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        
        //Taylor ***
        showUserSettings();
        
      //Adding some functionality to tips button
        textListener(this.findViewById(R.id.tips_edit_box));
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("User")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Settings")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;
    }


    public void addListenerOnButton1() {

        nearYouButton = (Button) findViewById(R.id.near_you_button);

        nearYouButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openNearYouScreen(arg0);
            }

        });

    }

    public void addListenerOnButton2() {

        nearYouButton = (Button) findViewById(R.id.meet_random_button);

        nearYouButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshMeetRandomButton(arg0);
            }

        });

    }

    public void addListenerOnButton3() {

        nearYouButton = (Button) findViewById(R.id.tips_button);

        nearYouButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshTipsButton(arg0);
            }

        });

    }

    // Navigates the user to the People Near You Screen
    public void openNearYouScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, NearYouActivity.class);
        startActivity(intent);
    }

    //refresh the text 
    public void refreshMeetRandomButton(View arg0){
        final Button button = (Button)findViewById(R.id.meet_random_button);
        
        if(meetRandom == null || meetRandom.isEmpty()){
        	System.err.println("DEBUG: Reloading meetRandom array");
        	reloadMeetRandomArray();
        }
        
        Random rand = new Random(System.currentTimeMillis());
        int random = rand.nextInt(meetRandom.size());
        String out = meetRandom.remove(random);
        button.setText(out);
    }

    public void refreshTipsButton(View arg0){
        final Button button = (Button)findViewById(R.id.tips_button);
        final EditText tipsText = (EditText) findViewById(R.id.tips_edit_box);
        
        // replace with random string from tips.xml
        if(tips == null || tips.isEmpty()){
        	System.err.println("DEBUG: Reloading tips array");
            this.reloadTipsArray();
        }
        
        String text = button.getText().toString();
        if(text.equals("Add Tip!")){
        	String what = tipsText.getText().toString();
        	tips.add(what);
        	tipsText.setText("");
        	button.setText(R.string.tips);
        }        
        else{
        	Random rand = new Random(System.currentTimeMillis());
        	int random = rand.nextInt(tips.size());
        	String out = tips.remove(random); // Remember that .remove also returns the removed element
        	button.setText(out);
        }
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
    
    public void reloadTipsArray(){
      if(tips == null){
      	tips = new ArrayList<String>();
      	
      	try {
					cache.saveFile(TIPS_FILE, "Here's a tip!\nHere's another tip!");
				} catch (Exception e){
					
				}
      }
      try {
				String[] _tips = cache.loadFile(TIPS_FILE).split("\n");
				Collections.addAll(tips, _tips);
			} catch (Exception e) {
				tips.add("Tips not available");
			}
  }
    
    public void reloadMeetRandomArray(){
        Resources res = getResources();
        if(meetRandom == null){
        	meetRandom = new ArrayList<String>();
        }
        String[] _randoms = res.getStringArray(R.array.meetRandomArray);
        Collections.addAll(meetRandom, _randoms);
    }
    /*Taylor's Settings section - Let's hope I don't break everything!
     * Too late.*/
    //-------------------------------------------------------------------
    
    private static final int RESULT_SETTINGS = 1;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	
    	case R.id.menu_settings:
    		Intent i = new Intent(this, UserSettingsActivity.class);
    		startActivityForResult(i, RESULT_SETTINGS);
    		break;
    	}
    	return true;
    }
    public void onTextEnter(View V){
    	Button tips = (Button) this.findViewById(R.id.tips_button);
    	
    	tips.setText("Add tip!");
    }
    
    public void textListener(View v){
        
    	//Adds action event for when data is entered in an EditText
        //This is currently being used for the tips field
    	
    	final EditText myTextBox  = (EditText) v;
		final Button tips = (Button) findViewById(R.id.tips_button);
    	myTextBox.addTextChangedListener(new TextWatcher(){
    		
    		
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count){
				String check = "" + myTextBox.getText();
				if(check == ""){// && !tips.isPressed()){
    				Button tips = (Button) findViewById(R.id.tips_button);
    				tips.setText("Tips");
    			}	
    		}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				//When text box is entered, the tips button becomes an Add tip! button
				
				tips.setText("Add Tip!");
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
    	});
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode){
    	case RESULT_SETTINGS:
    		showUserSettings();
    		break;
    	}
    }
    
    private void showUserSettings(){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	StringBuilder builder = new StringBuilder();
    	builder.append("\n Username: "+sharedPrefs.getString("prefUsername", "NULL"));
    	builder.append("\n Send Report: "+sharedPrefs.getBoolean("prefSendReport", false));
    	builder.append("\n Radius: "+sharedPrefs.getString("prefRadius", "NULL"));
    	TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);
    	settingsTextView.setText(builder.toString());
    }
    
    /* DEBUG SECTION REMOVE BEFORE FINAL*/
    //-------------------------------------------------------------------
    public void addListenerOnButton4() {

        debugButton = (Button) findViewById(R.id.debug_button);

        debugButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openDebugScreen(arg0);
            }

        });

    }
    
    public void openDebugScreen(View v){
        final Context context = this;
        Intent intent = new Intent(context, DebugActivity.class);
        startActivity(intent);
    }
    
    // :3
    //-------------------------------------------------------------------
    
}		
