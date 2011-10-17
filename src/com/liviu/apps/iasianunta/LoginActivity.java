package com.liviu.apps.iasianunta;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.utils.Console;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity implements ILoginNotifier,
													   OnClickListener{
	
	// Constants
	private 		final String TAG = "LoginActivity";
	public static 	final int REQUEST_ID = ActivityIdProvider.getInstance().getNewId(LoginActivity.class);
	public static final String PARENT_ACTIVITY_ID = "parent_activity_id";

	// data
	private API	api;
	private User user;  
	
	// UI
	private Button butLogin;
	private Button butLater;
	private LEditText edtxUserAuthName;
	private LEditText edtxUserPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.login);
         
        // Initialize
        api 			 = new API();
        user 		 	 = User.getInstance();
        butLater 		 = (Button)findViewById(R.id.login_but_later);
        butLogin 		 = (Button)findViewById(R.id.login_but_login);
        edtxUserAuthName = (LEditText)findViewById(R.id.edtx_username);
        edtxUserPassword = (LEditText)findViewById(R.id.edtx_user_password);                  
        
        // Set listeners
        butLater.setOnClickListener(this);
        butLogin.setOnClickListener(this);  
        
        if(user.isLoggedIn()){
			Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(toMainActivity);
			finish();
        }
	}

	
	// Interfaces
	@Override
	public void onLogin(boolean isSuccess, User pUser) {
		Console.debug(TAG, "onLogin: " + isSuccess + " " + pUser);
		if(isSuccess){
			user = pUser;
			Toast.makeText(LoginActivity.this, "Autentificare reusita.", Toast.LENGTH_SHORT).show();
			
			Class<?> clasz = MainActivity.class;
			if(getIntent() != null){
				if(getIntent().getIntExtra(PARENT_ACTIVITY_ID, -1) != -1){
					clasz = ActivityIdProvider.getActivity(getIntent().getIntExtra(PARENT_ACTIVITY_ID, -1));
				} else{
					// we have nothing to do here. 
					// the clasz == Mainactivity.class
				}
			} else{
				// we have nothing to do here. 
				// the clasz == Mainactivity.class				
			}
			Intent toMainActivity = new Intent(LoginActivity.this, clasz);
			startActivity(toMainActivity);
			finish();
		} else{
			Toast.makeText(LoginActivity.this, "Utilizatorul sau parola dvs nu valide. Va rugam reincercati.", Toast.LENGTH_SHORT).show();
		}	
	}

	@Override
	public void onLogout(boolean isSuccess, int pUserId) {
		Console.debug(TAG, "onLogout: " + isSuccess + " " + pUserId);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_but_login:
			if(edtxUserAuthName.getText().length() == 0 || edtxUserPassword.getText().length() == 0){
				Toast.makeText(LoginActivity.this, "Va rugam completati toate campurile.", Toast.LENGTH_SHORT).show();				
			} else{
				// watch for onLogin
				api.login(edtxUserAuthName.getText().toString(), edtxUserPassword.getText().toString(), LoginActivity.this);    
			}
			break;
		case R.id.login_but_later:
			Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(toMainActivity);
			finish();			
			break;
		default:
			break;
		}
	}
	
}
