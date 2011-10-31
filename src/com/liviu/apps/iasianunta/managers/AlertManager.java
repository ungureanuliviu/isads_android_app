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
	private final int		MSG_ALERTS_LOADED 	= 1;
	private final int 		MSG_ALERT_ADDED 	= 2;
	private final int 		MSG_ALERT_LOADED 	= 3;	
	private final int 		MSG_ALERT_REMOVED 	= 4;
	
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
				case MSG_ALERT_ADDED:
					if(null != mIAlertsNotifier){
						if(null != msg.obj){
							mIAlertsNotifier.onAlertAdded(true, (Alert)msg.obj);
						} else{
							mIAlertsNotifier.onAlertAdded(false, null);
						}
					}
					break;
				case MSG_ALERT_LOADED:
					if(null != mIAlertsNotifier){
						if(null != msg.obj){
							mIAlertsNotifier.onAlertLoaded(true, (Alert)msg.obj);
						} else{
							mIAlertsNotifier.onAlertLoaded(false, null);
						}
					}
					break;
				case MSG_ALERT_REMOVED:
					if(null != mIAlertsNotifier){
						if(null != msg.obj){
							mIAlertsNotifier.onAlertRemoved(msg.arg1 == 1, (Alert)msg.obj);
						} else{
							mIAlertsNotifier.onAlertRemoved(false, null);
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

	public void addNewAlert(Alert pAlert, String pUserAuth, String pUserPassword) {
		if(null == pAlert){
			mHandler.sendEmptyMessage(MSG_ALERT_ADDED);
			return;
		}
		
		final Alert 	cAlert 			= pAlert;
		final String 	cUserAuth 		= pUserAuth;
		final String 	cUserPassword 	= pUserPassword;		
		Thread tAddAlert = new Thread(new Runnable() {			
			@Override
			public void run() {
				Alert addedAlert = mApi.addAlert(cAlert, cUserAuth, cUserPassword);
				Message msg = new Message();
				msg.what = MSG_ALERT_ADDED;
				
				if(null != addedAlert){
					msg.obj = addedAlert;
				}
				
				mHandler.sendMessage(msg);
			}
		});
		tAddAlert.start();
	}

	public AlertManager getAlertWithAds(int pAlertId, int pUserId, String pUserAuthName, String pUserPassword) {
		if(-1 == pAlertId || -1 == pUserId){
			mHandler.sendEmptyMessage(MSG_ALERT_LOADED);
			return this;
		}
		
		final int 	 cAlertId 		= pAlertId;
		final int 	 cUserId  		= pUserId;
		final String cUserAuthName 	= pUserAuthName;
		final String cUserPassword 	= pUserPassword;
		
		Thread tGetAlert = new Thread(new Runnable() {			
			@Override
			public void run() {
				Alert alert = mApi.getAlertWithAds(cAlertId, cUserId, cUserAuthName, cUserPassword);
				Message msg = new Message();
				msg.what = MSG_ALERT_LOADED;
				if(null != alert){
					msg.obj = alert;
				}
				
				mHandler.sendMessage(msg);
			}
		});
		tGetAlert.start();		
		return this;
	}

	public void removeAlert(Alert pAlert, int pUserId, String pUserAuthName, String pUserPassword) {
		if(-1 == pAlert.getId()|| -1 == pUserId){
			mHandler.sendEmptyMessage(MSG_ALERT_REMOVED);
			return;
		}
		
		final Alert cAlert = pAlert;
		final int cUserId  = pUserId;
		final String cUserAuthName = pUserAuthName;
		final String cUserPassword = pUserPassword;
		
		Thread tRemoveAlert = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_ALERT_REMOVED;
				boolean wasRemoved = mApi.removeAlert(cAlert.getId(), cUserId, cUserAuthName, cUserPassword);
				msg.obj = cAlert;
				if(wasRemoved){
					msg.arg1 = 1;
				}else{
					msg.arg1 = 0;
				}
				
				mHandler.sendMessage(msg);
			}
		});
		tRemoveAlert.start();
	}
}
