package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.utils.Console;

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

public class ShowAdImagesAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "ShowAdImagesAdapter";
	
	// Data
	private ArrayList<AdImage> 	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	private int					mAdId;
	
	public ShowAdImagesAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<AdImage>();
	}
	
	public ShowAdImagesAdapter addItem(AdImage pAdImage){		
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
			convertView 	= (RelativeLayout)mLf.inflate(R.layout.ad_image_layout_large, parent, false);
			vh				= new ViewHolder();
			vh.pBar			= (ProgressBar)convertView.findViewById(R.id.loading_progress);
			vh.pImageView 	= (ImageView)convertView.findViewById(R.id.ad_main_img);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();
			
			Console.debug(TAG, "bitmap is " +  mItems.get(position).getBitmap());
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

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public int getAdId() {
		return mAdId;
	}
	
	public ShowAdImagesAdapter setAdId(int pAdId){
		mAdId = pAdId;
		return this;
	}

	public void clear() {
		for(int i = 0; i < mItems.size(); i++){
			if(mItems.get(i).getBitmap() != null)
				mItems.get(i).getBitmap().recycle();
		}
		mItems.clear();		
	}
		
}
