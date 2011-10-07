package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.adapters.TopCateogoriesAdapter;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ICategoryNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.TopCategoryView;
import com.liviu.apps.iasianunta.utils.Console;

public class ShowAdsActivity extends Activity{
	// Constants
	private final String	TAG 		= "ShowAdsActivity";
	private final int    	ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(ShowAdsActivity.class);
	/*
	private final int[]		COLORS		= new int[]{Color.parseColor("#00aeff"), Color.parseColor("#45e600"), 
													Color.parseColor("#ffcc00"), Color.parseColor("#ff8a00"),
													Color.parseColor("#ff3600")};
	 */
	// Data
	private User 			user;
	private AdsManager 		adsMan;	
	
	// UI
	private TopCategoryView categoryView;
	private RelativeLayout	layoutContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.show_ads_layout);		
        
        user = User.getInstance();        
        if(!user.isLoggedIn()){
        	// the user is not logged in
        	// we have to redirect him to login activity
        	Intent toLogin = new Intent(ShowAdsActivity.this, LoginActivity.class);
        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
        	startActivity(toLogin);
        	finish();
        	return;
        }
                
        // Initialize objects		
        adsMan 					= new AdsManager(this);
        categoryView			= (TopCategoryView)findViewById(R.id.top_categories);        
        layoutContent			= (RelativeLayout)findViewById(R.id.layout_content); 

        // load categories
        adsMan.getCategories(new ICategoryNotifier() {			
			@Override
			public void onCategoriesSyncronized(boolean isSuccess,
					ArrayList<Category> pCategories) {
			}
			
			@Override
			public void onCategoriesLoaded(boolean isSuccess, ArrayList<Category> pCategories) {
				Console.debug(TAG, "onCategoriesLoaded: " + isSuccess + " " + pCategories);
				int color = Color.parseColor("#00aeff");
				if(isSuccess){
					for(int i = 0; i < pCategories.size(); i++){
						categoryView.addCategory(pCategories.get(i), 100, color);
					}
				}
			}
		});          
	}
}
