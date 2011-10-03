package com.liviu.apps.iasianunta.interfaces;

import com.liviu.apps.iasianunta.data.Ad;

public interface IDbAdNotifier {
	public void onAdSaved(boolean isSuccess, Ad pSavedAd);
}
