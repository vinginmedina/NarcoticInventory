package com.ving.narcoticinventory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintAttributes.Resolution;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Toast;

public class ViewPrintReport extends Activity implements OnClickListener {

	private MyApplication myApp;
	private Context mContext = null;
	private File baseDir = null;
	private File reportDir = null;
	private File rptFile = null;
	private String reportName = null;
	private String fileName = null;
	private String baseURL = null;
	private String fileURL = null;
	private WebView webView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication) getApplication();
		mContext = this;
		setContentView(R.layout.activity_report);
		Intent myIntent = getIntent();
		reportName = myIntent.getStringExtra("Report");
		if (reportName == null) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("No report was specified.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							finish();
						}
					});
			dialog.show();
		}
		baseDir = myApp.baseDirectory();
		reportDir = new File(baseDir, reportName);
		fileName = "DrugInventoryLog-" + reportName + ".html";
		rptFile = new File(reportDir, fileName);
		if (!rptFile.canRead()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Cannot access the " + reportName + " report.");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							finish();
						}
					});
			dialog.show();
		}
		baseURL = "file://" + reportDir.toString();
		fileURL = "file://" + rptFile.toString();
		webView = (WebView) findViewById(R.id.rptView);
		// String data =
		// "<!DOCTYPE html>\n\n<html>\n\n<body>\n<p>This is a report</p>\n<p>"
		// +baseURL+"</p>\n<p>"+fileName+"</p>\n</body>\n\n</html>\n";
		webView.loadUrl(fileURL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.printRpt:
			// Get a PrintManager instance
			PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

			// Get a print adapter instance
			PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

			// Create a print job with name and adapter instance
			String jobName = getString(R.string.app_name) + " Report "
					+ reportName;
			
			PrintAttributes printAttrs = new PrintAttributes.Builder().
					setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).
					setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
					setMinMargins(PrintAttributes.Margins.NO_MARGINS).
					build();
			
			PrintJob printJob = printManager.print(jobName, printAdapter,
					printAttrs);

			myApp.toastMessage("Printing " + reportName, Toast.LENGTH_LONG);
			break;
		case R.id.emailRpt:
			String zipFileName = "DrugInventoryLog-" + reportName + ".zip";
			File zipFile = new File(baseDir,zipFileName);
			if (zipFile.canRead()) {
				Uri zipUri = Uri.fromFile(zipFile);
				String emailMessage = "Attached is the ";
				if (myApp.getFacilityName() != null) {
					emailMessage += myApp.getFacilityName() + " ";
				}
				emailMessage += "Medication Inventory " +
						"Log (" + zipFileName + ")" +" for " + reportName + ".";
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inventory Log for "+reportName);
				if (! myApp.getAdmin().email().equals("")) {
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {myApp.getAdmin().email()});
				}
				emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);
				emailIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
				emailIntent.setType("message/rfc822");
				try {
					startActivity(emailIntent);
				} catch (android.content.ActivityNotFoundException ex) {
				    myApp.toastMessage("There are no email clients installed.");
				}
			}
			break;
		case R.id.rtn:
			finish();
			break;
		}

	}

}
