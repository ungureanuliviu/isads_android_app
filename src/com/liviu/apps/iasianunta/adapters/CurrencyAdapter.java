package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.ui.LTextView;

public class CurrencyAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "CurrencyAdapter";
	
	// Data
	private ArrayList<String>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	public CurrencyAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<String>();
	}
	
	public CurrencyAdapter add(String pCurrency){
		mItems.add(pCurrency);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public String getItem(int position) {
		try{
			return mItems.get(position);
		} catch (IndexOutOfBoundsException e) {
			return "LEI";
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
			convertView 	= mLf.inflate(R.layout.category_list_item, parent, false);
			vh				= new ViewHolder();
			vh.txtCName	= (LTextView)convertView.findViewById(R.id.category_name);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();				
			
		vh.txtCName.setText(mItems.get(position));
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtCName;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	
	
}
