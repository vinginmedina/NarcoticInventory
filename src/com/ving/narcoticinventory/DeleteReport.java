package com.ving.narcoticinventory;

import java.io.File;

public class DeleteReport implements Runnable {
	
	private MyApplication myApp = null;
	private File dir = null;
	private String reportName = null;
	
	DeleteReport (MyApplication app, String report) {
		myApp = app;
		reportName = report;
	}
	
	@Override
	public void run() {
		dir = new File(myApp.baseDirectory(),reportName);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if ((file.exists()) && (file.canWrite())) {
					file.delete();
				}
			}
			dir.delete();
			myApp.toastMessage("Report "+reportName+" deleted.");
		}
	}

}
