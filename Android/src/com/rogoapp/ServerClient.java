package com.rogoapp;
	
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import java.util.*;


public class ServerClient{
	
	
	public void genericPostRequest(String request, List<NameValuePair> nameValuePairs) {
	    // Takes a request and a list of NameValuePairs for an http post request
		// Other programs must make nameValuePairs list
		// They will need org.apache.http.NameValuePair
		// and java.util.ArrayList
		// and java.util.List
		// Example of list:
		// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		// nameValuePairs.add(new BasicNameValuePair("id", "12345"));
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("https://api.rogoapp.com/request/" + request);
	
	    try {
	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        // Execute HTTP Post Request
	        HttpResponse httpresponse = httpclient.execute(httppost);
	        
	        BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
	        String response = "";
	        while (rd.ready()) {
	        	String line = rd.readLine();
	        	if(line == null){
	        		break;
	        	}else{
	        		response = response + rd.readLine();
	        	}
	        }
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	}
	
	public void register(String username, String email, String saltedPassword){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("password", saltedPassword));
		genericPostRequest("register.json", nameValuePairs);
		
		
		
	}
}