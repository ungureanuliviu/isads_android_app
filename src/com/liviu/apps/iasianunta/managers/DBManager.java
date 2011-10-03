package com.liviu.apps.iasianunta.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.interfaces.IDbAdNotifier;
import com.liviu.apps.iasianunta.utils.Console;

/**
 * 
 * @author Liviu
 * @see All methods should be syncronized
 */
public class DBManager {

	// Constants
	private final String TAG 			=  "DBManager";
	private final String DB_NAME 		= "is_db";
	private final int	 MSG_AD_SAVED	= 1;
	// Tables
	private final String TABLE_SAVED_ADS = "table_saved_ads";
	
	private final String CREATE_SAVED_ADS_TABLE = "create table if not exists table_saved_ads (" +
														"id integer primary key autoincrement, " + 
														"title text not null," +
														"content text not null," +
														"category_id integer not null default -1," + 
														"price double not null default 0.00," +
														"email text," +
														"phone text not null," +
														"address text," +
														"date long not null)";
	
	private final String AD_ID 				= "id";
	private final String AD_TITLE 			= "title";
	private final String AD_CONTENT 		= "content";
	private final String AD_CATEGORY_ID 	= "category_id";
	private final String AD_PRICE 			= "price";
	private final String AD_EMAIL 			= "email";
	private final String AD_PHONE 			= "phone";
	private final String AD_DATE  			= "date";
	
	
	// Interfaces
	private IDbAdNotifier mIdbAdNotifier;
	
	
	// Data
	private 		SQLiteDatabase 	mDb;
	private 		Context 		mContext;
	private static 	DBManager 		mInstance;
	
	private DBManager(Context pContext) {
		mContext = pContext;
	}
	
	public static DBManager getInstance(Context pContext){
		if(mInstance != null)
			return mInstance;
		else
			return (mInstance = new DBManager(pContext));
	}
	
	private synchronized boolean openOrCreateDatabase(){
		try{
			mDb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
			mDb.execSQL(CREATE_SAVED_ADS_TABLE);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Console.debug(TAG, "OpenOrCreateDatabase erorr");
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Console.debug(TAG, "sql exec error");
			return false;
		}
	}
	
	/**
	 * close the database (just if it was opened
	 */
	private synchronized void closeDatabase(){
		if(mDb.isOpen())
			mDb.close();
	}
	
	/**
	 * Add(or save) a new ad in database;
	 * @param pAd : the Ad object which will be added in database
	 * @return null if an error occure or the just added Ad (with id member updated)
	 */
	public synchronized Ad saveAd(Ad pAd){
		if(null == pAd){
			return null;
		}
		
		ContentValues values = new ContentValues();
		values.put(AD_TITLE, pAd.getTitle());
		values.put(AD_CONTENT, pAd.getContent());
		values.put(AD_DATE, pAd.getDate());
		values.put(AD_PHONE, pAd.getPhone());
		
		if(null != pAd.getEmail())
			values.put(AD_EMAIL, pAd.getEmail());
		
		if(0.00 > pAd.getPrice())
			values.put(AD_PRICE, pAd.getPrice());
		
		if(-1 != pAd.getCategoryId())
			values.put(AD_CATEGORY_ID, pAd.getCategoryId());
		
		openOrCreateDatabase();
		try{
			long newId = mDb.insertOrThrow(TABLE_SAVED_ADS, null, values);
			if(newId != -1){
				pAd.setId((int)newId);
				closeDatabase();
				return pAd;
			} else{				
				closeDatabase();
				return null;
			}			
		} catch(SQLException e){
			e.printStackTrace();
			closeDatabase();
			return null;
		}			
	}
}
