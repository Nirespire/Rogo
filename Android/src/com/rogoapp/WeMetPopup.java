package com.rogoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WeMetPopup extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Congrats on the Meetup").setPositiveButton("Add To Buddy List", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // adding to buddylist
            }
            
        }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
            
            public void onClick(DialogInterface dialog, int id) {
                // Return to main screen
            }
            
        });
        
        return builder.create();
    }
}