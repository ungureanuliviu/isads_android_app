package com.liviu.apps.iasianunta.ui;

import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Convertor;

public class CategoryView {
	// Constants
	private final String TAG = "CategoryView";
	
	// Data
	private Category mCategory;
	private boolean  mIsSelected;
	private int 	 mSelectedColor;
	private int 	 mDefaultColor;
	
	public CategoryView(Category pCategory, int pSelectedColor, int pDefaultColor) {
		mCategory 		= pCategory;
		mSelectedColor 	= pSelectedColor;
		mDefaultColor 	= pDefaultColor;
	}
	
	public int getSelectedColor(){
		return mSelectedColor;
	}
	
	public CategoryView setSelectedColor(int pSelectedColor){
		mSelectedColor = pSelectedColor;
		return this;
	}
	
	public CategoryView setDefaultColor(int pDefaultColor){
		mDefaultColor = pDefaultColor;
		return this;
	}
	
	public boolean isSelected(){
		return mIsSelected;
	}
	
	public CategoryView setSelected(boolean pIsSelected){
		mIsSelected = pIsSelected;		
		return this;
	}
	
	public Category getCategory(){
		return mCategory;
	}
	
	public CategoryView setCategory(Category pCategory){
		mCategory = pCategory;
		return this;
	}
	
	@Override
	public String toString() {	
		return Convertor.toString(this);
	}

	public int getDefaultColor() {
		return mDefaultColor;
	}

}
