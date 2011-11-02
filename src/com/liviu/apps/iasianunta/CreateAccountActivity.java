package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.CitiesAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.interfaces.ISyncNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.ui.LTextView;

public class CreateAccountActivity extends Activity implements ISyncNotifier,
															   OnClickListener{
	
	// Constants
	private 		final String TAG 		 = "CreateAccountActivity";
	public	static  final int	 ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(CreateAccountActivity.class);
		
	// Data
	private API api;
	private CitiesAdapter	adapterCitites;
	private AdsManager		adsManager; 
	private Handler			handler;
	
	// UI
	private LEditText	edtxUserName;
	private LEditText 	edtxUserPassword;
	private LEditText	edtxUserRepeatPassword;
	private LEditText	edtxUserEmail;
	private LEditText	edtxUserRealName;
	private Spinner		spinUserCity;
	private Button		butBack;
	private Button		butCreate;
	private ProgressBar barLoading;
	private LTextView	txtLoading;
	private RelativeLayout	layoutContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.create_account_layout);   
                
        // Init
        api = new API();
        edtxUserName 			= (LEditText)findViewById(R.id.edtx_username);
        edtxUserPassword		= (LEditText)findViewById(R.id.edtx_password);
        edtxUserRepeatPassword	= (LEditText)findViewById(R.id.edtx_repeat_password);
        edtxUserEmail			= (LEditText)findViewById(R.id.edtx_email);
        spinUserCity			= (Spinner)findViewById(R.id.cities);        
        adapterCitites			= new CitiesAdapter(this);
        adsManager				= new AdsManager(this);
        butBack					= (Button)findViewById(R.id.but_back);
        butCreate				= (Button)findViewById(R.id.but_create);
        edtxUserRealName		= (LEditText)findViewById(R.id.edtx_realname);
        barLoading				= (ProgressBar)findViewById(R.id.create_progress);
        txtLoading				= (LTextView)findViewById(R.id.txt_create_progress);
        layoutContent			= (RelativeLayout)findViewById(R.id.layout_content);
        handler					= new Handler(){
        	public void handleMessage(android.os.Message msg) {
        		barLoading.setVisibility(View.GONE);
        		txtLoading.setVisibility(View.GONE);
        		layoutContent.setVisibility(View.VISIBLE);
        		if(msg.what == 1){
        			// user created
        			Toast.makeText(CreateAccountActivity.this, "Contul dvs. este activ.", Toast.LENGTH_SHORT).show();
        			finish();
        		}else{
        			// create user error
        			butCreate.setEnabled(true);
        			Toast.makeText(CreateAccountActivity.this, "Din pacate numele utilizatorul specificat de dvs. este folosit de un alt user.", Toast.LENGTH_SHORT).show();
        		}
        	};
        };
        
        // Configure
        spinUserCity.setAdapter(adapterCitites);
        
        // Set notifiers
        adsManager.setDBSyncNotifier(this);
        butBack.setOnClickListener(this);
        butCreate.setOnClickListener(this);
        
        // Start tasks
        adsManager.getCities();                       
	}

	@Override
	public void onCategoriesSyncronized(boolean isSuccess,
			ArrayList<Category> pCategories) {
	}

	@Override
	public void onCategoriesLoaded(boolean isSuccess,
			ArrayList<Category> pCategories) {
	}

	@Override
	public void onCitiesSyncronized(boolean isSuccess, ArrayList<City> pCities) {
	}

	@Override
	public void onCitiesLoaded(boolean isSuccess, ArrayList<City> pCities) {
		if(isSuccess){
			for(int i = 0; i < pCities.size(); i++){
				adapterCitites.add(pCities.get(i));
			}
			adapterCitites.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_back:
			finish();
			break;
		case R.id.but_create:
			if(edtxUserRealName.getText().length() == 0){
				Toast.makeText(CreateAccountActivity.this, "Va rugam sa specificat numele dvs.", Toast.LENGTH_SHORT).show();
				return;
			}						
			if(edtxUserName.getText().length() == 0){
				Toast.makeText(CreateAccountActivity.this, "Campul 'username' nu poate fi gol.", Toast.LENGTH_SHORT).show();
				return;
			}			
			if(edtxUserPassword.getText().length() == 0 || edtxUserRepeatPassword.getText().length() == 0){
				Toast.makeText(CreateAccountActivity.this, "Campul 'password' nu este valid.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!edtxUserPassword.getText().toString().equals(edtxUserRepeatPassword.getText().toString())){
				Toast.makeText(CreateAccountActivity.this, "Parolele nu coincid.", Toast.LENGTH_SHORT).show();
				return;
			}			
			if(edtxUserEmail.getText().length() == 0){
				Toast.makeText(CreateAccountActivity.this, "Campul 'email' este invalid.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(spinUserCity.getSelectedItemPosition() == Spinner.INVALID_POSITION){
				Toast.makeText(CreateAccountActivity.this, "Campul 'oras' este invalid.", Toast.LENGTH_SHORT).show();
			}
			layoutContent.setVisibility(View.GONE);
			barLoading.setVisibility(View.VISIBLE);
			txtLoading.setVisibility(View.VISIBLE);
			butCreate.setEnabled(false);
			Thread tCreate = new Thread(new Runnable() {				
				@Override
				public void run() {
					boolean isSuccess = api.createUser(edtxUserRealName.getText().toString(), edtxUserName.getText().toString(), edtxUserPassword.getText().toString(), edtxUserEmail.getText().toString(), adapterCitites.getItem(spinUserCity.getSelectedItemPosition()).getId());
					handler.sendEmptyMessage(isSuccess == true ? 1 : 0);
				}
			});
			tCreate.start();			
			
			break;

		default:
			break;
		}
	}

}
