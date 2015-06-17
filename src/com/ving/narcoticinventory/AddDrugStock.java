package com.ving.narcoticinventory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AddDrugStock extends Activity implements OnItemClickListener {
	
	private MyApplication myApp;
	private Context mContext = null;
	private CountObject countItems = null;
	private Spinner nurses1 = null;
	private Spinner nurses2 = null;
	private SpinnerData[] nursesList = null;
	private String nurse1Value = "";
	private String nurse2Value = "";
	private String nurse1SigFile = "";
	private String nurse2SigFile = "";
	private int sigCollected;
	private ArrayAdapter<String> adapter = null;
	private ListView listView = null;
	private int verifyPosn = Transaction.UNDEF;
	private int qty = Transaction.UNDEF;
	private Boolean thingsHaveChanged = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication) getApplication();
		mContext = this;
		setContentView(R.layout.activity_adddrugstock);
		LinearLayout background = (LinearLayout) findViewById(R.id.addStockBackground);
		myApp.drawBackground(background);
		nursesList = myApp.getSpinnerData("Nurses");
		ArrayAdapter<SpinnerData> nurse1Adapter = new ArrayAdapter<SpinnerData>(
				this, android.R.layout.simple_spinner_item, nursesList);
        nurse1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nurses1 = (Spinner) findViewById(R.id.nurse1);
        nurses1.setAdapter(nurse1Adapter);
        nurses1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				nurse1Value = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        ArrayAdapter<SpinnerData> nurse2Adapter = new ArrayAdapter<SpinnerData>(
				this, android.R.layout.simple_spinner_item, nursesList);
        nurse2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nurses2 = (Spinner) findViewById(R.id.nurse2);
        nurses2.setAdapter(nurse2Adapter);
        nurses2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SpinnerData d = nursesList[position];

				// Get selected value of key
				nurse2Value = d.getValue();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        countItems = new CountObject();
        int i = 0;
        for (Drug drug : myApp.getDrugList()) {
        	countItems.add(drug.name() + " -- No Quantity Added");
        }
        adapter = new DrugListCountAdapter(this, countItems);
		listView = (ListView) findViewById(R.id.drugList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener((OnItemClickListener) this);
	}
	
	public void addDrugOnClick (View target) {
		Button btn = (Button) target;
		switch (btn.getId()) {
		case R.id.save:
			if ((nurse1Value.equals("")) || (nurse2Value.equals(""))) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	            dialog.setTitle("Both Nurses Not Set");
	            dialog.setCancelable(false);
	            dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,int id) {
	                	dialog.cancel();
	                }
	            });
	            dialog.show();
			} else if (nurse1Value.equals(nurse2Value)) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	            dialog.setTitle("Two Different Nurses Must Verify Additions");
	            dialog.setCancelable(false);
	            dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,int id) {
	                	dialog.cancel();
	                }
	            });
	            dialog.show();
			} else {
				sigCollected = 0;
				Intent sigIntent = new Intent(AddDrugStock.this, CaptureSignature.class);
            	sigIntent.putExtra("Name",nurse1Value);
                startActivityForResult(sigIntent,CaptureSignature.SIGNATURE_ACTIVITY);
			}
			break;
		case R.id.cancel:
			if (thingsHaveChanged) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	            dialog.setTitle("Changes Not Saved, Exit Anyway?");
	            dialog.setCancelable(false);
	            dialog.setPositiveButton("Go Back and Save",new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,int id) {
	                	dialog.cancel();
	                }
	            });
	            dialog.setNegativeButton("Exit Anyway", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,int id) {
	                    finish();
	                }
	            });
	            dialog.show();
			} else {
				finish();
			}
			break;
		}
	}
	
	public void onItemClick(AdapterView<?> arg0, final View target, final int position,
			long id) {
		final Drug drug = myApp.getDrug(position);
    	AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    	View dialogView = getLayoutInflater().inflate(R.layout.dialog_addstock,null);
    	dialog.setView(dialogView);
        dialog.setTitle("Add Stock");
        dialog.setCancelable(false);
        final AlertDialog enterDataDialog = dialog.create();
        enterDataDialog.show();
        TextView drugName = (TextView) dialogView.findViewById(R.id.drugname);
    	final TextView qtyItem = (TextView) dialogView.findViewById(R.id.qty);
    	drugName.setText(drug.name());
    	drugName.setTextColor(Color.MAGENTA);
    	Button qty0 = (Button)dialogView.findViewById(R.id.qty0);
        qty0.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"0");
			}
		});
        Button qty1 = (Button)dialogView.findViewById(R.id.qty1);
        qty1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"1");
			}
		});
        Button qty2 = (Button)dialogView.findViewById(R.id.qty2);
        qty2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"2");
			}
		});
        Button qty3 = (Button)dialogView.findViewById(R.id.qty3);
        qty3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"3");
			}
		});
        Button qty4 = (Button)dialogView.findViewById(R.id.qty4);
        qty4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"4");
			}
		});
        Button qty5 = (Button)dialogView.findViewById(R.id.qty5);
        qty5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"5");
			}
		});
        Button qty6 = (Button)dialogView.findViewById(R.id.qty6);
        qty6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"6");
			}
		});
        Button qty7 = (Button)dialogView.findViewById(R.id.qty7);
        qty7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"7");
			}
		});
        Button qty8 = (Button)dialogView.findViewById(R.id.qty8);
        qty8.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"8");
			}
		});
        Button qty9 = (Button)dialogView.findViewById(R.id.qty9);
        qty9.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText(qtyItem.getText()+"9");
			}
		});
        Button bksp = (Button)dialogView.findViewById(R.id.bksp);
        bksp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String qtyString = qtyItem.getText().toString();
				if (qtyString.length() > 0) {
					qtyString = qtyString.substring(0, qtyString.length()-1);
					qtyItem.setText(qtyString);
				}
			}
		});
        Button clr = (Button)dialogView.findViewById(R.id.clr);
        clr.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				qtyItem.setText("");
			}
		});
        Button pos = (Button)dialogView.findViewById(R.id.save);
        Button neg = (Button)dialogView.findViewById(R.id.cancel);
        neg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				enterDataDialog.dismiss();
			}
		});
        pos.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
				    qty = Integer.parseInt(qtyItem.getText().toString());
				}catch(NumberFormatException e) {
					qty = Transaction.UNDEF;
				}
				if (qty != Transaction.UNDEF) {
					countItems.setValues(position, drug.name() + " -- Added " + qty, qty);
					countItems.setColor(position, Color.GREEN);
					adapter.notifyDataSetChanged();
					thingsHaveChanged = true;
				}
				enterDataDialog.dismiss();
			}
        });
        pos.requestFocus();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		String msg;
        switch(requestCode) {
        case CaptureSignature.SIGNATURE_ACTIVITY: 
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String status  = bundle.getString("status");
                if(status.equalsIgnoreCase("done")){
                	String fileName = bundle.getString("filename");
                	if (sigCollected == 0) {
                		nurse1SigFile = fileName;
                		sigCollected = 1;
                	} else if (sigCollected == 1) {
                		nurse2SigFile = fileName;
                		sigCollected = 2;
                	}
                	msg = "Saved Signature";
                } else {
                	msg = "Signature Canceled";
                	sigCollected = 0;
                }
                myApp.toastMessage(msg);
                if (sigCollected == 1) {
                	Intent sigIntent = new Intent(AddDrugStock.this, CaptureSignature.class);
                	sigIntent.putExtra("Name",nurse2Value);
                    startActivityForResult(sigIntent,CaptureSignature.SIGNATURE_ACTIVITY);
                } else if (sigCollected == 2){
                	for (int i=0;i<countItems.size();i++) {
                		if ((countItems.valueSet(i)) && (countItems.quantity(i) > 0)) {
                			myApp.getDrug(i).transaction(countItems.quantity(i), nurse1Value, nurse2Value, nurse1SigFile, nurse2SigFile);
                			myApp.getDrug(i).calcQty();
                			SaveTransactionData saveTrans = new SaveTransactionData(
                					myApp.getDrug(i), myApp);
                			Thread saveTransactionsThread = new Thread(saveTrans);
                			saveTransactionsThread.start();
                		}
                	}
					myApp.notifyDrugAdapter();
					finish();
                }
            }
            break;
        }
    }
	
	@Override
	public void onBackPressed() {
		if (thingsHaveChanged) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Changes Not Saved, Exit Anyway?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Go Back and Save",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	dialog.cancel();
                }
            });
            dialog.setNegativeButton("Exit Anyway", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    finish();
                }
            });
            dialog.show();
		} else {
			finish();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}

}
