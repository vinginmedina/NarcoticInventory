package com.ving.narcoticinventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

public class Drug {
	public static final int UNDEF = -1;
	public static final int NAME = 1;
	public static final int START = 2;
	public static final int CURRENT = 3;
	public static final int FINAL = 4;
	public static final int STARTCURRENT = 5;
	public static final int STARTFINAL = 6;
	public static final int YESTERDAYFINAL = 7;
	public static final int YESTERDAYTODAY = 8;
	private String name;
	private String fileName;
	private int startQty;
	private int finalQty;
	private int savedFinalQty;
	private int qty;
	private Boolean patientNeeded;
	private Boolean startOverride;
	private String startOverrideComment;
	private Boolean endOverride;
	private String endOverrideComment;
	private TransactionList trans;
	
	public Drug (String name) {
		this.name = name;
		fileName = makeSaveName();
		startQty = UNDEF;
		finalQty = UNDEF;
		savedFinalQty = UNDEF;
		qty = UNDEF;
		patientNeeded = true;
		startOverride = false;
		startOverrideComment = null;
		endOverride = false;
		endOverrideComment = null;
		trans = new TransactionList();
	}
	
	public static Comparator<Drug> drugName = new Comparator<Drug>() {
		public int compare(Drug d1, Drug d2) {
			int rtn;
			rtn = d1.name.compareTo(d2.name);
			return rtn;
		}
	};
	
	public void setStartQty(int qty) {
		startQty = qty;
		this.qty = startQty;
		finalQty = UNDEF;
		trans = new TransactionList();
	}
	
	public void clearTransactions() {
		trans = new TransactionList();
		startOverride = false;
		startOverrideComment = null;
		endOverride = false;
		endOverrideComment = null;
	}
	
	public void setFinalQty(int qty) {
		finalQty = qty;
		savedFinalQty = qty;
		if (finalQty != UNDEF) {
			this.qty = qty;
		}
	}
	
	public void setSavedQty(int qty) {
		savedFinalQty = qty;
	}
	
	public void setOverride(int posn, String comment) {
		if (posn == START) {
			startOverride = true;
			startOverrideComment = comment;
		} else if (posn == FINAL) {
			endOverride = true;
			endOverrideComment = comment;
		}
	}
	
	public Boolean override(int posn) {
		Boolean rtn = null;
		if (posn == START) {
			rtn = startOverride;
		} else if (posn == FINAL) {
			rtn = endOverride;
		}
		return rtn;
	}
	
	public String overrideComment(int posn) {
		String rtn = null;
		if (posn == START) {
			rtn = startOverrideComment;
		} else if (posn == FINAL) {
			rtn = endOverrideComment;
		}
		return rtn;
	}
	
	public int finalQty() {
		return finalQty;
	}
	
	public int savedQty() {
		return savedFinalQty;
	}
	
	public int startQty() {
		return startQty;
	}
	
	public int qty() {
		return qty;
	}
	
	public void patientNotNeeded() {
		patientNeeded = false;
	}
	
	public void setPatientNeeded(Boolean chk) {
		patientNeeded = chk;
	}
	
	public Boolean patientNeeded() {
		return patientNeeded;
	}
	
	public void transaction(int act, int amt, String p1, String pat) {
		trans.add(act, amt, p1, pat);
	}
	
	public void transaction(int act, int amt, String p1, String p2, String orRm) {
		trans.add(act, amt, p1, p2, orRm);
	}
	
	public void transaction(int amt, String p1, String p2, String sig1, String sig2) {
		trans.add(amt, p1, p2, sig1, sig2);
	}
	
	public void transaction(String reason, int amt, String p1, String p2, String sig1, String sig2) {
		trans.add(reason, amt, p1, p2, sig1, sig2);
	}
	
	public void transaction(Transaction tran) {
		trans.add(tran);
	}
	
	public void transaction(String data) {
		trans.restoreData(data);
		calcQty();
	}
	
	public ArrayList<Transaction> getTransactionList() {
		return trans.getTransactions();
	}
	
	public Transaction getTransaction(int id) {
		return trans.get(id);
	}
	
	public int calcQty() {
		qty = trans.calcAmt(startQty);
		return qty;
	}
	
	public String name() {
		return this.name;
	}
	
	public String fileName() {
		return fileName;
	}
	
	public void updateName(String newName, String externDir) {
		this.name = newName;
		String newFileName = makeSaveName();
		File baseDir = new File(Environment.getExternalStorageDirectory().toString(),externDir);
		File transFile = new File(baseDir,fileName);
		if (transFile.exists()) {
			File newTransFile = new File(baseDir,newFileName);
			transFile.renameTo(newTransFile);
		}
		this.fileName = newFileName;
	}
	
	public String label(int type) {
		String rtn = "";
		switch (type) {
		case NAME:
			rtn = name;
			break;
		case START:
			rtn = name + " -- Start Quantity: " + prettyQty(startQty);
			break;
		case CURRENT:
			rtn = "Current Quantity: " + prettyQty(qty);
			break;
		case FINAL:
			rtn = name + " -- Final Quantity: " + prettyQty(finalQty);
			break;
		case STARTCURRENT:
			rtn = "Start Quantity: " + prettyQty(startQty) + " Current Quantity: " + prettyQty(qty);
			break;
		case STARTFINAL:
			rtn = "Start Quantity: " + prettyQty(startQty) + " Final Quantity: " + prettyQty(finalQty);
			break;
		case YESTERDAYFINAL:
			rtn = name + " -- Yesterday's Final Quantity: " + prettyQty(finalQty);
			break;
		case YESTERDAYTODAY:
			rtn = name + " -- Today's Start Quantity: " + prettyQty(startQty) +
				" Yesterday's Final Quantity: " + prettyQty(finalQty);
			break;
		}
		
		return rtn;
	}
	
	public String label(int type, int extra) {
		String rtn = "";
		switch (type) {
		case FINAL:
			rtn = name + " -- Final Quantity: " + prettyQty(extra);
			break;
		case YESTERDAYTODAY:
			rtn = name + " -- Today's Start Quantity: " + prettyQty(extra) +
			" Yesterday's Final Quantity: " + prettyQty(finalQty);
			break;
		}
		
		return rtn;
	}
	
	@Override
	public String toString() {
		return label(STARTCURRENT);
	}
	
	public static String prettyQty(int value) {
		String rtn;
		
		if (value == UNDEF) {
			rtn = "N/A";
		} else {
			rtn = String.valueOf(value);
		}
		return rtn;
	}
	
	public ArrayList<Transaction> transactionList() {
		return trans.getTransactions();
	}
	
	public String saveTransactions() {
		return trans.saveString();
	}
	
	private String makeSaveName() {
		char fileSep = File.separatorChar;
		char escape = '\\'; // ... or some other legal char.
		String fileName = name + "_Transactions";
		int len = fileName.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
		    char ch = fileName.charAt(i);
		    if (ch < ' ' || ch >= 0x7F || ch == fileSep
		        || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
		        || ch == escape) {
//		        sb.append(escape);
		        if (ch < 0x10) {
		            sb.append('0');
		        }
		        sb.append(Integer.toHexString(ch));
		    } else {
		        sb.append(ch);
		    }
		}
		Log.v("Drug","makeSaveName -- "+fileName+" becomes "+sb.toString());
		return sb.toString();
	}
 
}
