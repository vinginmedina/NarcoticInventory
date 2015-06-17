package com.ving.narcoticinventory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.ParseException;
import android.text.format.DateFormat;
import android.util.Log;

public class Transaction {
	public static final int UNDEF = -1;
	public static final int DATE = 1;
	public static final int ACTION = 2;
	public static final int DATEACTION = 3;
	public static final int DETAIL = 4;
	public static final int REMOVED = 5;
	public static final int REPLACED = 6;
	public static final int ADDSTOCK = 7;
	public static final int REMSTOCK = 8;
	public static final int WASTE = 9;
	public static final int CURRENT = 10;
	public static final int USEDFOR = 11;
	public static final int RESP = 12;
	private int action;
	private int amount;
	private int current;
	private Date timestamp;
	private String patient;
	private String person1;
	private String person2;
	private String orRoom;
	private String wasteAmt;
	private String signatureFile1;
	private String signatureFile2;
	private Boolean error;
	
	public Transaction() {
		action = UNDEF;
		amount = 0;
		current = UNDEF;
		timestamp = new Date();
		patient = null;
		person1 = null;
		person2 = null;
		orRoom = null;
		wasteAmt = null;
		signatureFile1 = null;
		signatureFile2 = null;
		error = false;
	}
	
	public Transaction(int act, int amt, String p1, String pat) {
		action = act;
		amount = amt;
		timestamp = new Date();
		patient = pat;
		person1 = p1;
		person2 = null;
		orRoom = null;
		wasteAmt = null;
		error = false;
	}
	
	public Transaction(int act, int amt, String p1, String p2, String orRm) {
		action = act;
		amount = amt;
		timestamp = new Date();
		patient = null;
		person1 = p1;
		person2 = p2;
		orRoom = orRm;
		wasteAmt = null;
		error = false;
	}
	
	public Transaction(int amt, String p1, String p2, String file1, String file2) {
		action = ADDSTOCK;
		amount = amt;
		timestamp = new Date();
		person1 = p1;
		person2 = p2;
		patient = null;
		orRoom = null;
		wasteAmt = null;
		error = false;
		signatureFile1 = file1;
		signatureFile2 = file2;
	}
	
	public Transaction(String reason, int amt, String p1, String p2, String file1, String file2) {
		action = REMSTOCK;
		amount = amt;
		timestamp = new Date();
		person1 = p1;
		person2 = p2;
		patient = reason;
		orRoom = null;
		wasteAmt = null;
		error = false;
		signatureFile1 = file1;
		signatureFile2 = file2;
	}
	
	public Transaction(String amt, String pat, String p1, String p2, String file1, String file2) {
		action = WASTE;
		amount = UNDEF;
		timestamp = new Date();
		wasteAmt = amt;
		patient = pat;
		person1 = p1;
		person2 = p2;
		signatureFile1 = file1;
		signatureFile2 = file2;
		orRoom = null;
		error = false;
	}
	
	public Transaction(String amt, String reason, String pat, String p1, String p2) {
		action = WASTE;
		amount = UNDEF;
		timestamp = new Date();
		wasteAmt = amt + " (" + reason + ")";
		patient = pat;
		person1 = p1;
		person2 = p2;
		signatureFile1 = null;
		signatureFile2 = null;
		orRoom = null;
		error = false;
	}
	
	public Transaction(String data) {
		String values[] = data.split("\\|");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		timestamp = new Date();
		try {
			timestamp = sdf.parse(values[0]);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		action = Integer.parseInt(values[1]);
		if (action == WASTE) {
			wasteAmt = values[2];
			amount = UNDEF;
		} else {
			amount = Integer.parseInt(values[2]);
			wasteAmt = null;
		}
		patient = null;
		person1 = null;
		person2 = null;
		orRoom = null;
		signatureFile1 = null;
		signatureFile2 = null;
		error = false;
		if (! values[3].equals("")) {
			patient = values[3];
		}
		if (! values[4].equals("")) {
			person1 = values[4];
		}
		if (! values[5].equals("")) {
			person2 = values[5];
		}
		if (! values[6].equals("")) {
			orRoom = values[6];
		}
		if (! values[7].equals("")) {
			signatureFile1 = values[7];
		}
		if (! values[8].equals("")) {
			signatureFile2 = values[8];
		}
		if (values[9].equals("T")) {
			error = true;
		}
	}
	
	public void setSignatureFile(String file) {
		signatureFile1 = file;
	}
	
	public void setSignatureFile(int posn, String file) {
		if (posn == 1) {
			signatureFile1 = file;
		} else if (posn == 2) {
			signatureFile2 = file;
		}
	}
	
	public String signatureFile() {
		String rtn = "";
		if (signatureFile1 != null) {
			rtn = signatureFile1;
		}
		return rtn;
	}
	
	public String signatureFile(int posn) {
		String rtn = "";
		if ((posn == 1) && (signatureFile1 != null)) {
			rtn = signatureFile1;
		} else if ((posn == 2) && (signatureFile2 != null)) {
			rtn = signatureFile2;
		}
		return rtn;
	}
	
	public int getAction() {
		return action;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public Date timestamp() {
		return timestamp;
	}
	
	public void markError() {
		error = true;
	}
	
	public Boolean inError() {
		return error;
	}
	
	public void setCurrent(int value) {
		current = value;
	}
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		String rtn = sdf.format(timestamp);
		if ((action == REMOVED) || (action == REMSTOCK)) {
			rtn += " Removed  " + amount;
		} else if (action == ADDSTOCK) {
			rtn += " Added to Stock " + amount;
		} else {
			rtn += " Replaced " + amount;
		}
		if (orRoom == null) {
			rtn += " Nurse: " + person1 + " for Patient: ";
			if ((patient != null) && (!patient.equals(""))) {
				rtn += patient;
			} else {
				rtn += "N/A";
			}
		} else {
			rtn += " " + orRoom + " Nurse: " + person1 + " CRNA/Doctor: " + person2;
		}
		
		return rtn;
	}
	
	public String label(int type) {
		String rtn = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		switch (type) {
		case DATE:
			rtn = sdf.format(timestamp);
			break;
		case ACTION:
			if (action == REMOVED) {
				rtn = "Removed  " + amount;
			} else if (action == WASTE) {
				rtn = "Wasted " + wasteAmt;
			} else if (action == ADDSTOCK) {
				rtn = "Added to stock " + amount;
			} else if (action == REMSTOCK) {
				rtn = "Removed " + amount + " from stock: " + patient;
			} else {
				rtn = "Replaced " + amount;
			}
			break;
		case DATEACTION:
			rtn = sdf.format(timestamp) + " -- " + label(ACTION);
			break;
		case DETAIL:
			if ((action == ADDSTOCK) || (action == REMSTOCK) || (action == WASTE)) {
				rtn = "Nurse 1: " + person1 + "\nNurse 2: " + person2;
			} else if (orRoom == null) {
				rtn = "Nurse: " + person1 + "\nPatient: ";
				if (patient != null) {
					rtn += patient;
				} else {
					rtn += "N/A";
				}
			} else {
				rtn = orRoom + "\nNurse: " + person1 + "\nCRNA/Doctor: " + person2;
			}
			if ((action != WASTE) && (current != UNDEF)) {
				rtn += "\nRunning Quantity: " + String.valueOf(current);
			}
			break;
		case REMOVED:
			if ((action == REMOVED) || (action == REMSTOCK)) {
				rtn = String.valueOf(amount);
			} else {
				rtn = "";
			}
			break;
		case REPLACED:
			if ((action == REPLACED) || (action == ADDSTOCK)) {
				rtn = String.valueOf(amount);
			} else {
				rtn = "";
			}
			break;
		case WASTE:
			rtn = wasteAmt;
			break;
		case CURRENT:
			if ((action != WASTE) && (current != UNDEF)) {
				rtn = String.valueOf(current);
			} else {
				rtn = "";
			}
			break;
		case USEDFOR:
			if (action == ADDSTOCK) {
				rtn = "Add to stock";
			} else if (action == REMSTOCK) {
				rtn = "Remove from stock: " + patient;
			} else if (action == WASTE) {
				rtn = "Wasted " + wasteAmt + "; Patient: ";
				if (patient != null) {
					rtn += patient;
				} else {
					rtn += "N/A";
				}
			} else if (orRoom == null) {
				rtn = "Patient: ";
				if (patient != null) {
					rtn += patient;
				} else {
					rtn += "N/A";
				}
			} else {
				rtn = orRoom + ", CRNA/Doctor: " + person2;
			}
			break;
		case RESP:
			if ((action == ADDSTOCK) || (action == REMSTOCK) || (action == WASTE)) {
				rtn = person1 + ", " + person2;
			} else {
				rtn = person1;
			}
			break;
		}
		
		return rtn;
	}
	
	public String saveString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		String rtn = sdf.format(timestamp) + "|" + action + "|";
		if (action == WASTE) {
			rtn += wasteAmt + "|";
		} else {
			rtn += amount + "|";
		}
		if (patient != null) {
			rtn += patient;
		}
		rtn += "|";
		if (person1 != null) {
			rtn += person1;
		}
		rtn += "|";
		if (person2 != null) {
			rtn += person2;
		}
		rtn += "|";
		if (orRoom != null) {
			rtn += orRoom;
		}
		rtn += "|";
		if (signatureFile1 != null) {
			rtn += signatureFile1;
		}
		rtn += "|";
		if (signatureFile2 != null) {
			rtn += signatureFile2;
		}
		rtn += "|";
		if (error) {
			rtn += "T\n";
		} else {
			rtn += "F\n";
		}
		
		return rtn;
	}

}
