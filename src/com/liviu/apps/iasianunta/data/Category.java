package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class Category {

	// Constants
	private final String TAG = "Category";
	
	// Data
	private String mName;
	private int    mId;
	
	public Category(int pId, String pName) {
		mName = pName;
		mId   = pId;
	}
	
	public Category setName(String pName){
		mName = pName;
		return this;
	}
	
	public String getName() {
		return mName;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

	public int getId() {
		return mId;
	}
	
	public Category setId(int pId){
		mId = pId;
		return this;
	}

}
