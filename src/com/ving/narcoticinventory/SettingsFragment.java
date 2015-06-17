package com.ving.narcoticinventory;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {

	private String mType = "";
	private View v = null;
	private EditText entryItem = null;
	private EditText emailItem = null;
	private TextView currPassLabel = null;
	private EditText currPass = null;
	private EditText newPass = null;
	private EditText vPass = null;
	private Spinner qtySp = null;
	private CheckBox patNeed = null;
	private SeekBar seekBar = null;
	private int backgroundAlpha;
	private ImageView bgImageView = null;
	private Bitmap bgImage = null;
	private Drawable bgDrawable = null;
	private String[] editList = null;
	private ArrayAdapter<String> adapter = null;
	private ListView listView = null;
	private ColorPickerDialog.OnColorChangedListener colorListener = null;
	private TextView fts = null;
	private Button fbc = null;
	private SeekBar ftsSeekBar = null;
	private TextView dts = null;
	private Button dbc = null;
	private SeekBar dtsSeekBar = null;
	private TextView tts = null;
	private TextView ets = null;
	private SeekBar ttsSeekBar = null;
	private Button tbc = null;
	private Button ebc = null;
	private SeekBar tesSeekBar = null;
	private int[] textSize = null;
	private int[] textColor = null;
	private ManageConfiguration myActivity = null;
	private MyApplication myApp = null;

	public static SettingsFragment newInstance(String settingType) {

		SettingsFragment sf = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString("type", settingType);
		sf.setArguments(args);
		return sf;
	}

	public static SettingsFragment newInstance(Bundle bundle) {
		String settingType = bundle.getString("type", "");
		return newInstance(settingType);
	}

	@Override
	public void onInflate(Activity myActivity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(myActivity, attrs, savedInstanceState);
	}

	@Override
	public void onAttach(Activity myActivity) {
		super.onAttach(myActivity);
		this.myActivity = (ManageConfiguration) myActivity;
	}

	@Override
	public void onCreate(Bundle myBundle) {
		super.onCreate(myBundle);

		mType = getArguments().getString("type", "");
	}

	public String getShownIndex() {
		return mType;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myApp = myActivity.getMyApp();
		if (mType.equals("Set Facility Name")) {
			v = inflater.inflate(R.layout.settings_facilityname, container,
					false);
		} else if (mType.equals("Set Facility Administrator")) {
			v = inflater.inflate(R.layout.settings_administrator, container,
					false);
		} else if (mType.equals("Manage Medication List")) {
			v = inflater.inflate(R.layout.settings_druglist, container, false);
		} else if (mType.equals("Set Text Color and Size")) {
			v = inflater.inflate(R.layout.settings_textcolorsize, container, false);
		} else if (mType.equals("Set Background Image")) {
			v = inflater.inflate(R.layout.settings_selectbackground, container,
					false);
		} else if (mType.equals("Advanced Options")) {
			v = inflater.inflate(R.layout.settings_advanced, container, false);
		} else {
			v = inflater.inflate(R.layout.settings_lists, container, false);
		}
		setUpView();
		return v;
	}

	private void setUpView() {
		currPass = null;
		newPass = null;
		vPass = null;
		if (mType.equals("Set Facility Name")) {
			entryItem = (EditText) v.findViewById(R.id.facility);
			entryItem.setText(myApp.getFacilityName());
			editList = null;
		} else if (mType.equals("Set Facility Administrator")) {
			editList = null;
			entryItem = (EditText) v.findViewById(R.id.adminName);
			emailItem = (EditText) v.findViewById(R.id.emailAddr);
			currPassLabel = (TextView) v.findViewById(R.id.label3);
			currPass = (EditText) v.findViewById(R.id.currPass);
			newPass = (EditText) v.findViewById(R.id.newPass);
			vPass = (EditText) v.findViewById(R.id.vPass);
			if (myApp.getAdmin().name() != null) {
				entryItem.setText(myApp.getAdmin().name());
				emailItem.setText(myApp.getAdmin().email());
				entryItem.setNextFocusDownId(R.id.emailAddr);
				emailItem.setNextFocusDownId(R.id.currPass);
				currPass.setNextFocusDownId(R.id.newPass);
				newPass.setNextFocusDownId(R.id.vPass);
				currPass.setFocusable(true);
			} else {
				entryItem.setNextFocusDownId(R.id.emailAddr);
				emailItem.setNextFocusDownId(R.id.newPass);
				currPass.setFocusable(false);
				currPass.setFocusableInTouchMode(false);
				currPassLabel.setTextColor(Color.LTGRAY);
				newPass.setNextFocusDownId(R.id.vPass);
			}
		} else if (mType.equals("Manage Medication List")) {
			entryItem = (EditText) v.findViewById(R.id.drugEntry);
			patNeed = (CheckBox) v.findViewById(R.id.patNeed);
			patNeed.setChecked(true);
			editList = myApp.getDrugListNameArray();
		} else if (mType.equals("Set Text Color and Size")) {
			entryItem = null;
			editList = null;
			textSize = new int[4];
			textColor = new int[4];
			textSize[ColorAndSize.FACILITY_NAME] = myApp.getTextSettings().getTextSize(ColorAndSize.FACILITY_NAME);
			textSize[ColorAndSize.DRUG_HEADING_ROW] = myApp.getTextSettings().getTextSize(ColorAndSize.DRUG_HEADING_ROW);
			textSize[ColorAndSize.TRANSACTION_DETAIL_ROW] = myApp.getTextSettings().getTextSize(ColorAndSize.TRANSACTION_DETAIL_ROW);
			textSize[ColorAndSize.TRANSACTION_ERROR_ROW] = myApp.getTextSettings().getTextSize(ColorAndSize.TRANSACTION_ERROR_ROW); 
			textColor[ColorAndSize.FACILITY_NAME] = myApp.getTextSettings().getColor(ColorAndSize.FACILITY_NAME);
			textColor[ColorAndSize.DRUG_HEADING_ROW] = myApp.getTextSettings().getColor(ColorAndSize.DRUG_HEADING_ROW);
			textColor[ColorAndSize.TRANSACTION_DETAIL_ROW] = myApp.getTextSettings().getColor(ColorAndSize.TRANSACTION_DETAIL_ROW);
			textColor[ColorAndSize.TRANSACTION_ERROR_ROW] = myApp.getTextSettings().getColor(ColorAndSize.TRANSACTION_ERROR_ROW);
			colorListener = 
					new ColorPickerDialog.OnColorChangedListener() {
				@Override
				public void colorChanged(int key, int color) {
					if (key == ColorAndSize.FACILITY_NAME) {
						textColor[ColorAndSize.FACILITY_NAME] = color;
						fts.setTextColor(color);
						fbc.setBackgroundColor(color);
					} else if (key == ColorAndSize.DRUG_HEADING_ROW) {
						textColor[ColorAndSize.DRUG_HEADING_ROW] = color;
						dts.setTextColor(color);
						dbc.setBackgroundColor(color);
					} else if (key == ColorAndSize.TRANSACTION_DETAIL_ROW) {
						textColor[ColorAndSize.TRANSACTION_DETAIL_ROW] = color;
						tts.setTextColor(color);
						tbc.setBackgroundColor(color);
					} else if (key == ColorAndSize.TRANSACTION_ERROR_ROW) {
						textColor[ColorAndSize.TRANSACTION_ERROR_ROW] = color;
						ets.setTextColor(color);
						ebc.setBackgroundColor(color);
					}
				}
			};
			fts = (TextView) v.findViewById(R.id.fts);
			fts.setTextSize(textSize[ColorAndSize.FACILITY_NAME]);
			fts.setTextColor(textColor[ColorAndSize.FACILITY_NAME]);
			fbc = (Button) v.findViewById(R.id.colorFT);
			fbc.setBackgroundColor(textColor[ColorAndSize.FACILITY_NAME]);
			fbc.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	            	ColorPickerDialog colorDialog = new ColorPickerDialog(myActivity.getContext(), colorListener,
	            			ColorAndSize.FACILITY_NAME, textColor[ColorAndSize.FACILITY_NAME],
	            			myApp.getTextSettings().defaultColor(ColorAndSize.FACILITY_NAME));
	            	colorDialog.show();
	            }
	        });
			dts = (TextView) v.findViewById(R.id.dts);
			dts.setTextSize(textSize[ColorAndSize.DRUG_HEADING_ROW]);
			dts.setTextColor(textColor[ColorAndSize.DRUG_HEADING_ROW]);
			dbc = (Button) v.findViewById(R.id.colorDT);
			dbc.setBackgroundColor(textColor[ColorAndSize.DRUG_HEADING_ROW]);
			dbc.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	            	ColorPickerDialog colorDialog = new ColorPickerDialog(myActivity.getContext(), colorListener,
	            			ColorAndSize.DRUG_HEADING_ROW, textColor[ColorAndSize.DRUG_HEADING_ROW],
	            			myApp.getTextSettings().defaultColor(ColorAndSize.DRUG_HEADING_ROW));
	            	colorDialog.show();
	            }
	        });
			tts = (TextView) v.findViewById(R.id.tts);
			tts.setTextSize(textSize[ColorAndSize.TRANSACTION_DETAIL_ROW]);
			tts.setTextColor(textColor[ColorAndSize.TRANSACTION_DETAIL_ROW]);
			tbc = (Button) v.findViewById(R.id.colorTT);
			tbc.setBackgroundColor(textColor[ColorAndSize.TRANSACTION_DETAIL_ROW]);
			tbc.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	            	ColorPickerDialog colorDialog = new ColorPickerDialog(myActivity.getContext(), colorListener,
	            			ColorAndSize.TRANSACTION_DETAIL_ROW, textColor[ColorAndSize.TRANSACTION_DETAIL_ROW],
	            			myApp.getTextSettings().defaultColor(ColorAndSize.TRANSACTION_DETAIL_ROW));
	            	colorDialog.show();
	            }
	        });
			ets = (TextView) v.findViewById(R.id.ets);
			ets.setTextSize(textSize[ColorAndSize.TRANSACTION_ERROR_ROW]);
			ets.setTextColor(textColor[ColorAndSize.TRANSACTION_ERROR_ROW]);
			ebc = (Button) v.findViewById(R.id.colorTE);
			ebc.setBackgroundColor(textColor[ColorAndSize.TRANSACTION_ERROR_ROW]);
			ebc.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	            	ColorPickerDialog colorDialog = new ColorPickerDialog(myActivity.getContext(), colorListener,
	            			ColorAndSize.TRANSACTION_ERROR_ROW, textColor[ColorAndSize.TRANSACTION_ERROR_ROW],
	            			myApp.getTextSettings().defaultColor(ColorAndSize.TRANSACTION_ERROR_ROW));
	            	colorDialog.show();
	            }
	        });
			ftsSeekBar = (SeekBar) v.findViewById(R.id.seekFTS);
			ftsSeekBar.setProgress(textSize[ColorAndSize.FACILITY_NAME]);
			ftsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar,
						int progresValue, boolean fromUser) {
					textSize[ColorAndSize.FACILITY_NAME] = progresValue;
					fts.setTextSize(textSize[ColorAndSize.FACILITY_NAME]);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			Button rfts = (Button) v.findViewById(R.id.sizeFT);
			rfts.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					int size = myApp.getTextSettings().defaultSize(ColorAndSize.FACILITY_NAME);
					ftsSeekBar.setProgress(size);
					textSize[ColorAndSize.FACILITY_NAME] = size;
					fts.setTextSize(textSize[ColorAndSize.FACILITY_NAME]);
				}
			});
			dtsSeekBar = (SeekBar) v.findViewById(R.id.seekDTS);
			dtsSeekBar.setProgress(textSize[ColorAndSize.DRUG_HEADING_ROW]);
			dtsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar,
						int progresValue, boolean fromUser) {
					textSize[ColorAndSize.DRUG_HEADING_ROW] = progresValue;
					dts.setTextSize(textSize[ColorAndSize.DRUG_HEADING_ROW]);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			Button rdts = (Button) v.findViewById(R.id.sizeDT);
			rdts.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					int size = myApp.getTextSettings().defaultSize(ColorAndSize.DRUG_HEADING_ROW);
					dtsSeekBar.setProgress(size);
					textSize[ColorAndSize.DRUG_HEADING_ROW] = size;
					dts.setTextSize(textSize[ColorAndSize.DRUG_HEADING_ROW]);
				}
			});
			ttsSeekBar = (SeekBar) v.findViewById(R.id.seekTTS);
			ttsSeekBar.setProgress(textSize[ColorAndSize.TRANSACTION_DETAIL_ROW]);
			ttsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar,
						int progresValue, boolean fromUser) {
					textSize[ColorAndSize.TRANSACTION_DETAIL_ROW] = progresValue;
					tts.setTextSize(textSize[ColorAndSize.TRANSACTION_DETAIL_ROW]);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			Button rtts = (Button) v.findViewById(R.id.sizeTT);
			rtts.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					int size = myApp.getTextSettings().defaultSize(ColorAndSize.TRANSACTION_DETAIL_ROW);
					ttsSeekBar.setProgress(size);
					textSize[ColorAndSize.TRANSACTION_DETAIL_ROW] = size;
					tts.setTextSize(textSize[ColorAndSize.TRANSACTION_DETAIL_ROW]);
				}
			});
			tesSeekBar = (SeekBar) v.findViewById(R.id.seekTES);
			tesSeekBar.setProgress(textSize[ColorAndSize.TRANSACTION_ERROR_ROW]);
			tesSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar,
						int progresValue, boolean fromUser) {
					textSize[ColorAndSize.TRANSACTION_ERROR_ROW] = progresValue;
					ets.setTextSize(textSize[ColorAndSize.TRANSACTION_ERROR_ROW]);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			Button rtes = (Button) v.findViewById(R.id.sizeTE);
			rtes.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					int size = myApp.getTextSettings().defaultSize(ColorAndSize.TRANSACTION_ERROR_ROW);
					tesSeekBar.setProgress(size);
					textSize[ColorAndSize.TRANSACTION_ERROR_ROW] = size;
					ets.setTextSize(textSize[ColorAndSize.TRANSACTION_ERROR_ROW]);
				}
			});
		} else if (mType.equals("Set Background Image")) {
			entryItem = null;
			editList = null;
			bgImageView = (ImageView) v.findViewById(R.id.viewImage);
			seekBar = (SeekBar) v.findViewById(R.id.seekAlpha);
			backgroundAlpha = myApp.getBackgroundAlpha();
			seekBar.setProgress(backgroundAlpha);
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar,
						int progresValue, boolean fromUser) {
					backgroundAlpha = progresValue;
					if (bgDrawable != null) {
						bgDrawable.setAlpha(backgroundAlpha);
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			setBgImage(myApp.getBackground());
		} else if (mType.equals("Advanced Options")) {
			entryItem = null;
			editList = null;
		} else {
			String label;
			if (mType.equals("Manage Nurses")) {
				label = "Nurse to add:";
			} else if (mType.equals("Manage CRNAs")) {
				label = "CRNA to add:";
			} else if (mType.equals("Manage CRNAs/Doctors")) {
				label = "CRNA/Doctor to add:";
			} else {
				label = "OR Room to add:";
			}
			TextView text1 = (TextView) v.findViewById(R.id.settingsLabel);
			text1.setText(mType);
			TextView text2 = (TextView) v.findViewById(R.id.label1);
			text2.setText(label);
			entryItem = (EditText) v.findViewById(R.id.listEntry);
			editList = myActivity.getListArray(mType);
		}
		if (editList != null) {
			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, editList);
			listView = (ListView) v.findViewById(R.id.settingList);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener((OnItemClickListener) this);
			listView.setOnItemLongClickListener((OnItemLongClickListener) this);
		} else {
			adapter = null;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
	}

	public String settingType() {
		return mType;
	}

	public String getEntryText() {
		InputMethodManager mgr = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(entryItem.getWindowToken(), 0);
		return entryItem.getText().toString();
	}
	
	public void clearEntryText() {
		entryItem.setText("");
	}
	
	public Boolean getPatientNeeded() {
		return patNeed.isChecked();
	}

	public void updateAdmin() {

		InputMethodManager mgr = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(entryItem.getWindowToken(), 0);
		String name = entryItem.getText().toString();
		String email = emailItem.getText().toString();
		String cPassS = currPass.getText().toString();
		String nPassS = newPass.getText().toString();
		String vPassS = vPass.getText().toString();
		String title = "";
		String message = "";
		if (myApp.getAdmin().name() != null) {
			if (!myApp.getAdmin().verify(cPassS)) {
				title = "Password Value Not Correct";
				message = "The current password value is not correct.";
			} else {
				if (nPassS.equals("")) {
					if (!myApp.getAdmin().set(name, email, cPassS, cPassS)) {
						title = "Unable to set Facility Administrator";
						message = "Not able to set the facility Administrator.\n\nPlease verify that all of the values are correct.";
					}
				} else if (!nPassS.equals(vPassS)) {
					title = "Password Value Don't Match";
					message = "The two password values do not match.";
				} else if (!myApp.getAdmin().set(name, email, nPassS, cPassS)) {
					title = "Unable to set Facility Administrator";
					message = "Not able to set the facility Administrator.\n\nPlease verify that all of the values are correct.";
				}
			}
		} else {
			if (nPassS.equals("")) {
				title = "Password not set";
				message = "The new password needs to be set";
			} else if (!nPassS.equals(vPassS)) {
				title = "Password Value Don't Match";
				message = "The two password values do not match.";
			} else if (!myApp.getAdmin().set(name, email, nPassS, cPassS)) {
				title = "Unable to set Facility Administrator";
				message = "Not able to set the facility Administrator.\n\nPlease verify that all of the values are correct.";
			}
		}
		if (!title.equals("")) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					myActivity.getContext());
			dialog.setTitle(title);
			dialog.setMessage(message);
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else {
			currPass.setFocusable(true);
			currPass.setFocusableInTouchMode(true);
			currPassLabel.setTextColor(Color.BLACK);
			entryItem.setNextFocusDownId(R.id.emailAddr);
			emailItem.setNextFocusDownId(R.id.currPass);
			currPass.setNextFocusDownId(R.id.newPass);
			newPass.setNextFocusDownId(R.id.vPass);
			currPass.setText("");
			newPass.setText("");
			vPass.setText("");
		}
	}

	public void updateList(String[] newList) {
		entryItem.setText("");
		if (patNeed != null) {
			patNeed.setChecked(true);
		}
		editList = newList;
		if (editList != null) {
			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, editList);
			listView.setAdapter(adapter);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View target, final int position,
			long id) {
		myApp.toastMessage("Long click on " + adapter.getItem(position) + " to remove");
		AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
		View dialogView;
		final CheckBox editPatNeed;
		if (mType.equals("Manage Medication List")) {
			dialogView = myActivity.getLayoutInflater().inflate(R.layout.edit_drug_entry_dialog,null);
			editPatNeed = (CheckBox) dialogView.findViewById(R.id.patNeed);
			if (myApp.getDrug(position).patientNeeded()) {
				editPatNeed.setChecked(true);
			} else {
				editPatNeed.setChecked(false);
			}
		} else {
			dialogView = myActivity.getLayoutInflater().inflate(R.layout.edit_entry_dialog,null);
			editPatNeed = null;
		}
		dialog.setView(dialogView);
        dialog.setTitle("Edit or Delete this Entry");
        dialog.setCancelable(false);
        final AlertDialog editEntryDialog = dialog.create();
        editEntryDialog.show();
        final EditText entry = (EditText) dialogView.findViewById(R.id.editItem);
        entry.setText(adapter.getItem(position));
        Button neg = (Button)dialogView.findViewById(R.id.cancel);
        neg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editEntryDialog.dismiss();
			}
		});
        Button del = (Button)dialogView.findViewById(R.id.delete);
        del.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editEntryDialog.dismiss();
				deleteItemDialog(position);
			}
		});
        Button save = (Button)dialogView.findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Boolean chk = null;
				if (editPatNeed != null) {
					chk = editPatNeed.isChecked();
				}
				myActivity.updateEntry(position, mType, entry.getText().toString(), chk);
				editEntryDialog.dismiss();
			}
		});
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View target,
			int position, long id) {
		deleteItemDialog(position);
		return true;
	}
	
	public void deleteItemDialog(final int position) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
		dialog.setTitle("Do you Want to Delete " + adapter.getItem(position)
				+ "?");
		dialog.setCancelable(false);
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				myActivity.dismissEntry(position, mType);
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	public void setBgImage(Bitmap b) {
		bgImage = b;
		if (bgImage != null) {
			bgDrawable = new BitmapDrawable(getResources(),bgImage);
		} else {
			bgDrawable = getResources().getDrawable(R.drawable.pills);
		}
		bgImageView.setBackground(bgDrawable);
		bgDrawable.setAlpha(backgroundAlpha);
	}
	
	public int getAlpha() {
		return backgroundAlpha;
	}
	
	public int[] getTextSize() {
		return textSize;
	}
	
	public int[] getTextColor() {
		return textColor;
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}
