package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class ContentFilter extends Filter{
	public static final String TYPE = "content";

	// Constants
	private final String TAG = "ContentFilter";
	
	// Data
	private String mContent;
	
	public ContentFilter(String pContent) {
		super("content");
		mContent = pContent;		
	}
	
	public String getContent(){
		return mContent;
	}
	
	public ContentFilter setContent(String pContent){
		mContent = pContent;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}
