package com.liviu.apps.iasianunta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.ui.LTextView;

public class MainActivity extends Activity implements OnClickListener{
	
	// Constants
	private final String TAG = "LoginActivity";
	public  final static int ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(MainActivity.class);
	
	// Data
	private User user;
	private API api;

	// UI
	private LTextView txtUserName;
	private Button butLogin;
	private RelativeLayout butAddNewAdd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);             
        setContentView(R.layout.main);
        
        user = User.getInstance(); 
        api = API.getInstance();
        txtUserName = (LTextView)findViewById(R.id.user_name);
        butLogin = (Button)findViewById(R.id.main_but_login);
        butAddNewAdd = (RelativeLayout)findViewById(R.id.but_add_ad);
        
        // check if the use is logged in
        if(user.isLoggedIn()){
        	txtUserName.setText("Salut " + user.getName());
        	butLogin.setText("Logout");
        } else{
        	// the user is not logged in.
        	// we have to update the UI to reflect this state
        	butLogin.setText("Login");
        }
        
        // set listeners
        butLogin.setOnClickListener(this);
        butAddNewAdd.setOnClickListener(this);                                
    }
// ========================== Interfaces ========================
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_add_ad:
			// start the create new ad activity
			Intent toCreateNewAdActivity = new Intent(MainActivity.this, CreateNewAddActivity.class);
			startActivity(toCreateNewAdActivity);
			break;
		case R.id.main_but_login:
			if(butLogin.getText().equals("Login")){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(MainActivity.this, LoginActivity.class);
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
								// logout done
								user.logout();
								Toast.makeText(MainActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Holla amigos");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(MainActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
							}
						}					
						@Override
						public void onLogin(boolean isSuccess, User pUser) {
							// nothing here
						}
					});
			}
			break;

		default:
			break;
		}
	}
}