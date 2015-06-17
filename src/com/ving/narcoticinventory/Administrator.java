package com.ving.narcoticinventory;

import android.content.SharedPreferences;

public class Administrator {
	private String name;
	private String email;
	private String password;
	
	public Administrator() {
		name = null;
		password = null;
	}
	
	public String name() {
		return name;
	}
	
	public String email() {
		return email;
	}
	
	public Boolean verify(String pass) {
		Boolean rtn = false;
		
		if ((password != null) && (pass != null)) {
			rtn = password.equals(pass);
		}
		return rtn;
	}
	
	public Boolean setInitial(SharedPreferences settings) {
		String savedName = null;
		String savedPass = null;
		Boolean rtn = false;
		
		savedName = settings.getString("AdminName","");
		if ((savedName != null) && (!savedName.equals(""))) {
			savedPass = settings.getString("AdminPassword","");
			if ((savedPass != null) && (!savedPass.equals(""))) {
				name = savedName;
				email = settings.getString("AdminEMail", "");
				password = savedPass;
				rtn = true;
			}
		}
		
		return rtn;
	}
	
	public Boolean set(String name, String email, String pass, String vPass) {
		Boolean rtn = false;
		
		if ((name != null) && (!name.equals("") && (pass != null) && (!pass.equals("")))) {
			if ((this.name == null) && (password == null)) {
				this.name = name;
				this.email = email;
				password = pass;
				rtn = true;
			} else if (verify(vPass)) {
				this.name = name;
				this.email = email;
				password = pass;
				rtn = true;
			}
		}
		
		return rtn;
	}
	
	public void save(SharedPreferences.Editor editor) {
		editor.putString("AdminName",name);
		editor.putString("AdminPassword", password);
		editor.putString("AdminEMail", email);
	}

}
