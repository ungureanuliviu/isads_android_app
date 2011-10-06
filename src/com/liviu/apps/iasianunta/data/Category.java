package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class Category {

	// Constants
	private final String TAG = "Category";
	
	// Data
	private String mTitle;
	
	public Category(String pTitle) {
		mTitle = pTitle;
	}
	
	public Category setTitle(String pTitle){
		mTitle = pTitle;
		return this;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

}
