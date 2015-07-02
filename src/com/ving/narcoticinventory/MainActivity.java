package com.ving.narcoticinventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener {
	public static String TAG = "MainActivity";
	private MyApplication myApp = null;
	private Context mContext = null;
	private MainActivity mActivity = null;
	private ExpandableListView transview = null;
	private ArrayAdapter<String> adapter = null;
	private AlertDialog printReportDialog = null;
	private AlertDialog deleteReportDialog = null;
	private TransactionDialog transDialog = null;
	private Drug currentDrug = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication) getApplication();
		mContext = this;
		mActivity = this;
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		TextView facilityName = (TextView) findViewById(R.id.facilityname);
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainBackground);
		transview = (ExpandableListView) findViewById(R.id.transview);
		transview.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Drug drug = myApp.getDrug(groupPosition);
				if (drug.getTransactionList().size() == 0) {
					myApp.toastMessage("There are no transactions for "
							+ drug.name());
				}
				return false;
			}
		});
		myApp.setStuff(mContext, mActivity, settings, facilityName, transview,
				mainLayout, getResources().getString(R.string.external_dir));
		myApp.setBackgroundImage(null, 50, null);
		InitData initDataTask = new InitData(mContext);
		initDataTask.execute(myApp);
	}

	public MyApplication getMyApp() {
		return myApp;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent userCreationIntent = null;
		Boolean rtn = true;

		switch (item.getItemId()) {
		case R.id.exit:
			finish();
			break;
		case R.id.action_settings:
			userCreationIntent = new Intent(myApp, ManageConfiguration.class);
			break;
		case R.id.set_counts:
			userCreationIntent = new Intent(myApp, SetDrugCounts.class);
			break;
		case R.id.endofday:
		case R.id.set_finalCounts:
			if (myApp.getDrugs().getEndOfDay()) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle("Day is finished");
				dialog.setMessage("The final counts have been set and the final report has been generated.");
				dialog.setCancelable(false);
				dialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				dialog.show();
			} else {
				userCreationIntent = new Intent(myApp, SetFinalDrugCounts.class);
			}
			break;
		case R.id.add_stock:
		case R.id.addStock:
			if (myApp.getDrugs().getEndOfDay()) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle("Day is finished");
				dialog.setMessage("The final counts have been set.\n\nNo further transactions can be entered.\n\nGo to the Menu and select 'Set Start of Day Med Counts' to begin a new day.");
				dialog.setCancelable(false);
				dialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				dialog.show();
			} else if (!myApp.getDrugs().inDay()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Start of Day Counts Not Set");
				builder.setMessage("The start of day counts have not been set. Click OK to be taken to the Set Counts screen.");
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								Intent uci = new Intent(myApp,
										SetDrugCounts.class);
								uci.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								myApp.startActivity(uci);
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
				builder.setCancelable(false);
				AlertDialog myAlertDialog = builder.create();
				myAlertDialog.show();
			} else {
				userCreationIntent = new Intent(myApp, AddDrugStock.class);
			}
			break;
		case R.id.remStock:
			if (myApp.getDrugs().getEndOfDay()) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle("Day is finished");
				dialog.setMessage("The final counts have been set.\n\nNo further transactions can be entered.\n\nGo to the Menu and select 'Set Start of Day Med Counts' to begin a new day.");
				dialog.setCancelable(false);
				dialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				dialog.show();
			} else if (!myApp.getDrugs().inDay()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Start of Day Counts Not Set");
				builder.setMessage("The start of day counts have not been set. Click OK to be taken to the Set Counts screen.");
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								Intent uci = new Intent(myApp,
										SetDrugCounts.class);
								uci.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								myApp.startActivity(uci);
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
				builder.setCancelable(false);
				AlertDialog myAlertDialog = builder.create();
				myAlertDialog.show();
			} else {
				userCreationIntent = new Intent(myApp, RemDrugStock.class);
			}
			break;
		case R.id.printReport:
			printReport();
			break;
		case R.id.deleteReport:
			deleteReport();
			break;
		case R.id.about:
			View dialogViewAbout = getLayoutInflater().inflate(R.layout.dialog_about, null);
			Button okBtn = (Button) dialogViewAbout.findViewById(R.id.positiveB);
			TextView versionTV = (TextView) dialogViewAbout.findViewById(R.id.versionNum);
			PackageInfo pinfo;
			String version;
			try {
				pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
				version = "Version: " + pinfo.versionName;
			} catch (NameNotFoundException e) {
				version = "Version: N/A";
			}
			versionTV.setText(version);
			Button emailBtn = (Button) dialogViewAbout.findViewById(R.id.emailAddr);
			final String emailAddr = getResources().getString(R.string.emailAddr);
			emailBtn.setText(emailAddr);
			emailBtn.setTextColor(Color.BLUE);
			emailBtn.setPaintFlags(emailBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			Button webSiteBtn = (Button) dialogViewAbout.findViewById(R.id.webSite);
			final String webSite = getResources().getString(R.string.webpage);
			webSiteBtn.setText(webSite);
			webSiteBtn.setTextColor(Color.BLUE);
			webSiteBtn.setPaintFlags(emailBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setView(dialogViewAbout);
			dialog.setCancelable(false);
			final AlertDialog versionDialog = dialog.create();
			versionDialog.setView(dialogViewAbout);
			versionDialog.setCancelable(false);
			final String subject = "Gas Mileage App " + version;
			emailBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					versionDialog.dismiss();
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { emailAddr });
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
					sendIntent.setType("message/rfc822");
					try {
						startActivity(sendIntent);
					} catch (android.content.ActivityNotFoundException ex) {
						myApp.toastMessage("There are no email clients installed. "
								+ ex.toString());
					}
				}
			});
			webSiteBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					versionDialog.dismiss();
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
					startActivity(browserIntent);
				}
			});
			okBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					versionDialog.dismiss();
				}
			});
			versionDialog.show();
			break;
		default:
			rtn = super.onOptionsItemSelected(item);
		}
		if (userCreationIntent != null) {
			userCreationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			myApp.startActivity(userCreationIntent);
		}
		return rtn;
	}

	private void printReport() {
		File files[] = myApp.baseDirectory().listFiles();
		ArrayList<String> reports = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				reports.add(files[i].getName());
			}
		}
		Collections.sort(reports, Collections.reverseOrder());
		if (reports.size() == 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No Reports are Available");
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
					R.layout.dialog_selectreport, null);
			Button can = (Button) dialogView.findViewById(R.id.cancel);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, reports);
			ListView listView = (ListView) dialogView
					.findViewById(R.id.reportList);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener((OnItemClickListener) this);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setView(dialogView);
			dialog.setTitle(R.string.printReport);
			dialog.setCancelable(false);
			printReportDialog = dialog.create();
			can.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					printReportDialog.dismiss();
				}
			});
			deleteReportDialog = null;
			printReportDialog.show();
		}
	}

	private void deleteReport() {
		File files[] = myApp.baseDirectory().listFiles();
		ArrayList<String> reports = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if ((files[i].isDirectory())
					&& (!files[i].getName().equals(
							myApp.getDrugs().todayString()))) {
				reports.add(files[i].getName());
			}
		}
		Collections.sort(reports, Collections.reverseOrder());
		if (reports.size() == 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No Reports are Available");
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
					R.layout.dialog_deletereport, null);
			Button can = (Button) dialogView.findViewById(R.id.cancel);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, reports);
			ListView listView = (ListView) dialogView
					.findViewById(R.id.reportList);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener((OnItemClickListener) this);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setView(dialogView);
			dialog.setTitle(R.string.deleteReport);
			dialog.setCancelable(false);
			deleteReportDialog = dialog.create();
			can.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					deleteReportDialog.dismiss();
				}
			});
			printReportDialog = null;
			deleteReportDialog.show();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View target, int position,
			long id) {
		if (printReportDialog != null) {
			printReportDialog.dismiss();
			Intent userCreationIntent = new Intent(myApp, ViewPrintReport.class);
			userCreationIntent.putExtra("Report", adapter.getItem(position));
			startActivity(userCreationIntent);
		}
		if (deleteReportDialog != null) {
			deleteReportDialog.dismiss();
			Thread deleteReportThread = new Thread(new DeleteReport(myApp,
					adapter.getItem(position)));
			deleteReportThread.start();
		}
	}

	public void addTransaction(Drug drug) {
		currentDrug = drug;
		if (myApp.getDrugs().getEndOfDay()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Day is finished");
			dialog.setMessage("The final counts have been set.\n\nNo further transactions can be entered.\n\nGo to the Menu and select 'Set Start of Day Med Counts' to begin a new day.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog.show();
		} else if (!myApp.getDrugs().inDay()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Start of Day Counts Not Set");
			builder.setMessage("The start of day counts have not been set. Click OK to be taken to the Set Counts screen.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							Intent userCreationIntent = new Intent(myApp,
									SetDrugCounts.class);
							userCreationIntent
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							myApp.startActivity(userCreationIntent);
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					});
			builder.setCancelable(false);
			AlertDialog myAlertDialog = builder.create();
			myAlertDialog.show();
		} else {
			transDialog = new TransactionDialog(currentDrug, myApp);
			transDialog.displayDialog();
		}
	}

	public void errorTransaction(final Drug drug, final Transaction transInError) {
		final SaveTransactionData saveTrans = new SaveTransactionData(drug,
				myApp);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle("Transaction for " + drug.name());
		dialog.setMessage("Transaction:\n\n"
				+ transInError.label(Transaction.DATEACTION) + "\n"
				+ transInError.label(Transaction.DETAIL)
				+ "\n\nDo you want to mark this transaction in error?");
		dialog.setCancelable(false);
		dialog.setPositiveButton("Yes, it is\nan error",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						transInError.markError();
						drug.calcQty();
						Thread saveTransactionsThread = new Thread(saveTrans);
						saveTransactionsThread.start();
						myApp.notifyDrugAdapter();
						dialog.cancel();
					}
				});
		dialog.setNegativeButton("No, leave\nit alone",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Toast toast = null;
		switch (requestCode) {
		case CaptureSignature.SIGNATURE_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String status = bundle.getString("status");
				if (status.equalsIgnoreCase("done")) {
					String fileName = bundle.getString("filename");
					if (transDialog.transaction().getAction() == Transaction.WASTE) {
						if (transDialog.transaction().signatureFile()
								.equals("")) {
							transDialog.transaction()
									.setSignatureFile(fileName);
							Intent sigIntent = new Intent(MainActivity.this,
									CaptureSignature.class);
							sigIntent.putExtra("Name", transDialog.nurse2());
							startActivityForResult(sigIntent,
									CaptureSignature.SIGNATURE_ACTIVITY);
						} else {
							transDialog.transaction().setSignatureFile(2,
									fileName);
							currentDrug.transaction(transDialog.transaction());
							SaveTransactionData saveTrans = new SaveTransactionData(
									currentDrug, myApp);
							Thread saveTransactionsThread = new Thread(
									saveTrans);
							saveTransactionsThread.start();
							transDialog.closeDialog();
							transDialog = null;
							myApp.notifyDrugAdapter(currentDrug);
						}
					} else {
						transDialog.transaction().setSignatureFile(fileName);
						currentDrug.transaction(transDialog.transaction());
						currentDrug.calcQty();
						SaveTransactionData saveTrans = new SaveTransactionData(
								currentDrug, myApp);
						Thread saveTransactionsThread = new Thread(saveTrans);
						saveTransactionsThread.start();
						transDialog.closeDialog();
						transDialog = null;
						myApp.notifyDrugAdapter(currentDrug);
					}
				} else {
					myApp.toastMessage("Transaction Canceled");
				}
			}
			break;
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
