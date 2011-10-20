package com.liviu.apps.iasianunta.data;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.utils.Convertor;
import com.liviu.apps.iasianunta.utils.Utils;

public class Alert {
	// Constants
	private final String TAG = "Alert";
	
	
	// Data
	private int mId;
	private int mUserId;
	private int mCatId;
	private int mTotalAdsSinceLastCheck;
	private long mAddedDate;
	private long mLastCheckedDate;
	private ArrayList<String> mFilters;
	private String mTitle;
	private String mAddedDateFormatted;
	private String mLastChekedDateFormatted;
	
	public Alert() {
		mId 				= -1;
		mUserId				= -1;
		mAddedDate			= 0;
		mLastCheckedDate	= 0;
		mTitle				= "";
		mCatId				= -1;
		mFilters			= new ArrayList<String>();
		mAddedDateFormatted			= "";
		mLastChekedDateFormatted 	= "";
		mTotalAdsSinceLastCheck		= 0;
	}
	
	public Alert setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public Alert setUserId(int pUserId){
		mUserId = pUserId;
		return this;
	}
	
	public int getUserId(){
		return mUserId;
	}
	
	public Alert setTitle(String pTitle){
		mTitle = pTitle;
		return this;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public Alert setAddedDate(long pAddedDate){
		mAddedDate = pAddedDate;
		mAddedDateFormatted = Utils.formatDate(pAddedDate, "E, dd MMM yyyy HH:mm");
		return this;
	}
	
	public long getAddedDate(){
		return mAddedDate;
	}
	
	public String getFormattedAddedDate(){
		return mAddedDateFormatted;
	}
	
	public Alert setLastCheckedDate(long pLastCheckedDate){
		mLastCheckedDate = pLastCheckedDate;
		mLastChekedDateFormatted	= Utils.formatDate(pLastCheckedDate, "E, dd MMM yyyy HH:mm");
		return this;
	}
	
	public long getLastCheckedDate(){
		return mLastCheckedDate;
	}
	
	public String getFormattedLastCheckedDate(){
		return mLastChekedDateFormatted;
	}
	
	public Alert addFilter(String pFilter){
		mFilters.add(pFilter);
		return this;
	}
	
	public ArrayList<String> getFilters(){
		return mFilters;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

	public Alert setCategoryId(int pCatId) {
		mCatId = pCatId;
		return this;
	}
	
	public int getCategoryId(){
		return mCatId;
	}

	public Alert setTotalAdsSinceLastCheck(int pTotal) {
		mTotalAdsSinceLastCheck = pTotal;
		return this;
	}
	
	public int  getTotalAdsSinceLastCheck() {
		return mTotalAdsSinceLastCheck;
	}
}
