package com.liviu.apps.iasianunta.managers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.iasianunta.MainActivity;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.interfaces.ICategoryNotifier;

public class SyncManager{
	
	// Constants
	private final String TAG 							= "SyncManager";
	private final String SHOULD_SYNC_KEY 				= "should_sync";
	private final int	 MSG_CATEGORIES_ADDED_IN_DB 	= 1;
	
	// Data
	private Context 			mContext;
	private API					mApi; 
	private DBManager			mDbMan;
	private SharedPreferences	mPrefs;
	private Handler				mHandler;
	
	// Notifiers
	private ICategoryNotifier   mICategoryNotifier;
	
	public SyncManager(Context pContext) {
		mContext = pContext;
		mApi 	 = new API();
		mDbMan	 = DBManager.getInstance(mContext);
		mPrefs	 = mContext.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_CATEGORIES_ADDED_IN_DB:
					if(null != mICategoryNotifier){
						if(null != msg.obj){
							mICategoryNotifier.onCategoriesSyncronized(true, (ArrayList<Category>)msg.obj);
							SharedPreferences.Editor ed = mPrefs.edit();
							ed.putBoolean(SHOULD_SYNC_KEY, false);
							ed.commit();
						} else{
							mICategoryNotifier.onCategoriesSyncronized(false, null);
						}												
					}
					break;

				default:
					break;
				}
			};
		};
	}
	
	public boolean shouldSyncCategories(){
		return mPrefs.getBoolean(SHOULD_SYNC_KEY, true);
	}
	
	public SyncManager syncCategories(){		
		mApi.getAllCategories();
		return this;							
	}

	public void setOnCategoriesSyncedNotifier(final ICategoryNotifier pCategoryNotifier) {
		mApi.setCategoryNotifier(new ICategoryNotifier() {			
			@Override
			public void onCategoriesSyncronized(boolean isSuccess, ArrayList<Category> pCategories) {
				if(isSuccess == true && null != pCategories){
					mICategoryNotifier = pCategoryNotifier;
					final ArrayList<Category> cCategories = pCategories;
					Thread tAddCategories = new Thread(new Runnable() {					
						@Override
						public void run() {						
							for(int i = 0; i < cCategories.size(); i++){
								mDbMan.addCategory(cCategories.get(i));
							}							
							Message msg = new Message();
							msg.what = MSG_CATEGORIES_ADDED_IN_DB;
							msg.obj  = cCategories;
							
							mHandler.sendMessage(msg);							
						}
					});	
					tAddCategories.start();
				} else{ 
					pCategoryNotifier.onCategoriesSyncronized(isSuccess, pCategories);
				}
			}

			@Override
			public void onCategoriesLoaded(boolean isSuccess,
					ArrayList<Category> pcaArrayList) {
				// nothing here
			}
		});
	}
}
