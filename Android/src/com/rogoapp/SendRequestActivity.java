package com.rogoapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.rogoapp.auth.AccountAuthenticator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendRequestActivity extends Activity {

    Button sendRequestButton;
    String userID;
    String trait;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_request, menu);
        return true;
    }
    
    public void onRequest(View v){
    	//Intent intent = getIntent();
    	//TODO:  FOR THIS TO WORK, VALUES MUST BE PASSED IN FROM OPENING ACTIVITY USING THE FOLLOWING CODE AS A GUIDELINE:
    	/*
    	Intent i=new Intent(context,SendMessage.class); //Create the Intent
		i.putExtra("id", user.getUserAccountId()+"");	//Use "putExtra" to include bonus info into new activity
		i.putExtra("name", user.getUserFullName());
		context.startActivity(i);						//Start Activity
    	 */
    	//String TargetUserID = intent.getStringExtra("TargetUserID"); // or should we be using username?
    	//TODO:  How do we know our current user's username?
    	//String RequestingUserID = intent.getStringExtra("RequestingUserID");
    	
    	//temp
    	String userID = "1234";
    	String targetID = "5678";
    	//end temp
    	
        EditText trait = (EditText) findViewById(R.id.request_trait);
        EditText location = (EditText) findViewById(R.id.request_location);
        
        System.out.println(trait.toString());
        System.out.println(location.toString());
        
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	//nameValuePairs.add(new BasicNameValuePair("TargetUserID", TargetUserID));
    	
    	//temp
    	nameValuePairs.add(new BasicNameValuePair("RequestingUser", userID));
    	nameValuePairs.add(new BasicNameValuePair("TargetUser", targetID));
    	//end temp
    	
		nameValuePairs.add(new BasicNameValuePair("trait", trait.getText().toString()));
		//TODO:  Should be coordinates.  Maybe generate by map searching the input string?
		nameValuePairs.add(new BasicNameValuePair("location", location.getText().toString()));
		
        ServerClient sc = new ServerClient();
        JSONObject jObj = sc.genericPostRequest("meetup_request", nameValuePairs);
        String status = null;
        try{
        	status = jObj.getString("status");
        }catch(JSONException e){
        	System.err.print(e);
        }
        //TODO:  Remove this!
        System.out.println("status = " + status);
        
        final Context context = this;
        if(status.equals("success")){
            final Intent start = new Intent(context, MainScreenActivity.class);
            start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(start);
        }
        
    }
/*
    @SuppressWarnings("deprecation")
    public void openRequestPopup(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Send Request");
        alertDialog.setMessage("Send Request to User?");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO NEED TO UPDATE FOR MEETUP REQUEST
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_id1", ""));
                nameValuePairs.add(new BasicNameValuePair("user1_trait", ""));
                nameValuePairs.add(new BasicNameValuePair("location", ""));

                ServerClient sc = new ServerClient();

                //TODO create server request for this post
                JSONObject jObj = sc.genericPostRequest("meetup_request", nameValuePairs);
                String uid = null;
                String status = null;

                try{
                    //uid = sc.getLastResponse().getString("uid");
                    status = jObj.getString("status");
                }catch(JSONException e){
                    System.err.print(e);
                }
                System.out.println("status = " + status + ", uid = " + uid);
            }
        });
        alertDialog.show();
    }
*/


}