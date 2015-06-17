package com.ving.narcoticinventory;

import java.util.ArrayList;

public class CountObject {
	
	private ArrayList<String> objTitle;
	private ArrayList<Integer> qty;
	private ArrayList<Boolean> override;
	private ArrayList<String> overrideComment;
	private ArrayList<Boolean> valueSet;
	private ArrayList<Integer> color;
	
	public CountObject () {
		objTitle = new ArrayList<String>();
		qty = new ArrayList<Integer>();
		override = new ArrayList<Boolean>();
		overrideComment = new ArrayList<String>();
		valueSet = new ArrayList<Boolean>();
		color = new ArrayList<Integer>();
	}
	
	public void add(String title) {
		objTitle.add(title);
		qty.add(Transaction.UNDEF);
		override.add(false);
		overrideComment.add(null);
		valueSet.add(false);
		color.add(null);
	}
	
	public int size() {
		return objTitle.size();
	}
	
	public ArrayList<String> allTitles() {
		return objTitle;
	}
	
	public Boolean allSet() {
		Boolean rtn = false;
		
		if (valueSet.size() > 0) {
			rtn = true;
		}
		for (Boolean val : valueSet) {
			rtn = rtn && val;
		}
		
		return rtn;
	}
	
	public Boolean notAllMatch(MyApplication myApp) {
		Boolean rtn = false;
		
		for (int i=0;i<qty.size();i++) {
			if ((qty.get(i) != myApp.getDrug(i).qty()) && (!override.get(i))) {
				rtn = true;
			}
		}
		
		return rtn;
	}
	
	public void setAllColor(int newColor) {
		for (int i=0; i<color.size(); i++) {
			color.set(i, newColor);
		}
	}
	
	public void setColor(int posn, int newColor) {
		if (posn <= objTitle.size()) {
			color.set(posn, newColor);
		}
	}
	
	public void saveStartCounts(MyApplication myApp) {
		for (int i=0;i<qty.size();i++) {
			myApp.getDrug(i).setStartQty(qty.get(i));
			if (override.get(i)) {
				myApp.getDrug(i).setOverride(Drug.START, overrideComment.get(i));
			}
		}
	}
	
	public void saveFinalCounts(MyApplication myApp) {
		for (int i=0;i<qty.size();i++) {
			myApp.getDrug(i).setFinalQty(qty.get(i));
			if (override.get(i)) {
				myApp.getDrug(i).setOverride(Drug.FINAL, overrideComment.get(i));
			}
		}
	}
	
	public void setValues(int posn, String newTitle, int newQty) {
		if (posn <= objTitle.size()) {
			objTitle.set(posn, newTitle);
			qty.set(posn, newQty);
			override.set(posn, false);
			overrideComment.set(posn, null);
			valueSet.set(posn, true);
		}
	}
	
	public void setOverride(int posn, String comment) {
		if (posn <= objTitle.size()) {
			override.set(posn, true);
			overrideComment.set(posn, comment);
		}
	}
	
	public String title(int posn) {
		String rtn = null;
		if (posn <= objTitle.size()) {
			rtn = objTitle.get(posn);
		}
		return rtn;
	}
	
	public int quantity(int posn) {
		int rtn = Transaction.UNDEF;
		if (posn <= objTitle.size()) {
			rtn = qty.get(posn);
		}
		return rtn;
	}
	
	public Boolean override(int posn) {
		Boolean rtn = false;
		if (posn <= objTitle.size()) {
			rtn = override.get(posn);
		}
		return rtn;
	}
	
	public String comment(int posn) {
		String rtn = null;
		if (posn <= objTitle.size()) {
			rtn = overrideComment.get(posn);
		}
		return rtn;
	}
	
	public Boolean valueSet(int posn) {
		Boolean rtn = false;
		if (posn <= objTitle.size()) {
			rtn = valueSet.get(posn);
		}
		return rtn;
	}
	
	public Integer color(int posn) {
		Integer rtn = null;
		if (posn <= objTitle.size()) {
			rtn = color.get(posn);
		}
		return rtn;
	}

}
