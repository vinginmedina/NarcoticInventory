package com.ving.narcoticinventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionDialog {
	public static final int UNDEF = 0;
	public static final int REMOVE = 1;
	public static final int REPLACE = 2;
	public static final int WASTE = 3;
	public static final int PATIENTTRANS = 4;
	public static final int ORTRANS = 5;
	public static final int WASTETRANS = 6;
	private Context mContext = null;
	private MyApplication myApp = null;
	private AlertDialog enterTranDialog = null;
	private Transaction trans = null;
	private Drug drug = null;
	private TextView qtyItem = null;
	private Button qty0 = null;
	private Button qty1 = null;
	private Button qty2 = null;
	private Button qty3 = null;
	private Button qty4 = null;
	private Button qty5 = null;
	private Button qty6 = null;
	private Button qty7 = null;
	private Button qty8 = null;
	private Button qty9 = null;
	private Button bksp = null;
	private Button clr = null;
	private EditText patientItem = null;
	private EditText wasteItem = null;
	private EditText wastepatientItem = null;
	private EditText wasteReasonItem = null;
	private Spinner patNurses = null;
	private Spinner orNurses = null;
	private Spinner wasteNurses1 = null;
	private Spinner wasteNurses2 = null;
	private Spinner doctors = null;
	private Spinner orRms = null;
	private SpinnerData[] nursesList = null;
	private SpinnerData[] doctorList = null;
	private SpinnerData[] orRmsList = null;
	private String patNurseValue = "";
	private String orNurseValue = "";
	private String doctorValue = "";
	private String orRmValue = "";
	private String wasteNurse1Value = "";
	private String wasteNurse2Value = "";
	private RadioButton rtn = null;
	private RadioButton rmv = null;
	private RadioButton waste = null;
	private RadioButton patTrans = null;
	private RadioButton orTrans = null;
	private RadioButton wasteTrans = null;
	private AlertDialog.Builder dialog = null;
	private AlertDialog.Builder confirmDialog = null;
	private int qty;
	
	public TransactionDialog (Drug drug, MyApplication myApp) {
		this.drug = drug;
		this.myApp = myApp;
		mContext = myApp.mainContext();
	}

	public void displayDialog() {
		LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = li.inflate(R.layout.dialog_new_trans, null);
		dialog = new AlertDialog.Builder(mContext);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        enterTranDialog = dialog.create();
        
		TextView drugNameTV = (TextView) dialogView.findViewById(R.id.drug_name);
		drugNameTV.setText("Add Transaction for "+drug.name());
		TextView currentCountTV = (TextView) dialogView.findViewById(R.id.count);
		currentCountTV.setText(drug.label(Drug.CURRENT));
		
		rtn = (RadioButton) dialogView.findViewById(R.id.returnDrug);
        rmv = (RadioButton) dialogView.findViewById(R.id.removeDrug);
        waste = (RadioButton) dialogView.findViewById(R.id.wasteDrug);
        rtn.setChecked(false);
        rtn.requestFocus();
        rmv.setChecked(false);
        waste.setChecked(false);
        rtn.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectAction(v);
        	}
        });
        rmv.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectAction(v);
        	}
        });
        waste.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectAction(v);
        	}
        });
        
        patTrans = (RadioButton) dialogView.findViewById(R.id.selPat);
        orTrans = (RadioButton) dialogView.findViewById(R.id.selOR);
        wasteTrans = (RadioButton) dialogView.findViewById(R.id.selWaste);
        patTrans.setChecked(false);
        orTrans.setChecked(false);
        wasteTrans.setChecked(false);
        patTrans.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectTransType(v);
        	}
        });
        orTrans.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectTransType(v);
        	}
        });
        wasteTrans.setOnClickListener(new RadioButton.OnClickListener() {
        	public void onClick(View v) {
        		selectTransType(v);
        	}
        });
		
		qtyItem = (TextView) dialogView.findViewById(R.id.qty);
		patientItem = (EditText) dialogView.findViewById(R.id.patientEntry);
		wasteItem = (EditText) dialogView.findViewById(R.id.wasteEntry);
		wastepatientItem = (EditText) dialogView.findViewById(R.id.patientEntryW);
		wasteReasonItem = (EditText) dialogView.findViewById(R.id.wasteReason);
        
		nursesList = myApp.getSpinnerData("Nurses");
        doctorList = myApp.getSpinnerData("Doctors");
        orRmsList = myApp.getSpinnerData("ORRooms");
        
        ArrayAdapter<SpinnerData> nurseAdapter = new ArrayAdapter<SpinnerData>(
				mContext, android.R.layout.simple_spinner_item, nursesList);
        nurseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patNurses = (Spinner) dialogView.findViewById(R.id.nurse);
        patNurses.setAdapter(nurseAdapter);
        patNurses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				patNurseValue = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        ArrayAdapter<SpinnerData> orRmAdapter = new ArrayAdapter<SpinnerData>(
        		mContext, android.R.layout.simple_spinner_item, orRmsList);
        orRmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orRms = (Spinner) dialogView.findViewById(R.id.orRm);
        orRms.setAdapter(orRmAdapter);
        orRms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = orRmsList[position];

				// Get selected value of key
				orRmValue = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        ArrayAdapter<SpinnerData> orNurseAdapter = new ArrayAdapter<SpinnerData>(
        		mContext, android.R.layout.simple_spinner_item, nursesList);
        orNurseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orNurses = (Spinner) dialogView.findViewById(R.id.orNurse);
        orNurses.setAdapter(orNurseAdapter);
        orNurses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				orNurseValue = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        ArrayAdapter<SpinnerData> wasteNurse1Adapter = new ArrayAdapter<SpinnerData>(
        		mContext, android.R.layout.simple_spinner_item, nursesList);
        wasteNurse1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wasteNurses1 = (Spinner) dialogView.findViewById(R.id.nurse1W);
        wasteNurses1.setAdapter(wasteNurse1Adapter);
        wasteNurses1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				wasteNurse1Value = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        ArrayAdapter<SpinnerData> wasteNurse2Adapter = new ArrayAdapter<SpinnerData>(
        		mContext, android.R.layout.simple_spinner_item, nursesList);
        wasteNurse2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wasteNurses2 = (Spinner) dialogView.findViewById(R.id.nurse2W);
        wasteNurses2.setAdapter(wasteNurse2Adapter);
        wasteNurses2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				wasteNurse2Value = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        ArrayAdapter<SpinnerData> doctorAdapter = new ArrayAdapter<SpinnerData>(
        		mContext, android.R.layout.simple_spinner_item, doctorList);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctors = (Spinner) dialogView.findViewById(R.id.doctor);
        doctors.setAdapter(doctorAdapter);
        doctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = doctorList[position];

				// Get selected value of key
				doctorValue = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        Button save = (Button)dialogView.findViewById(R.id.saveTrans);
        Button can = (Button)dialogView.findViewById(R.id.cancel);
        can.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager mgr = (InputMethodManager) myApp.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(patientItem.getWindowToken(), 0);
				enterTranDialog.dismiss();
			}
		});
        save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		InputMethodManager mgr = (InputMethodManager) myApp.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        mgr.hideSoftInputFromWindow(patientItem.getWindowToken(), 0);
        		confirm();
        	}
        });
        
        qty0 = (Button)dialogView.findViewById(R.id.qty0);
        qty0.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"0");
			}
		});
        qty1 = (Button)dialogView.findViewById(R.id.qty1);
        qty1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"1");
			}
		});
        qty2 = (Button)dialogView.findViewById(R.id.qty2);
        qty2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"2");
			}
		});
        qty3 = (Button)dialogView.findViewById(R.id.qty3);
        qty3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"3");
			}
		});
        qty4 = (Button)dialogView.findViewById(R.id.qty4);
        qty4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"4");
			}
		});
        qty5 = (Button)dialogView.findViewById(R.id.qty5);
        qty5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"5");
			}
		});
        qty6 = (Button)dialogView.findViewById(R.id.qty6);
        qty6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"6");
			}
		});
        qty7 = (Button)dialogView.findViewById(R.id.qty7);
        qty7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"7");
			}
		});
        qty8 = (Button)dialogView.findViewById(R.id.qty8);
        qty8.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"8");
			}
		});
        qty9 = (Button)dialogView.findViewById(R.id.qty9);
        qty9.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"9");
			}
		});
        bksp = (Button)dialogView.findViewById(R.id.bksp);
        bksp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String qtyString = qtyItem.getText().toString();
				if (qtyString.length() > 0) {
					qtyString = qtyString.substring(0, qtyString.length()-1);
					qtyItem.setText(qtyString);
				}
			}
		});
        clr = (Button)dialogView.findViewById(R.id.clr);
        clr.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText("");
			}
		});
        
        enterTranDialog.show();
	}
	
	public void selectAction(View target) {
		switch (target.getId()) {
		case R.id.removeDrug:
			rtn.setChecked(false);
	        rmv.setChecked(true);
			waste.setChecked(false);
			wasteTrans.setChecked(false);
			break;
		case R.id.returnDrug:
			rtn.setChecked(true);
	        rmv.setChecked(false);
			waste.setChecked(false);
			wasteTrans.setChecked(false);
			break;
		case R.id.wasteDrug:
			rtn.setChecked(false);
	        rmv.setChecked(false);
			waste.setChecked(true);
			patTrans.setChecked(false);
			orTrans.setChecked(false);
			wasteTrans.setChecked(true);
			break;
		}
	}
	
	public void selectTransType(View target) {
		switch (target.getId()) {
		case R.id.selPat:
			patTrans.setChecked(true);
	        orTrans.setChecked(false);
	        wasteTrans.setChecked(false);
	        waste.setChecked(false);
	        break;
		case R.id.selOR:
			patTrans.setChecked(false);
	        orTrans.setChecked(true);
	        wasteTrans.setChecked(false);
	        waste.setChecked(false);
	        break;
		case R.id.selWaste:
			patTrans.setChecked(false);
			orTrans.setChecked(false);
			wasteTrans.setChecked(true);
			rtn.setChecked(false);
	        rmv.setChecked(false);
			waste.setChecked(true);
		}
	}
	
	public int getTransAction() {
		int r = UNDEF;
		if (rtn.isChecked()) {
			r = REPLACE;
		} else if (rmv.isChecked()) {
			r = REMOVE;
		} else if (waste.isChecked()) {
			r = WASTE;
		}
		return r;
	}
	
	public int getTransType() {
		int r = UNDEF;
		if (patTrans.isChecked()) {
			r = PATIENTTRANS;
		} else if (orTrans.isChecked()) {
			r = ORTRANS;
		} else if (wasteTrans.isChecked()) {
			r = WASTETRANS;
		}
		return r;
	}
	
	private void confirm() {
		final String patient = patientItem.getText().toString();
		final String wasteAmt = wasteItem.getText().toString();
		final String patientWaste = wastepatientItem.getText().toString();
		final String wasteReason = wasteReasonItem.getText().toString();
		String action = "";
		final int act;
		try {
		    qty = Integer.parseInt(qtyItem.getText().toString());
		}catch(NumberFormatException e) {
			qty = Transaction.UNDEF;
		}
		if (qty < 0) {
			qty = Transaction.UNDEF;
		}
		if (getTransAction() == UNDEF) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No Action Selected");
			dialog.setMessage("Please select a transaction action.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if (getTransType() == UNDEF) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No Type Selected");
			dialog.setMessage("Please select a transaction type.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if ((qty == Transaction.UNDEF) && (getTransType() != WASTETRANS)) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Invalid Quantity");
			dialog.setMessage("Please enter a valid quantity.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if ((getTransType() == PATIENTTRANS) &&
				(drug.patientNeeded()) &&
				((patNurseValue.equals("")) || (patient.equals("")))) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Missing Data");
			dialog.setMessage("Please enter the patient and nurse.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if ((getTransType() == PATIENTTRANS) &&
				(! drug.patientNeeded()) && (patNurseValue.equals(""))) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Missing Data");
			dialog.setMessage("Please enter the nurse.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if ((getTransType() == ORTRANS) &&
				((orRmValue.equals("")) || (doctorValue.equals("")) ||
				(orNurseValue.equals("")))) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Missing Data");
			dialog.setMessage("Please enter the OR Room, Nurse, and CRNA/Doctor.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if ((getTransType() == WASTETRANS) &&
				((wasteAmt.equals("")) || (wasteReason.equals("")) ||
				(wasteNurse1Value.equals("")) || (wasteNurse2Value.equals("")))) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Missing Data");
            dialog.setMessage("Please enter the Wasted Amount, Reason and both Nurses");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	dialog.cancel();
                }
            });
            dialog.show();
		} else if ((getTransType() == WASTETRANS) &&
				(wasteNurse1Value.equals(wasteNurse2Value))) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Select Two Different Nurses");
            dialog.setMessage("You must select two different nurses to witness the waste.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	dialog.cancel();
                }
            });
            dialog.show();
		} else if ((getTransAction() == REMOVE) && (qty > drug.qty())) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Selected Quantity Too Large");
            dialog.setMessage("The available quantity for "+drug.name()+" is "+drug.qty()+".\n"+
            		"You entered a quantity of "+qty+".");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	dialog.cancel();
                }
            });
            dialog.show();
		} else {
			if (getTransAction() == REMOVE) {
				action = "Remove";
				act = Transaction.REMOVED;
			} else if (getTransAction() == REPLACE) {
				action = "Return";
				act = Transaction.REPLACED;
			} else {
				action = "Waste";
				act = Transaction.WASTE;
			}
			confirmDialog = new AlertDialog.Builder(mContext);
			confirmDialog.setTitle("Transaction Information Correct?");
			confirmDialog.setCancelable(false);
			confirmDialog.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					});
			if (getTransType() == PATIENTTRANS) {
				confirmDialog.setMessage(action + " Quantity: "
						+ qty + "\n" + drug.name() + "\n" + "Nurse: "
						+ patNurseValue + "\n" + "patient: "
						+ patient);
				confirmDialog.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								trans = new Transaction(act, qty, patNurseValue, patient);
								Intent sigIntent = new Intent(myApp.getActivity(), CaptureSignature.class);
								sigIntent.putExtra("Name", patNurseValue);
								myApp.getActivity().startActivityForResult(sigIntent,
										CaptureSignature.SIGNATURE_ACTIVITY);
								 dialog.cancel();
							}
						});
			} else if (getTransType() == ORTRANS) {
				confirmDialog.setMessage(action + " Quantity: "
						+ qty + "\n" + drug.name() + "\n" + "OR Room: "
						+ orRmValue + "\n" + "Nurse: "
						+ orNurseValue + "\n" + "CRNA/Doctor: "
						+ doctorValue);
				confirmDialog.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								trans = new Transaction(act, qty, orNurseValue, doctorValue,
										orRmValue);
								Intent sigIntent = new Intent(myApp.getActivity(), CaptureSignature.class);
								sigIntent.putExtra("Name", orNurseValue);
								myApp.getActivity().startActivityForResult(sigIntent,
										CaptureSignature.SIGNATURE_ACTIVITY);
								 dialog.cancel();
							}
						});
			} else if (getTransType() == WASTETRANS) {
				confirmDialog.setMessage(action + " Quantity: "
						+ wasteAmt + "\n" + drug.name() + "\n" + "Patient: "
						+ patientWaste + "\n" + "Reason for waste: " + wasteReason + "\n"
						+ "Nurse 1: " + wasteNurse1Value + "\n" + "Nurse 2: "
						+ wasteNurse2Value);
				confirmDialog.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								trans = new Transaction(wasteAmt, wasteReason, patientWaste, wasteNurse1Value, wasteNurse2Value);
								Intent sigIntent = new Intent(myApp.getActivity(), CaptureSignature.class);
								sigIntent.putExtra("Name", wasteNurse1Value);
								myApp.getActivity().startActivityForResult(sigIntent,
										CaptureSignature.SIGNATURE_ACTIVITY);
								 dialog.cancel();
							}
						});
			}
			confirmDialog.show();
		}
	}
	
	public Transaction transaction() {
		return trans;
	}
	
	public String nurse2() {
		return wasteNurse2Value;
	}
	
	public void closeDialog() {
		enterTranDialog.cancel();
	}

}
