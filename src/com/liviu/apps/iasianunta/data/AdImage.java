package com.liviu.apps.iasianunta.data;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.net.Uri;

public class AdImage {
	// Constants
	private final String TAG = "AdImage";
	
	// Data
	private Uri mUri;
	private JSONObject mServerFilInfo;
	private Bitmap	mBitmap;
	
	public AdImage(Uri pUri, JSONObject pServerInfo) {
		mUri 			= pUri;
		mServerFilInfo 	= pServerInfo;
		mBitmap			= null;
	}
	
	public Uri getUri(){
		return mUri;
	}
	
	public AdImage setUri(Uri pUri){
		mUri = pUri;
		return this;
	}
	
	public JSONObject getServerFileInfo(){
		return mServerFilInfo;
	}
	
	public AdImage setServerFileInfo(JSONObject pServerFileInfo){
		mServerFilInfo = pServerFileInfo;
		return this;
	}

	public AdImage setBitmap(Bitmap pBitmap) {
		mBitmap = pBitmap;
		return this;
	}
	
	public Bitmap getBitmap(){
		return mBitmap;
	}
}
