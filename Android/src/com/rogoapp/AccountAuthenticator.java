/*Follows tutorials found at
 * http://www.lukeslog.de/?page_id=1052 ---> much more implemenataion, less explanation
 * http://www.finalconcept.com.au/article/view/android-account-manager-step-by-step ---> Explains more
 */

package com.rogoapp;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

	final Context mContext;
	final static String ACCOUNT_TYPE = "com.rogoapp";
	public AccountAuthenticator(Context context) {
		super(context);
		mContext = context; //added for use when adding an account
		// TODO I don't see any reason to alter this
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		final Bundle result;
		final Intent intent; //Creates a new intent that will be bundled in the result
		
		//intent = new Intent(); //can't figure out the next few lines
		//Trying to put 
		intent = new Intent(this.mContext, RogoAuthenticatorActivity.class);
		//intent.putExtra(Constants.ACCOUNT_TYPE, authTokenType); //ACCOUNT_TYPE was AUTHTOKEN_TYPE
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		
		result = new Bundle();
		
		return result;
		// TODO Figure out intent? 
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
