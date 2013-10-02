package com.example.test;

import android.accounts.*;
import android.content.Context;
import android.os.Bundle;

public class Auth extends AbstractAccountAuthenticator{
	//Auth should be created when the App starts
	public Auth(Context context){	//check for saved data on creation
		super(context);
	}
	//Auth will need to know certain details about the user in order to communicate accurately with the servers
	//This stored data will be private to the Auth class.
	private class UserDetails{
		public UserDetails(){}	//Default user data is empty assuming no user has saved their login information
		public UserDetails(String username, String passHash){
			this.setUsername(username);
			this.setPassHash(passHash);
		}
		
		private String username;	//saves username for serverside database searches
		private String passHash;	//retains only the encrypted version of the user's password for security reasons
		
		public String getPassHash() {	//For Auth to verify user data
			return passHash;
		}
		private void setPassHash(String passHash) {	//Will only be used when logging in
			this.passHash = passHash;
		}
		public String getUsername() {	//maybe unused?
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		
		public void hashPass(String password){	//encrypts and stores password data
			//hashing
			setPassHash(passHash);
		}
	}
	
	private boolean loggedIn;
	public UserDetails openAcc;
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	private void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	 @Override  
	    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)  
	            throws NetworkErrorException {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public String getAuthTokenLabel(String authTokenType) {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	  
	    @Override  
	    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }

	public boolean login(String username, String Password, boolean save){ //takes in user data and compares it to stored data to connect a device to an account
		return false;
		//TODO write method
		
	}
	
	public boolean signUp(){ //takes in user data and compares it to server data to determine whether to create an account or not
		return false;
		//TODO write method
	}
	public boolean passCheck(){ //takes in user data and compares it to stored data to verify a user action
		return false;
		//TODO write method
	}
	
	private void userVerified(){
		//TODO write method	
	}
	
	private void verifyMess(){
		//TODO write method
	}
	
	
}
