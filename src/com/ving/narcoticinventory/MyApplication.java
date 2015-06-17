package com.ving.narcoticinventory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyApplication extends Application {

	private MyApplication myApp = null;
	private Context context = null;
	private MainActivity myActivity = null;
	private SharedPreferences settings = null;
	private String facilityName = null;
	private String externalDir = null;
	private File baseDir = null;
	private ColorAndSize textSettings = null;
	private StaffSpinnerData nurses = null;
	private StaffSpinnerData crna = null;
	private StaffSpinnerData doctors = null;
	private StaffSpinnerData orRooms = null;
	private DrugList drugs = null;
	private TextView facilityNameTV = null;
	private String[] drugStrings = null;
	private ListView mainDrugList = null;
	private Administrator admin = null;
	private ExpandableListView mainRptView = null;
	private DrugListAdapter mainRptAdapter = null;
	private LinearLayout backgroundLayout = null;
	private Bitmap background = null;
	private Uri backgroundUri = null;
	private int backgroundAlpha;
	private Drug errorDrug = null;
	private Transaction errorTrans = null;

	public MyApplication() {
		myApp = this;
		admin = new Administrator();
		drugs = new DrugList();
	}

	public void setStuff(Context mContext, MainActivity activity,
			SharedPreferences sp, TextView tv, ExpandableListView rpt, LinearLayout layout, String externDir) {
		context = mContext;
		myActivity = activity;
		settings = sp;
		facilityNameTV = tv;
		mainRptView = rpt;
		backgroundLayout = layout;
		externalDir = externDir;
		baseDir = new File(Environment.getExternalStorageDirectory().toString(),externalDir);
	}

	public Context mainContext() {
		return context;
	}

	public MainActivity getActivity() {
		return myActivity;
	}

	public SharedPreferences getSharedPreferences() {
		return settings;
	}
	
	public void setDrugAdapter() {
		if (mainRptView != null) {
			mainRptAdapter = new DrugListAdapter(context, drugs.getList(), myApp);
			mainRptView.setAdapter(mainRptAdapter);
		}
	}
	
	public void scrollDrugAdapter(final int posn) {
		if ((mainRptView != null) && (mainRptAdapter != null) && ((posn >= 0) && (posn < drugs.getList().size()))) {
			mainRptView.post(new Runnable() {
	            @Override
	            public void run() {
	            	mainRptView.setSelectedGroup(posn);
	            	mainRptView.smoothScrollToPosition(posn);
	            }
	        });
		}
	}
	
	public void notifyDrugAdapter() {
		if ((mainRptView != null) && (mainRptAdapter != null)) {
			mainRptView.post(new Runnable() {
				@Override
	            public void run() {
					mainRptAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	public void notifyDrugAdapter(final Drug drug) {
		if ((mainRptView != null) && (mainRptAdapter != null)) {
			mainRptView.post(new Runnable() {
				@Override
	            public void run() {
					mainRptAdapter.notifyDataSetChanged();
					int i = 0;
					for (Drug d : drugs.getList()) {
						if (drug.equals(d)) {
							mainRptView.expandGroup(i);
							mainRptView.setSelectedGroup(i);
							mainRptView.smoothScrollToPosition(i);
						}
						i++;
					}
				}
			});
		}
	}
	
	public void toastMessage(final String msg) {
		backgroundLayout.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(myActivity, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void toastMessage(final String msg, final int length) {
		backgroundLayout.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(myActivity, msg, length);
			}
		});
	}
	
	public void popUpMessage(final String title, final String msg) {
		backgroundLayout.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setMessage(msg);
				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
				builder.setCancelable(false);
				AlertDialog myAlertDialog = builder.create();
				myAlertDialog.show();
			}
		});
	}
	
	public void setTextSettings(ColorAndSize textSettings) {
		this.textSettings = textSettings;
	}
	
	public ColorAndSize getTextSettings() {
		return textSettings;
	}
	
	public File baseDirectory() {
		return baseDir;
	}

	public void setFacilityName(String name) {
		facilityName = name;
	}

	public void updateFacilityView() {
		if ((facilityNameTV != null) && (facilityName != null)) {
			facilityNameTV.setText(facilityName);
			if (textSettings != null) {
				facilityNameTV.setTextColor(textSettings.getColor(ColorAndSize.FACILITY_NAME));
				facilityNameTV.setTextSize(textSettings.getTextSize(ColorAndSize.FACILITY_NAME));
			}
		}
	}

	public Administrator getAdmin() {
		return admin;
	}

	public void setBackgroundImage(Bitmap img, int alpha, Uri imgFile) {
		background = img;
		backgroundAlpha = alpha;
		backgroundUri = imgFile;
		drawBackground();
	}
	
	public void drawBackground() {
		if (backgroundLayout != null) {
			backgroundLayout.post(new Runnable() {
				@Override
				public void run() {
					Drawable d;
					if (background == null) {
						d = getResources().getDrawable(R.drawable.pills);
					} else {
						d = new BitmapDrawable(getResources(), background);
					}
					d.setAlpha(backgroundAlpha);
					backgroundLayout.setBackground(d);
				}
			});
		}
	}
	
	public void drawBackground(final LinearLayout layout) {
		if (layout != null) {
			layout.post(new Runnable() {
				@Override
				public void run() {
					Drawable d;
					if (background == null) {
						d = getResources().getDrawable(R.drawable.pills);
					} else {
						d = new BitmapDrawable(getResources(), background);
					}
					d.setAlpha(backgroundAlpha);
					layout.setBackground(d);
				}
			});
		}
	}

	public Bitmap getBackground() {
		return background;
	}

	public int getBackgroundAlpha() {
		return backgroundAlpha;
	}

	public Uri getBackgroundUri() {
		return backgroundUri;
	}

	public DrugList getDrugs() {
		return drugs;
	}

	public ArrayList<Drug> getDrugList() {
		return drugs.getList();
	}

	public void setNurses(StaffSpinnerData nurses) {
		this.nurses = nurses;
	}
	
	public void setCRNA(StaffSpinnerData crna) {
		this.crna = crna;
	}

	public void setDoctors(StaffSpinnerData doctors) {
		this.doctors = doctors;
	}

	public void setOrRms(StaffSpinnerData orRooms) {
		this.orRooms = orRooms;
	}

	public void setDrugList(DrugList drugs) {
		this.drugs = drugs;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public SpinnerData[] getSpinnerData(String type) {
		SpinnerData rtn[] = null;

		if (type.equals("Nurses")) {
			rtn = nurses.getItems();
		} else if (type.equals("CRNA")) {
			rtn = crna.getItems();
		} else if (type.equals("Doctors")) {
			rtn = doctors.getItems();
		} else if (type.equals("ORRooms")) {
			rtn = orRooms.getItems();
		}

		return rtn;
	}

	public String[] getSpinnerStrings(String type) {
		String rtn[] = null;

		if (type.equals("Nurses")) {
			rtn = nurses.getStrings();
		} else if (type.equals("CRNA")) {
			rtn = crna.getStrings();
		} else if (type.equals("Doctors")) {
			rtn = doctors.getStrings();
		} else if (type.equals("ORRooms")) {
			rtn = orRooms.getStrings();
		}

		return rtn;
	}

	public String spinnerStringsToString(String type) {
		String rtn = null;

		if (type.equals("Nurses")) {
			rtn = nurses.toString();
		} else if (type.equals("CRNA")) {
			rtn = crna.toString();
		} else if (type.equals("Doctors")) {
			rtn = doctors.toString();
		} else if (type.equals("ORRooms")) {
			rtn = orRooms.toString();
		}

		return rtn;
	}

	public void addToSpinner(String item, String type) {
		if (type.equals("Nurses")) {
			nurses.addItem(item);
		} else if (type.equals("CRNA")) {
			crna.addItem(item);
		} else if (type.equals("Doctors")) {
			doctors.addItem(item);
		} else if (type.equals("ORRooms")) {
			orRooms.addItem(item);
		}
	}

	public void removeFromSpinner(int posn, String type) {
		if (type.equals("Nurses")) {
			nurses.delItem(posn);
		} else if (type.equals("CRNA")) {
			crna.delItem(posn);
		} else if (type.equals("Doctors")) {
			doctors.delItem(posn);
		} else if (type.equals("ORRooms")) {
			orRooms.delItem(posn);
		}
	}
	
	public void updateSpinner(int posn, String type, String newValue) {
		if (type.equals("Nurses")) {
			nurses.updateItem(posn, newValue);
		} else if (type.equals("CRNA")) {
			crna.updateItem(posn, newValue);
		} else if (type.equals("Doctors")) {
			doctors.updateItem(posn, newValue);
		} else if (type.equals("ORRooms")) {
			orRooms.updateItem(posn, newValue);
		}
	}

	public Drug getDrug(int index) {
		return drugs.getDrug(index);
	}

	public String[] getDrugStrings() {
		if (drugStrings == null) {
			drugStrings = drugs.arrayList();
		}
		return drugStrings;
	}

	public String[] getDrugListNameArray() {
		return drugs.nameArrayList();
	}

	public String drugListToString() {
		return drugs.stringList();
	}

	public void addToDrugList(String name, Boolean patNeed) {
		drugs.add(name, patNeed);
		notifyDrugAdapter();
	}

	public void removeFromDrugList(int posn) {
		drugs.delete(posn);
		notifyDrugAdapter();
	}
	
	public void updateDrugName(int posn, String newName, String externDir, Boolean chk) {
		drugs.update(posn, newName, externDir, chk);
	}
	
	public void finishedErrorTrans() {
		errorDrug = null;
		errorTrans = null;
	}
	
	public Transaction transInError() {
		return errorTrans;
	}
	
	public Drug drugWithError() {
		return errorDrug;
	}

}
