package com.liviu.apps.iasianunta.managers;

import org.json.JSONObject;

import com.liviu.apps.iasianunta.MainActivity;
import com.liviu.apps.iasianunta.apis.API;

import android.content.Context;
import android.content.SharedPreferences;

public class SyncManager{
	
	// Constants
	private final String TAG 				= "SyncManager";
	private final String SHOULD_SYNC_KEY 	= "should_sync";
	
	// Data
	private Context 			mContext;
	private API					mApi; 
	private DBManager			mDbMan;
	private SharedPreferences	mPrefs;
	
	public SyncManager(Context pContext) {
		mContext = pContext;
		mApi 	 = new API();
		mDbMan	 = DBManager.getInstance(mContext);
		mPrefs	 = mContext.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public boolean syncCategories(){
		if(!mPrefs.getBoolean(SHOULD_SYNC_KEY, true)){
			// we don't have to start a sync now
			return true;
		}
		
		Thread tCategoriesSync = new Thread(new Runnable() {			
			@Override
			public void run() {
				JSONObject apiRepsonse = mApi.getAllCategories();
			}
		});
		tCategoriesSync.start();
		return false;
	}
}
