package com.rogoapp;

import java.io.File;
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
	
	public void saveFile(String filename, String content) throws FileNotFoundException, IOException {
		File cacheDir = context.getCacheDir();
		File file = new File(cacheDir, filename);

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
	}
	
	public void addFile(String filename, String content) throws FileNotFoundException, IOException {
		File cacheDir = context.getCacheDir();
		File file = new File(cacheDir, filename);

		FileOutputStream fos = new FileOutputStream(file, true);
		fos.write(content.getBytes());
		fos.close();
	}

	
	public String loadFile(String filename) throws FileNotFoundException, IOException {
		File cacheDir = context.getCacheDir();
		File file = new File(cacheDir, filename);
		
		StringBuilder contentString = new StringBuilder();

		FileInputStream fis = new FileInputStream(file);
		int content;
		while((content = fis.read()) != -1){
			contentString.append((char)content);
		}
		fis.close();

		return contentString.toString();
	}
	
}
