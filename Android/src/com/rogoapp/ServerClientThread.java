package com.rogoapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class ServerClientThread extends Thread {
	private boolean finished = false;
	private JSONObject finalJsonResult = null;
	HttpPost httppost;
	
    
    public ServerClientThread(HttpPost httppost){
    	this.httppost = httppost;
    }
	
    public void run(){
    	this.finished = false;
    	ServerClient sc = new ServerClient();
    	sc.reset();
    	
    	System.out.println("DEBUG: Begin ServerClientThread");
    	String response = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			
	        // Execute HTTP Post Request
	        HttpResponse httpresponse = httpclient.execute(httppost);	
	        
	        BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        
	        while ((line = rd.readLine()) != null) {
	        	sb.append(line + "\n");
	        	System.out.println(line);
	        }
	        response = sb.toString();
	        System.out.println("DEBUG in ServerClientThread: "+ response);
	        JSONObject JSONResponse = new JSONObject(response);
	        
	        this.finished = true;
	        this.finalJsonResult = JSONResponse;
	        sc.setLastResponse(this.finalJsonResult);
	        
	    } catch (ClientProtocolException e) {
	        System.err.print(e);
	    } catch (IOException e) {
	    	System.err.print(e);
	    } catch (JSONException e){
	    	System.err.print(e);
	    }
		
    }
	
}
