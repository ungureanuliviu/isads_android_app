package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Category;

public interface ICategoryNotifier {
	public void onCategoriesSyncronized(boolean isSuccess, ArrayList<Category> pCategories);
	public void onCategoriesLoaded(boolean isSuccess, ArrayList<Category> pCategories);
}
