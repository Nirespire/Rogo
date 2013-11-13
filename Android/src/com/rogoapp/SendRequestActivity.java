package com.rogoapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SendRequestActivity extends Activity {

    Button sendRequestButton;
    String userID;
    String trait;
    String location;



    //TODO on create, text fields related to user that has been selected must be updated
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



}
