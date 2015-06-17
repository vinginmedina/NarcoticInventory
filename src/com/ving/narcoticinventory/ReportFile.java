package com.ving.narcoticinventory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.os.Environment;
import android.util.Log;

public class ReportFile {
	
	private File baseDir = null;
	private File reportDir = null;
	private String errorMsg = null;
	
	public ReportFile(File base, File rpt) {
		baseDir = base;
		reportDir = rpt;
		errorMsg = null;
	}
	
	public Boolean exists(String fileName) {

		File inpFile = new File(reportDir,fileName);
		Boolean rtn = inpFile.exists();
		
		return rtn;
	}
	
	public Boolean copy(String fileName) {
		Boolean rtn = false;
	    InputStream in = null;
	    OutputStream out = null;
	    errorMsg = null;
	    File inpFile = new File(baseDir,fileName);
	    File outFile = new File(reportDir,fileName);
	    if (inpFile.canRead()) {
	    	try {
		        in = new FileInputStream(inpFile);        
		        out = new FileOutputStream(outFile);
		        byte[] buffer = new byte[1024];
		        int read;
		        while ((read = in.read(buffer)) != -1) {
		            out.write(buffer, 0, read);
		        }
		        in.close();
		        in = null;
	            out.flush();
		        out.close();
		        out = null;        
		        rtn = true;
		    } catch (Exception e) {
		    	errorMsg = e.getMessage();
		    }
		}
		return rtn;
	}
	
	public Boolean makeZip(String today) {
		Boolean rtn = false;
		errorMsg = null;
		
		String filename = "DrugInventoryLog-" + today + ".zip";
		File zipFile = new File(baseDir,filename);
		File fileLst[] = reportDir.listFiles();
		String zipDir = "DrugInventoryLog-" + today + "/";
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFile);

			ZipOutputStream out = new ZipOutputStream(
					new BufferedOutputStream(dest));

			byte data[] = new byte[1024];
			int count;

			for (int i = 0; i < fileLst.length; i++) {
				if (fileLst[i].canRead() &&
						(fileLst[i].getName().matches(".*png") || fileLst[i].getName().matches(".*html"))) {
					FileInputStream fi = new FileInputStream(fileLst[i]);
					origin = new BufferedInputStream(fi, 1024);
					ZipEntry entry = new ZipEntry(zipDir+fileLst[i].getName());
					out.putNextEntry(entry);
					while ((count = origin.read(data, 0, 1024)) != -1) {
						out.write(data, 0, count);
					}
					origin.close();
				}
			}
			out.close();
			rtn = true;
		} catch (Exception e) {
			errorMsg = e.getMessage();
		}
		
		return rtn;
	}
	
	public String getMessage() {
		return errorMsg;
	}
	

}
