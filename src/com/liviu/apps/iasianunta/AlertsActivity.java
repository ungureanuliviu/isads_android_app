package com.liviu.apps.iasianunta;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.liviu.apps.iasianunta.adapters.AlertsAdapter;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAlertsNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AlertManager;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class AlertsActivity extends Activity implements IAlertsNotifier,
														OnItemClickListener,
														OnClickListener{
	// Constants
	private 		final String TAG ="AlertsActivity";
	public	static  final int	 ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(AlertsActivity.class);
	
	// Data
	private User 			user;
	private AlertManager	alertsMan;
	private AlertsAdapter 	adapter;	
	
	// UI
	private ProgressBar		progressBar;
	private LTextView		progressText;
	private ListView		lst;
	private LTextView		txtNoAlerts;
	
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
        lst			= (ListView)findViewById(R.id.lst_alerts);
        progressBar	= (ProgressBar)findViewById(R.id.alerts_progress);
        progressText= (LTextView)findViewById(R.id.txt_alerts_progress);
        adapter		= new AlertsAdapter(this);
        txtNoAlerts	= (LTextView)findViewById(R.id.txt_no_alerts);
        
        // Set notifiers
        alertsMan.setAlertsNotifier(this);	
        lst.setOnItemClickListener(this);
        adapter.setOnClickListener(this);
        
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
	}

}
