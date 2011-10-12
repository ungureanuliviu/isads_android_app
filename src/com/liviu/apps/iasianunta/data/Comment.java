package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class Comment {
	// Constants
	private final String TAG = "Comment";
	
	
	// Data
	private String 	mTitle;
	private String 	mContent;
	private String 	mAuthor;
	private String  mFormattedDate;
	private long    mDate;
	private int	   	mId;
	private int     mAdId;
	private int 	mAuthorId;
	private int		mRating;
	
	public Comment() {
		
	}
	
	public int getAdId(){
		return mAdId;
	}
	
	public Comment setAdId(int pAdId){
		mAdId = pAdId;
		return this;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public Comment setTitle(String pTitle){
		mTitle = pTitle;
		return this;
	}
	
	public String getContent(){
		return mContent;
	}
	
	public Comment setContent(String pContent){
		mContent = pContent;
		return this;
	}
	
	public String getAuthor(){
		return mAuthor;
	}
	
	public Comment setAuthor(String pAuthor){
		mAuthor = pAuthor;
		return this;
	}
	
	public long getDate(){
		return mDate;		
	}
	
	public Comment setDate(long pDate){
		mDate = pDate;
		return this;
	}
	
	public String getFormattedDate(){
		return mFormattedDate;
	}
	
	public Comment setFormattedDate(String pFormattedDate){
		mFormattedDate = pFormattedDate;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public Comment setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getAuthorId(){
		return mAuthorId;
	}
	
	public Comment setAuthorId(int pAuthorId){
		mAuthorId = pAuthorId;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

	public Comment setRating(int pRating) {
		mRating = pRating;
		return this;
	}
	
	public int getRating(){
		return mRating;
	}
}
