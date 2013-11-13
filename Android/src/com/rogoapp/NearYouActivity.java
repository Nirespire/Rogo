package com.rogoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class NearYouActivity extends SherlockActivity {

    Button goToMapButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_you);

        goToMapButton = (Button) findViewById(R.id.near_you_map);
        goToMapButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                startActivity(new Intent(NearYouActivity.this, NearYouMapActivity.class));

            }
        });


    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("User")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Settings")
        .setOnMenuItemClickListener(this.SettingsClickListener)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    public void openUserSettings(View v){
        final Context context = this;
        Intent intent = new Intent(context, UserSettingsActivity.class);
        startActivity(intent);
    }
    OnMenuItemClickListener SettingsClickListener = new OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem item){
            Intent intent = new Intent(NearYouActivity.this, UserSettingsActivity.class);
            startActivity(intent);
            return false;
        }
    };


    
}

