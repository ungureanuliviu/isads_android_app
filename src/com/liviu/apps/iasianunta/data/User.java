package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class User {
	// Constants
	private final String TAG = "User";
		
	// Data
	private int mId;
	private String mName;
	private String mPassword;
	private String mAuthName;
	private String mEmail;
	private String mActivationCode;	
	private boolean mIsActive;
	private boolean mIsLoggedIn;	
	private static User mInstance;
	
	
	private User() {
		mId = -1;
		mName = "unknown";
		mPassword = "";
		mEmail = "";
		mAuthName = "unknown";
		mActivationCode = "";
		mIsActive = false;
		mIsLoggedIn = false;		
	}
	
	public static User getInstance(){
		if(mInstance != null)
			return mInstance;
		else
			return (mInstance = new User());
	}
	
	public String getName(){
		return mName;
	}
	
	public User setName(String pName){
		mName = pName;
		return this;
	}
	
	public String getAuthName(){
		return mAuthName;
	}
	
	public User setAuthName(String pAuthName){
		mAuthName = pAuthName;
		return this;
	}
	
	public String getPassword(){
		return mPassword;
	}
	
	public User setPassword(String pPassword){
		mPassword = pPassword;
		return this;
	}
	
	public String getActivationCode(){
		return mActivationCode;
	}
	
	public User setActivationCode(String pActivationCode){
		mActivationCode = pActivationCode;
		return this;
	}
	
	public boolean isLoggedIn(){
		return mIsLoggedIn;
	}
	
	public User setLoginStatus(boolean pIsLoggedIn){
		mIsLoggedIn = pIsLoggedIn;
		return this;
	}
	
	public boolean isActive(){
		return mIsActive;
	}
	
	public User setActiveStatus(boolean pIsActive){
		mIsActive = pIsActive;
		return this;
	}
	
	public String toString(){
		return Convertor.toString(this);	  
	}

	public User setEmail(String pEmail) {
		mEmail = pEmail;
		return this;
	}
	
	public String getEmail(){
		return mEmail;
	}

	public User setId(int pId) {
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
	}

	public void logout() {
		mId = -1;
		mName = "unknown";
		mPassword = "";
		mEmail = "";
		mAuthName = "unknown";
		mActivationCode = "";
		mIsActive = false;
		mIsLoggedIn = false;
	}	
}
