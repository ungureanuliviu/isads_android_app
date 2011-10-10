package com.liviu.apps.iasianunta.managers;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.JSONResponse;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ICategoryNotifier;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class AdsManager {
		
	// Constants
	private final String 	TAG 					= "AdsManager";
	private final int	 	MSG_AD_SAVED 			= 1;
	private final int 		MSG_CATEGORIES_LOADED 	= 2;
	private final int 		MSG_TH_IMAGE_LOADED 	= 3;

	// Data
	private API 		mApi;
	private DBManager 	mDbManager;
	private Context 	mContext;
	private Handler		mHandler;
	
	// Notifiers
	private IAdsNotifier			mIAdsNotifier;	
	private ICategoryNotifier 		mICategoryNotifier;
	
	
	public AdsManager(Context pContext) {
		mContext 	= pContext;
		mApi 		= new API();
		mDbManager 	= DBManager.getInstance(pContext);
		mHandler	= new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_AD_SAVED:
					if(mIAdsNotifier != null){
						if(null != msg.obj){
							mIAdsNotifier.onAdSaved(true, (Ad)msg.obj);
						} else{
							mIAdsNotifier.onAdSaved(false, null);
						}
					}
					break;
				case MSG_CATEGORIES_LOADED:
					if(null != mICategoryNotifier){
						if(null != msg.obj){
							mICategoryNotifier.onCategoriesLoaded(true, (ArrayList<Category>)msg.obj);
						} else{
							mICategoryNotifier.onCategoriesLoaded(false, null);
						}
					}
					break;
				case MSG_TH_IMAGE_LOADED:
					if(null != mIAdsNotifier){
						if(null != msg.obj){
							if(msg.arg2 == 1)
								mIAdsNotifier.onImageDownloaded(true, msg.arg1, (Bitmap)msg.obj, true);
							else 
								mIAdsNotifier.onImageDownloaded(true, msg.arg1, (Bitmap)msg.obj, false);
						} else{
							mIAdsNotifier.onImageDownloaded(false, msg.arg1, null, false);
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
		if(Utils.isConnected(mContext)){
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
	 * @return nothing but check for {@link IAdsNotifier#onAdSaved(boolean, Ad)}
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
	
	public AdsManager setAdsNotifier(IAdsNotifier pAdsNotifier){
		mIAdsNotifier = pAdsNotifier;
		mApi.setAdNotifier(pAdsNotifier);
		return this;
	}

	public AdsManager getCategories(ICategoryNotifier pICategoryNotifier) {
		if(null != pICategoryNotifier){
			mICategoryNotifier = pICategoryNotifier;
			Thread tLoadCategories = new Thread(new Runnable() {				
				@Override
				public void run() {
					ArrayList<Category> loadedCategories = mDbManager.getAllCategories();
					Message msg = new Message();
					msg.what 	= MSG_CATEGORIES_LOADED;
					msg.obj  	= loadedCategories;
					
					mHandler.sendMessage(msg);
				}
			});
			tLoadCategories.start();
		} else{
			Console.debug(TAG, "no notifier specified for categories load");
		}		
		return this;
	}

	public void getAds(int pCategoryId, int pCurrentPage, int pAdsPerPage) {
		mApi.getAds(pCategoryId, pCurrentPage, pAdsPerPage);
	}

	public AdsManager loadThImages(ArrayList<Ad> pLoadedAds) {
		final ArrayList<Ad> cAds = pLoadedAds;
		Thread loadImagesThread = new Thread(new Runnable() {			
			@Override
			public void run() {
				 
				for(int i = 0; i < cAds.size(); i++){
					Message msg = new Message();
					msg.what = MSG_TH_IMAGE_LOADED;
					if(cAds.get(i).getImages().size() > 0){
						String imgUrl;
						try {
							imgUrl = "http://iasianunta.info/library/img/ads_img/th/" + cAds.get(i).getImages().get(0).getServerFileInfo().getString("name");
							Console.debug(TAG, "imgUrl: " + imgUrl);
							Bitmap bmp = mApi.downloadThImage(imgUrl, null);
							msg.obj 	= bmp;
							msg.arg1 	= cAds.get(i).getId();
							mHandler.sendMessage(msg);														
						} catch (JSONException e) {
							e.printStackTrace();
							msg.arg1 = cAds.get(i).getId();
							mHandler.sendMessage(msg);
						}											
					} else{
						msg.arg1 = cAds.get(i).getId();
						mHandler.sendMessage(msg);
					}
				}
			}
		});
		loadImagesThread.start();
		return this;
	}
	
	public AdsManager loadImages(Ad pAd) {
		final Ad cAd = pAd;
		Thread loadImagesThread = new Thread(new Runnable() {			
			@Override
			public void run() {				 
					Console.debug(TAG, "cAd.getImages().size(): " + cAd.getImages().size());					
					if(cAd.getImages().size() > 0){
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 2;
						for(int i = 0; i < cAd.getImages().size(); i++){
							Message msg = new Message();
							msg.what = MSG_TH_IMAGE_LOADED;
							String imgUrl;
							try {
								imgUrl = "http://iasianunta.info/library/img/ads_img/" + cAd.getImages().get(i).getServerFileInfo().getString("name");
								Console.debug(TAG, "imgUrl: " + imgUrl);
								Bitmap bmp = mApi.downloadThImage(imgUrl, options);
								msg.obj 	= bmp;
								msg.arg1 	= cAd.getId();
								msg.arg2    = 1;
								mHandler.sendMessage(msg);														
							} catch (JSONException e) {
								e.printStackTrace();
								msg.arg1 = cAd.getId();
								msg.arg2 = 1;
								mHandler.sendMessage(msg);
							}
						}
					} else{
						Message msg = new Message();
						msg.what 	= MSG_TH_IMAGE_LOADED;
						msg.arg1 	= cAd.getId();
						msg.arg2	= 1;
						mHandler.sendMessage(msg);
					}
				}
		});
		loadImagesThread.start();
		return this;
	}	
}
