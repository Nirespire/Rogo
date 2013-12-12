package com.rogoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeetingSomeoneActivity extends Activity {

    private TextView user;
    private TextView interests;
    private TextView location;
    private TextView trait;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_someone);

        user = (TextView)findViewById(R.id.user2);
        interests = (TextView)findViewById(R.id.user2_interests);
        location = (TextView)findViewById(R.id.meet_location);
        trait = (TextView)findViewById(R.id.user2_traits);

        String targetID = (String) getIntent().getSerializableExtra("user");
        String u2trait = (String) getIntent().getSerializableExtra("trait");
        String u2location = (String) getIntent().getSerializableExtra("location");

        user.setText(targetID);
        location.setText(u2location);
        trait.setText(u2trait);


        //TODO NEED TO PULL: OTHER USER'S INTERESTS, OTHER USER'S ENTERED TRAIT, NEARBY LOCATION TO MEET AT AND SET IN TEXTVIEWS

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    public void weMet(View V){
        toaster();
        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void toaster(){
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("CONGRATS");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
