package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.CategoriesAdapter;
import com.liviu.apps.iasianunta.adapters.FiltersAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.data.ContentFilter;
import com.liviu.apps.iasianunta.data.PriceFilter;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAlertsNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.managers.AlertManager;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class CreateAlertActivity extends Activity implements OnClickListener,
															 IAlertsNotifier,
															 ISyncNotifier{
	// Constants
	private 		final String TAG 		 = "CreateAlertActivity";
	public	static  final int	 ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(CreateAlertActivity.class);
	public  static  final int    RESULT_ALERT_ADDED = 1;
	
	// Data
	private User 				user;
	private AlertManager		alertsMan;
	private FiltersAdapter		adapterContentFilters;
	private FiltersAdapter		adapterPriceFilters;
	private CategoriesAdapter 	adapterCategories;
	private Alert				newAlert;		
	private AdsManager			adsMan;
	private API					api;
	
	// UI
	private ProgressBar		progressBar;
	private LTextView		progressText;
	private Button			butViewFiltersDetails;
	private LTextView		txtFilterDetails;	
	private RelativeLayout	lstContentFilters;
	private RelativeLayout	lstPriceFilters;
	private LEditText		edtxContentFilter;
	private LEditText		edtxMinPriceFilter;
	private LEditText		edtxMaxPriceFilter;
	private Spinner			spinCurrency;
	private Button			butAddContentFilter;
	private Button			butAddPriceFilter;
	private Button			butRemoveContentFilters;
	private Button			butRemovePriceFilters;
	private LEditText		edtxName;
	private Spinner			spinCategories;	
	private Button			butAddAlert;
	private Button			butBack;
	private Button			butLogin;
	private LTextView 		txtUserName;
	
	// Services
	private InputMethodManager	imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.create_alert_layout);   
        
        if(!Utils.isConnected(this)){
        	Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }                
        
        user = User.getInstance();        
        if(!user.isLoggedIn()){
        	// the user is not logged in
        	// we have to redirect him to login activity
        	Intent toLogin = new Intent(CreateAlertActivity.this, LoginActivity.class);
        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
        	startActivity(toLogin);
        	finish();
        	return;
        }        
        
        // Initialize
        alertsMan 				= new AlertManager();
        api						= new API();
        progressBar				= (ProgressBar)findViewById(R.id.create_alert_progress);
        progressText			= (LTextView)findViewById(R.id.create_alert_txt_progress);
        butViewFiltersDetails 	= (Button)findViewById(R.id.create_alert_but_filter_info);
        txtFilterDetails 		= (LTextView)findViewById(R.id.create_alert_txt_filters_info);
        lstContentFilters		= (RelativeLayout)findViewById(R.id.lst_content_filters);
        lstPriceFilters			= (RelativeLayout)findViewById(R.id.lst_price_filters);
        adapterContentFilters 	= new FiltersAdapter(this);
        adapterPriceFilters		= new FiltersAdapter(this);
        butAddContentFilter		= (Button)findViewById(R.id.create_alert_but_add_content_filter);
        butAddPriceFilter		= (Button)findViewById(R.id.create_alert_but_add_price_filter);
        edtxContentFilter		= (LEditText)findViewById(R.id.create_alert_edtx_filter_content);
        edtxMaxPriceFilter		= (LEditText)findViewById(R.id.create_alert_edtx_filter_max_price);
        edtxMinPriceFilter		= (LEditText)findViewById(R.id.create_alert_edtx_filter_min_price);
        spinCurrency			= (Spinner)findViewById(R.id.create_alert_currency);
        newAlert				= new Alert();
        imm						= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        butRemoveContentFilters	= (Button)findViewById(R.id.lst_content_filters_content_remove);
        butRemovePriceFilters	= (Button)findViewById(R.id.lst_content_filters_price_remove);
        edtxName				= (LEditText)findViewById(R.id.create_alert_edx_name);
        spinCategories			= (Spinner)findViewById(R.id.create_alert_spinner_categorie);
        adapterCategories		= new CategoriesAdapter(this);
        butAddAlert				= (Button)findViewById(R.id.but_add_alert);
        adsMan					= new AdsManager(this);
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
        
        
        // set listeners and notifiers
        butLogin.setOnClickListener(this);
        butBack.setOnClickListener(this);
        butViewFiltersDetails.setOnClickListener(this);
        butAddContentFilter.setOnClickListener(this);  
        butAddPriceFilter.setOnClickListener(this);
        butRemoveContentFilters.setOnClickListener(this);
        butRemovePriceFilters.setOnClickListener(this);    
        butAddAlert.setOnClickListener(this);
        alertsMan.setAlertsNotifier(this);
        adsMan.setDBSyncNotifier(this);
        adsMan.getCategories();        
	}

	@Override 
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(CreateAlertActivity.this, LoginActivity.class);
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
								Toast.makeText(CreateAlertActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Salut");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(CreateAlertActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
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
		case R.id.but_add_alert:
			if(edtxName.getText().length() == 0){
				Toast.makeText(CreateAlertActivity.this, "Numele alertei este invalid.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(adapterContentFilters.getCount() == 0 && adapterPriceFilters.getCount() == 0){
				Toast.makeText(CreateAlertActivity.this, "Nu ati specificat niciun filtru.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// get category
			int selectedCategoryPosition = spinCategories.getSelectedItemPosition();
			if(Spinner.INVALID_POSITION == selectedCategoryPosition)
				selectedCategoryPosition = 0;			
			newAlert.setCategoryId(adapterCategories.getItem(selectedCategoryPosition).getId());
			newAlert.setTitle(edtxName.getText().toString());
			for(int i = 0; i < adapterContentFilters.getCount(); i++)
				newAlert.addFilter(adapterContentFilters.getItem(i));
			for(int i = 0; i < adapterPriceFilters.getCount(); i++)
				newAlert.addFilter(adapterPriceFilters.getItem(i));
			newAlert.setUserId(user.getId());
			newAlert.setCityId(user.getCity().getId()); 
			Console.debug(TAG, "new alert to add: " + newAlert);
			alertsMan.addNewAlert(newAlert, user.getAuthName(), user.getPassword());
			break;
		case R.id.lst_content_filters_price_remove:
			adapterPriceFilters.clear();
			lstPriceFilters.removeAllViews();
			lstPriceFilters.setVisibility(View.GONE);
			butRemovePriceFilters.setVisibility(View.GONE);			
			break;
		case R.id.lst_content_filters_content_remove:
			adapterContentFilters.clear();
			lstContentFilters.removeAllViews();
			lstContentFilters.setVisibility(View.GONE);
			butRemoveContentFilters.setVisibility(View.GONE);
			break;
		case R.id.create_alert_but_filter_info:
			if(butViewFiltersDetails.getText().toString().equals("Despre filtre")){
				butViewFiltersDetails.setText("Ascunde");
				txtFilterDetails.setVisibility(View.VISIBLE);
			}else{
				butViewFiltersDetails.setText("Despre filtre");
				txtFilterDetails.setVisibility(View.GONE);				
			}				
			break;
		case R.id.create_alert_but_add_price_filter:
			if(edtxMinPriceFilter.getText().length() == 0 || edtxMaxPriceFilter.getText().length()== 0){
				Toast.makeText(CreateAlertActivity.this, "Va rugam specificati pretul minim si maxim.", Toast.LENGTH_SHORT).show();
			} else if(Double.parseDouble(edtxMinPriceFilter.getText().toString()) > Double.parseDouble(edtxMaxPriceFilter.getText().toString())){
				Toast.makeText(CreateAlertActivity.this, "Pretul minim nu poate fi mai mare decat pretul maxim.", Toast.LENGTH_SHORT).show();
			} else if(adapterPriceFilters.getCount() >= 1){
				Toast.makeText(CreateAlertActivity.this, "Ne pare rau insa nu puteti adauga mai mult de 1 filtru.", Toast.LENGTH_SHORT).show();
			} else{
				int selectedPosition = spinCurrency.getSelectedItemPosition();				
				if(Spinner.INVALID_POSITION == selectedPosition)
					selectedPosition = 0;
				String currency = (String)spinCurrency.getAdapter().getItem(selectedPosition);
				adapterPriceFilters.add(new PriceFilter(Double.parseDouble(edtxMinPriceFilter.getText().toString()), Double.parseDouble(edtxMaxPriceFilter.getText().toString()), currency));
				lstPriceFilters.addView(adapterPriceFilters.getView(adapterPriceFilters.getCount() - 1, null, null));				
				
				if(adapterPriceFilters.getCount() != 0){
					lstPriceFilters.setVisibility(View.VISIBLE);
					butRemovePriceFilters.setVisibility(View.VISIBLE);
				}
				imm.hideSoftInputFromWindow(edtxContentFilter.getWindowToken(), 0);
				edtxMaxPriceFilter.setText("");
				edtxMinPriceFilter.setText("");
			}
			break;
		case R.id.create_alert_but_add_content_filter:
			if(edtxContentFilter.getText().length() == 0){
				Toast.makeText(CreateAlertActivity.this, "Va rugam specificati un filtru.", Toast.LENGTH_SHORT).show();
			} else if(adapterContentFilters.getCount() >= 3){
				Toast.makeText(CreateAlertActivity.this, "Ne pare rau insa nu puteti adauga mai mult de 3 filtre.", Toast.LENGTH_SHORT).show();
			} else{				
				adapterContentFilters.add(new ContentFilter(edtxContentFilter.getText().toString()));
				lstContentFilters.addView(adapterContentFilters.getView(adapterContentFilters.getCount() - 1, null, null));				
				
				if(adapterContentFilters.getCount() != 0){
					lstContentFilters.setVisibility(View.VISIBLE);
					butRemoveContentFilters.setVisibility(View.VISIBLE);
				}
				imm.hideSoftInputFromWindow(edtxContentFilter.getWindowToken(), 0);
				edtxContentFilter.setText("");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onAlertsLoaded(boolean isSuccess, ArrayList<Alert> pLoadedAlerts) {
	}

	@Override
	public void onAlertAdded(boolean isSuccess, Alert pAddedAlert) {
		if(isSuccess){
			Toast.makeText(CreateAlertActivity.this, "Alerta dvs. a fost adaugata.", Toast.LENGTH_SHORT).show();
			Intent data = new Intent();
			data.putExtra("added_alert", pAddedAlert.toJSON().toString());
			setResult(RESULT_ALERT_ADDED, data);
			finish();
		} else{
			Toast.makeText(CreateAlertActivity.this, "Alerta dvs. nu a putut fi adaugata. Va rugam reincercati peste cateva minute.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAlertLoaded(boolean isSuccess, Alert pLoadedAlert) {
	}

	@Override
	public void onCategoriesSyncronized(boolean isSuccess,
			ArrayList<Category> pCategories) {
	}

	@Override
	public void onCategoriesLoaded(boolean isSuccess, ArrayList<Category> pCategories) {
		if(isSuccess){
			for(int i = 0; i < pCategories.size(); i++){
				if(!pCategories.get(i).getName().equals("Toate"))
					adapterCategories.add(pCategories.get(i));
			}										
		}else{
			adapterCategories.add(new Category(1, "Toate"));
		}
		spinCategories.setAdapter(adapterCategories);		
	}

	@Override
	public void onCitiesSyncronized(boolean isSuccess, ArrayList<City> pCities) {
	}

	@Override
	public void onCitiesLoaded(boolean isSuccess, ArrayList<City> pCities) {
	}

	@Override
	public void onAlertRemoved(boolean isSuccess, Alert pAlertRemoved) {
	}
}
