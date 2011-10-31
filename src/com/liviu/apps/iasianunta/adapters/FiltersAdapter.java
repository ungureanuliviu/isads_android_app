package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.ContentFilter;
import com.liviu.apps.iasianunta.data.Filter;
import com.liviu.apps.iasianunta.data.PriceFilter;
import com.liviu.apps.iasianunta.ui.LTextView;

public class FiltersAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "FiltersAdapter";
	
	// Data
	private ArrayList<Filter>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	// Listeners & notifiers
	private OnClickListener mOnClickListener;
	
	public FiltersAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<Filter>();		
	}
	
	public FiltersAdapter add(Filter pFilter){
		mItems.add(pFilter);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Filter getItem(int position) {
		try{
			return mItems.get(position);
		} catch (IndexOutOfBoundsException e) {
			return new Filter(Filter.INVALID_TYPE);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){			
			if(null == parent){
				convertView 		= mLf.inflate(R.layout.filter_list_item, null);
				convertView.setId(position + 1);
				vh					= new ViewHolder();
				vh.txtFilterContent	= (LTextView)convertView.findViewById(R.id.filter_content);			
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				if(0 != position)
					params.addRule(RelativeLayout.BELOW, position);
				convertView.setLayoutParams(params);	
			} else{
				convertView 		= mLf.inflate(R.layout.filter_list_item, parent, false);				
				vh					= new ViewHolder();
				vh.txtFilterContent	= (LTextView)convertView.findViewById(R.id.filter_content);							
			}
							
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();
				
		if(mItems.get(position) instanceof ContentFilter){
			vh.txtFilterContent.setText(((ContentFilter)mItems.get(position)).getContent());
		} else if(mItems.get(position) instanceof PriceFilter){			
			vh.txtFilterContent.setText("Min price: " + ((PriceFilter)mItems.get(position)).getCurrency() + ((PriceFilter)mItems.get(position)).getMinPrice()  + " Max price: " + ((PriceFilter)mItems.get(position)).getCurrency() + ((PriceFilter)mItems.get(position)).getMaxPrice());
		}
					
		return convertView;
	}  
	
	private class ViewHolder{
		public LTextView 	txtFilterContent;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	public FiltersAdapter setOnRemoveButtonClickListener(OnClickListener pListener){
		mOnClickListener = pListener;
		return this;
	}

	public void clear() {
		mItems.clear();
	}

	
	
}
