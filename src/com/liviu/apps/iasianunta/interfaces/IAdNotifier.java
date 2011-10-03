package com.liviu.apps.iasianunta.interfaces;

import com.liviu.apps.iasianunta.data.Ad;

public interface IAdNotifier {
	public void onAdSaved(boolean isSuccess, Ad pSavedAd);

	public void onAdRemoteAdded(boolean isSuccess, Ad pAdRemoteAdded);
}
