package com.ving.narcoticinventory;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StaffSpinnerData {
	private SpinnerData items[];
	private Boolean itemsReady;

	public StaffSpinnerData() {
		items = new SpinnerData[1];
		items[0] = new SpinnerData("");
		itemsReady = false;
	}

	public StaffSpinnerData(String data) {
		String dataArray[] = data.split(";");
		Arrays.sort(dataArray);
		items = null;

		if (dataArray.length > 0) {
			items = new SpinnerData[dataArray.length + 1];
			items[0] = new SpinnerData("");
			for (int i = 0; i < dataArray.length; i++) {
				items[i+1] = new SpinnerData(dataArray[i]);
			}
			itemsReady = true;
		}
	}
	
	public void addItem(String newItem) {
		SpinnerData buf[] = null;
		int i;
		
		if ((items != null) && (items.length > 0)) {
			buf = new SpinnerData[items.length + 1];
			for (i=0; i < items.length; i++) {
				buf[i] = items[i];
			}
			buf[i] = new SpinnerData(newItem);
			items = buf;
			sort();
		} else {
			items = new SpinnerData[1];
			items[0] = new SpinnerData(newItem);
		}
		itemsReady = true;
	}
	
	public void delItem(String item) {
		SpinnerData buf[] = null;
		int i, j, ptr;
		
		if ((items != null) && (items.length > 0)) {
			ptr = -1;
			for (i=0;i<items.length;i++) {
				if (items[i].matchText(item)) {
					ptr = i;
					i = items.length;
				}
			}
			if (ptr >= 0) {
				buf = new SpinnerData[items.length-1];
				j = 0;
				for (i=0;i<ptr;i++) {
					buf[j] = items[i];
					j++;
				}
				for (i=ptr+1;i<items.length;i++) {
					buf[j] = items[i];
					j++;
				}
				items = buf;
			}
		}
	}
	
	public void delItem(int posn) {
		SpinnerData buf[] = null;
		int i, j, ptr;
		
		if ((posn >= 0) && (posn < items.length-1)) {
			buf = new SpinnerData[items.length-1];
			// The first one is the blank entry
			buf[0] = items[0];
			j = 1;
			for (i=1;i<=posn;i++) {
				buf[j] = items[i];
				j++;
			}
			for (i=posn+2;i<items.length;i++) {
				buf[j] = items[i];
				j++;
			}
			items = buf;
		}
	}
	
	public void updateItem(int posn, String newValue) {
		items[posn+1].setSpinnerText(newValue);
		sort();
	}
	
	public void sort() {
		
		if ((items != null) && (items.length > 0)) {
			String buf[] = new String[items.length-1];
			for (int i=1;i < items.length; i++) {
				buf[i-1] = items[i].getSpinnerText();
			}
			Arrays.sort(buf);
			for (int i=0;i < items.length-1; i++) {
				items[i+1].setSpinnerText(buf[i]);
			}
		}
	}

	public Boolean dataReady() {
		return itemsReady;
	}

	public SpinnerData[] getItems() {
		return items;
	}
	
	public String[] getStrings() {
		String buf[] = null;
		
		if ((items != null) && (items.length > 0)) {
			buf = new String[items.length-1];
			for (int i=1;i < items.length;i++) {
				buf[i-1] = items[i].getSpinnerText();
			}
		} else {
			buf = new String[0];
		}
		
		return buf;
	}
	
	public String toString() {
		String rtn = "";
		if ((items != null) && (items.length > 1)) {
			rtn = items[1].getSpinnerText();
			for (int i=2;i < items.length;i++) {
				rtn += ";" + items[i].getSpinnerText();
			}
		}
	    return rtn;
	}
}
