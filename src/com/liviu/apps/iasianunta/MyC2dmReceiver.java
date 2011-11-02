package com.liviu.apps.iasianunta;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.utils.Console;

public class MyC2dmReceiver extends BroadcastReceiver {
	public static final String PREFS_NAME = "c2dmPref";
	public static final String KEY_USER_ID = "user_id";
	private final int MSG_NEW_ALERT_LOADED = 1;
	
	public static final String REGISTRATION_KEY = "registrationKey";
	public static final String TAG = "MyC2dmReceiver";

	// Data
	private API		mApi;
	private Context mContext;
	private Handler	mHandler;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    this.mContext = context;
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	    }
	 }

	private void handleRegistration(Context context, Intent intent) {
		Log.e(TAG, "handleRegistration: " + intent);
		mApi = new API();
		
	    String registration = intent.getStringExtra("registration_id");
	    if (intent.getStringExtra("error") != null) {
	        // Registration failed, should try again later.
		    Log.d("c2dm", "registration failed");
		    String error = intent.getStringExtra("error");
		    if(error == "SERVICE_NOT_AVAILABLE"){
		    	Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
		    }else if(error == "ACCOUNT_MISSING"){
		    	Log.d("c2dm", "ACCOUNT_MISSING");
		    }else if(error == "AUTHENTICATION_FAILED"){
		    	Log.d("c2dm", "AUTHENTICATION_FAILED");
		    }else if(error == "TOO_MANY_REGISTRATIONS"){
		    	Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
		    }else if(error == "INVALID_SENDER"){
		    	Log.d("c2dm", "INVALID_SENDER");
		    }else if(error == "PHONE_REGISTRATION_ERROR"){
		    	Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
		    }
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // unregistration done, new messages from the authorized sender will be rejected
	    	Log.d("c2dm", "unregistered");

	    } else if (registration != null) {
	    	Log.d("c2dm", registration);
	    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	    	Editor editor = prefs.edit();
            editor.putString(REGISTRATION_KEY, registration);
    		editor.commit();
    		
    		mApi.updateDeviceId(prefs.getInt(KEY_USER_ID, -1), registration);
    		
	       // Send the registration ID to the 3rd party site that is sending the messages.
	       // This should be done in a separate thread.
	       // When done, remember that all registration is done.
	    }
	}

	private void handleMessage(Context context, Intent intent)
	{
		mApi = new API();
		final Context mContext = context;
		final NotificationManager mNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mHandler =  new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_NEW_ALERT_LOADED:    
					if(null != msg.obj){
						Alert alert = (Alert)msg.obj;
						
						String message = "";
						if(alert.getAds().size() > 0){    
							Ad ad = alert.getAds().get(0);
							message = ad.getContent();
						}
						Intent intent = new Intent( mContext, ShowAlertActivity.class );
						intent.putExtra(ShowAlertActivity.ALERT_TRANSPORT, msg.arg1);
					    Notification notification = new Notification(R.drawable.ic_notif, "Anunt nou: " + message, System.currentTimeMillis());
					    notification.flags |= Notification.FLAG_AUTO_CANCEL;
					    notification.setLatestEventInfo(mContext, "A fost adaugat un anunt relevant", message, PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)); 
					    mNotifManager.notify(1, notification);					
					} 
					break;
				default:
					break;
				}
			}
		};
		Log.e(TAG, "message: " + intent.getExtras().getString("message"));
		try{
			JSONObject jsonMessage = new JSONObject(intent.getExtras().getString("message"));
			if(jsonMessage.getString("action").equals("alert_ad_found")){
				final int cAlertId = jsonMessage.getInt("alert_id");
				Thread tGetFirstAd = new Thread(new Runnable() {					
					@Override
					public void run() {
						Alert newAlert = mApi.getAlertAdsCountWithFirstAd(cAlertId);
						Console.debug(TAG, "newAlert: " + newAlert);
						if(null != newAlert){
							Message msg = new Message();
							msg.what = MSG_NEW_ALERT_LOADED;
							msg.arg1 = cAlertId;
							msg.obj = newAlert;
							mHandler.sendMessage(msg);
						}
					}
				});
				tGetFirstAd.start();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//Do whatever you want with the message
	}
}