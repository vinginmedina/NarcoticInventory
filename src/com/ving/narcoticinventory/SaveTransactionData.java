package com.ving.narcoticinventory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class SaveTransactionData implements Runnable {
	
	private MyApplication myApp = null;
	private Drug drug;
	private File dir = null;
	private File myFile = null;
	
	SaveTransactionData (Drug d, MyApplication myApp) {
		drug = d;
		this.myApp = myApp;
		dir = myApp.getDrugs().storageDirectory();
	}
	
	@Override
	public void run() {
		String errorMsg = null;
		
		myFile = new File(dir,drug.fileName());
		try {
			FileOutputStream iStream =  new FileOutputStream(myFile);
			OutputStreamWriter iStreamWriter = new OutputStreamWriter(iStream);
			BufferedWriter myWriter = new BufferedWriter(iStreamWriter);
			myWriter.write(drug.saveTransactions());
			myWriter.close();
			Thread reportGeneratorThread = new Thread(new ReportGenerator(myApp, false));
			reportGeneratorThread.start();
			myApp.toastMessage("Transaction Saved");
		}catch(Exception e) {
			errorMsg = "Error Saving Transaction file "+drug.fileName()+".\n"+e.toString();
		}
		if(errorMsg != null) {
			myApp.popUpMessage("Error Saving Transaction", errorMsg);
		}
	}
	
}
