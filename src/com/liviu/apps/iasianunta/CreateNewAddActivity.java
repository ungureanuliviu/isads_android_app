package com.liviu.apps.iasianunta;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.NewAdImagesAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.interfaces.IUploadNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;

public class CreateNewAddActivity extends Activity implements OnClickListener,
															  IUploadNotifier,
															  OnItemClickListener,
															  IAdsNotifier{
	
	// Constants
	private final 		 	String 	TAG 						= "CreateNewAddActivity";
	public  final static 	int 	ACTIVITY_ID 				= ActivityIdProvider.getInstance().getNewId(CreateNewAddActivity.class);
	private final 			int 	SELECT_PHOTO_REQUEST_CODE 	= 1; 
	private final 			int     MAX_UPLOADED_IMAGES 		= 5;
	public  final static	int 	VIB_LENGTH					= 30;
	
	// Data
	private Ad 				newAd;
	private API 			api;
	private NewAdImagesAdapter adapterGalleryImages;
	private AdsManager 		adMan;
	private User 			user;
	
	// UI
	private Button 			butAddImage;
	private Button			butSave;
	private Button			butAdd;
	private Button			butLogin;
	private Gallery			galImages;
	private LTextView		txtNoImages;
	private LEditText		edtxTitle;
	private LEditText		edtxPhone;
	private LEditText		edtxContent;
	private LEditText		edtxEmail;
	private LEditText		edtxAddress;
	private LTextView		txtUserName;
	private Typeface		typeface;
	private RelativeLayout	layoutContent;
	private LTextView		txtPostingProgress;
	private ProgressBar		barPosting;
	
	
	// Services
	private Vibrator		vbb;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.create_ad_layout);		
        
        user = User.getInstance();        
        if(!user.isLoggedIn()){
        	// the user is not logged in
        	// we have to redirect him to login activity
        	Intent toLogin = new Intent(CreateNewAddActivity.this, LoginActivity.class);
        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
        	startActivity(toLogin);
        	finish();
        	return;
        }
        
        // Initialize objects
        newAd 		 = new Ad();
        butAddImage  = (Button)findViewById(R.id.but_add_image);
        galImages	 = (Gallery)findViewById(R.id.gallery_ad_images);
        api 		 = new API();
        adapterGalleryImages = new NewAdImagesAdapter(this);
        vbb			 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        txtNoImages  = (LTextView)findViewById(R.id.txt_no_images);
        edtxTitle	 = (LEditText)findViewById(R.id.edtx_ad_title);
        edtxContent	 = (LEditText)findViewById(R.id.edtx_ad_content);
        edtxPhone	 = (LEditText)findViewById(R.id.edtx_ad_phone);
        edtxEmail	 = (LEditText)findViewById(R.id.edtx_ad_email);
        edtxAddress	 = (LEditText)findViewById(R.id.edtx_ad_address);
        butAdd		 = (Button)findViewById(R.id.but_add_ad);
        butSave		 = (Button)findViewById(R.id.but_save);
        butLogin	 = (Button)findViewById(R.id.main_but_login);
        txtUserName	 = (LTextView)findViewById(R.id.user_name);
        adMan		 = new AdsManager(this);     
        typeface	 = Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
        txtPostingProgress 	= (LTextView)findViewById(R.id.txt_posting);
        barPosting	 		= (ProgressBar)findViewById(R.id.posting_progress);
        layoutContent  		= (RelativeLayout)findViewById(R.id.layout_content);
        
        txtUserName.setText(user.getName());
        
        butAddImage.setOnClickListener(this);
        butAdd.setOnClickListener(this);
        butSave.setOnClickListener(this);
        api.setUploadNotifier(this);        
        galImages.setAdapter(adapterGalleryImages);
        galImages.setOnItemClickListener(this);
        adMan.setAdsNotifier(this);        
        butLogin.setOnClickListener(this);       
        
        butAdd.setTypeface(typeface);
        butSave.setTypeface(typeface);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_PHOTO_REQUEST_CODE:
			if(resultCode == RESULT_OK){
				Uri uri = data.getData();				
				boolean canAd = true;
				// check if this uri already exists 
				for(int i = 0; i < adapterGalleryImages.getCount(); i++){
					if(adapterGalleryImages.getItem(i).getUri().equals(uri)){
						canAd = false;
					}
				}				
				if(canAd){
					if(adapterGalleryImages.getCount() == 0){
						txtNoImages.setVisibility(View.GONE);
					}
					butAddImage.setEnabled(false);
					butAddImage.setText("Uploading...");
					AdImage adImage = new AdImage(uri, null);
					adapterGalleryImages.addItem(adImage);
					adapterGalleryImages.notifyDataSetChanged();
					galImages.setSelection(adapterGalleryImages.getCount() - 1, true);
					api.uploadImage(uri, CreateNewAddActivity.this);
				} else{
					Toast.makeText(CreateNewAddActivity.this, "Aceasta imagine deja a fost incarcata pe server.", Toast.LENGTH_LONG).show();
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		Console.debug(TAG, "onclick on " + v);		
		switch (v.getId()) {  
		case R.id.but_add_image:
				if(newAd.getImages().size() >= MAX_UPLOADED_IMAGES){
					Toast.makeText(CreateNewAddActivity.this, "Ati atins numarul maxim de imagini ce pot fi atasate unui anunt.", Toast.LENGTH_LONG).show();
				} else{
			        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
			        pickImageIntent.setType("image/*");
			        startActivityForResult(pickImageIntent, SELECT_PHOTO_REQUEST_CODE);
				}
				break;	
		case R.id.but_save:
				if(constructNewAdd()){
					Console.debug(TAG, "new ad to save: " + newAd);
					adMan.saveAd(newAd);
				}
				break;
		case R.id.but_add_ad:
		        if(!user.isLoggedIn()){
		        	// the user is not logged in
		        	// we have to redirect him to login activity
		        	Intent toLogin = new Intent(CreateNewAddActivity.this, LoginActivity.class);
		        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
		        	startActivity(toLogin);
		        	finish();
		        	return;
		        } else if(constructNewAdd()){
		        		Console.debug(TAG, "add new ad to server");
						adMan.addnewAd(newAd, user.getId(), user.getAuthName(), user.getPassword());
						butAdd.setText("Public anuntul...");
						butAdd.setEnabled(false);
						layoutContent.setVisibility(View.GONE);
						barPosting.setVisibility(View.VISIBLE);
						txtPostingProgress.setVisibility(View.VISIBLE);
					}
				break;
		case R.id.main_but_login:
			if(!user.isLoggedIn()){
				// we have to log in the user so 
				// let's start login activity
				Intent toLoginIntent = new Intent(CreateNewAddActivity.this, LoginActivity.class);
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
								Toast.makeText(CreateNewAddActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
								butLogin.setText("Login"); // update the main button
								txtUserName.setText("Holla amigos");
							} else{
								// hmmm.. something went wrong...
								Toast.makeText(CreateNewAddActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
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

	@Override
	public void onImageUploaded(boolean isSuccess, Uri uri, String jsonFileServerInfo) {
		Console.debug(TAG, "sisSuccess: " + isSuccess + " uri: " + jsonFileServerInfo);		
		if(isSuccess){
			try{
				InputStream imageStream 	= getContentResolver().openInputStream(uri);
				BitmapFactory.Options opts 	= new BitmapFactory.Options();
				opts.inSampleSize = 4;
				Bitmap bitmapOrg = BitmapFactory.decodeStream(imageStream, null, opts);
												  				
//				AdImageView newImage = (AdImageView)findViewById(newAd.getImages().size() + 1);
//				newImage.setImage(bitmapOrg);						
				for(int i = 0; i < adapterGalleryImages.getCount(); i++){
					if(adapterGalleryImages.getItem(i).getUri().equals(uri)){
						JSONObject serverFileInfo = new JSONObject(jsonFileServerInfo);
						adapterGalleryImages.getItem(i).setServerFileInfo(serverFileInfo);		
						newAd.addImage(adapterGalleryImages.getItem(i));
						if(null != bitmapOrg){  
							adapterGalleryImages.getItem(i).setBitmap(bitmapOrg);
							adapterGalleryImages.notifyDataSetChanged();
							break;    
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(CreateNewAddActivity.this, "Imaginea nu a putut fi incarcata pe server. Va rugam re-incercati in cateva minute.", Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(CreateNewAddActivity.this, "Imaginea nu a putut fi incarcata pe server. Va rugam re-incercati in cateva minute.", Toast.LENGTH_LONG).show();
			}
		} else{
			Toast.makeText(CreateNewAddActivity.this, "Imaginea nu a putut fi incarcata pe server. Va rugam re-incercati in cateva minute.", Toast.LENGTH_LONG).show();
		}
		
		butAddImage.setEnabled(true);
		butAddImage.setText("Adauga imagine");
		if(newAd.getImages().size() == 2){
			Toast.makeText(CreateNewAddActivity.this, "Click pe imagine pentru a o sterge.", Toast.LENGTH_LONG).show();
		}
	}
	    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		AdImage imageToRemove = adapterGalleryImages.getItem(position);
		newAd.removeImageByUri(imageToRemove.getUri());
		adapterGalleryImages.removeAt(position);
		adapterGalleryImages.notifyDataSetChanged();	
		vbb.vibrate(VIB_LENGTH);
		if(adapterGalleryImages.getCount() == 0){
			txtNoImages.setVisibility(View.VISIBLE);
		}
	}
	
	private boolean constructNewAdd(){		
		if(edtxTitle.getText().toString().length() > 0){
			if(edtxTitle.getText().toString().length() < 5){
				Toast.makeText(CreateNewAddActivity.this, "Campul 'Titlu' trebuie sa contina minim 5 caractere", Toast.LENGTH_SHORT).show();
				return false;				
			} else
				newAd.setTitle(edtxTitle.getText().toString());
		}
		else{
			Toast.makeText(CreateNewAddActivity.this, "Campul 'Titlu' nu este invalid.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(edtxPhone.getText().toString().length() > 0)
			newAd.setPhone(edtxPhone.getText().toString());
		else{
			Toast.makeText(CreateNewAddActivity.this, "Campul 'Telefon' nu este invalid.", Toast.LENGTH_SHORT).show();
			return false;
		}		
		
		if(edtxContent.getText().toString().length() > 0){
			if(edtxContent.getText().toString().length() < 20){
				Toast.makeText(CreateNewAddActivity.this, "Campul 'Continut' trebuie sa contina minim 20 caractere", Toast.LENGTH_SHORT).show();
				return false;				
			} else
				newAd.setContent(edtxContent.getText().toString());	
		}			
		else{
			Toast.makeText(CreateNewAddActivity.this, "Campul 'Continut' nu este invalid.", Toast.LENGTH_SHORT).show();
			return false;
		}		
		
		if(edtxEmail.getText().toString().length() > 0)
			newAd.setEmail(edtxEmail.getText().toString());
		
		if(edtxAddress.getText().toString().length() > 0)
			newAd.setAddress(edtxAddress.getText().toString());
		newAd.setSource(Ad.SOURCE_ANDROID);
		
		return true;
	}

	@Override
	public void onAdSaved(boolean isSuccess, Ad pSavedAd) {
		Console.debug(TAG, "isSuccess: " + isSuccess + " pSavedAd: " + pSavedAd);
		boolean postingFailed = !butAdd.isEnabled();
		if(!butAdd.isEnabled()){
			butAdd.setText("Publica");
			butAdd.setEnabled(true);
			layoutContent.setVisibility(View.VISIBLE);
			barPosting.setVisibility(View.GONE);
			txtPostingProgress.setVisibility(View.GONE);			
		}
		
		if(isSuccess){		
			if(postingFailed)
				Toast.makeText(CreateNewAddActivity.this, "Anuntul dvs nu a putut fi adaugat insa acesta a fost salvat local si il veti putea adauga mai tarziu.", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(CreateNewAddActivity.this, "Anuntul a fost salvat local. ID: " + pSavedAd.getId(), Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(CreateNewAddActivity.this, "Anuntul nu a putut fi salvat.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAdRemoteAdded(boolean isSuccess, Ad pAdRemoteAdded) {
		butAdd.setText("Publica");
		butAdd.setEnabled(true);
		layoutContent.setVisibility(View.VISIBLE);
		barPosting.setVisibility(View.GONE);
		txtPostingProgress.setVisibility(View.GONE);		
		Console.debug(TAG, "onAdRemoteAdded " + isSuccess + " ad: " + pAdRemoteAdded );
		if(isSuccess){
			Toast.makeText(CreateNewAddActivity.this, "Anuntul dvs. a fost adaugat pe \nwww.iasianunta.info. ID: " + pAdRemoteAdded.getId(), Toast.LENGTH_LONG).show();
			finish();
		} else{
			Toast.makeText(CreateNewAddActivity.this, "Anuntul dvs. nu poate fi adaugat pentru momenent. Va rugam re-incercati in cateva minute.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onAdsLoaded(boolean isSuccess, int pCategoryId, int pPage,
			int pAdsPerPage, ArrayList<Ad> pLoadedAds) {
	}

	@Override
	public void onImageDownloaded(boolean isSuccess, int pAdId, Bitmap pImg, boolean isFullImage) {
	}
}
