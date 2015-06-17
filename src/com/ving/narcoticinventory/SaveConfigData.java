package com.ving.narcoticinventory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class SaveConfigData implements Runnable {
	
	private MyApplication myApp = null;
	private File dir = null;
	private String backupFileName = null;
	private SharedPreferences.Editor editor = null;
	
	SaveConfigData (MyApplication app) {
		myApp = app;
		editor = myApp.getSharedPreferences().edit();
	}
	
	SaveConfigData (MyApplication app, String fileName) {
		myApp = app;
		backupFileName = fileName;
		dir = myApp.baseDirectory();
	}
	
	@Override
	public void run() {
		
		String drugLabel = null;
		File backupFile = null;
		String errorMsg = null;
		if (backupFileName == null) {
			myApp.getAdmin().save(editor);
			myApp.getTextSettings().saveValues(editor);
			editor.putString("Facility", myApp.getFacilityName());
			editor.putString("Nurses", myApp.spinnerStringsToString("Nurses"));
			editor.putString("Doctors", myApp.spinnerStringsToString("Doctors"));
			editor.putString("ORRooms", myApp.spinnerStringsToString("ORRooms"));
			editor.putString("StartNurses", myApp.getDrugs().startSaveString());
			editor.putString("EndNurses", myApp.getDrugs().endSaveString());
			editor.putString("Drugs", myApp.drugListToString());
			for (Drug drug : myApp.getDrugList()) {
				drugLabel = drug.name() + "_start";
				editor.putInt(drugLabel, drug.startQty());
				drugLabel = drug.name() + "_startOverride";
				editor.putBoolean(drugLabel, drug.override(Drug.START));
				drugLabel = drug.name() + "_startOverrideComment";
				editor.putString(drugLabel, drug.overrideComment(Drug.START));
				drugLabel = drug.name() + "_final";
				editor.putInt(drugLabel, drug.finalQty());
				drugLabel = drug.name() + "_finalOverride";
				editor.putBoolean(drugLabel, drug.override(Drug.FINAL));
				drugLabel = drug.name() + "_finalOverrideComment";
				editor.putString(drugLabel, drug.overrideComment(Drug.FINAL));
				drugLabel = drug.name() + "_savedFinal";
				editor.putInt(drugLabel, drug.savedQty());
				drugLabel = drug.name() + "_patNeed";
				editor.putBoolean(drugLabel, drug.patientNeeded());
			}
			editor.commit();
			myApp.toastMessage("Settings Saved");
		} else {
			backupFile = new File(dir,backupFileName);
			try {
				FileOutputStream iStream =  new FileOutputStream(backupFile);
				OutputStreamWriter iStreamWriter = new OutputStreamWriter(iStream);
				BufferedWriter myWriter = new BufferedWriter(iStreamWriter);
				myApp.getTextSettings().saveValues(myWriter);
				if (myApp.getBackground() == null) {
					myWriter.write("Background:null\n");
				} else {
					File backgroundFile = new File(dir,"background.png");
					myWriter.write("Background:"+backgroundFile.getPath()+"\n");
				}
				myWriter.write("BackgroundAlpha:"+myApp.getBackgroundAlpha()+"\n");
				myWriter.write("Facility:"+myApp.getFacilityName()+"\n");
				myWriter.write("Nurses:"+myApp.spinnerStringsToString("Nurses")+"\n");
				myWriter.write("Doctors:"+myApp.spinnerStringsToString("Doctors")+"\n");
				myWriter.write("ORRooms:"+myApp.spinnerStringsToString("ORRooms")+"\n");
				myWriter.write("Drugs:"+myApp.drugListToString()+"\n");
				myWriter.close();
				myApp.toastMessage("Setting Backup File "+backupFileName+" created.");
			}catch(Exception e) {
				errorMsg = "Error creating backup file "+backupFileName+".\n"+e.toString();
			}
			if(errorMsg != null) {
				myApp.popUpMessage("Error Creating Backup File", errorMsg);
			}
		}
	}

}

