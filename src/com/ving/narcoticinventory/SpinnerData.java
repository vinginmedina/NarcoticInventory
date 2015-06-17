package com.ving.narcoticinventory;

public class SpinnerData {
	private String spinnerText = null;
	private String value = null;

	public SpinnerData(String spinnerText) {
		this.spinnerText = spinnerText;
		this.value = spinnerText;
	}
	
	public SpinnerData(String spinnerText, String value) {
		this.spinnerText = spinnerText;
		this.value = value;
	}
	
	public void setSpinnerText(String spinnerText) {
		this.spinnerText = spinnerText;
		this.value = spinnerText;
	}
	
	public void setSpinnerText(String spinnerText, String value) {
		this.spinnerText = spinnerText;
		this.value = value;
	}
	
	public Boolean matchText(String txt) {
		return spinnerText.equals(txt);
	}

	public String getSpinnerText() {
		return spinnerText;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return spinnerText;
	}

}
