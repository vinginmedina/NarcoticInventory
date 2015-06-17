package com.ving.narcoticinventory;

import java.util.ArrayList;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TransactionList {
	private ArrayList<Transaction> transactionList;

	public TransactionList() {
		transactionList = new ArrayList<Transaction>();
	}
	
	public int size() {
		return transactionList.size();
	}
	
	public void add(int act, int amt, String p1, String pat) {
		Transaction trans = new Transaction(act, amt, p1, pat);
		transactionList.add(trans);
	}
	
	public void add(int act, int amt, String p1, String p2, String orRm) {
		Transaction trans = new Transaction(act, amt, p1, p2, orRm);
		transactionList.add(trans);
	}
	
	public void add(int amt, String p1, String p2, String sig1, String sig2) {
		Transaction trans = new Transaction(amt, p1, p2, sig1, sig2);
		transactionList.add(trans);
	}
	
	public void add(String reason, int amt, String p1, String p2, String sig1, String sig2) {
		Transaction trans = new Transaction(reason, amt, p1, p2, sig1, sig2);
		transactionList.add(trans);
	}
	
	public void add(String amt, String pat, String p1, String p2, String sig1, String sig2) {
		Transaction trans = new Transaction(amt, pat, p1, p2, sig1, sig2);
		transactionList.add(trans);
	}
	
	public void add(Transaction trans) {
		transactionList.add(trans);
	}
	
	public Transaction get(int id) {
		return transactionList.get(id);
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactionList;
	}
	
	public int calcAmt(int qty) {
		int rtn = qty;
		for (Transaction trans : transactionList) {
			if (! trans.inError()) {
				if ((trans.getAction() == Transaction.REMOVED) ||
						(trans.getAction() == Transaction.REMSTOCK)) {
					rtn -= trans.getAmount();
				} else if ((trans.getAction() == Transaction.REPLACED) ||
						(trans.getAction() == Transaction.ADDSTOCK)) {
					rtn += trans.getAmount();
				}
				trans.setCurrent(rtn);
			} else {
				trans.setCurrent(Transaction.UNDEF);
			}
		}
		
		return rtn;
	}
	
	public String saveString() {
		String rtn = "";
		for (Transaction trans : transactionList) {
			rtn += trans.saveString();
		}
		
		return rtn;
	}
	
	public void restoreData(String data) {
		Transaction trans = null;
		String dataArray[] = data.split("\n");
		for (String item : dataArray) {
			trans = new Transaction(item);
			transactionList.add(trans);
		}
	}
}
