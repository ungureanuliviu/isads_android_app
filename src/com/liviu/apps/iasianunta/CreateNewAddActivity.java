package com.liviu.apps.iasianunta;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
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
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.NewAdImagesAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdNotifier;
import com.liviu.apps.iasianunta.interfaces.IUploadNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.AdImageView;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;

public class CreateNewAddActivity extends Activity implements OnClickListener,
															  IUploadNotifier,
															  OnItemClickListener,
															  IAdNotifier{
	
	// Constants
	private final 		 	String 	TAG 						= "CreateNewAddActivity";
	public  final static 	int 	ACTIVITY_ID 				= ActivityIdProvider.getInstance().getNewId(CreateNewAddActivity.class);
	private final 			int 	SELECT_PHOTO_REQUEST_CODE 	= 1; 
	private final 			int     MAX_UPLOADED_IMAGES 		= 5;
	private final 			int 	VIB_LENGTH					= 30;
	
	// Data
	private Ad 		newAd;
	private API 	api;
	private NewAdImagesAdapter adapterGalleryImages;
	private AdsManager adMan;
	private User user;
	
	// UI
	private Button 			butAddImage;
	private Button			butSave;
	private Button			butAdd;
	private Gallery			galImages;
	private AdImageView 	newImage;
	private LTextView		txtNoImages;
	private LEditText		edtxTitle;
	private LEditText		edtxPhone;
	private LEditText		edtxContent;
	private LEditText		edtxEmail;
	private LEditText		edtxAddress;
	
	// Services
	private Vibrator		vbb;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.create_ad_layout);		
        
        // Initialize objects
        newAd 		 = new Ad();
        butAddImage  = (Button)findViewById(R.id.but_add_image);
        galImages	 = (Gallery)findViewById(R.id.gallery_ad_images);
        api 		 = API.getInstance();
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
        adMan		 = new AdsManager(this);
        user		 = User.getInstance();
        
        butAddImage.setOnClickListener(this);
        butAdd.setOnClickListener(this);
        butSave.setOnClickListener(this);
        api.setUploadNotifier(this);        
        galImages.setAdapter(adapterGalleryImages);
        galImages.setOnItemClickListener(this);
        adMan.setAdNotifier(this);
        api.setAdNotifier(this);
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
					Toast.makeText(CreateNewAddActivity.this, "Ai atins numarul maxim de imagini ce pot fi atasate unui anunt.", Toast.LENGTH_LONG).show();
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
				if(constructNewAdd()){
					Console.debug(TAG, "add new ad to server");
					adMan.addnewAd(newAd, user.getId(), user.getAuthName(), user.getPassword());
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
		if(isSuccess){
			Toast.makeText(CreateNewAddActivity.this, "Anuntul a fost salvat. Id: " + pSavedAd.getId(), Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(CreateNewAddActivity.this, "Anuntul nu a putut fi salvat.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAdRemoteAdded(boolean isSuccess, Ad pAdRemoteAdded) {
		Console.debug(TAG, "onAdRemoteAdded " + isSuccess + " ad: " + pAdRemoteAdded );
		if(isSuccess){
			Toast.makeText(CreateNewAddActivity.this, "Ad added to server " + pAdRemoteAdded.getId(), Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(CreateNewAddActivity.this, "The current ad cannot be added on server for the moment", Toast.LENGTH_SHORT).show();
		}
	}
}
