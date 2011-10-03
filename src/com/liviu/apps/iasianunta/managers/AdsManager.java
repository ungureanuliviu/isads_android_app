package com.liviu.apps.iasianunta.managers;

import android.content.Context;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;

public class AdsManager {
	// Constants
	private final String TAG = "AdsManager";
	
	// Data
	private API 		mApi;
	private DBManager 	mDbManager;
	private Context 	mContext;
	
	public AdsManager(Context pContext) {
		mContext 	= pContext;
		mApi 		= API.getInstance();
		mDbManager 	= DBManager.getInstance(pContext);
	}
	
	public AdsManager addnewAd(Ad pNewAd){
		if(mApi.isAvailable()){
			
		} else{
			// save the ad in database. We will add it on remote server
			// once the user is connected to Internet
			final Ad cAd = pNewAd;
			Thread tSaveAd = new Thread(new Runnable() {				
				@Override
				public void run() {
					mDbManager.saveAd(cAd);
				}
			});
		}
		return this;
	}
}
