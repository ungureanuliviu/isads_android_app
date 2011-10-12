package com.liviu.apps.iasianunta.data;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.liviu.apps.iasianunta.utils.Convertor;
import com.liviu.apps.iasianunta.utils.Utils;


public class Ad {
	
	// Constants
	private final String TAG = "Ad";
	public static final String SOURCE_ANDROID = "android";
	
	// Data
	private int mId;
	private int mCategoryId;
	private int mViewsCount;
	private int mTotalComments;
	private int mUserId;	
	private double mPrice;
	private String mAuthor;
	private String mCategoryName;
	private String mTitle;
	private String mContent;
	private String mPhone;
	private String mEmail;
	private String mAddress;
	private String mSource;	
	private String mFormattedDate;
	private long   mDate;		
	private ArrayList<Comment> mComments;
	private ArrayList<AdImage> mImages;
	
	public Ad() {
		mId 			= -1;
		mCategoryId 	= -1;
		mViewsCount 	= 0;
		mPrice 			= 0.00;
		mTitle 			= "";
		mContent 		= "";
		mPhone 			= "";
		mEmail 			= "";
		mAddress 		= "";
		mSource 		= "";
		mDate 			= Utils.now();
		mImages 		= new ArrayList<AdImage>();
		mComments 		= new ArrayList<Comment>();
		mTotalComments 	= 0;
		mUserId 		= -1;
		mAuthor 		= "";
		mCategoryId 	= -1;
		mFormattedDate 	= "";
	}
	
	public Ad(JSONObject json) throws JSONException {
		if(null != json){
			mPhone 			= json.getString("mPhone");
			mCategoryId 	= json.getInt("mCategoryId");
			mPrice 			= json.getDouble("mPrice");
			mFormattedDate 	= json.getString("mFormattedDate");
			mDate 			= json.getLong("mDate");
			mCategoryName 	= json.getString("mCategoryName");
			mId 			= json.getInt("mId");
			mViewsCount 	= json.getInt("mViewsCount");
			mSource 		= json.getString("mSource");
			mTotalComments 	= json.getInt("mTotalComments");
			mTitle 			= json.getString("mTitle");
			mAddress 		= json.getString("mAddress");
			mContent 		= json.getString("mContent");
			mUserId 		= json.getInt("mUserId");
			mEmail 			= json.getString("mEmail");
			mImages 		= new ArrayList<AdImage>();
			mComments 		= new ArrayList<Comment>();
		}
	}

	public Ad setFormattedDate(String pFormattedDate){
		mFormattedDate = pFormattedDate;
		return this;
	}
	
	public String getFormattedDate(){
		return mFormattedDate;
	}
	
	public Ad setAuthor(String pAuthor){
		mAuthor = pAuthor;
		return this;
	}
	
	public String getAuthor(){
		return mAuthor;
	}
	
	public Ad setCategoryName(String pCategoryName){
		mCategoryName = pCategoryName;
		return this;
	}
	
	public String getCategoryName(){
		return mCategoryName;
	}
	
	public ArrayList<AdImage> getImages(){
		return mImages;
	}
	
	public Ad addImage(AdImage pAdImage){
		mImages.add(pAdImage);
		return this;
	}

	public int getId(){
		return mId;
	}
	
	public Ad setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getCategoryId(){
		return mCategoryId;
	}
	
	public Ad setCategoryId(int pCategoryId) {
		mCategoryId = pCategoryId;
		return this;
	}
	
	public int getViewsCount(){
		return mViewsCount;
	}
	
	public Ad setViewsCount(int pViewsCount){
		mViewsCount = pViewsCount;
		return this;
	}
	
	public double getPrice(){
		return mPrice;
	}
	
	public Ad setPrice(double pPrice){
		mPrice = pPrice;
		return this;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public Ad setTitle(String pTitle){
		mTitle = pTitle;
		return this;
	}
	
	public String getContent(){
		return mContent;
	}
	
	public Ad setContent(String pContent){
		mContent = pContent;
		return this;
	}
	
	public String getPhone(){
		return mPhone;
	}
	
	public Ad setPhone(String pPhone){
		mPhone = pPhone;
		return this;
	}
	
	public String getEmail(){
		return mEmail;
	}
	
	public Ad setEmail(String pEmail){
		mEmail = pEmail;
		return this;
	}
	
	public String getAddress(){
		return mAddress;
	}
	
	public Ad setAddress(String pAddress){
		mAddress = pAddress;
		return this;
	}
	
	public String getSource(){
		return mSource;
	}
	
	public Ad setSource(String pSource){
		mSource = pSource;
		return this;
	}
	
	public long getDate(){
		return mDate;
	}
	
	public Ad setDate(long pDate){
		mDate = pDate;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

	public AdImage removeImageByUri(Uri pUri) {
		if(mImages.isEmpty())
			return null;
		if(null == pUri)
			return null;
		
		for(int i = 0; i < mImages.size(); i++){			
			if(pUri.equals(mImages.get(i).getUri())){
				return mImages.remove(i);
			}			
		}
		return null;
	}

	public Ad setTotalComments(int pTotalComments) {
		mTotalComments = pTotalComments;
		return this;
	}
	
	public  int getTotalComments() {
		return mTotalComments;
	}

	public Ad setUserId(int pUserId) {
		mUserId = pUserId;
		return this;
	}
	
	public int getUserId(){
		return mUserId;
	}

	public ArrayList<Comment> getComments() {
		return mComments;
	}
	
	public Ad adComment(Comment pComment){
		mComments.add(pComment);
		return this;
	}

	public JSONObject toJson() {
		return Convertor.toJson(this, false);
	}
}
