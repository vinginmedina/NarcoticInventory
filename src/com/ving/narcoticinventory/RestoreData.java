package com.ving.narcoticinventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class RestoreData extends AsyncTask<MyApplication, Integer, MyApplication> {
	
	private Context mContext;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	private String backupFileName = null;
	private Boolean foundData = false;
	private File dir = null;
	
	RestoreData (Context context, String backupFile) {
		mContext = context;
		backupFileName = backupFile;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	    pd = ProgressDialog.show(mContext, "Restore", "Restoring Backup Settings");
	}
	
	@Override
	protected MyApplication doInBackground(MyApplication... myApps) {
		StaffSpinnerData dataNurses = new StaffSpinnerData();
		StaffSpinnerData dataDoctors = new StaffSpinnerData();
		StaffSpinnerData dataOR = new StaffSpinnerData();
		DrugList drugs = new DrugList();
		ColorAndSize textSettings = new ColorAndSize();
		int alpha = 50;
		Bitmap backgroundImage = null;
		Uri selectedImage = null;
		
		dir = myApps[0].baseDirectory();
		File backupFile = new File(dir,backupFileName);
		if (backupFile.canRead()) {
			try {
				FileInputStream iStream =  new FileInputStream(backupFile);
				InputStreamReader iStreamReader = new InputStreamReader(iStream);
				BufferedReader myReader = new BufferedReader(iStreamReader);
				String line = "";
				while ((line = myReader.readLine()) != null) {
					String[] tagValue = line.split(":");
					if (tagValue[0].equals("BackgroundAlpha")) {
						alpha = Integer.parseInt(tagValue[1]);
					} else if (tagValue[0].equals("Background")) {
						if (!tagValue[1].equals("null")) {
							selectedImage = Uri.fromFile(new File(tagValue[1]));
						}
						if (selectedImage != null) {
							InputStream imageStream;
							try {
								imageStream = myApps[0].getActivity().getContentResolver().openInputStream(selectedImage);
								backgroundImage = BitmapFactory.decodeStream(imageStream);
							} catch (FileNotFoundException e) {
								selectedImage = null;
								backgroundImage = null;
							}
						}
					} else if (tagValue[0].equals("Facility")) {
						myApps[0].setFacilityName(tagValue[1]);
					} else if (tagValue[0].equals("Nurses")) {
						dataNurses = new StaffSpinnerData(tagValue[1]);
					} else if (tagValue[0].equals("Doctors")) {
						dataDoctors = new StaffSpinnerData(tagValue[1]);
					} else if (tagValue[0].equals("ORRooms")) {
						dataOR = new StaffSpinnerData(tagValue[1]);
					} else if (tagValue[0].equals("Drugs")) {
						drugs = new DrugList(tagValue[1]);
					} else {
						textSettings.setValues(tagValue);
					}
				}
				myReader.close();
			}catch(Exception e) {
				errorMsg = e.toString();
				cancel(true);
			}
			if (! isCancelled()) {
				myApps[0].getDrugs().clearData();
				myApps[0].setTextSettings(textSettings);
				myApps[0].setBackgroundImage(backgroundImage, alpha, selectedImage);
				myApps[0].setNurses(dataNurses);
				myApps[0].setDoctors(dataDoctors);
				myApps[0].setOrRms(dataOR);
				myApps[0].setDrugList(drugs);
			}
		}
		return myApps[0];
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
	}
	
	@Override
	protected void onCancelled(MyApplication myApp) {
		pd.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setTitle("Error in Restore");
	    builder.setMessage("Sorry, there was an error trying to restore the backup settings.\n\nNo changes have been made.\n\n" + errorMsg);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int arg1) {
		            dialog.dismiss();
		        }});
	    builder.setCancelable(false);
	    AlertDialog myAlertDialog = builder.create();
	    myAlertDialog.show();
	}
	
	@Override
	protected void onPostExecute(final MyApplication myApp) {
		pd.dismiss();
		if (! isCancelled()) {
			myApp.drawBackground();
			Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
	    	saveSettingsThread.start();
	    	myApp.updateFacilityView();
	    	myApp.setDrugAdapter();
			myApp.notifyDrugAdapter();
		}
	}

}
