package com.rogoapp;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.rogoapp.auth.RegisterActivity;
import com.rogoapp.auth.RogoAuthenticatorActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
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
        	AccountManager am = AccountManager.get(SplashScreen.showContext());
    		Account account = am.getAccountsByType(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE)[0];

    		//clears the stored password and invalidates the current auth-token if one exists
    		String authToken = am.peekAuthToken(account, RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);

    		am.clearPassword(account);
    		am.invalidateAuthToken(account.type, authToken);
    		am.removeAccount(account, null, null);
    		
    		Intent intent = new Intent(SplashScreen.showContext(), RogoAuthenticatorActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
    		
    		return true;
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
