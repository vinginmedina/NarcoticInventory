package com.ving.narcoticinventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ManageConfiguration extends FragmentActivity implements OnItemClickListener {
	
	private static final int SELECT_PHOTO = 100;
	private Context mContext = null;
	private MyApplication myApp;
	private Uri selectedImage = null;
	private SettingsFragment settings = null;
	private Bitmap backgroundImage = null;
	private ArrayAdapter adapter = null;
	private AlertDialog restoreDataDialog = null;
	public static String TAG = "ManageConfiguration";
	public static String OPTIONS[] = {
		"Set Facility Name",
		"Set Facility Administrator",
		"Manage Nurses",
		"Manage CRNAs/Doctors",
		"Manage OR Rooms",
		"Manage Medication List",
		"Set Text Color and Size",
		"Set Background Image",
		"Advanced Options"
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		myApp = (MyApplication) getApplication();
		backgroundImage = myApp.getBackground();
		setContentView(R.layout.activity_manageconfig);
	}
	
	public MyApplication getMyApp() {
		return myApp;
	}
	
	@Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    public void pickOption(int index) {

        settings = (SettingsFragment)
                getSupportFragmentManager().findFragmentById(R.id.settings);
        if (settings == null || (! settings.getShownIndex().equals(OPTIONS[index]))) {
            settings = SettingsFragment.newInstance(OPTIONS[index]);
            FragmentTransaction ft
                    = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in,
            		android.R.anim.fade_out);
            ft.replace(R.id.settings, settings);
            ft.commit();
        }
    }
    
    public void settingsOnClickFacility (View target) {
		myApp.setFacilityName(settings.getEntryText());
		myApp.updateFacilityView();
		Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
	}
    
    public void settingsOnClickAdmin (View target) {
    	settings.updateAdmin();
    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
    }
    
    public void settingsOnClickList (View target) {
    	String type = settings.settingType();
    	String newEntry = settings.getEntryText();
    	String[] newList = null;
    	
    	if (! newEntry.equals("")) {
	    	if (type.equals("Manage Nurses")) {
	    	    myApp.addToSpinner(newEntry, "Nurses");
	    	    newList = myApp.getSpinnerStrings("Nurses");
	    	} else if (type.equals("Manage CRNAs")) {
	    		myApp.addToSpinner(newEntry, "CRNA");
	    		newList = myApp.getSpinnerStrings("CRNA");
	    	} else if (type.equals("Manage CRNAs/Doctors")) {
	    		myApp.addToSpinner(newEntry, "Doctors");
	    		newList = myApp.getSpinnerStrings("Doctors");
	    	} else {
	    		myApp.addToSpinner(newEntry, "ORRooms");
	    		newList = myApp.getSpinnerStrings("ORRooms");
	    	}
	    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
	    	saveSettingsThread.start();
	    	settings.updateList(newList);
    	}
    }
    
    public void settingsOnClickDrug (View target) {
    	String newEntry = settings.getEntryText();
    	Boolean patNeed = settings.getPatientNeeded();
    	String[] newList = null;
    	
    	myApp.addToDrugList(newEntry, patNeed);
    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
    	newList = myApp.getDrugListNameArray();
    	settings.updateList(newList);
    	myApp.notifyDrugAdapter();
    }
    
    public void settingsOnClickTextColor (View target) {
    	myApp.getTextSettings().setTextSize(ColorAndSize.FACILITY_NAME, settings.getTextSize()[ColorAndSize.FACILITY_NAME]);
    	myApp.getTextSettings().setTextSize(ColorAndSize.DRUG_HEADING_ROW, settings.getTextSize()[ColorAndSize.DRUG_HEADING_ROW]);
    	myApp.getTextSettings().setTextSize(ColorAndSize.TRANSACTION_DETAIL_ROW, settings.getTextSize()[ColorAndSize.TRANSACTION_DETAIL_ROW]);
    	myApp.getTextSettings().setTextSize(ColorAndSize.TRANSACTION_ERROR_ROW, settings.getTextSize()[ColorAndSize.TRANSACTION_ERROR_ROW]);
    	myApp.getTextSettings().setColor(ColorAndSize.FACILITY_NAME, settings.getTextColor()[ColorAndSize.FACILITY_NAME]);
    	myApp.getTextSettings().setColor(ColorAndSize.DRUG_HEADING_ROW, settings.getTextColor()[ColorAndSize.DRUG_HEADING_ROW]);
    	myApp.getTextSettings().setColor(ColorAndSize.TRANSACTION_DETAIL_ROW, settings.getTextColor()[ColorAndSize.TRANSACTION_DETAIL_ROW]);
    	myApp.getTextSettings().setColor(ColorAndSize.TRANSACTION_ERROR_ROW, settings.getTextColor()[ColorAndSize.TRANSACTION_ERROR_ROW]);
    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
    	myApp.updateFacilityView();
    	myApp.notifyDrugAdapter();
    }
    
    public void settingsSelectImage (View target) {
    	switch (target.getId()) {
    	case R.id.selectImage:
	    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	    	photoPickerIntent.setType("image/*");
	    	startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	    	break;
    	case R.id.defaultImage:
    		backgroundImage = null;
    		selectedImage = null;
    		settings.setBgImage(backgroundImage);
    		break;
    	case R.id.clearImage:
    		backgroundImage = Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888);
    		selectedImage = null;
    		settings.setBgImage(backgroundImage);
    		break;
    	case R.id.saveConfig:
    		myApp.setBackgroundImage(backgroundImage, settings.getAlpha(), selectedImage);
    		Thread saveSettingsThread = new Thread(new SaveBackgroundImage(myApp));
        	saveSettingsThread.start();
    		break;
    	}
    }
    
    public void settingsOnClickBackupData (View target) {
    	Date todayDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String backupFileName = "BackupSettings." + sdf.format(todayDate);
		Thread saveSettingsThread = new Thread(new SaveConfigData(myApp, backupFileName));
    	saveSettingsThread.start();
    }
    
    public void settingsOnClickRestoreData (View target) {
    	View dialogView = getLayoutInflater().inflate(R.layout.dialog_getadminpass,null);
    	TextView passTitle = (TextView) dialogView.findViewById(R.id.getPassTitle);
    	passTitle.setText("Restore Settings");
    	TextView passMessage = (TextView) dialogView.findViewById(R.id.getPassMessage);
    	passMessage.setText("If you want to restore the settings, please enter the Administrator Password below and click on the Continue button.\n\nPlease note: this operation will delete any existing transaction information before restoring the settings.\n\nThere is no undo for this operation.");
		final EditText password = (EditText) dialogView.findViewById(R.id.adminPass);
		Button cont = (Button) dialogView.findViewById(R.id.cont);
		Button can = (Button)dialogView.findViewById(R.id.cancel);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog getPassDialog = dialog.create();
        can.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
		        getPassDialog.dismiss();
			}
		});
        cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
		        if (myApp.getAdmin().verify(password.getText().toString())) {
		        	restoreSettings();
		        	getPassDialog.dismiss();
		        } else {
		        	password.setText("");
		        	AlertDialog.Builder bpw = new AlertDialog.Builder(mContext);
		        	bpw.setTitle("Password Incorrect");
		        	bpw.setMessage("The password entered is invalid.");
		        	bpw.setCancelable(false);
		        	bpw.setPositiveButton("OK",new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface bpw,int id) {
		                	bpw.cancel();
		                }
		            });
		        	bpw.show();
		        }
		        
			}
		});
        getPassDialog.show();
    }
    
    private void restoreSettings() {
    	File files[] = myApp.baseDirectory().listFiles();
		ArrayList<String> backups = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains("BackupSettings")) {
				backups.add(files[i].getName());
			}
		}
		Collections.sort(backups, Collections.reverseOrder());
		if (backups.size() == 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No Backup Files are Available");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else {
			View dialogView = getLayoutInflater().inflate(
					R.layout.dialog_selectbackup, null);
			Button can = (Button) dialogView.findViewById(R.id.cancel);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, backups);
			ListView listView = (ListView) dialogView
					.findViewById(R.id.backupFiles);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener((OnItemClickListener) this);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setView(dialogView);
			dialog.setTitle(R.string.printReport);
			dialog.setCancelable(false);
			restoreDataDialog = dialog.create();
			can.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					restoreDataDialog.dismiss();
				}
			});
			restoreDataDialog.show();
		}
    }
    
    public void onItemClick(AdapterView<?> arg0, View target, int position,
			long id) {
    	restoreDataDialog.dismiss();
    	RestoreData restoreDataTask = new RestoreData(mContext, (String) adapter.getItem(position));
		restoreDataTask.execute(myApp);
	}
    
    public void settingsOnClickClearData (View target) {
    	View dialogView = getLayoutInflater().inflate(R.layout.dialog_getadminpass,null);
    	TextView passTitle = (TextView) dialogView.findViewById(R.id.getPassTitle);
    	passTitle.setText("Clear Transaction Data");
    	TextView passMessage = (TextView) dialogView.findViewById(R.id.getPassMessage);
    	passMessage.setText("If you want to clear the transaction data, please enter the Administrator Password below and click on the Continue button.\n\nThere is no undo for this operation.");
		final EditText password = (EditText) dialogView.findViewById(R.id.adminPass);
		Button cont = (Button) dialogView.findViewById(R.id.cont);
		Button can = (Button)dialogView.findViewById(R.id.cancel);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog getPassDialog = dialog.create();
        can.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
		        getPassDialog.dismiss();
			}
		});
        cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
		        if (myApp.getAdmin().verify(password.getText().toString())) {
		        	myApp.getDrugs().clearData();
    		    	myApp.notifyDrugAdapter();
    	        	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	        	saveSettingsThread.start();
		        	getPassDialog.dismiss();
		        } else {
		        	password.setText("");
		        	AlertDialog.Builder bpw = new AlertDialog.Builder(mContext);
		        	bpw.setTitle("Password Incorrect");
		        	bpw.setMessage("The password entered is invalid.");
		        	bpw.setCancelable(false);
		        	bpw.setPositiveButton("OK",new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface bpw,int id) {
		                	bpw.cancel();
		                }
		            });
		        	bpw.show();
		        }
		        
			}
		});
        getPassDialog.show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == RESULT_OK){  
                selectedImage = imageReturnedIntent.getData();
                try {
	                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
	                backgroundImage = BitmapFactory.decodeStream(imageStream);
	                settings.setBgImage(backgroundImage);
                }catch(FileNotFoundException e) {
                	backgroundImage = null;
                	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                	builder.setTitle("Error in Getting Image");
            	    builder.setMessage("Sorry, there was an error trying to get that image.\n" + e.toString());
            	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		        public void onClick(DialogInterface dialog, int arg1) {
            		            dialog.dismiss();
            		        }});
            	    builder.setCancelable(false);
            	    AlertDialog myAlertDialog = builder.create();
            	    myAlertDialog.show();
                }
            }
        }
    }
    
    public void dismissEntry (int posn, String type) {
		String[] newList = null;
    	if (type.equals("Manage Medication List")) {
    		myApp.removeFromDrugList(posn);
    		newList = myApp.getDrugStrings();
    		myApp.notifyDrugAdapter();
    	} else if (type.equals("Manage Nurses")) {
    	    myApp.removeFromSpinner(posn, "Nurses");
    	    newList = myApp.getSpinnerStrings("Nurses");
    	} else if (type.equals("Manage CRNAs")) {
    		myApp.removeFromSpinner(posn, "CRNA");
    		newList = myApp.getSpinnerStrings("CRNA");
    	} else if (type.equals("Manage CRNAs/Doctors")) {
    		myApp.removeFromSpinner(posn, "Doctors");
    		newList = myApp.getSpinnerStrings("Doctors");
    	} else {
    		myApp.removeFromSpinner(posn, "ORRooms");
    		newList = myApp.getSpinnerStrings("ORRooms");
    	}
    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
    	settings.updateList(newList);
    }
    
    public void updateEntry (int posn, String type, String newValue, Boolean chk) {
		String[] newList = null;
    	if (type.equals("Manage Medication List")) {
    		myApp.updateDrugName(posn, newValue, getResources().getString(R.string.external_dir), chk);
    		newList = myApp.getDrugStrings();
    		myApp.notifyDrugAdapter();
    	} else if (type.equals("Manage Nurses")) {
    	    myApp.updateSpinner(posn, "Nurses", newValue);
    	    newList = myApp.getSpinnerStrings("Nurses");
    	} else if (type.equals("Manage CRNAs/Doctors")) {
    		myApp.updateSpinner(posn, "Doctors", newValue);
    		newList = myApp.getSpinnerStrings("Doctors");
    	} else {
    		myApp.updateSpinner(posn, "ORRooms", newValue);
    		newList = myApp.getSpinnerStrings("ORRooms");
    	}
    	Thread saveSettingsThread = new Thread(new SaveConfigData(myApp));
    	saveSettingsThread.start();
    	settings.updateList(newList);
    }
    
    public String[] getListArray(String type) {
    	String[] rtn = null;
    	if (type.equals("Manage Nurses")) {
    	    rtn = myApp.getSpinnerStrings("Nurses");
    	} else if (type.equals("Manage CRNAs/Doctors")) {
    		rtn = myApp.getSpinnerStrings("Doctors");
    	} else {
    		rtn = myApp.getSpinnerStrings("ORRooms");
    	}
    	
    	return rtn;
    }
    
    public Context getContext() {
    	return mContext;
    }
    
    public String[] getDrugListArray() {
    	return myApp.getDrugStrings();
    }
    
    public void manageConfigurationOnClick (View target) {
        finish();
    }
    
    @Override
	public void onBackPressed() {
    	finish();
    }

}
