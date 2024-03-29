package com.liviu.apps.iasianunta.managers;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
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
	
	// Tables
	private final String TABLE_SAVED_ADS 	= "table_saved_ads";
	private final String TABLE_IMAGES	 	= "table_ad_images";
	private final String TABLE_CATEGORIES 	= "table_categories";
	private final String TABLE_CITIES		= "table_cities";

	private final String CREATE_TABLE_CITIES = "create table if not exists table_cities (" +
											   "id integer not null," +
											   "name text not null)";
	private final String CITY_ID = "id";
	private final String CITY_NAME = "name";
	
	private final String CREATE_TABLE_CATEGORIES = "create table if not exists table_categories (" +
												   "cat_id integer not null," +
												   "cat_name text not null)";
	
	private final String CAT_ID 	= "cat_id";
	private final String CAT_NAME 	= "cat_name";
	
	private final String CREATE_IMAGES_TABLE = "create table if not exists table_ad_images (" +
											   "ad_id integer not null, " +
											   "uri text not null, " +
											   "name text not null," +
											   "url text not null)";
	private final String IMAGE_AD_ID = "ad_id";
	private final String IMAGE_URI	 = "uri";
	private final String IMAGE_NAME  = "name";
	private final String IMAGE_URL	 = "url";
 	
	private final String CREATE_SAVED_ADS_TABLE = "create table if not exists table_saved_ads (" +
														"id integer primary key autoincrement, " + 
														"title text not null," +
														"content text not null," +
														"category_id integer not null default -1," + 
														"price double not null default 0.00," +
														"email text," +
														"phone text not null," +
														"address text," +
														"source text," +
														"date long not null)";
	
	private final String AD_ID 				= "id";
	private final String AD_TITLE 			= "title";
	private final String AD_CONTENT 		= "content";
	private final String AD_CATEGORY_ID 	= "category_id";
	private final String AD_PRICE 			= "price";
	private final String AD_EMAIL 			= "email";
	private final String AD_PHONE 			= "phone";
	private final String AD_DATE  			= "date";
	private final String AD_SOURCE			= "source";
	
	
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
			mDb.execSQL(CREATE_IMAGES_TABLE);
			mDb.execSQL(CREATE_TABLE_CATEGORIES);
			mDb.execSQL(CREATE_TABLE_CITIES);
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
	 * @return null if an error occur or the just added Ad (with id member updated)
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
		values.put(AD_SOURCE, pAd.getSource());
		
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
				for(int i = 0; i < pAd.getImages().size(); i++)
					saveAdImage(pAd.getImages().get(i), pAd.getId());
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
	
	public synchronized boolean saveAdImage(AdImage pAdImage, int pAdId){
		Console.debug(TAG, "Add image : " + pAdImage + " id: " + pAdId);
		
		if(null == pAdImage)
			return false;
		try {
			ContentValues values = new ContentValues();
			values.put(IMAGE_AD_ID, pAdId);		
			values.put(IMAGE_NAME, pAdImage.getServerFileInfo().getString("name"));
			values.put(IMAGE_URI, pAdImage.getUri().toString());
			values.put(IMAGE_URL, pAdImage.getServerFileInfo().getString("url"));
			
			openOrCreateDatabase();
			long newRowId = mDb.insert(TABLE_IMAGES, null, values);
			closeDatabase();
			return true;			
		} catch (JSONException e) {
			closeDatabase();
			e.printStackTrace();
			return false;
		}
	}
	
	public synchronized Category addCategory(Category pCategory){
		if(null == pCategory)
			return null;
		
		ContentValues  values = new ContentValues();
		values.put(CAT_ID, pCategory.getId());
		values.put(CAT_NAME, pCategory.getName());
		
		try{
			openOrCreateDatabase();
			int newId = (int)mDb.insert(TABLE_CATEGORIES, null, values);
			if(newId > -1){
				// insert success
				pCategory.setId(newId);
				closeDatabase();
				return pCategory;
			} else{
				// insert error
				closeDatabase();
				closeDatabase();
				return null;
			}			
		}catch (SQLException e) {
			e.printStackTrace();
			closeDatabase();
			return null;
		}
	}

	public synchronized ArrayList<Category> getAllCategories() {
		ArrayList<Category> categories = new ArrayList<Category>();
		String[] projection 		   = new String[]{CAT_ID, CAT_NAME};
		Cursor c 					   = null;
		
		openOrCreateDatabase();
		c = mDb.query(TABLE_CATEGORIES, projection, null, null, null, null, CAT_ID + " ASC");
		if(c == null){
			closeDatabase();
			return categories;
		} else if(c.getCount() == 0){
			c.close();
			closeDatabase();
			return categories;
		} else {
			int numRows = c.getCount();
			c.moveToFirst();
			
			for(int i = 0; i < numRows; i++){
				categories.add(new Category(c.getInt(0), c.getString(1)));
				c.moveToNext();
			}
			
			c.close();
			closeDatabase();
			return categories;				
		}						
	}

	public synchronized City addCity(City pCity) {
		if(null == pCity)
			return null;
		
		ContentValues  values = new ContentValues();
		values.put(CITY_ID, pCity.getId());
		values.put(CITY_NAME, pCity.getName());
		
		try{
			openOrCreateDatabase();
			int newId = (int)mDb.insert(TABLE_CITIES, null, values);
			if(newId > -1){
				// insert success
				pCity.setId(newId);
				closeDatabase();
				return pCity;
			} else{
				// insert error
				closeDatabase();
				closeDatabase();
				return null;
			}			
		}catch (SQLException e) {
			e.printStackTrace();
			closeDatabase();
			return null;
		}
	}

	public synchronized ArrayList<City> getAllCities() {
		ArrayList<City> cities = new ArrayList<City>();
		String[] projection	   = new String[]{CITY_ID, CITY_NAME};
		Cursor c 			   = null;
		
		openOrCreateDatabase();
		c = mDb.query(TABLE_CITIES, projection, null, null, null, null, CITY_ID + " ASC");
		if(c == null){
			closeDatabase();
			return cities;
		} else if(c.getCount() == 0){
			c.close();
			closeDatabase();
			return cities;
		} else {
			int numRows = c.getCount();
			c.moveToFirst();
			
			for(int i = 0; i < numRows; i++){
				cities.add(new City(c.getInt(0), c.getString(1)));
				c.moveToNext();
			}
			
			c.close();
			closeDatabase();
			return cities;				
		}						
	}
}
