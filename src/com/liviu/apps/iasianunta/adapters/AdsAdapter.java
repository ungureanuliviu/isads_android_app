package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdView;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.CommentsView;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class AdsAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "AdsAdapter";
	
	// Data
	private ArrayList<AdView>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	// Listeners
	private OnClickListener 	mOnClickListener;
	
	public AdsAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<AdView>();
	}
	
	public AdsAdapter addItem(Ad pAd){
		mItems.add(new AdView(pAd));
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public AdView getItem(int position) {
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
			convertView 	= mLf.inflate(R.layout.ad_list_item, parent, false);
			vh				= new ViewHolder();
			vh.txtTitle		= (LTextView)convertView.findViewById(R.id.ad_title);
			vh.txtAuthor	= (LTextView)convertView.findViewById(R.id.ad_author);
			vh.txtCategory	= (LTextView)convertView.findViewById(R.id.ad_category);
			vh.txtContent	= (LTextView)convertView.findViewById(R.id.ad_content);
			vh.viewImages	= (ImageButton)convertView.findViewById(R.id.ad_view_images);
			vh.butCall		= (Button)convertView.findViewById(R.id.ad_but_call);
			vh.butComments	= (Button)convertView.findViewById(R.id.ad_but_comments);
			vh.txtDate		= (LTextView)convertView.findViewById(R.id.ad_date);
			vh.txtIndex		= (LTextView)convertView.findViewById(R.id.ad_index);
			vh.txtViewsCount= (LTextView)convertView.findViewById(R.id.ad_views);
			if(null != mOnClickListener){
				vh.butCall.setOnClickListener(mOnClickListener);
				vh.butComments.setOnClickListener(mOnClickListener);
				vh.viewImages.setOnClickListener(mOnClickListener);		
			}
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();		
			
			vh.txtIndex.setText(Integer.toString(position + 1));
			vh.txtTitle.setText(mItems.get(position).getAd().getTitle());
			vh.txtAuthor.setText((mItems.get(position).getAd().getAuthor() == null ? "iasianunta.info" : mItems.get(position).getAd().getAuthor()));
			vh.txtCategory.setText(mItems.get(position).getAd().getCategoryName());
			vh.txtContent.setText(mItems.get(position).getAd().getContent());
			vh.butComments.setText(mItems.get(position).getAd().getTotalComments() + " " + (mItems.get(position).getAd().getTotalComments() == 1 ? "comentariu" : "comentarii"));
			vh.txtDate.setText(mItems.get(position).getAd().getFormattedDate());
			vh.txtViewsCount.setText(mItems.get(position).getAd().getViewsCount() + (mItems.get(position).getAd().getViewsCount() == 1 ? " vizualizare" : " vizualizari"));
			if(mItems.get(position).getAd().getPhone() != null && mItems.get(position).getAd().getPhone().length() > 0){
				vh.butCall.setText(mItems.get(position).getAd().getPhone().replaceAll("/", ""));
			} else{
				if(mItems.get(position).getAd().getEmail() !=  null && mItems.get(position).getAd().getEmail().length() > 0){
					vh.butCall.setText("Email: " + mItems.get(position).getAd().getEmail());					
				}else{
					vh.butCall.setTag("-");
				}
			} 

			if(mItems.get(position).getImage() != null){
				vh.viewImages.setVisibility(View.VISIBLE);
				vh.viewImages.setImageBitmap(mItems.get(position).getImage());
				((RelativeLayout.LayoutParams)vh.txtContent.getLayoutParams()).topMargin = 5;
			} else{
				vh.viewImages.setVisibility(View.GONE);
				((RelativeLayout.LayoutParams)vh.txtContent.getLayoutParams()).topMargin = 60;
				//vh.viewImages.setImageResource(R.drawable.ic_no_ad_image);
			}
			vh.butCall.setTag(position);
			vh.butComments.setTag(position);
			vh.viewImages.setTag(position);
			
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView	txtTitle;
		public LTextView	txtCategory;
		public LTextView	txtAuthor;
		public LTextView	txtContent;
		public LTextView	txtDate;
		public LTextView	txtIndex;
		public LTextView	txtViewsCount;
		public ImageButton	viewImages;
		public Button		butCall;
		public Button		butComments;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public void setImage(int pAdId, Bitmap pImg) {
		for(int i = 0; i < mItems.size(); i++){
			if(mItems.get(i).getAd().getId() == pAdId){
				mItems.get(i).setImage(pImg);
				break;
			}
		}
	}

	public AdsAdapter setOnClickListener(OnClickListener pListener) {
		mOnClickListener = pListener;
		return this;
	}
	
	
}
