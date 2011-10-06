package com.liviu.apps.iasianunta;

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
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.TopCategoryView;

public class ShowAdsActivity extends Activity{
	// Constants
	private final String	TAG 		= "ShowAdsActivity";
	private final int    	ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(ShowAdsActivity.class);
	
	// Data
	private User 			user;
	private AdsManager 		adsMan;
	private TopCateogoriesAdapter adapterTopCategories;
	
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
        adapterTopCategories 	= new TopCateogoriesAdapter(this);
        layoutContent			= (RelativeLayout)findViewById(R.id.layout_content); 

                        
        categoryView.addCategory(new Category("Cat "), 100, Color.parseColor("#00aeff"));
        categoryView.addCategory(new Category("Cat "), 100, Color.parseColor("#8FA359"));        
          
	}
}
