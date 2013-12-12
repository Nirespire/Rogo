package com.rogoapp;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.rogoapp.auth.RogoAuthenticatorActivity;

import android.content.Intent;
import android.os.Bundle;

public class UserSettingsActivity extends SherlockPreferenceActivity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.user_settings);
	}
	
	
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 1, 1, "Logout")
        .setOnMenuItemClickListener(this.LogoutClickListener)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }
    
    OnMenuItemClickListener LogoutClickListener = new OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem item){
            RogoAuthenticatorActivity.logout();
            return false;
        }
    };
	
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 0:
			startActivity(new Intent(this, ShowSettingsActivity.class));
			return true;
		}
		return false;
	}
	*/
}
