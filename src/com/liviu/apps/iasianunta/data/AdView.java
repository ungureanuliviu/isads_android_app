package com.liviu.apps.iasianunta.data;

import android.graphics.Bitmap;

public class AdView {

	// Constants
	private final String TAG = "AdView";
	
	// Data
	private Ad 		mAd;
	private Bitmap 	mBitmap;
	
	
	public AdView(Ad pAd) {
		mAd = pAd;
	}
	
	public AdView setAd(Ad pAd){
		mAd = pAd;
		return this;
	}
	
	public Ad getAd(){
		return mAd;
	}

	public AdView setImage(Bitmap pImg) {
		mBitmap = pImg;
		return this;
	}
	
	public Bitmap getImage(){
		return mBitmap;
	}		
}
