package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

import android.test.IsolatedContext;

public class AlertView {
	// Constants
	private final String TAG = "AlertView";
	
	// Data
	private Alert mAlert;
	private boolean mIsSelected;
	
	
	public AlertView(Alert pAlert) {
		mAlert = pAlert;
		mIsSelected = false;
	}
	
	public AlertView setSelectedValue(boolean pIsSelected){
		mIsSelected = pIsSelected;
		return this;
	}
	
	public boolean isSelected(){
		return mIsSelected;
	}
	
	public Alert getAlert(){
		return mAlert;
	}
	
	public AlertView setAlert(Alert pAlert){
		mAlert = pAlert;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}
