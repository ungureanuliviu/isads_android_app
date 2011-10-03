package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.AdImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class NewAdImagesAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "NewAdImagesAdapter";
	
	// Data
	private ArrayList<AdImage> 	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	public NewAdImagesAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<AdImage>();
	}
	
	public NewAdImagesAdapter addItem(AdImage pAdImage){
		mItems.add(pAdImage);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public AdImage getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			convertView 	= (RelativeLayout)mLf.inflate(R.layout.ad_image_layout, parent, false);
			vh				= new ViewHolder();
			vh.pBar			= (ProgressBar)convertView.findViewById(R.id.loading_progress);
			vh.pImageView 	= (ImageView)convertView.findViewById(R.id.ad_main_img);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();
				
			if(null != mItems.get(position).getBitmap()){				 
				vh.pImageView.setImageBitmap(mItems.get(position).getBitmap());
				vh.pImageView.setVisibility(View.VISIBLE);
			} else{
				vh.pBar.setVisibility(View.VISIBLE);
				vh.pImageView.setVisibility(View.GONE);
			}
			
		return convertView;
	}
	
	private class ViewHolder{
		public ProgressBar pBar;
		public ImageView   pImageView;
	}
}
