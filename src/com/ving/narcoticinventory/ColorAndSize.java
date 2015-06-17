package com.ving.narcoticinventory;

import java.io.BufferedWriter;
import java.io.IOException;

import android.content.SharedPreferences;
import android.graphics.Color;

public class ColorAndSize {
	public static final int UNDEF = -1;
	public static final int FACILITY_NAME = 0;
	public static final int DRUG_HEADING_ROW = 1;
	public static final int TRANSACTION_DETAIL_ROW = 2;
	public static final int TRANSACTION_ERROR_ROW = 3;
	private static final int FACILITY_SIZE = 60;
	private static final int DRUG_SIZE = 30;
	private static final int TRANS_SIZE = 20;
	private static final int TRANS_ERROR_SIZE = 20;
	private int[] textSize;
	private int[] textColor;
	
	public ColorAndSize() {
		textSize = new int[4];
		textColor = new int[4];
		textSize[FACILITY_NAME] = FACILITY_SIZE;
		textSize[DRUG_HEADING_ROW] = DRUG_SIZE;
		textSize[TRANSACTION_DETAIL_ROW] = TRANS_SIZE;
		textSize[TRANSACTION_ERROR_ROW] = TRANS_ERROR_SIZE;
		textColor[FACILITY_NAME] = Color.BLACK;
		textColor[DRUG_HEADING_ROW] = Color.BLACK;
		textColor[TRANSACTION_DETAIL_ROW] = Color.BLACK;
		textColor[TRANSACTION_ERROR_ROW] = Color.RED;
	}
	
	public void setValues(SharedPreferences settings) {
		int value;
		value = settings.getInt("FNTS", UNDEF);
		if (value != UNDEF) {
			textSize[FACILITY_NAME] = value;
			value = settings.getInt("FNTC", Color.BLACK);
			textColor[FACILITY_NAME] = value;
		}
		value = settings.getInt("DHTS", UNDEF);
		if (value != UNDEF) {
			textSize[DRUG_HEADING_ROW] = value;
			value = settings.getInt("DHTC", Color.BLACK);
			textColor[DRUG_HEADING_ROW] = value;
		}
		value = settings.getInt("TDTS", UNDEF);
		if (value != UNDEF) {
			textSize[TRANSACTION_DETAIL_ROW] = value;
			value = settings.getInt("TDTC", Color.BLACK);
			textColor[TRANSACTION_DETAIL_ROW] = value;
		}
		value = settings.getInt("TETS", UNDEF);
		if (value != UNDEF) {
			textSize[TRANSACTION_ERROR_ROW] = value;
			value = settings.getInt("TETC", Color.RED);
			textColor[TRANSACTION_ERROR_ROW] = value;
		}
	}
	
	public void setValues(String[] tagValue) {
		int value;
		if (tagValue[0].equals("FNTS")) {
			value = Integer.parseInt(tagValue[1]);
			textSize[FACILITY_NAME] = value;
		} else if (tagValue[0].equals("FNTC")) {
			value = Integer.parseInt(tagValue[1]);
			textColor[FACILITY_NAME] = value;
		} else if (tagValue[0].equals("DHTS")) {
			value = Integer.parseInt(tagValue[1]);
			textSize[DRUG_HEADING_ROW] = value;
		} else if (tagValue[0].equals("DHTC")) {
			value = Integer.parseInt(tagValue[1]);
			textColor[DRUG_HEADING_ROW] = value;
		} else if (tagValue[0].equals("TDTS")) {
			value = Integer.parseInt(tagValue[1]);
			textSize[TRANSACTION_DETAIL_ROW] = value;
		} else if (tagValue[0].equals("TDTC")) {
			value = Integer.parseInt(tagValue[1]);
			textColor[TRANSACTION_DETAIL_ROW] = value;
		} else if (tagValue[0].equals("TETS")) {
			value = Integer.parseInt(tagValue[1]);
			textSize[TRANSACTION_ERROR_ROW] = value;
		} else if (tagValue[0].equals("TETC")) {
			value = Integer.parseInt(tagValue[1]);
			textColor[TRANSACTION_ERROR_ROW] = value;
		}
	}
	
	public void saveValues(SharedPreferences.Editor editor) {
		editor.putInt("FNTS", textSize[FACILITY_NAME]);
		editor.putInt("FNTC", textColor[FACILITY_NAME]);
		editor.putInt("DHTS", textSize[DRUG_HEADING_ROW]);
		editor.putInt("DHTC", textColor[DRUG_HEADING_ROW]);
		editor.putInt("TDTS", textSize[TRANSACTION_DETAIL_ROW]);
		editor.putInt("TDTC", textColor[TRANSACTION_DETAIL_ROW]);
		editor.putInt("TETS", textSize[TRANSACTION_ERROR_ROW]);
		editor.putInt("TETC", textColor[TRANSACTION_ERROR_ROW]);
	}
	
	public void saveValues(BufferedWriter myWriter) throws IOException {
		myWriter.write("FNTS:"+textSize[FACILITY_NAME]+"\n");
		myWriter.write("FNTC:"+textColor[FACILITY_NAME]+"\n");
		myWriter.write("DHTS:"+textSize[DRUG_HEADING_ROW]+"\n");
		myWriter.write("DHTC:"+textColor[DRUG_HEADING_ROW]+"\n");
		myWriter.write("TDTS:"+textSize[TRANSACTION_DETAIL_ROW]+"\n");
		myWriter.write("TDTC:"+textColor[TRANSACTION_DETAIL_ROW]+"\n");
		myWriter.write("TETS:"+textSize[TRANSACTION_ERROR_ROW]+"\n");
		myWriter.write("TETC:"+textColor[TRANSACTION_ERROR_ROW]+"\n");
	}
	
	public void setColor(int type, int color) {
		if ((type >= FACILITY_NAME) && (type <= TRANSACTION_ERROR_ROW)) {
			textColor[type] = color;
		}
	}
	
	public void setTextSize(int type, int size) {
		if ((type >= FACILITY_NAME) && (type <= TRANSACTION_ERROR_ROW)) {
			textSize[type] = size;
		}
	}
	
	public int getColor(int type) {
		int rtn = Color.BLACK;
		if ((type >= FACILITY_NAME) && (type <= TRANSACTION_ERROR_ROW)) {
			rtn = textColor[type];
		}
		return rtn;
	}
	
	public int getTextSize(int type) {
		int rtn = UNDEF;
		if ((type >= FACILITY_NAME) && (type <= TRANSACTION_ERROR_ROW)) {
			rtn = textSize[type];
		}
		return rtn;
	}
	
	public int defaultSize(int type) {
		int rtn = UNDEF;
		switch (type) {
			case FACILITY_NAME:
				rtn = FACILITY_SIZE;
				break;
			case DRUG_HEADING_ROW:
				rtn = DRUG_SIZE;
				break;
			case TRANSACTION_DETAIL_ROW:
				rtn = TRANS_SIZE;
				break;
			case TRANSACTION_ERROR_ROW:
				rtn = TRANS_ERROR_SIZE;
				break;
		}
		return rtn;
	}
	
	public int defaultColor(int type) {
		int rtn = Color.BLACK;
		if (type == TRANSACTION_ERROR_ROW) {
			rtn = Color.RED;
		}
		return rtn;
	}

}
