package com.liviu.apps.iasianunta.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.utils.Console;

public class AdImageView extends RelativeLayout{
	// Constants
	private final String TAG = "AdImageView";
	
	// Data
	private Context 		mContext;
	private RelativeLayout 	mMainLayout;
	private ImageView		mMainImage;
	private ProgressBar		mProgressBar;
	
	// System's
	private LayoutInflater  mLf;
	
	public AdImageView(Context context) {
		super(context);
		mLf 			= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainLayout 	= (RelativeLayout)mLf.inflate(R.layout.ad_image_layout, null);
		mMainImage 		= (ImageView)mMainLayout.findViewById(R.id.ad_main_img);
		mProgressBar	= (ProgressBar)mMainLayout.findViewById(R.id.loading_progress);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
		setLayoutParams(params);
		setPadding(5, 5, 5, 5);
		setClickable(true);
		setFocusable(true);
		setBackgroundResource(R.drawable.bg_white);		
		
		mMainLayout.removeAllViews();		
		addView(mProgressBar);
		addView(mMainImage);
	}		
	
	public AdImageView setImage(Bitmap pImageBitmap){
		if(null != pImageBitmap){
			mMainImage.setImageBitmap(pImageBitmap);
			mProgressBar.setVisibility(View.GONE);
			mMainImage.setVisibility(View.VISIBLE);
		} else{
			Console.debug(TAG, "the bitmap is null");
		}
		return this;
	}
}
