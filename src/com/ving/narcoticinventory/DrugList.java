package com.ving.narcoticinventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

public class DrugList {
	
	private ArrayList<Drug> drugs = null;
	private String startNurse1 = null;
	private String startNurse2 = null;
	private String nurse1StartSigFile = null;
	private String nurse2StartSigFile = null;
	private String endNurse1 = null;
	private String endNurse2 = null;
	private String nurse1EndSigFile = null;
	private String nurse2EndSigFile = null;
	private String dateStartCount = null;
	private String dateFinalCount = null;
	private Boolean dayStarted = false;
	private Boolean dayFinished = false;
	private File todayDir = null;
	
	public DrugList() {
		startNurse1 = "";
		nurse1StartSigFile = "";
		startNurse2 = "";
		nurse2StartSigFile = "";
		endNurse1 = "";
		nurse1EndSigFile = "";
		endNurse2 = "";
		nurse2EndSigFile = "";
		dateStartCount = "UNDEF";
		dateFinalCount = "UNDEF";
		drugs = new ArrayList<Drug>();
	}
	
	public DrugList(String data) {
		String dataArray[] = data.split(";");
		Arrays.sort(dataArray);
		Drug drug;
		
		drugs = new ArrayList<Drug>();

		for (int i = 0; i < dataArray.length; i++) {
			drug = new Drug(dataArray[i]);
			drugs.add(drug);
		}
		sort();
		startNurse1 = "";
		nurse1StartSigFile = "";
		startNurse2 = "";
		nurse2StartSigFile = "";
		endNurse1 = "";
		nurse1EndSigFile = "";
		endNurse2 = "";
		nurse2EndSigFile = "";
		dateStartCount = "UNDEF";
		dateFinalCount = "UNDEF";
	}
	
	public void startOfDay(String nurse1, String nurse1Sig, String nurse2, String nurse2Sig, File baseDir) {
		startNurse1 = nurse1;
		nurse1StartSigFile = nurse1Sig;
		startNurse2 = nurse2;
		nurse2StartSigFile = nurse2Sig;
		dayStarted = true;
		dayFinished = false;
		todayDir = new File(baseDir, dateStartCount);
		dateFinalCount = "UNDEF";
	}
	
	public void startOfDay(String nurseLst, File baseDir) {
		if ((nurseLst.equals("")) || (nurseLst.equals(";;;;"))) {
			startNurse1 = "";
			nurse1StartSigFile = "";
			startNurse2 = "";
			nurse2StartSigFile = "";
			dayStarted = false;
		} else {
			String nurses[] = nurseLst.split(";");
			startNurse1 = nurses[0];
			nurse1StartSigFile = nurses[1];
			startNurse2 = nurses[2];
			nurse2StartSigFile = nurses[3];
			dateStartCount = nurses[4]; 
			if (! dateStartCount.equals("UNDEF")) {
				dayStarted = true;
				todayDir = new File(baseDir, dateStartCount);
			}
		}
	}
	
	public String setUpStorage(String date, File baseDir) {
		String errorMsg = null;
		Boolean isCancelled = false;
		
		if ((date != null) && (! date.equals("UNDEF"))) {
			dateStartCount = date;
			todayDir = new File(baseDir, dateStartCount);
		}
		
		if (todayDir != null) {
			if (! todayDir.exists()) {
				if (!todayDir.mkdirs()) {
					errorMsg = "Unable to create directory "+todayDir.getPath();
					isCancelled = true;
				}
			}
			if (! isCancelled){
				File noMedia = new File(todayDir,".nomedia");
				if (! noMedia.exists()) {
					try {
						noMedia.createNewFile();
					} catch(IOException e) {
						errorMsg = "Unable to create .nomedia file in "+todayDir.getPath()+".\n"+e.toString();
			        }
				}
			}
		} else {
			errorMsg = "Report Directory not set.";
		}
		
		return errorMsg;
	}
	
	public void endOfDay(String nurse1, String nurse1Sig, String nurse2, String nurse2Sig) {
		endNurse1 = nurse1;
		nurse1EndSigFile = nurse1Sig;
		endNurse2 = nurse2;
		nurse2EndSigFile = nurse2Sig;
		dateFinalCount = dateStartCount;
		dayFinished = true;
	}
	
	public void endOfDay(String nurseLst) {
		if ((nurseLst.equals("")) || (nurseLst.equals(";;;;"))) {
			endNurse1 = "";
			nurse1EndSigFile = "";
			endNurse2 = "";
			nurse2EndSigFile = "";
			dayFinished = false;
		} else {
			String nurses[] = nurseLst.split(";");
			endNurse1 = nurses[0];
			nurse1EndSigFile = nurses[1];
			endNurse2 = nurses[2];
			nurse2EndSigFile = nurses[3];
			dateFinalCount = nurses[4];
			if (! dateFinalCount.equals("UNDEF")) {
				dayFinished = true;
			}
		}
	}
	
	public String startSaveString() {
		return startNurse1 + ";" + nurse1StartSigFile + ";" + startNurse2 + ";" + nurse2StartSigFile + ";" + dateStartCount;
	}
	
	public String endSaveString() {
		return endNurse1 + ";" + nurse1EndSigFile + ";" + endNurse2 + ";" + nurse2EndSigFile + ";" + dateFinalCount;
	}
	
	public String startNurse1() {
		return startNurse1;
	}
	
	public String startNurse1Sig() {
		return nurse1StartSigFile;
	}
	
	public String startNurse2() {
		return startNurse2;
	}
	
	public String startNurse2Sig() {
		return nurse2StartSigFile;
	}
	
	public String endNurse1() {
		return endNurse1;
	}
	
	public String endNurse1Sig() {
		return nurse1EndSigFile;
	}
	
	public String endNurse2() {
		return endNurse2;
	}
	
	public String endNurse2Sig() {
		return nurse2EndSigFile;
	}
	
	public Boolean inDay() {
		return (dayStarted && (! dayFinished));
	}
	
	public Boolean getStartOfDay() {
		return dayStarted;
	}
	
	public Boolean getEndOfDay() {
		return dayFinished;
	}
	
	public Boolean stillSameDay(String date) {
		Boolean rtn = true;
		
		rtn = dateFinalCount.equals(date);
		
		return rtn;
	}
	
	public File storageDirectory() {
		return todayDir;
	}
	
	public String todayString() {
		return dateStartCount;
	}
	
	public Drug getDrug(int index) {
		return drugs.get(index);
	}
	
	public ArrayList<Drug> getList() {
		return drugs;
	}
	
	public String[] arrayList() {
		String rtn[] = null;
		
		if (drugs.size() > 0) {
			rtn = new String[drugs.size()];
			int i = 0;
			for (Drug drug : drugs) {
				rtn[i] = drug.label(Drug.NAME);
				i++;
			}
		} else {
			rtn = new String[0];
		}
		
		return rtn;
	}
	
	public String[] nameArrayList() {
		String rtn[] = null;
		
		if (drugs.size() > 0) {
			rtn = new String[drugs.size()];
			int i = 0;
			for (Drug drug : drugs) {
				rtn[i] = drug.name();
				i++;
			}
		} else {
			rtn = new String[0];
		}
		
		return rtn;
	}
	
	public String stringList() {
		String rtn = "";
		
		if (drugs.size() > 0) {
			for (Drug drug : drugs) {
				if (rtn.equals("")) {
					rtn = drug.name();
				} else {
					rtn += ";" + drug.name();
				}
			}
		}
		
		return rtn;
	}
	
	public void add(String name) {
		Drug newEntry = new Drug(name);
		
		drugs.add(newEntry);
		sort();
	}
	
	public void add(String name, Boolean patNeed) {
		Drug newEntry = new Drug(name);
		if (! patNeed) {
			newEntry.patientNotNeeded();
		}
		if (inDay()) {
			newEntry.setStartQty(0);
			newEntry.setFinalQty(0);
			newEntry.setSavedQty(0);
		} else {
			newEntry.setStartQty(Transaction.UNDEF);
			newEntry.setFinalQty(Transaction.UNDEF);
			newEntry.setSavedQty(Transaction.UNDEF);
		}
		
		drugs.add(newEntry);
		sort();
	}
	
	public void delete(int posn) { 
		if ((drugs.size() > 0) && (posn < drugs.size())) {
			drugs.remove(posn);
		}
	}
	
	public void update(int posn, String newName, String externDir, Boolean chk) {
		if ((drugs.size() > 0) && (posn < drugs.size())) {
			if (! drugs.get(posn).name().equals(newName)) {
				drugs.get(posn).updateName(newName, externDir);
			}
			drugs.get(posn).setPatientNeeded(chk);
		}
	}
	
	public void sort() {
		Collections.sort(drugs, Drug.drugName);
	}
	
	public void clearData() {
		
		if (drugs.size() > 0) {
			for (Drug drug : drugs) {
				drug.setStartQty(Transaction.UNDEF);
				drug.setFinalQty(Transaction.UNDEF);
				drug.setSavedQty(Transaction.UNDEF);
				drug.clearTransactions();
			}
		}
		if (todayDir != null) {
			File[] files = todayDir.listFiles();
			for (File file : files) {
				if ((file.exists()) && (file.canWrite())) {
					file.delete();
				}
			}
			todayDir.delete();
		}
		todayDir = null;
		startNurse1 = "";
		nurse1StartSigFile = "";
		startNurse2 = "";
		nurse2StartSigFile = "";
		endNurse1 = "";
		nurse1EndSigFile = "";
		endNurse2 = "";
		nurse2EndSigFile = "";
		dateStartCount = "UNDEF";
		dateFinalCount = "UNDEF";
		dayStarted = false;
		dayFinished = false;
	}

}
