package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.SyncManager;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;

public class MainActivity extends Activity implements OnClickListener,
													  ISyncNotifier{
	
	// Constants
	private final String TAG = "LoginActivity";
	public  final static int ACTIVITY_ID 	= ActivityIdProvider.getInstance().getNewId(MainActivity.class);
	public  final static String PREFS_NAME 	= "IS_ADS_PREFS";
	
	// Data
	private User user;
	private API api;
	private SyncManager syncMan;

	// UI
	private LTextView 		txtUserName;
	private Button 			butLogin;
	private RelativeLayout 	butAddNewAdd;
	private RelativeLayout 	butShowAds;
	private RelativeLayout 	butAlerts;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);             
        setContentView(R.layout.main);
        
        user 			= User.getInstance(); 
        api 			= new API();
        txtUserName 	= (LTextView)findViewById(R.id.user_name);
        butLogin 		= (Button)findViewById(R.id.main_but_login);
        butAddNewAdd 	= (RelativeLayout)findViewById(R.id.but_add_ad);
        butShowAds   	= (RelativeLayout)findViewById(R.id.but_recent_ads);
        butAlerts		= (RelativeLayout)findViewById(R.id.but_alerts);
        syncMan			= new SyncManager(this);
        
        
        // check if the use is logged in
        if(user.isLoggedIn()){
        	txtUserName.setText("Salut " + user.getName());
        	butLogin.setText("Logout");
        } else{
        	// the user is not logged in.
        	// we have to update the UI to reflect this state
        	butLogin.setText("Login");
        }
        
        // set listeners
        butLogin.setOnClickListener(this);
        butAddNewAdd.setOnClickListener(this);   
        butShowAds.setOnClickListener(this);
        butAlerts.setOnClickListener(this);
        syncMan.setOnSyncedNotifier(this);

        
        
    }
// ========================== Interfaces ========================
	@Override
	public void onClick(View v) {
		switch (v.getId()) {		    
		
		case R.id.but_alerts:
			Intent toAlertsActivity = new Intent(MainActivity.this, AlertsActivity.class);
			startActivity(toAlertsActivity);
			break;
		case R.id.but_recent_ads:
			// show the ads activity
			Intent toShowAdsActivity = new Intent(MainActivity.this, ShowAdsActivity.class);
			startActivity(toShowAdsActivity);
			break;
		case R.id.but_add_ad:
			// start the create new ad activity
			Intent toCreateNewAdActivity = new Intent(MainActivity.this, CreateNewAddActivity.class);
			startActivity(toCreateNewAdActivity);
			break;
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(MainActivity.this, LoginActivity.class);
				toLoginIntent.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
				startActivity(toLoginIntent);
				finish();
			} else{
				// the user is logged in. 
				// Now, we will log him out.
				api.logoutUser(user, new ILoginNotifier() {					
						@Override
						public void onLogout(boolean isSuccess, int pUserId) {
							if(isSuccess){
								api.updateDeviceId(user.getId(), "NULL");
								// logout done
								user.logout();
								Toast.makeText(MainActivity.this, "Logout succes.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Salut");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(MainActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
							}
						}					
						@Override
						public void onLogin(boolean isSuccess, User pUser) {
							// nothing here
						}
					});
			}
			break;

		default:
			break;
		}
	}
	@Override
	public void onCategoriesSyncronized(boolean isSuccess, ArrayList<Category> pCategories) {
		Console.debug(TAG, "onCategoriesReceveid: " + isSuccess + " " + pCategories);		
	}
	@Override
	public void onCategoriesLoaded(boolean isSuccess,
			ArrayList<Category> pcaArrayList) {
		// nothing here
	}
	@Override
	public void onCitiesSyncronized(boolean isSuccess, ArrayList<City> pCities) {
		Console.debug(TAG, "onCitiesSyncronized: " + isSuccess + " " + pCities);
	}
	@Override
	public void onCitiesLoaded(boolean isSuccess, ArrayList<City> pCities) {
	}
}
/*
	DQAAAMEAAAAcZArTYpaDS14tBrSxrzga3oH2BTkrVz4o7SlX_m_6YBY5kXjSPH6s8ZKmRfqHbPPQYkULM_FC4p80uQQ6WWqdJFiPNWMsmEvnrQUI3L8jKS3p3I6nNL2vsOgGkT5xFdXVJhNIpR08enEqOD5w--hGeCdXrzcOiOzyPfvGo6d0K2kmT84ZmyXqBxEHcr41-7vhJZnYrXOWTIjgo20yytFOvu2Lo386Gb-m9S99Q5U_lmSrnaSMtwAFyxVFGtIMl7MwXy6ijyJR-wY4_dq3nw6s
	APA91bFJwCGGcgG9eKvJLCzI7c_5L0ITxThuzG5G2SqFjX82uDqyR_h8ZhKFnCTP_Dw2Pr22qNW6hcNIQ3xilmKw4_37b7bqvEKYLVFT-s8Rijrdi02Xhxgvwvk62zxQgtew6rIiBb7R11l7M_vlGDb7_X6st_Cshg



	curl --header 'Authorization: GoogleLogin auth=DQAAAMEAAAAcZArTYpaDS14tBrSxrzga3oH2BTkrVz4o7SlX_m_6YBY5kXjSPH6s8ZKmRfqHbPPQYkULM_FC4p80uQQ6WWqdJFiPNWMsmEvnrQUI3L8jKS3p3I6nNL2vsOgGkT5xFdXVJhNIpR08enEqOD5w--hGeCdXrzcOiOzyPfvGo6d0K2kmT84ZmyXqBxEHcr41-7vhJZnYrXOWTIjgo20yytFOvu2Lo386Gb-m9S99Q5U_lmSrnaSMtwAFyxVFGtIMl7MwXy6ijyJR-wY4_dq3nw6s' 'https://android.apis.google.com/c2dm/send' -d registration_id=APA91bFJwCGGcgG9eKvJLCzI7c_5L0ITxThuzG5G2SqFjX82uDqyR_h8ZhKFnCTP_Dw2Pr22qNW6hcNIQ3xilmKw4_37b7bqvEKYLVFT-s8Rijrdi02Xhxgvwvk62zxQgtew6rIiBb7R11l7M_vlGDb7_X6st_Cshg -d 'data.payload=This data will be send to your application as payload' -d collapse_key=0	
*/