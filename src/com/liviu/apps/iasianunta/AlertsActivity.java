package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.AlertsAdapter;
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

public class AlertsActivity extends Activity implements IAlertsNotifier,
														OnItemClickListener,
														OnClickListener{
	// Constants
	private 		final String TAG = "AlertsActivity";
	public	static  final int	 ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(AlertsActivity.class);
	
	// Data
	private User 			user;
	private AlertManager	alertsMan;
	private AlertsAdapter 	adapter;		
	private API				api;
	
	// UI
	private ProgressBar		progressBar;
	private LTextView		progressText;
	private ListView		lst;
	private LTextView		txtNoAlerts;
	private Button			butCreateAlert;
	private LTextView 		txtUserName;
	private Button			butLogin;
	private Button			butBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.alerts_layout);   
        
        if(!Utils.isConnected(this)){
        	Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }                
        
        user = User.getInstance();        
        if(!user.isLoggedIn()){
        	// the user is not logged in
        	// we have to redirect him to login activity
        	Intent toLogin = new Intent(AlertsActivity.this, LoginActivity.class);
        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
        	startActivity(toLogin);
        	finish();
        	return;
        }        
        
        // Initialize
        alertsMan 	= new AlertManager();
        api			= new API();
        txtUserName = (LTextView)findViewById(R.id.user_name);
        lst			= (ListView)findViewById(R.id.lst_alerts);
        progressBar	= (ProgressBar)findViewById(R.id.alerts_progress);
        progressText= (LTextView)findViewById(R.id.txt_alerts_progress);
        adapter		= new AlertsAdapter(this);
        txtNoAlerts	= (LTextView)findViewById(R.id.txt_no_alerts);
        butCreateAlert = (Button)findViewById(R.id.but_add_alert);       
        butLogin 	= (Button)findViewById(R.id.main_but_login);        
        butBack		= (Button)findViewById(R.id.but_back);
        
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
        lst.setOnItemClickListener(this);
        adapter.setOnClickListener(this);
        butCreateAlert.setOnClickListener(this);
        butBack.setOnClickListener(this);
        
        // Work
        lst.setAdapter(adapter);
        alertsMan.getAllAlerts(user.getAuthName(), user.getPassword(), user.getId());
	}

	@Override
	public void onAlertsLoaded(boolean isSuccess, ArrayList<Alert> pLoadedAlerts) {
		progressBar.setVisibility(View.GONE);
		progressText.setVisibility(View.GONE);			
		if(isSuccess){
			for(int i = 0; i < pLoadedAlerts.size(); i++){
				adapter.addItem(pLoadedAlerts.get(i));
			}
			if(adapter.getCount() > 0){
				adapter.notifyDataSetChanged();
				lst.setVisibility(View.VISIBLE);
			} else{
				txtNoAlerts.setVisibility(View.VISIBLE);
			}
		} else{
			txtNoAlerts.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {	
		Console.debug(TAG, "on click on " + position);
		adapter.setSelection(position, !adapter.getItem(position).isSelected());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_back:
			finish();
			break;
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(AlertsActivity.this, LoginActivity.class);
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
								Toast.makeText(AlertsActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Salut");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(AlertsActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
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
			Integer position = (Integer)v.getTag();
			Console.debug(TAG, "position: " + position);
			if(null != position){
				Intent showAlertActivityIntent = new Intent(AlertsActivity.this, ShowAlertActivity.class);
				showAlertActivityIntent.putExtra(ShowAlertActivity.ALERT_TRANSPORT, adapter.getItem(position).getAlert().getId());
				startActivityForResult(showAlertActivityIntent, ShowAlertActivity.ACTIVITY_ID);
			}
			break;
		case R.id.lst_alert_remove:
			Integer alertPosition = (Integer)v.getTag();
			Console.debug(TAG, "position: " + alertPosition);
			if(null != alertPosition){
				Alert alertToRemove = adapter.getItem(alertPosition).getAlert();
				if(null != alertToRemove)
					alertsMan.removeAlert(alertToRemove, user.getId(), user.getAuthName(), user.getPassword());
			}			
			break;
		case R.id.but_add_alert:
			Intent toCreateAlertActivity = new Intent(AlertsActivity.this, CreateAlertActivity.class);
			startActivityForResult(toCreateAlertActivity, CreateAlertActivity.ACTIVITY_ID);
			break;

		default:
			break;
		}
	}

	@Override
	public void onAlertAdded(boolean isSuccess, Alert pAddedAlert) {
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Console.debug(TAG, "onactivity result: " + requestCode + " resultCOde " + resultCode + " data " + data + " " + CreateNewAddActivity.ACTIVITY_ID);		
		if(requestCode == CreateAlertActivity.ACTIVITY_ID){
			if(resultCode == CreateAlertActivity.RESULT_ALERT_ADDED){
				try {
					Alert addedAlert = new Alert(new JSONObject(data.getStringExtra("added_alert")));
					Console.debug(TAG, "alert " + addedAlert);
					adapter.addItem(addedAlert);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onAlertLoaded(boolean isSuccess, Alert pLoadedAlert) {
	}

	@Override
	public void onAlertRemoved(boolean isSuccess, Alert pAlertRemoved) {
		if(isSuccess){
			if(adapter.removeItemByAlertId(pAlertRemoved.getId())){
				adapter.notifyDataSetChanged();
				Toast.makeText(AlertsActivity.this, "Alerta dvs - " + pAlertRemoved.getTitle() + " - a fost stearsa.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(AlertsActivity.this, "Alerta dvs - " + pAlertRemoved.getTitle() + " - nu a putut fi stearsa pentru moment.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
