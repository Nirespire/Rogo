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

import android.os.AsyncTask;

public class ServerClientAsync extends AsyncTask<HttpPost, Void, JSONObject> {
	private boolean finished = false;
	private JSONObject finalJsonResult = null;
    @Override
    protected JSONObject doInBackground(HttpPost... params) {
    	this.finished = false;
    	if(params.length < 1){ return null; }
    	
    	System.out.println("DEBUG: Begin Do in background");
    	String response = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			System.out.println("DEBUG: " + params[0].getURI());
			
	
	        // Execute HTTP Post Request
	        HttpResponse httpresponse = httpclient.execute(params[0]);	//causes exception
	        
	        BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        
	        while ((line = rd.readLine()) != null) {
	        	sb.append(line + "\n");
	        	System.out.println("DEBUG: " + line);
	        }
	        response = sb.toString();
	        System.out.println("DEBUG: "+ response);
	        JSONObject JSONResponse = new JSONObject(response);
	        
	        this.finished = true;
	        this.finalJsonResult = JSONResponse;
	        return JSONResponse;
	        
	    } catch (ClientProtocolException e) {
	        System.err.print(e);
	    } catch (IOException e) {
	    	System.err.print(e);
	    } catch (JSONException e){
	    	System.err.print(e);
	    }
		return null;
    }

    protected void onPostExecute(JSONObject result) {
    	this.finished = true;
    	this.finalJsonResult = result;
    }
    public boolean isFinished(){
    	return this.finished;
    }
    public JSONObject getResult(){
    	return this.finalJsonResult;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}