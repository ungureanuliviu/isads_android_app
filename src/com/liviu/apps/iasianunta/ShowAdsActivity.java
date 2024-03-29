package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import org.json.JSONObject;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.AdsAdapter;
import com.liviu.apps.iasianunta.adapters.CitiesAdapter;
import com.liviu.apps.iasianunta.adapters.ShowAdImagesAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.ui.TopCategoryView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class ShowAdsActivity extends Activity implements IAdsNotifier, 
														 OnScrollListener,
														 OnClickListener, 
														 ISyncNotifier,
														 OnItemSelectedListener{
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
	private int				currentCategoryId = 1;
	private boolean 		canLoadMoreAds 	= true;
	private ShowAdImagesAdapter galleryAdapter;
	private CitiesAdapter	adapterCities;
	private City 			selectedCity;
	private API				api;
	
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
	private LTextView		txtNoAds;
	private Spinner			spinCities;
	private LTextView		txtCity;
	private Button			butAddAd;
	private Button			butBack;
	private Button			butLogin;
	private LTextView 		txtUserName;	
		
	
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
        api						= new API();
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
        txtNoAds				= (LTextView)findViewById(R.id.txt_no_ads);
        spinCities				= (Spinner)findViewById(R.id.spin_cities);
        txtCity					= (LTextView)findViewById(R.id.city);
        adapterCities			= new CitiesAdapter(this);
        selectedCity 			= user.getCity();
        butAddAd				= (Button)findViewById(R.id.but_add_ad);
        butBack					= (Button)findViewById(R.id.but_back);
        butLogin 				= (Button)findViewById(R.id.main_but_login);
        txtUserName 			= (LTextView)findViewById(R.id.user_name);
        
        // check if the use is logged in
        if(user.isLoggedIn()){
        	txtUserName.setText("Salut " + user.getName());
        	butLogin.setText("Logout");
        } else{
        	// the user is not logged in.
        	// we have to update the UI to reflect this state
        	butLogin.setText("Login");
        }                        
        txtCity.setText(selectedCity.getName());
        
        galImages.setAdapter(galleryAdapter);
        categoryView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				// get position
				Integer position = (Integer)v.getTag();
				if(null != position){
					position = position.intValue();
				}else{
					Console.debug(TAG, "cat position is null");
					return;
				}			
				
				Category selectedCategory = categoryView.getItem(position);
				currentPage 			  = 0;
				adsAdapter.clear();
				adsAdapter.notifyDataSetChanged();
				
				pBarLoading.setVisibility(View.VISIBLE);
				txtLoading.setVisibility(View.VISIBLE);
				loadMoreAdsLayout.setVisibility(View.GONE);
				txtNoAds.setVisibility(View.GONE);
				adsAdapter.notifyDataSetChanged();
				canLoadMoreAds = false;
				currentCategoryId = selectedCategory.getId();
				adsMan.getAds(currentCategoryId, currentPage, adsPerPage, selectedCity.getId());				
			}
		});
        
        
        butLogin.setOnClickListener(this);
        adsMan.setAdsNotifier(this);
        adsMan.setDBSyncNotifier(this);                               
        lstAds.setAdapter(adsAdapter);
        lstAds.setOnScrollListener(this);
        adsAdapter.setOnClickListener(this);
        butGalBack.setOnClickListener(this);
        layoutTop.setOnClickListener(this);          
        spinCities.setOnItemSelectedListener(this);
        txtCity.setOnClickListener(this);
        butBack.setOnClickListener(this);
        butAddAd.setOnClickListener(this);
        
        // load categories and cities
        adsMan.getCategories();
        adsMan.getCities();
        
        spinCities.setAdapter(adapterCities);            
        
        // get ads
        adsMan.getAds(currentCategoryId, currentPage, adsPerPage, selectedCity.getId());
        txtNoAds.setVisibility(View.GONE);
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
			adsAdapter.notifyDataSetChanged();
			canLoadMoreAds = true;			
			vbb.vibrate(CreateNewAddActivity.VIB_LENGTH);			
			if(adsAdapter.getCount() == 0){
				Console.debug(TAG, "visible");
				txtNoAds.setVisibility(View.VISIBLE);
			}
			else{
				Console.debug(TAG, "gone");
				txtNoAds.setVisibility(View.GONE);
			}
		}else{
			Console.debug(TAG, "visible2");
			  if(adsAdapter.getCount() == 0)
				  txtNoAds.setVisibility(View.VISIBLE);
		}
		
		pBarLoading.setVisibility(View.GONE);
		txtLoading.setVisibility(View.GONE);
		loadMoreAdsLayout.setVisibility(View.GONE);		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {		
		if((firstVisibleItem + 1 + visibleItemCount) > totalItemCount && totalItemCount != 0){						
			if(canLoadMoreAds){				
				currentPage++;				
				adsMan.getAds(currentCategoryId,currentPage, adsPerPage, selectedCity.getId());		
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
		//Console.debug(TAG, "onImageDownloaded: " + isSuccess + " adId: " + pAdId + " bitmap" + pImg + " isFullImage: " + isFullImage);
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
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(ShowAdsActivity.this, LoginActivity.class);
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
								Toast.makeText(ShowAdsActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Salut");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(ShowAdsActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
							}
						}					
						@Override
						public void onLogin(boolean isSuccess, User pUser) {
							// nothing here
						}
					});
			}
			break;				
			case R.id.but_back:
				finish();
				break;
			case R.id.but_add_ad:
				Intent toCreateAd = new Intent(ShowAdsActivity.this, CreateNewAddActivity.class);
				startActivity(toCreateAd);
				break;
			case R.id.city:
				Console.debug(TAG, "on city txt clicked");
				spinCities.performClick();				
				break;  
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
			case R.id.ad_but_comments:
				if(position == -1){
					return;
				}
				Intent toCommentsActivity = new Intent(ShowAdsActivity.this, CommentsActivity.class);
				JSONObject jsonAd = adsAdapter.getItem(position).getAd().toJson();
				if(null != jsonAd){
					Console.debug(TAG, "json ad: " + jsonAd);				
					toCommentsActivity.putExtra(Utils.TRANSPORT_KEY, jsonAd.toString());
					toCommentsActivity.putExtra("ad_index", position + 1);
					startActivityForResult(toCommentsActivity, CommentsActivity.ACTIVITY_ID);				
				} else{
					Toast.makeText(ShowAdsActivity.this, "Internal problem.", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}			
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Console.debug(TAG, "onActivityResult: requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
		
		if(requestCode == CommentsActivity.ACTIVITY_ID){
			if(resultCode == Utils.RESULT_CODE_COMMENTS_COUNT_CHANGED){
				if(null != data){
					int newCommentsCount = data.getIntExtra("new_comments_count", -1);
					int ad_id 			 = data.getIntExtra("ad_id", -1);
					
					for(int i = 0; i < adsAdapter.getCount(); i++){
						if(adsAdapter.getItem(i).getAd().getId() == ad_id){
							adsAdapter.getItem(i).getAd().setTotalComments(newCommentsCount);
							adsAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCategoriesSyncronized(boolean isSuccess,
			ArrayList<Category> pCategories) {
	}

	@Override
	public void onCategoriesLoaded(boolean isSuccess,ArrayList<Category> pCategories) {		
		Console.debug(TAG, "onCategoriesLoaded: " + isSuccess + " " + pCategories);
		int color = Color.parseColor("#24c1d8");
		if(isSuccess){
			for(int i = 0; i < pCategories.size(); i++){
				categoryView.addCategory(pCategories.get(i), 100, color);
			}			
		}
	}

	@Override
	public void onCitiesSyncronized(boolean isSuccess, ArrayList<City> pCities) {
	}

	@Override
	public void onCitiesLoaded(boolean isSuccess, ArrayList<City> pCities) {
		Console.debug(TAG, "onCititesLoaded: " + isSuccess + " pCitites: " + pCities);
		int selectedPosition = 0;
		if(isSuccess){
			for(int i = 0; i < pCities.size(); i++){				
				adapterCities.add(pCities.get(i));
				if(pCities.get(i).getId() == user.getCity().getId()){
					selectedPosition = i;
				}				
			}			
			adapterCities.notifyDataSetChanged();
			spinCities.setSelection(selectedPosition);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
		Console.debug(TAG, "onitemselected: " + selectedCity.getName().toLowerCase() + "        " + txtCity.getText().toString().toLowerCase() + " val: " + selectedCity.getName().toLowerCase().equals(txtCity.getText().toString().toLowerCase()));
		if(Spinner.INVALID_POSITION != position){
			selectedCity = adapterCities.getItem(position);
			if(!selectedCity.getName().toLowerCase().equals(txtCity.getText().toString().toLowerCase())){
				txtCity.setText(selectedCity.getName());			
				currentPage = 0;
				adsMan.getAds(currentCategoryId,currentPage, adsPerPage, selectedCity.getId());		
				pBarLoading.setVisibility(View.VISIBLE);
				txtLoading.setVisibility(View.VISIBLE);			
				loadMoreAdsLayout.setVisibility(View.GONE);
				txtNoAds.setVisibility(View.GONE);
				adsAdapter.clear();
				adsAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
