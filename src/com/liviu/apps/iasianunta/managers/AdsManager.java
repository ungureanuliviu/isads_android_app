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
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.data.LocalCache;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ICommentsNotifier;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class AdsManager {
					
	// Constants
	private final String 	TAG 					= "AdsManager";
	private final int	 	MSG_AD_SAVED 			= 1;
	private final int 		MSG_CATEGORIES_LOADED 	= 2;
	private final int 		MSG_TH_IMAGE_LOADED 	= 3;
	private final int 		MSG_COMMENTS_LOADED		= 4;
	private final int       MSG_COMMENT_ADDED		= 5;
	private final int 		MSG_CITIES_LOADED 		= 6;

	// Data
	private API 		mApi;
	private DBManager 	mDbManager;
	private Context 	mContext;
	private Handler		mHandler;
	
	// Notifiers
	private IAdsNotifier			mIAdsNotifier;	
	private ISyncNotifier 		mIDBSyncNotifier;
	private ICommentsNotifier		mCommentsNotifer;
	
	
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
					if(null != mIDBSyncNotifier){
						if(null != msg.obj){
							mIDBSyncNotifier.onCategoriesLoaded(true, (ArrayList<Category>)msg.obj);
							LocalCache.categories = (ArrayList<Category>)msg.obj;							
						} else{
							mIDBSyncNotifier.onCategoriesLoaded(false, null);							
						}
					}
					break;
				case MSG_CITIES_LOADED:
					if(null != mIDBSyncNotifier){
						if(null != msg.obj){
							mIDBSyncNotifier.onCitiesLoaded(true, (ArrayList<City>)msg.obj);
							LocalCache.cities = (ArrayList<City>)msg.obj;
						} else{
							mIDBSyncNotifier.onCitiesLoaded(false, null);
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
				case MSG_COMMENTS_LOADED:
					if(null != mCommentsNotifer){
						if(null != msg.obj){
							mCommentsNotifer.onCommentLoaded(true, msg.arg1, (ArrayList<Comment>)msg.obj);
						}else{
							mCommentsNotifer.onCommentLoaded(true, -1, null);
						}
					}
					break;
				case MSG_COMMENT_ADDED:
					if(null != mCommentsNotifer){
						if(null != msg.obj){
							mCommentsNotifer.onCommentAdded(true, (Comment)msg.obj);
						} else{
							mCommentsNotifer.onCommentAdded(false, null);
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

	public AdsManager getCategories() {		
		if(null == LocalCache.categories){
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
		}else{
			Message msg = new Message();
			msg.what = MSG_CATEGORIES_LOADED;
			msg.obj = LocalCache.categories;
			mHandler.sendMessage(msg);
		}
		return this;
	}
	
	public AdsManager getCities() {
		if(null == LocalCache.cities){
			Thread tLoadCities = new Thread(new Runnable() {				
				@Override
				public void run() {
					ArrayList<City> loadedCities= mDbManager.getAllCities();
					Message msg = new Message();
					msg.what 	= MSG_CITIES_LOADED;
					msg.obj  	= loadedCities;
					mHandler.sendMessage(msg);
				}
			});
			tLoadCities.start();	
		}else{
			Message msg = new Message();
			msg.what = MSG_CITIES_LOADED;
			msg.obj = LocalCache.cities;
			mHandler.sendMessage(msg);
		}
		return this;		
	}	

	public void getAds(int pCategoryId, int pCurrentPage, int pAdsPerPage, int pCityId) {
		mApi.getAds(pCategoryId, pCurrentPage, pAdsPerPage, pCityId);
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

	public void setCommentsNotifier(ICommentsNotifier lNotifier) {
		mCommentsNotifer = lNotifier;
	}

	public AdsManager getAllComments(int pAdId) {
		if(0 > pAdId){
			mHandler.sendEmptyMessage(MSG_COMMENTS_LOADED);
		} else{
			final int cAdId = pAdId;
			Thread tLoadComments = new Thread(new Runnable() {				
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = MSG_COMMENTS_LOADED;
					
					ArrayList<Comment> commentsList = mApi.getComments(cAdId);
					if(null != commentsList){
						msg.obj 	= commentsList;
						msg.arg1 	= cAdId;
					}
					
					mHandler.sendMessage(msg);
				}
			});
			tLoadComments.start();
		}
		
		return this;
	}

	public AdsManager addComment(Comment pComment, String pUserAuth, String pUserPassword) {
		
		if(null == pComment){
			mHandler.sendEmptyMessage(MSG_COMMENT_ADDED);
			return this;
		}
		final Comment cComment 	= pComment;
		final String  cUserAuth = pUserAuth;
		final String  cUserPass = pUserPassword;
		
		Thread tAddComment = new Thread(new Runnable() {			
			@Override
			public void run() {
				Comment addedComment = mApi.addComment(cComment, cUserAuth, cUserPass);
				Message msg = new Message();
				msg.what 	= MSG_COMMENT_ADDED;
				
				if(null != addedComment){
					msg.obj = addedComment;					
				}
				
				mHandler.sendMessage(msg);
			}
		});
		tAddComment.start();
		return this;
	}	
	
	public AdsManager setDBSyncNotifier(ISyncNotifier pNotifier){
		mIDBSyncNotifier = pNotifier;
		return this;
	}

}
