package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class City {

	// Constants
	private final String  TAG = "TAG";
	
	// Data
	private String mName;
	private int    mId;
	
	public City() {
		mName = "iasi";
		mId   = 1;
	}
	
	public City(int pId, String pName) {
		mId 	= pId;
		mName 	= pName;
	}

	public City setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public City setName(String pName){
		mName = pName;
		return this;
	}
	
	public String getName(){
		return mName;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}
