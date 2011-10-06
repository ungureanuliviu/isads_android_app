package com.liviu.apps.iasianunta.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.interfaces.IAdNotifier;

public class AdsManager {
	// Constants
	private final String 	TAG = "AdsManager";
	private final int	 	MSG_AD_SAVED = 1;
	
	// Data
	private API 		mApi;
	private DBManager 	mDbManager;
	private Context 	mContext;
	private Handler		mHandler;
	
	// Notifiers
	private IAdNotifier	mIAdNotifier;	
	
	public AdsManager(Context pContext) {
		mContext 	= pContext;
		mApi 		= new API();
		mDbManager 	= DBManager.getInstance(pContext);
		mHandler	= new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_AD_SAVED:
					if(mIAdNotifier != null){
						if(null != msg.obj){
							mIAdNotifier.onAdSaved(true, (Ad)msg.obj);
						} else{
							mIAdNotifier.onAdSaved(false, null);
						}
					}
					break;
				default:
					break;
				}
			};
		};
	}
	
	public AdsManager addnewAd(Ad pNewAd, int pUserId, String pUserAuth, String pUserPassword){
		if(mApi.isAvailable(mContext)){
			mApi.addNewAd(pNewAd, pUserId, pUserAuth, pUserPassword);
		} else{
			// save the ad in database. We will add it on remote server
			// once the user is connected to Internet
			final Ad cAd = pNewAd;
			Thread tSaveAd = new Thread(new Runnable() {				
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = MSG_AD_SAVED;
					Ad justAdded = mDbManager.saveAd(cAd);
					if(null != justAdded){
						msg.obj = justAdded;						
					} 
					
					mHandler.sendMessage(msg);
				}
			});
			
			tSaveAd.start();
		}
		return this;
	}

	/**
	 * Save an ad in database
	 * @param pNewAd : the ad which will be saved in database
	 * @return nothing but check for {@link IAdNotifier#onAdSaved(boolean, Ad)}
	 */
	public void saveAd(Ad pNewAd) {
		if(null == pNewAd){
			// if the ad is null is not need to create a new thread
			mHandler.sendEmptyMessage(MSG_AD_SAVED);
		} else{
			// create a copy of the pNewAd and make it final(persistent)
			final Ad cNewAd = pNewAd;
			
			// create a thread and save the thread 
			// to local database			
			Thread tSaveAd = new Thread(new Runnable() {				
				@Override
				public void run() {
					Ad 		justAddedAd = mDbManager.saveAd(cNewAd);
					Message msg			= new Message();
					msg.what			= MSG_AD_SAVED;
					
					if(null != justAddedAd){
						msg.obj = justAddedAd;
						mHandler.sendMessage(msg);
					} else{
						mHandler.sendMessage(msg);
					}
					// and we are done here :)
				}
			});
			tSaveAd.start();
		}
	}
	
	public AdsManager setAdNotifier(IAdNotifier pAdNotifier){
		mIAdNotifier = pAdNotifier;
		return this;
	}
}
