package com.rogoapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends Activity {
	
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmNewPassword;
	private Button mConfirmPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_change);
		
		oldPassword = (EditText)findViewById(R.id.passwordOld);
		newPassword = (EditText)findViewById(R.id.passwordNew);
		confirmNewPassword = (EditText)findViewById(R.id.passwordConfirm);
		
		mConfirmPassword = (Button)findViewById(R.id.changePasswordButton);
		mConfirmPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PasswordChangeActivity.this, R.string.sendToServer_toast, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
}
