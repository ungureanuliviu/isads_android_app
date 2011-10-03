package com.liviu.apps.iasianunta.data;

import java.util.ArrayList;

import org.json.JSONException;

import android.net.Uri;

import com.liviu.apps.iasianunta.utils.Convertor;
import com.liviu.apps.iasianunta.utils.Utils;


public class Ad {
	// Constants
	private final String TAG = "Ad";
		
	// Data
	private int mId;
	private int mCategoryId;
	private int mViewsCount;
	private double mPrice;
	private String mTitle;
	private String mContent;
	private String mPhone;
	private String mEmail;
	private String mAddress;
	private String mSource;	
	private long   mDate;
	private ArrayList<AdImage> mImages;
	
	public Ad() {
		mId = -1;
		mCategoryId = -1;
		mViewsCount = 0;
		mPrice = 0.00;
		mTitle = "";
		mContent = "";
		mPhone = "";
		mEmail = "";
		mAddress = "";
		mSource = "";
		mDate = Utils.now();
		mImages = new ArrayList<AdImage>();
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

	public AdImage removeImageByName(String imgName) {
		if(mImages.isEmpty())
			return null;
		if(null == imgName)
			return null;
		
		for(int i = 0; i < mImages.size(); i++){
			try {
				if(imgName.equals(mImages.get(i).getServerFileInfo().get("name"))){
					return mImages.remove(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
