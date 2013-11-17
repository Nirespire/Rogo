package com.rogoapp.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.rogoapp.CacheClient;
import com.rogoapp.MainScreenActivity;
import com.rogoapp.R;
import com.rogoapp.ServerClient;
import com.rogoapp.auth.EmailValidator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RogoAuthenticatorActivity extends AccountAuthenticatorActivity {

	public static final String PARAM_AUTHTOKEN_TYPE = "com.rogoapp";  
	public static final String PARAM_CREATE = "create";
	public static final String PARAM_USERNAME = "username";
	public static final String OPEN_MAIN = "open main";

	public static final String PARAM_CONFIRMCREDENTIALS = "server confirmation"; 

	public static boolean createToken;
	public boolean openMain;
	
	private CheckBox rememberMe;

	private EditText tvUsername;  
	private EditText tvPassword;

	private TextView txtUsername;
	private TextView txtPassword;
	
	private Button register;
	private Button login;

	private String username;  
	private String password;
	
	private boolean remove;


	final Context context = this;
	private CacheClient cache;
	private static AccountManager am;

	@Override  
	protected void onCreate(Bundle icicle) {  

		// recreates from saved state -- icicle is the same as savedInstanceState  
		super.onCreate(icicle);  

		//creates a view from the login.xml file
		this.setContentView(R.layout.login);

		am = AccountManager.get(this);
		cache = new CacheClient(this);
		this.remove = false;
		
		//set variables and aesthetic changes
		rememberMe = (CheckBox)this.findViewById(R.id.remember_me_check);

		
		tvUsername = (EditText) this.findViewById(R.id.auth_txt_username);  
		tvPassword = (EditText) this.findViewById(R.id.auth_txt_pswd);

		txtUsername = (TextView) this.findViewById(R.id.txt_username);
		txtPassword = (TextView) this.findViewById(R.id.txt_pswd);

		username = tvUsername.getText().toString();  
		password = tvPassword.getText().toString();
		
		login = (Button) this.findViewById(R.id.btnLogin);
		register = (Button) this.findViewById(R.id.link_to_register);
		register.setBackgroundColor(Color.WHITE);
		if(createToken)
			login.setText("Remember Login");

		openMain = getIntent().getBooleanExtra(OPEN_MAIN, false);
				Account[] accounts = am.getAccountsByType(PARAM_AUTHTOKEN_TYPE);
				if(accounts.length != 0){
					String username = accounts[0].name;
					tvUsername.setText(username);
					tvUsername.setFocusable(false);
					register.setText("Not " + username + "?");
					
		//			sString pass = am.getPassword(accounts[0]);
		//			String token = am.peekAuthToken(accounts[0], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
		//			if(token == null || token == "")
		//				am.setAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE, "Hello token!");
				}
		//		if(accounts.length >= 1){
		//			String pass = am.getPassword(accounts[1]);
		//			String token = am.peekAuthToken(accounts[1], RogoAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE);
		//			if(token == null || token == "")
		//				am.setAuthToken(accounts[1], PARAM_AUTHTOKEN_TYPE, "Hello token!");
		//		}
	}


	@Override
	protected void onNewIntent(Intent intent){

		//when this intent is returned to, any data entered into it previously is reset
		super.onNewIntent(intent);
		if(intent.getBooleanExtra("reset", false)){
			tvUsername.setText("");
			tvPassword.setText("");
			if(createToken){
				rememberMe.performClick();
			}
		}
	}

	public void setVar(){

		//sets all the local variables for the login features
		tvUsername = (EditText) this.findViewById(R.id.auth_txt_username);  
		tvPassword = (EditText) this.findViewById(R.id.auth_txt_pswd);

		txtUsername = (TextView) this.findViewById(R.id.txt_username);
		txtPassword = (TextView) this.findViewById(R.id.txt_pswd); 
	}

	private boolean getToken(){
		return createToken;
	}
	private void setToken(boolean Token){
		createToken = Token;
	}

	public void onCancelClick(View v) {  

		this.finish();  

	}  

	public void openRegisterScreen(View v){
		
		String check = (String) register.getText();
		
		if(check.equals("Register")){
			//opens the registration form
			Intent intent = new Intent(context, RegisterActivity.class);
			intent.putExtra("remove", remove);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}
		else{
			tvUsername.setText("");
			tvUsername.setFocusableInTouchMode(true);
			register.setText(R.string.register);
			this.remove = true;
		}
			
	}
	public void onRememberMe(View V){

		//When the user has chosen to be remembered, sets the Login to store an auth-token
		//and changes the Login button appropriately to reflect their choice
		if(getToken()){
			this.setToken(false);
		}
		else this.setToken(true);

		if(getToken()){
			login.setText("Remember Login");
		}
		else login.setText("Login"); 
	}

	public static void logout(){

		Account account = am.getAccountsByType(PARAM_AUTHTOKEN_TYPE)[0];
		
		//clears the stored password and invalidates the current auth-token if one exists
		String authToken = am.peekAuthToken(account, PARAM_AUTHTOKEN_TYPE);

		am.clearPassword(account);
		am.invalidateAuthToken(account.type, authToken);
		am.removeAccount(account, null, null);
	}

	public void onSaveClick(View v) {  

		//checks for any errors within the form
		//if errors are found, the login exits
		if(hasErrors())
			return;

		else{

			// Now that we have done some simple "client side" validation it  
			// is time to check with the server  

			this.password = AccountAuthenticator.hashPassword(password);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			JSONObject json = ServerClient.genericPostRequest("login", nameValuePairs, context);

			String toHash = "";
			try {
				String check = json.getString("data");
				if(check.equals("Email or password is incorrect!")){
					tvPassword.setText("");
					tvPassword.setHighlightColor(Color.RED);
					tvUsername.setHighlightColor(Color.RED);
					Toast.makeText(context, check, Toast.LENGTH_LONG).show();
					return;
				}
				else{
					JSONObject data = json.getJSONObject("data");
					String session = data.getString("session");
					String secret = data.getString("secret");

					cache.saveFile(CacheClient.SESSION_CACHE, session);
					toHash = secret;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("Json did not return");
			}

			// finished  

			String accountType = this.getIntent().getStringExtra(PARAM_AUTHTOKEN_TYPE);  
			if (accountType == null) {  
				accountType = AccountAuthenticator.ACCOUNT_TYPE;  
			}  

			Account[] accounts = am.getAccountsByType(PARAM_AUTHTOKEN_TYPE);
			if(remove && accounts.length == 1)
				logout();
			
			accounts = am.getAccountsByType(PARAM_AUTHTOKEN_TYPE);

			if(accounts.length != 0 && !accounts[0].name.equalsIgnoreCase(username)){
				showMessage("Only one user is allowed on this phone.\nPlease log out before continuing.");
				return;
			}
			else if(accounts.length == 1 && accounts[0].name.equalsIgnoreCase(username)){
				username = accounts[0].name;
			}

			// This is the magic that adds the account to the Android Account Manager  
			final Account account = new Account(username, accountType);
			am.addAccountExplicitly(account, password, null);  
//			am.addAccount(PARAM_AUTHTOKEN_TYPE, PARAM_AUTHTOKEN_TYPE, null, null, this, null, null);

			// Now we tell our caller, could be the Android Account Manager or even our own application  
			// that the process was successful  

			final Intent intent = new Intent();

			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);  
			intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
			intent.putExtra(AccountManager.KEY_PASSWORD, password);
			if(getToken()){
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, toHash);
				am.setAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE, toHash);
			}
			this.setAccountAuthenticatorResult(intent.getExtras());  
			this.setResult(RESULT_OK, intent);



			//after setting the account, close the login activity and open the MainScreenActivity
			this.finish();
			if(openMain){
				final Intent start = new Intent(context, MainScreenActivity.class);
				start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(start);
			}

		}
	}

	private Boolean hasErrors(){

		this.username = tvUsername.getText().toString();  
		this.password = tvPassword.getText().toString(); 
		EmailValidator validate = new EmailValidator();

		boolean hasErrors = false;
		boolean badUsername = false;
		boolean badPass = false;



		if(validate.validate(username) && !badUsername){
			txtUsername.setBackgroundColor(Color.WHITE);
			txtUsername.setText("Username");
			tvUsername.setBackgroundColor(Color.WHITE);
		}
		if(!badPass){
			txtPassword.setBackgroundColor(Color.WHITE);
			txtPassword.setText("Password");
			tvPassword.setBackgroundColor(Color.WHITE);
		}

		if (!validate.validate(username)) {  
			hasErrors = true;
			badUsername = true;

			txtUsername.setText("Invalid Email Address");
			txtUsername.setBackgroundColor(Color.RED);
			tvUsername.setBackgroundColor(Color.MAGENTA);
		}  
		else if (password.length() < 6) {  
			hasErrors = true;
			badPass = true;

			txtPassword.setText("Incorrect Password");
			txtPassword.setBackgroundColor(Color.RED);
			tvPassword.setBackgroundColor(Color.MAGENTA);  
		}  
		return hasErrors;
	}

	private void showMessage(final String msg) {
		if (TextUtils.isEmpty(msg))
			return;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
}