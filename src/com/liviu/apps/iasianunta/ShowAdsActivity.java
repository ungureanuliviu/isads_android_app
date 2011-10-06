package com.liviu.apps.iasianunta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Gallery;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.adapters.TopCateogoriesAdapter;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;

public class ShowAdsActivity extends Activity{
	// Constants
	private final String	TAG 		= "ShowAdsActivity";
	private final int    	ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(ShowAdsActivity.class);
	
	// Data
	private User 			user;
	private AdsManager 		adsMan;
	private TopCateogoriesAdapter adapterTopCategories;
	
	// UI
	private Gallery			galleryTopCategories;
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
        galleryTopCategories 	= (Gallery)findViewById(R.id.gal_top_categories);
        adapterTopCategories 	= new TopCateogoriesAdapter(this);
        layoutContent			= (RelativeLayout)findViewById(R.id.layout_content);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);        

        MarginLayoutParams mlp = (MarginLayoutParams) galleryTopCategories.getLayoutParams();
        mlp.setMargins(-(metrics.widthPixels/2) - 100, 
                       mlp.topMargin, 
                       mlp.rightMargin, 
                       mlp.bottomMargin
        );                               
        
        adapterTopCategories.addItem(new Category("Toate"));
        adapterTopCategories.addItem(new Category("Imobiliare")); 
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Toate"));
        adapterTopCategories.addItem(new Category("Imobiliare")); 
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));        
        adapterTopCategories.addItem(new Category("Toate"));
        adapterTopCategories.addItem(new Category("Imobiliare")); 
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));
        adapterTopCategories.addItem(new Category("Vanzari"));
        
        galleryTopCategories.setAdapter(adapterTopCategories);        
	}
}
