package com.liviu.apps.iasianunta.managers;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.interfaces.IAlertsNotifier;
import com.liviu.apps.iasianunta.utils.Console;

public class AlertManager {
	// Constants
	private final String 	TAG = "AlertManager";
	private final int		MSG_ALERTS_LOADED = 1;
	
	// Data
	private API mApi;
	private Handler mHandler;
	
	// Notifiers	
	private IAlertsNotifier mIAlertsNotifier;
	
	public AlertManager() {
		mApi = new API();
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_ALERTS_LOADED:
					if(null != mIAlertsNotifier){
						if(null != msg.obj){
							mIAlertsNotifier.onAlertsLoaded(true, (ArrayList<Alert>)msg.obj);
						} else{
							mIAlertsNotifier.onAlertsLoaded(false, null);
						}
					}
					break;

				default:
					break;
				}
			};
		};
	}
	
	public AlertManager setAlertsNotifier(IAlertsNotifier pNotifier){
		mIAlertsNotifier = pNotifier;
		return this;		
	}
	public AlertManager getAllAlerts(String pUserAuth, String pUserPassword, int pUserId){
		
		if(null == pUserAuth || null == pUserPassword){
			mHandler.sendEmptyMessage(MSG_ALERTS_LOADED);
			Console.debug(TAG, "user auth or password are null " + pUserAuth + " " + pUserPassword);
			return this;
		}
		
		// all is good: we have user's informations
		final String cUserAuth 		= pUserAuth;
		final String cUserPassword 	= pUserPassword;
		final int    cUserId		= pUserId;
		
		Thread tGetAlerts = new Thread(new Runnable() {			
			@Override
			public void run() {
				ArrayList<Alert> loadedAlerts = mApi.getAllAlerts(cUserId, cUserAuth, cUserPassword);
				Message msg = new Message();
				msg.what 	= MSG_ALERTS_LOADED;
				
				if(null != loadedAlerts)
					msg.obj = loadedAlerts;
				mHandler.sendMessage(msg);
			}
		});
		tGetAlerts.start();		
		return this;
	}
}
