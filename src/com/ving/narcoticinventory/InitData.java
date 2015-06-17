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

public class InitData extends AsyncTask<MyApplication, Integer, MyApplication> {
	
	private Context mContext;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	private SharedPreferences settings = null;
	private Boolean foundData = false;
	private File dir = null;
	
	InitData (Context context) {
		mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	    pd = ProgressDialog.show(mContext, "Initalizing", "Setting up Data");
	}
	
	@Override
	protected MyApplication doInBackground(MyApplication... myApps) {
		String result;
		String password;
		StaffSpinnerData data;
		DrugList drugs;
		ColorAndSize textSettings;
		
		settings = myApps[0].getSharedPreferences();
		dir = myApps[0].baseDirectory();
		if (! dir.exists()) {
			if (!dir.mkdirs()) {
				errorMsg = "Unable to create directory "+dir.getPath();
				cancel(true);
			}
		}
		if (!isCancelled()) {
			File noMedia = new File(dir,".nomedia");
			if (! noMedia.exists()) {
				try {
					noMedia.createNewFile();
				} catch(IOException e) {
					errorMsg = e.toString();
					cancel(true);
		        }
			}
		}
		if (!isCancelled()) {
			int alpha = settings.getInt("BackgroundAlpha", 50);
			result = settings.getString("Background", "null");
			Uri selectedImage = null;
			Bitmap backgroundImage = null;
			if (!result.equals("null")) {
				selectedImage = Uri.fromFile(new File(result));
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
			myApps[0].setBackgroundImage(backgroundImage, alpha, selectedImage);
		}
		if (!isCancelled()) {
			textSettings = new ColorAndSize();
			textSettings.setValues(settings);
			myApps[0].setTextSettings(textSettings);
			result = settings.getString("Facility", "");
			if ((result != null) && (!result.equals(""))) {
				myApps[0].setFacilityName(result);
				foundData = true;
			}
			myApps[0].getAdmin().setInitial(settings);
			result = settings.getString("Nurses","");
			if ((result != null) && (!result.equals(""))) {
				data = new StaffSpinnerData(result);
				foundData = true;
			} else {
				data = new StaffSpinnerData();
			}
			myApps[0].setNurses(data);
			result = settings.getString("Doctors","");
			if ((result != null) && (!result.equals(""))) {
				data = new StaffSpinnerData(result);
				foundData = true;
			} else {
				data = new StaffSpinnerData();
			}
			myApps[0].setDoctors(data);
			result = settings.getString("ORRooms","");
			if ((result != null) && (!result.equals(""))) {
				data = new StaffSpinnerData(result);
				foundData = true;
			} else {
				data = new StaffSpinnerData();
			}
			myApps[0].setOrRms(data);
			result = settings.getString("Drugs","");
			if ((result != null) && (!result.equals(""))) {
				drugs = new DrugList(result);
				foundData = true;
			} else {
				drugs = new DrugList();
			}
			myApps[0].setDrugList(drugs);
			result = settings.getString("StartNurses", "");
			myApps[0].getDrugs().startOfDay(result, dir);
			result = settings.getString("EndNurses", "");
			myApps[0].getDrugs().endOfDay(result);
		}
		if (!isCancelled()) {
			String drugLabel;
			int qty;
			dir = myApps[0].getDrugs().storageDirectory();
			for (Drug drug : myApps[0].getDrugList()) {
				drugLabel = drug.name() + "_start";
				qty = settings.getInt(drugLabel, Drug.UNDEF);
				drug.setStartQty(qty);
				drugLabel = drug.name() + "_startOverride";
				if (settings.getBoolean(drugLabel, false)) {
					drugLabel = drug.name() + "_startOverrideComment";
					result = settings.getString(drugLabel, null);
					drug.setOverride(Drug.START, result);
				}
				drugLabel = drug.name() + "_final";
				qty = settings.getInt(drugLabel, Drug.UNDEF);
				drug.setFinalQty(qty);
				drugLabel = drug.name() + "_finalOverride";
				if (settings.getBoolean(drugLabel, false)) {
					drugLabel = drug.name() + "_finalOverrideComment";
					result = settings.getString(drugLabel, null);
					drug.setOverride(Drug.FINAL, result);
				}
				drugLabel = drug.name() + "_savedFinal";
				qty = settings.getInt(drugLabel, Drug.UNDEF);
				drug.setSavedQty(qty);
				drugLabel = drug.name() + "_patNeed";
				if (! settings.getBoolean(drugLabel, true)) {
					drug.patientNotNeeded();
				}
				if (dir != null) {
					File myFile = new File(dir,drug.fileName());
					if (myFile.exists()) {
						try {
							FileInputStream iStream =  new FileInputStream(myFile);
							InputStreamReader iStreamReader = new InputStreamReader(iStream);
							BufferedReader myReader = new BufferedReader(iStreamReader);
							String row = "";
							String transData = "";
							while ((row = myReader.readLine()) != null) {
								transData += row + "\n";
							}
							myReader.close();
							drug.transaction(transData);
						}catch(Exception e) {
							errorMsg = e.toString();
							cancel(true);
						}
					}
				}
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
	    builder.setTitle("Error in Initalization");
	    builder.setMessage("Sorry, there was an error trying to initalize the data.\n\n" + errorMsg);
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
			if (! foundData) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			    builder.setTitle("No Configuration Settings");
			    builder.setMessage("There is no configuration data set up for the app. Click OK to be taken to the settings screen.");
			    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int arg1) {
				            dialog.dismiss();
				            Intent userCreationIntent = new Intent(myApp,
									ManageConfiguration.class);
							userCreationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							myApp.startActivity(userCreationIntent);
				        }});
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int arg1) {
				            dialog.dismiss();
				        }});
			    builder.setCancelable(false);
			    AlertDialog myAlertDialog = builder.create();
			    myAlertDialog.show();
			} else if (! myApp.getDrugs().inDay()) {
				myApp.updateFacilityView();
				myApp.setDrugAdapter();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			    builder.setTitle("Start of Day Counts Not Set");
			    builder.setMessage("The start of day counts have not been set. Click OK to be taken to the Set Counts screen.");
			    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int arg1) {
				            dialog.dismiss();
				            Intent userCreationIntent = new Intent(myApp,
									SetDrugCounts.class);
							userCreationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							myApp.startActivity(userCreationIntent);
				        }});
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int arg1) {
				            dialog.dismiss();
				        }});
			    builder.setCancelable(false);
			    AlertDialog myAlertDialog = builder.create();
			    myAlertDialog.show();
			} else {
				Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
		    	saveSettingsThread.start();
		    	myApp.updateFacilityView();
		    	myApp.setDrugAdapter();
				myApp.notifyDrugAdapter();
				myApp.scrollDrugAdapter(0);
			}
		}
	}

}
