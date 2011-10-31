package com.liviu.apps.iasianunta.data;

import com.liviu.apps.iasianunta.utils.Convertor;

public class PriceFilter extends Filter{
	public static final String TYPE = "price";

	// Constants
	private final String TAG = "PriceFilter";
	
	// Data
	private double mMinPrice;
	private double mMaxPrice;	
	private String mCurrency;
	
	public PriceFilter() {
		super("price");
		mMinPrice = 0;
		mMaxPrice = 0;
		mCurrency = "LEI";
	}
	
	public PriceFilter(double pMinPrice, double pMaxPrice, String pCurrency) {
		super("price");
		mMinPrice = pMinPrice;
		mMaxPrice = pMaxPrice;
		mCurrency = pCurrency;
	}
	
	public String getCurrency(){
		return mCurrency;
	}
	
	public PriceFilter setCurrency(String pCurrency){
		mCurrency = pCurrency;
		return this;
	}

	public double getMinPrice(){
		return mMinPrice;
	}
	
	public PriceFilter setMinPrice(double pMinPrice){
		mMinPrice = pMinPrice;
		return this;
	}
	
	public double getMaxPrice(){
		return mMaxPrice;
	}
	
	public PriceFilter setMaxPrice(double pMaxPrice){
		mMaxPrice = pMaxPrice;
		return this;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}
