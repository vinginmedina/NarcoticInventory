package com.ving.narcoticinventory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ReportGenerator implements Runnable {

	private MyApplication myApp;
	private ProgressDialog pd = null;
	private Boolean finalRpt = false;
	private Boolean isCancelled = false;
	private String errorMsg = null;
	private String today = "";
	private DrugList drugs = null;
	private File baseDir = null;
	private File reportDir = null;
	private File htmlFile = null;
	private ReportFile reportFiles = null;

	ReportGenerator(MyApplication myApp, Boolean finalRpt) {
		this.myApp = myApp;
		this.finalRpt = finalRpt;
	}

	@Override
	public void run() {

		drugs = myApp.getDrugs();
		today = drugs.todayString();
		baseDir = myApp.baseDirectory();
		reportDir = drugs.storageDirectory();
		reportFiles = new ReportFile(baseDir, reportDir);
		String rptTitle = null;
		if (myApp.getFacilityName() != null) {
			rptTitle = myApp.getFacilityName() + " Narcotic Log " + today;
		} else {
			rptTitle = "Narcotic Log " + today;
		}

		String htmlHead = "<!DOCTYPE html>\n\n<html>\n<head>\n<title>"
				+ rptTitle
				+ "</title>\n"
				+ "<style>\n<--\n@page { size: portrait; margin: 0.25in }\n"
				+ "@media print {\n  p {\n    winows: 2;\n    orphans: 2;\n  }\n"
                + "  h2 {\n    page-break-after: avoid;\n    orphans: 0;\n  }\n"
                + "  table,tr,th,td {\n    page-break-inside: avoid;\n  }\n}\n-->\n"
				+ "body {\nbackground-color: #fff;\n}\n"
				+ "h1 {\ntext-align:center;\n}\n"
				+ "h2 {\npage-break-after:avoid\n}\n"
				+ "p {\nmargin-bottom: 0.08in;\ndirection: ltr;\nfont: 20px calibrii, sans-serif;\nmargin-bottom: 0in;\nline-height: 100%;\n}\n"
				+ "p.indent {\nfont: 20px calibrii, sans-serif;\nmargin-left: 0.5in;\nmargin-bottom: 0in;\nline-height: 100%;\n}\n"
				+ "table,th,td {\nborder:1px solid black;\npage-break-inside:avoid\n}\n"
				+ "th {\nvertical-align:bottom;\ntext-align:center;\npadding:3px;\n}\n"
				+ "td {\nvertical-align:top;\ntext-align:right;\npadding:3px;\n}\n"
				+ "td.left {\nvertical-align:top;\ntext-align:left;\npadding:3px;\n}\n"
				+ "td.center {\nvertical-align:top;\ntext-align:center;\npadding:3px;\n}\n"
				+ "td.error {\nvertical-align:top;\ntext-align:right;\npadding:3px;\ntext-decoration: line-through;\n}\n"
				+ "td.lefterror {\nvertical-align:top;\ntext-align:left;\npadding:3px;\ntext-decoration: line-through;\n}\n"
				+ "td.centererror {\nvertical-align:top;\ntext-align:center;\npadding:3px;\ntext-decoration: line-through;\n}\n"
				+ "</style>\n</head>\n\n" + "<body>\n\n<h1>" + rptTitle
				+ "</h1>\n\n";
		String line = "";
		String filename = "DrugInventoryLog-" + today + ".html";
		htmlFile = new File(reportDir, filename);
		errorMsg = "";
		try {
			FileOutputStream iStream = new FileOutputStream(htmlFile);
			OutputStreamWriter iStreamWriter = new OutputStreamWriter(iStream);
			BufferedWriter myWriter = new BufferedWriter(iStreamWriter);
			myWriter.write(htmlHead);
			line = "<p>Initial Counts verified by</p>\n" + "<p class=indent>"
					+ myApp.getDrugs().startNurse1();
			if ((!drugs.startNurse1Sig().equals("")) &&
					(reportFiles.exists(drugs.startNurse1Sig()))) {
				line += "<img src=\"" + drugs.startNurse1Sig()
						+ "\" width=\"220\" align=\"middle\">";
			}
			line += "<br />" + drugs.startNurse2();
			if ((!drugs.startNurse2Sig().equals("")) &&
					(reportFiles.exists(drugs.startNurse2Sig()))) {
				line += "<img src=\"" + drugs.startNurse2Sig()
						+ "\" width=\"220\" align=\"middle\">";
			}
			line += "</p>\n\n";
			myWriter.write(line);
			line = "<p>End of Day Counts verified by</p>\n"
					+ "<p class=indent>" + drugs.endNurse1();
			if ((!drugs.endNurse1Sig().equals("")) &&
					(reportFiles.exists(drugs.endNurse1Sig()))) {
				line += "<img src=\"" + drugs.endNurse1Sig()
						+ "\" width=\"220\" align=\"middle\">";
			}
			line += "<br />" + drugs.endNurse2();
			if ((!drugs.endNurse2Sig().equals("")) &&
					(reportFiles.exists(drugs.endNurse2Sig()))) {
				line += "<img src=\"" + drugs.endNurse2Sig()
						+ "\" width=\"220\" align=\"middle\"><br />";
			}
			line += "</p>\n\n";
			myWriter.write(line);
			for (Drug drug : myApp.getDrugList()) {
				line = "<h2>"
						+ drug.name()
						+ " Start quantity: "
						+ drug.startQty()
						+ "; Final quantity: "
						+ Drug.prettyQty(drug.finalQty())
						+ "</h2>\n";
				myWriter.write(line);
				if (drug.override(Drug.START)) {
					line = "<h2>Start count for today did not match yesterday file count</h2>\n" +
							"<p class=indent>" + drug.overrideComment(Drug.START) + "</p>\n";
					myWriter.write(line);
				}
				if ((drug.finalQty() != Transaction.UNDEF) && drug.override(Drug.FINAL)) {
					line = "<h2>Final count for today does not match transaction count</h2>\n" +
							"<p class=indent>" + drug.overrideComment(Drug.FINAL) + "</p>\n";
					myWriter.write(line);
				}
				if (drug.transactionList().size() > 0) {
					line = "<table width=1008 cellpadding=5 cellspacing=0>\n"
							+ "<col width=125>\n<col width=65>\n<col width=65>\n<col width=65>\n<col width=148>\n<col width=223>\n<col width=226>\n"
							+ "<tr><th>Date/Time</th><th>Qty<br />Removed</th><th>Qty<br />Returned</th><th>Running<br />Qty</th>"
							+ "<th>Nurse Responsible</th><th>Used For</th><th>Signature</th></tr>\n";
				} else {
					line = "<p class=indent>There were no transactions.</p>\n\n";
				}
				myWriter.write(line);
				for (Transaction trans : drug.transactionList()) {
					if (trans.inError()) {
						line = "<tr><td class=lefterror>"
								+ trans.label(Transaction.DATE)
								+ "</td><td class=centererror>"
								+ trans.label(Transaction.REMOVED)
								+ "</td><td class=centererror>"
								+ trans.label(Transaction.REPLACED)
								+ "</td><td class=centererror>"
								+ trans.label(Transaction.CURRENT)
								+ "</td><td class=lefterror>"
								+ trans.label(Transaction.RESP)
								+ "</td><td class=lefterror>"
								+ trans.label(Transaction.USEDFOR)
								+ "</td><td>";
					} else {
						line = "<tr><td class=left>"
								+ trans.label(Transaction.DATE)
								+ "</td><td class=center>"
								+ trans.label(Transaction.REMOVED)
								+ "</td><td class=center>"
								+ trans.label(Transaction.REPLACED)
								+ "</td><td class=center>"
								+ trans.label(Transaction.CURRENT)
								+ "</td><td class=left>"
								+ trans.label(Transaction.RESP)
								+ "</td><td class=left>"
								+ trans.label(Transaction.USEDFOR)
								+ "</td><td>";
					}
					if ((trans.getAction() == Transaction.ADDSTOCK) ||
							(trans.getAction() == Transaction.REMSTOCK) ||
							(trans.getAction() == Transaction.WASTE)) {
						if ((!trans.signatureFile(1).equals("")) &&
								(reportFiles.exists(trans.signatureFile(1)))) {
							line += "<img src=\"" + trans.signatureFile(1) + "\" width=\"220\"><br />";
						} else {
							line += "&nbsp;<br />";
						}
						if ((!trans.signatureFile(2).equals("")) &&
								(reportFiles.exists(trans.signatureFile(2)))) {
							line += "<img src=\"" + trans.signatureFile(2) + "\" width=\"220\">";
						} else {
							line += "&nbsp;";
						}
					} else if ((!trans.signatureFile().equals("")) &&
							(reportFiles.exists(trans.signatureFile()))) {
						line += "<img src=\"" + trans.signatureFile()
								+ "\" width=\"220\">";
					} else {
						line += "&nbsp;";
					}
					line += "</td></tr>\n";
					myWriter.write(line);
				}
				if (drug.transactionList().size() > 0) {
					line = "</table>\n\n";
					myWriter.write(line);
				}
			}
			line = "</body>\n\n</html>\n";
			myWriter.write(line);
			myWriter.close();
		} catch (Exception e) {
			errorMsg += e.toString();
			isCancelled = true;
		}
		if (!isCancelled) {
			if (! reportFiles.makeZip(today)) {
				errorMsg = reportFiles.getMessage();
				isCancelled = true;
			}
		}
		if ((!isCancelled) && (finalRpt)) {
			for (Drug drug : myApp.getDrugList()) {
				File drugFile = new File(reportDir,drug.fileName());
				if ((drugFile.exists()) && (drugFile.canWrite())) {
					drugFile.delete();
				}
				drug.clearTransactions();
			}
		}
		if (isCancelled) {
			myApp.popUpMessage("Error in Generating Report",
					"Sorry, there was an error trying to generate and save the report.\n"
					+ errorMsg);
		}
	}
}
