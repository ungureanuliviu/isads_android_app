package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.AdsAdapter;
import com.liviu.apps.iasianunta.adapters.ShowAdImagesAdapter;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.LocalCache;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ICategoryNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.ui.TopCategoryView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class ShowAdsActivity extends Activity implements IAdsNotifier, 
														 OnScrollListener,
														 OnClickListener{
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
	private AdsAdapter		adsAdapter;
	private int				currentPage 	= 0;	
	private int				adsPerPage  	= 10;
	private boolean 		canLoadMoreAds 	= true;
	private ShowAdImagesAdapter galleryAdapter;
	
	// UI
	private TopCategoryView categoryView;
	private RelativeLayout	layoutContent;
	private ListView		lstAds;
	private ProgressBar		pBarLoading;
	private LTextView		txtLoading;
	private RelativeLayout	loadMoreAdsLayout;
	private Gallery			galImages;
	private Button			butGalBack;
	private RelativeLayout	overlay;
	private RelativeLayout	layoutTop;
		
	
	// Services
	private Vibrator		vbb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.show_ads_layout);		
        
        if(!Utils.isConnected(this)){
        	Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }        
        user = User.getInstance();        
                
        // Initialize objects		
        adsMan 					= new AdsManager(this);        
        categoryView			= (TopCategoryView)findViewById(R.id.top_categories);        
        layoutContent			= (RelativeLayout)findViewById(R.id.layout_content); 
        lstAds					= (ListView)findViewById(R.id.ads_list);
        adsAdapter				= new AdsAdapter(this);
        loadMoreAdsLayout		= (RelativeLayout)findViewById(R.id.layout_progress);
        pBarLoading				= (ProgressBar)findViewById(R.id.loading_ads_progress);
        txtLoading				= (LTextView)findViewById(R.id.txt_loading_ads);
        vbb						= (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        overlay					= (RelativeLayout)findViewById(R.id.layout_images);
        galImages				= (Gallery)findViewById(R.id.gallery_images);
        butGalBack				= (Button)findViewById(R.id.gallery_but_back);
        galleryAdapter			= new ShowAdImagesAdapter(this);
        layoutTop				= (RelativeLayout)findViewById(R.id.layout_top);
        
        galImages.setAdapter(galleryAdapter);
        // load categories
        // get them from cache is possible
        if(null != LocalCache.categories){
        	int color = Color.parseColor("#00aeff");
			for(int i = 0; i < LocalCache.categories.size(); i++){
				categoryView.addCategory(LocalCache.categories.get(i), 100, color);
			}
        } else{
        	// the cache is empty: get them from db        	
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
						// feed the cache
						LocalCache.categories = pCategories;
					}
				}
			}); 
        }
        adsMan.setAdsNotifier(this);
        adsMan.getAds(1, currentPage, adsPerPage);
        lstAds.setAdapter(adsAdapter);
        lstAds.setOnScrollListener(this);
        adsAdapter.setOnClickListener(this);
        butGalBack.setOnClickListener(this);
        layoutTop.setOnClickListener(this);
	}

	@Override
	public void onAdSaved(boolean isSuccess, Ad pSavedAd) {
	}

	@Override
	public void onAdRemoteAdded(boolean isSuccess, Ad pAdRemoteAdded) {
	}

	@Override
	public void onAdsLoaded(boolean isSuccess, int pCategoryId, int pPage,
			int pAdsPerPage, ArrayList<Ad> pLoadedAds) {
		if(isSuccess){			
			for(int i = 0; i < pLoadedAds.size(); i++){
				adsAdapter.addItem(pLoadedAds.get(i));				
			}
			adsMan.loadThImages(pLoadedAds);
			pBarLoading.setVisibility(View.GONE);
			txtLoading.setVisibility(View.GONE);
			loadMoreAdsLayout.setVisibility(View.GONE);
			adsAdapter.notifyDataSetChanged();
			canLoadMoreAds = true;			
			vbb.vibrate(CreateNewAddActivity.VIB_LENGTH);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {		
		if((firstVisibleItem + 1 + visibleItemCount) > totalItemCount && totalItemCount != 0){						
			if(canLoadMoreAds){				
				currentPage++;				
				adsMan.getAds(1,currentPage, adsPerPage);		
				loadMoreAdsLayout.setVisibility(View.VISIBLE);				
			}
			canLoadMoreAds = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onImageDownloaded(boolean isSuccess, int pAdId, Bitmap pImg, boolean isFullImage) {
		Console.debug(TAG, "onImageDownloaded: " + isSuccess + " adId: " + pAdId + " bitmap" + pImg + " isFullImage: " + isFullImage);
		if(isSuccess){
			Console.debug(TAG, "width: " + pImg.getWidth() + " height: " + pImg.getHeight());
			if(isFullImage){
				for(int i = 0; i < galleryAdapter.getCount(); i++)
					if(galleryAdapter.getItem(i).getBitmap() == null){
							galleryAdapter.getItem(i).setBitmap(pImg);
							Console.debug(TAG, "i is: " + i);
							break;							
					}
				
				galleryAdapter.notifyDataSetChanged();
			} else{
				adsAdapter.setImage(pAdId, pImg);
				adsAdapter.notifyDataSetChanged();			
			}
		}
	}

	@Override
	public void onClick(View v) {
		Integer positionObj = ((Integer)v.getTag());
		if(null == positionObj){
			Console.debug(TAG, "Position is null in tag");
			positionObj = new Integer(-1);
		}
			
		int position = positionObj.intValue();
		Console.debug(TAG, "clicked position: " + position);
		switch (v.getId()) {
			case R.id.ad_but_call:
				if(position == -1)
					return;
				
				if(adsAdapter.getItem(position).getAd().getPhone() != null && adsAdapter.getItem(position).getAd().getPhone().length() > 0){
		            Intent callIntent = new Intent(Intent.ACTION_CALL);
		            callIntent.setData(Uri.parse("tel:"+adsAdapter.getItem(position).getAd().getPhone()));
		            startActivity(callIntent);
				} else{
					Toast.makeText(ShowAdsActivity.this, "Numarul " + adsAdapter.getItem(position).getAd().getPhone() + " este invalid.", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.ad_view_images:
				Console.debug(TAG, "hereee");
				if(position == -1)
					return;
				
				overlay.setVisibility(View.VISIBLE);			
				if(galleryAdapter.getCount() > 0 && galleryAdapter.getAdId() == adsAdapter.getItem(position).getAd().getId()){
					
				} else{
					if(galleryAdapter.getCount() > 0){
						galleryAdapter.clear();				  
						galleryAdapter.notifyDataSetChanged();
					}
					adsMan.loadImages(adsAdapter.getItem(position).getAd());
					galleryAdapter.setAdId(adsAdapter.getItem(position).getAd().getId());
				
					for(int i = 0; i < adsAdapter.getItem(position).getAd().getImages().size(); i++){					
						galleryAdapter.addItem(adsAdapter.getItem(position).getAd().getImages().get(i));
					}
				}
				galleryAdapter.notifyDataSetChanged();
				break;
			case R.id.gallery_but_back:
				overlay.setVisibility(View.GONE);
				break;
			case R.id.layout_top:
				if(adsAdapter.getCount() > 0)
					lstAds.setSelectionAfterHeaderView();	
				break;
			default:
				break;
		}
	}			
}
