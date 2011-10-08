package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Ad;

public interface AdsNotifier {
	public void onAdsLoaded(int isSuccess, int pCategoryId, int pPage, int pAdsPerPage, ArrayList<Ad> pLoadedAds);
}
