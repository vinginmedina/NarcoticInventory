package com.ving.narcoticinventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class SaveBackgroundImage implements Runnable {
	
	private MyApplication myApp = null;
	private File dir = null;
	private SharedPreferences.Editor editor = null;
	
	SaveBackgroundImage (MyApplication app) {
		myApp = app;
		dir = myApp.baseDirectory();
	}
	
	@Override
	public void run() {
		
		File backgroundFile;
		editor = myApp.getSharedPreferences().edit();
		myApp.getAdmin().save(editor);
		String errorMsg = null;
		editor.putInt("BackgroundAlpha", myApp.getBackgroundAlpha());
		if (myApp.getBackground() == null) {
			editor.putString("Background", "null");
		} else {
			backgroundFile = new File(dir,"background.png");
			try {
				FileOutputStream fOut = new FileOutputStream(backgroundFile);
				myApp.getBackground().compress(Bitmap.CompressFormat.PNG, 85, fOut);
				fOut.flush();
				fOut.close();
				editor.putString("Background", backgroundFile.getPath());
			} catch (FileNotFoundException e) {
				errorMsg = e.toString();
				e.printStackTrace();
			} catch (IOException e) {
				errorMsg = e.toString();
				e.printStackTrace();
			}
		}
		editor.commit();
		if (errorMsg != null) {
			myApp.popUpMessage("Error Saving Backgroud", "There was an error saving the backgroud immage.\n"+errorMsg);
		} else {
			myApp.toastMessage("Background Saved");
		}
	}

}
