package com.liviu.apps.iasianunta.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.liviu.apps.iasianunta.utils.Convertor;
import com.liviu.apps.iasianunta.utils.Utils;

public class Alert {
	// Constants
	private final String TAG = "Alert";
	
	
	// Data
	private int mId;
	private int mUserId;
	private int mCatId;
	private int mCityId;
	private int mTotalAdsSinceLastCheck;
	private long mAddedDate;
	private long mLastCheckedDate;
	private ArrayList<Filter> mFilters;
	private ArrayList<Ad> mAds;
	private String mTitle;
	private String mAddedDateFormatted;
	private String mLastChekedDateFormatted;
	private String mCatName;	
	
	public Alert() {
		mId 				= -1;
		mUserId				= -1;
		mAddedDate			= 0;
		mLastCheckedDate	= 0;
		mTitle				= "";
		mCatId				= -1;
		mFilters			= new ArrayList<Filter>();
		mAddedDateFormatted			= "";
		mLastChekedDateFormatted 	= "";
		mTotalAdsSinceLastCheck		= 0;
		mAds				= new ArrayList<Ad>();
		mCatName			= "";
		mCityId				= 1;
	}
	
	public Alert(JSONObject jAlert){
		try {
			mId 					 = jAlert.getInt("mId");
			mTitle					 = jAlert.getString("mTitle");
			mAddedDateFormatted 	 = jAlert.getString("mAddedDateFormatted");
			mLastChekedDateFormatted = jAlert.getString("mLastChekedDateFormatted");
			mLastCheckedDate		 = jAlert.getLong("mLastCheckedDate");
			mAddedDate				 = jAlert.getLong("mAddedDate");
			mUserId					 = jAlert.getInt("mUserId");		
			mCatName				 = jAlert.getString("mCatName");
			mCityId					 = jAlert.getInt("mCityId");
		} catch (JSONException e) {
			e.printStackTrace();
			mId 				= -1;
			mUserId				= -1;
			mAddedDate			= 0;
			mLastCheckedDate	= 0;
			mTitle				= "";
			mCatId				= -1;
			mFilters			= new ArrayList<Filter>();
			mAddedDateFormatted			= "";
			mLastChekedDateFormatted 	= "";
			mTotalAdsSinceLastCheck		= 0;
			mCatName = "";
		}
		mAds 	 = new ArrayList<Ad>();
		mFilters = new ArrayList<Filter>();
	}
	
	public String getCategoryName(){
		return mCatName;
	}
	
	public Alert setCategoryName(String pCategoryName){
		mCatName = pCategoryName;
		return this;
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
	
	public Alert addFilter(Filter pFilter){
		mFilters.add(pFilter);
		return this;
	}
	
	public ArrayList<Filter> getFilters(){
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

	public JSONObject toJSON() {
		return Convertor.toJson(this, false);
	}

	public Alert addAd(Ad ad) {
		mAds.add(ad);
		return this;
	}
	
	public ArrayList<Ad> getAds(){
		return mAds;
	}

	public Alert setCityId(int pCityId) {
		mCityId = pCityId;
		return this;
	}
	
	public int getCityId(){
		return mCityId;
	}
		
}
