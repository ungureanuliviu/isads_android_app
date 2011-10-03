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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.NewAdImagesAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.interfaces.IUploadNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.ui.AdImageView;
import com.liviu.apps.iasianunta.utils.Console;

public class CreateNewAddActivity extends Activity implements OnClickListener,
															  IUploadNotifier,
															  OnItemClickListener{
	
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
	
	// UI
	private Button 			butAddImage;
	private Gallery			galImages;
	private AdImageView 	newImage;
	
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
        
        butAddImage.setOnClickListener(this);
        api.setUploadNotifier(this);        
        galImages.setAdapter(adapterGalleryImages);
        galImages.setOnItemClickListener(this);
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
					butAddImage.setEnabled(false);
					butAddImage.setText("Uploading...");
					AdImage adImage = new AdImage(uri, null);
					adapterGalleryImages.addItem(adImage);
					adapterGalleryImages.notifyDataSetChanged();
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
		butAddImage.setText("Adauga\nimagine");
		if(newAd.getImages().size() == 5){
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
	}
}
