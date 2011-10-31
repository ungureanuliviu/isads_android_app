package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;

public interface ISyncNotifier {
	public void onCategoriesSyncronized(boolean isSuccess, ArrayList<Category> pCategories);
	public void onCategoriesLoaded(boolean isSuccess, ArrayList<Category> pCategories);
	public void onCitiesSyncronized(boolean isSuccess, ArrayList<City> pCities);
	public void onCitiesLoaded(boolean isSuccess, ArrayList<City> pCities);
	
}
