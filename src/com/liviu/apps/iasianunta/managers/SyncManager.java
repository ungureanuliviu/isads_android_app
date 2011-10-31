package com.liviu.apps.iasianunta.managers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.iasianunta.MainActivity;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.utils.Console;

public class SyncManager{
	
	
	// Constants
	private final String TAG 							= "SyncManager";
	private final String SHOULD_SYNC_CATEGORIES_KEY 	= "should_sync";
	private final String SHOULD_SYNC_CITIES_KEY 		= "should_sync_cities";
	private final int	 MSG_CATEGORIES_ADDED_IN_DB 	= 1;
	private final int 	 MSG_CITIES_ADDED_IN_DB 		= 2;
	
	// Data
	private Context 			mContext;
	private API					mApi; 
	private DBManager			mDbMan;
	private SharedPreferences	mPrefs;
	private Handler				mHandler;
	
	// Notifiers
	private ISyncNotifier   mISyncNotifier;
	
	public SyncManager(Context pContext) {
		mContext = pContext;
		mApi 	 = new API();
		mDbMan	 = DBManager.getInstance(mContext);
		mPrefs	 = mContext.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_CATEGORIES_ADDED_IN_DB:
					if(null != mISyncNotifier){
						if(null != msg.obj){
							mISyncNotifier.onCategoriesSyncronized(true, (ArrayList<Category>)msg.obj);
							SharedPreferences.Editor ed = mPrefs.edit();
							ed.putBoolean(SHOULD_SYNC_CATEGORIES_KEY, false);
							ed.commit();
						} else{
							mISyncNotifier.onCategoriesSyncronized(false, null);
						}												
					}
					break;
				case MSG_CITIES_ADDED_IN_DB:
					if(null != mISyncNotifier){
						if(null != msg.obj){
							mISyncNotifier.onCitiesSyncronized(true, (ArrayList<City>)msg.obj);
							SharedPreferences.Editor ed = mPrefs.edit();
							ed.putBoolean(SHOULD_SYNC_CITIES_KEY, false);
							ed.commit();
						} else{
							mISyncNotifier.onCitiesSyncronized(false, null);
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
		Console.debug(TAG, "should sync categories: " + mPrefs.getBoolean(SHOULD_SYNC_CATEGORIES_KEY, true));
		return mPrefs.getBoolean(SHOULD_SYNC_CATEGORIES_KEY, true);
	}
	
	public SyncManager syncCategories(){		
		Thread tGetCategories = new Thread(new Runnable(){
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_CATEGORIES_ADDED_IN_DB;
				ArrayList<Category> loadedCategories = mApi.getAllCategories();				
				if(null != loadedCategories){
					for(int i = 0; i < loadedCategories.size(); i++){
						Category addedCategory = mDbMan.addCategory(loadedCategories.get(i));
						if(null != addedCategory){
							loadedCategories.get(i).setId(addedCategory.getId());
						}
					}											
					msg.obj  = loadedCategories;					
					mHandler.sendMessage(msg);
				} else{				
					mHandler.sendEmptyMessage(MSG_CATEGORIES_ADDED_IN_DB);
				}
			}			
		});
		tGetCategories.start();
		return this;							
	}

	public void setOnSyncedNotifier(final ISyncNotifier pSyncNotifier) {
		mISyncNotifier = pSyncNotifier;
	}
	
	public boolean shouldSyncCities(){
		Console.debug(TAG, "should sync cities: " + mPrefs.getBoolean(SHOULD_SYNC_CITIES_KEY, true));
		return mPrefs.getBoolean(SHOULD_SYNC_CITIES_KEY, true);
	}
	
	public SyncManager syncCities(){
		Thread tSyncCities = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_CITIES_ADDED_IN_DB;
				ArrayList<City> loadedCities= mApi.getAllCities();
				if(null != loadedCities){
					for(int i = 0; i < loadedCities.size(); i++){
						City addedCity = mDbMan.addCity(loadedCities.get(i));
						if(null != addedCity){
							loadedCities.get(i).setId(addedCity.getId());
						}
					}											
					msg.obj  = loadedCities;					
					mHandler.sendMessage(msg);
				} else{				
					mHandler.sendEmptyMessage(MSG_CITIES_ADDED_IN_DB);
				}				
			}
		});
		tSyncCities.start();		
		return this;							
	}	
}
