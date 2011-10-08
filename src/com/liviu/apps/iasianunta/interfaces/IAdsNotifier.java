package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.liviu.apps.iasianunta.data.Ad;

public interface IAdsNotifier {
	public void onAdSaved(boolean isSuccess, Ad pSavedAd);
	public void onAdRemoteAdded(boolean isSuccess, Ad pAdRemoteAdded);
	public void onAdsLoaded(boolean isSuccess, int pCategoryId, int pPage, int pAdsPerPage, ArrayList<Ad> pLoadedAds);
	public void onImageDownloaded(boolean isSuccess, int pAdId, Bitmap pImg);	
}
