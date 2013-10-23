package com.rogoapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

// how to write to files:
// http://developer.android.com/guide/topics/data/data-storage.html#filesInternal

// a detailed case here:
// http://web.archive.org/web/20110803104357/http://www.flexjockey.com/2011/03/create-a-pretty-simple-cache-for-android/

public class CacheClient {

	final Context context;
	
	public CacheClient(Context context) { // from an Activity, pass "this"
		this.context = context;
	}
	
	public void saveFile() {
		String FILENAME = "hello_file";
		String string = "hello world!";

		FileOutputStream fos;
		try {
			fos = context.openFileOutput(context.getCacheDir() + FILENAME, Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public String loadFile() {
//		String FILENAME = "hello_file";
//		
//		FileInputStream fos;
//		fos = context.openFileInput(context.getCacheDir() + FILENAME);
//		fos.read();
//		fos.close();
//	}
	
}
