package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class Filter {
	// Constants
	private 		final String TAG = "Filter";
	public  static 	final String INVALID_TYPE = "invalid_type";
	
	// Data
	protected String mType = INVALID_TYPE;
	
	public Filter(String pType) {
		mType = pType;
	}	
	
	public String getType(){
		return mType;
	}
	
	public Filter setType(String pType){
		mType = pType;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}
