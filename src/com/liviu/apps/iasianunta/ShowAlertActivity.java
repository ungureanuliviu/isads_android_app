package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.AdsAdapter;
import com.liviu.apps.iasianunta.adapters.AlertsAdapter;
import com.liviu.apps.iasianunta.adapters.FiltersAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAlertsNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AlertManager;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class ShowAlertActivity extends Activity implements 	IAlertsNotifier,
															OnItemClickListener,
															OnClickListener{
	// Constants
	private 		final String TAG ="ShowAlertActivity";
	public	static  final int	 ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(ShowAlertActivity.class);
	public  static  final String ALERT_TRANSPORT = "alert_transport";
	
	// Data
	private User 			user;
	private AlertManager	alertsMan;
	private AdsAdapter 		adapterAds;	
	private FiltersAdapter	adapterFilters;
	private Alert			alert;
	private API				api;
	
	// UI
	private ProgressBar		progressBar;
	private LTextView		progressText;
	private RelativeLayout	lstFilters;
	private ListView		lstAds;
	private LTextView		txtNoAlerts;
	private Button			butBack;
	private Button			butRemove;
	private LTextView		txtTitle;
	private LTextView		txtAddedDate;
	private LTextView		txtLastCheckedDate;
	private RelativeLayout	rellContentHolder;
	private LTextView		txtNoAds;
	private LTextView		txtCategory;
	private LTextView 		txtUserName;
	private Button			butLogin;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.view_layout);   
        alert = new Alert();
        
        if(!Utils.isConnected(this)){
        	Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }                
        
        user = User.getInstance();        
        if(!user.isLoggedIn()){
        	// the user is not logged in
        	// we have to redirect him to login activity
        	Intent toLogin = new Intent(ShowAlertActivity.this, LoginActivity.class);
        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
        	startActivity(toLogin);
        	finish();
        	return;
        }        
        
        if(null != getIntent()){
        	if(getIntent().getIntExtra(ALERT_TRANSPORT, -1) != -1){
        		alert.setId(getIntent().getIntExtra(ALERT_TRANSPORT, -1));
        	} else{
        		Toast.makeText(ShowAlertActivity.this, "Error: no alert id specified.", Toast.LENGTH_SHORT).show();
        		setResult(RESULT_CANCELED);
        		finish();
        		return;
        	}
        } else{
        	Toast.makeText(ShowAlertActivity.this, "Error: no alert id specified.", Toast.LENGTH_SHORT).show();
    		setResult(RESULT_CANCELED);
    		finish();
    		return;        	
        }
        
        alert.setUserId(user.getId());
        
        // Initialize
        alertsMan 		= new AlertManager();
        api				= new API();
        lstAds			= (ListView)findViewById(R.id.lst_latest_ads);
        lstFilters		= (RelativeLayout)findViewById(R.id.lst_filters);
        progressBar		= (ProgressBar)findViewById(R.id.show_alert_progress);
        progressText	= (LTextView)findViewById(R.id.txt_show_alert_progress);
        adapterAds		= new AdsAdapter(this);
        txtNoAlerts		= (LTextView)findViewById(R.id.txt_no_alerts);
        butRemove		= (Button)findViewById(R.id.but_alert_remove);
        adapterFilters	= new FiltersAdapter(this);
        txtTitle		= (LTextView)findViewById(R.id.txt_alert_title);
        txtAddedDate	= (LTextView)findViewById(R.id.txt_alert_added_date);
        txtLastCheckedDate	= (LTextView)findViewById(R.id.txt_alert_last_checked_date);
        rellContentHolder	= (RelativeLayout)findViewById(R.id.show_alert_content_holder);  
        txtNoAds		= (LTextView)findViewById(R.id.txt_no_ads);
        txtCategory		= (LTextView)findViewById(R.id.txt_alert_category);
        butLogin 		= (Button)findViewById(R.id.main_but_login);      
        txtUserName = (LTextView)findViewById(R.id.user_name);
        butBack			= (Button)findViewById(R.id.but_back);
        
        // check if the use is logged in
        if(user.isLoggedIn()){
        	txtUserName.setText("Salut " + user.getName());
        	butLogin.setText("Logout");
        } else{
        	// the user is not logged in.
        	// we have to update the UI to reflect this state
        	butLogin.setText("Login");
        }
        
        // Set notifiers
        butLogin.setOnClickListener(this);
        alertsMan.setAlertsNotifier(this);	
        adapterAds.setOnClickListener(this);
        butRemove.setOnClickListener(this);
        butBack.setOnClickListener(this);
        
        // Work
        lstAds.setAdapter(adapterAds);
        alertsMan.getAlertWithAds(alert.getId(), alert.getUserId(), user.getAuthName(), user.getPassword());
	}

	@Override
	public void onAlertsLoaded(boolean isSuccess, ArrayList<Alert> pLoadedAlerts) {		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {	
		Console.debug(TAG, "on click on " + position);
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
		case R.id.but_back:
    		setResult(RESULT_CANCELED);
    		finish();
			break;
		case R.id.but_alert_remove:
			alertsMan.removeAlert(alert, user.getId(), user.getAuthName(), user.getPassword());
			break;
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(ShowAlertActivity.this, LoginActivity.class);
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
								Toast.makeText(ShowAlertActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Salut");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(ShowAlertActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
							}
						}					
						@Override
						public void onLogin(boolean isSuccess, User pUser) {
							// nothing here
						}
					});
			}
			break;			
		case R.id.lst_alert_open:
			
			break;
		case R.id.but_add_alert:
			Intent toCreateAlertActivity = new Intent(ShowAlertActivity.this, CreateAlertActivity.class);
			startActivityForResult(toCreateAlertActivity, CreateAlertActivity.ACTIVITY_ID);
			break;
		case R.id.ad_but_call:
			if(position == -1)
				return;
			
			if(adapterAds.getItem(position).getAd().getPhone() != null && adapterAds.getItem(position).getAd().getPhone().length() > 0){
	            Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:"+adapterAds.getItem(position).getAd().getPhone()));
	            startActivity(callIntent);
			} else{
				Toast.makeText(ShowAlertActivity.this, "Numarul " + adapterAds.getItem(position).getAd().getPhone() + " este invalid.", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ad_but_comments:
			if(position == -1){
				return;
			}
			Intent toCommentsActivity = new Intent(ShowAlertActivity.this, CommentsActivity.class);
			JSONObject jsonAd = adapterAds.getItem(position).getAd().toJson();
			if(null != jsonAd){
				Console.debug(TAG, "json ad: " + jsonAd);				
				toCommentsActivity.putExtra(Utils.TRANSPORT_KEY, jsonAd.toString());
				toCommentsActivity.putExtra("ad_index", position + 1);
				startActivityForResult(toCommentsActivity, CommentsActivity.ACTIVITY_ID);				
			} else{
				Toast.makeText(ShowAlertActivity.this, "Internal problem.", Toast.LENGTH_SHORT).show();
			}
			break;			
		default:
			break;
		}
	}

	@Override
	public void onAlertAdded(boolean isSuccess, Alert pAddedAlert) {
	}

	@Override
	public void onAlertLoaded(boolean isSuccess, Alert pLoadedAlert) {
		if(isSuccess){
			txtTitle.setText(pLoadedAlert.getTitle());
			txtAddedDate.setText("Adaugat:" + pLoadedAlert.getFormattedAddedDate());			
			txtCategory.setText("Categorie: " + pLoadedAlert.getCategoryName());
			txtLastCheckedDate.setText("Ultima notificare: " + pLoadedAlert.getFormattedLastCheckedDate());
			for(int i = 0; i < pLoadedAlert.getFilters().size(); i++){
				adapterFilters.add(pLoadedAlert.getFilters().get(i));
				lstFilters.addView(adapterFilters.getView(adapterFilters.getCount() - 1, null, null));
			}
			
			for(int i = 0; i < pLoadedAlert.getAds().size(); i++){
				adapterAds.addItem(pLoadedAlert.getAds().get(i));
			}
			adapterAds.notifyDataSetChanged();
			if(adapterAds.getCount() == 0)
				txtNoAds.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			progressText.setVisibility(View.GONE);		
			rellContentHolder.setVisibility(View.VISIBLE);
		} else{
			Toast.makeText(ShowAlertActivity.this, "Nu am putut prelua alerta dvs. Va rugam verificat conexiunea la Internet.", Toast.LENGTH_LONG).show();						
			progressBar.setVisibility(View.GONE);
			progressText.setVisibility(View.GONE);					
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
					
					for(int i = 0; i < adapterAds.getCount(); i++){
						if(adapterAds.getItem(i).getAd().getId() == ad_id){
							adapterAds.getItem(i).getAd().setTotalComments(newCommentsCount);
							adapterAds.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onAlertRemoved(boolean isSuccess, Alert pAlertRemoved) {
		if(isSuccess){
			Toast.makeText(ShowAlertActivity.this, "Alerta dvs. a fost stearsa.", Toast.LENGTH_SHORT).show();
			finish();
		}else{
			Toast.makeText(ShowAlertActivity.this, "Alerta dvs. nu poate fi stearsa pentru moment.", Toast.LENGTH_SHORT).show();
		}
	}	
}
