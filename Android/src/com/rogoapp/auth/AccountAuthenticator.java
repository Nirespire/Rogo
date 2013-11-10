/*Follows tutorials found at
 * http://www.lukeslog.de/?page_id=1052 ---> much more implemenataion, less explanation
 * http://www.finalconcept.com.au/article/view/android-account-manager-step-by-step ---> Explains more
 */

package com.rogoapp.auth;

import com.rogoapp.ServerClient;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

        final Context mContext;
        final static String ACCOUNT_TYPE = "com.rogoapp";
        private ServerClient server;
        public AccountAuthenticator(Context context) {
                super(context);
                mContext = context; //added for use when adding an account
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response,
                        String accountType, String authTokenType,
                        String[] requiredFeatures, Bundle options)
                        throws NetworkErrorException {
                final Bundle result;
                final Intent intent; //Creates a new intent that point to the login page
                
                //adds authTokenType and response information to the intent
                intent = new Intent(this.mContext, RogoAuthenticatorActivity.class);
                intent.putExtra(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                
                //creates a bundle with the created intent in it and returns the result of that intent
                result = new Bundle();
                result.putParcelable(AccountManager.KEY_INTENT, intent);
                
                return result;
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
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
         
        	//check the token type to make sure it matches the expected type
        	if(!authTokenType.equals(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE)){
        		//if it doesn't match, return an error message
        		final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ERROR_MESSAGE,
                    "invalid authTokenType");
                return result;
        	}
        	
            // Extract the username and password from the Account Manager, and ask
            // the server for an appropriate AuthToken.
            final AccountManager am = AccountManager.get(mContext);
         
            String authToken = am.peekAuthToken(account, authTokenType);
         
            // Lets give another try to authenticate the user
            if (TextUtils.isEmpty(authToken)) {
                final String password = am.getPassword(account);
                if (password != null) {
                    //TODO authToken = server.userSignIn(account.name, password, authTokenType);
                }
            }
         
            // If we get an authToken - we return it
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }
         
            // If we get here, then we couldn't access the user's password - so we
            // need to re-prompt them for their credentials. We do that by creating
            // an intent to display our AuthenticatorActivity.
            final Intent intent = new Intent(mContext, RogoAuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            //intent.putExtra(RogoAuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
            intent.putExtra(RogoAuthenticatorActivity.PARAM_USERNAME, account.name); //TODO
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
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