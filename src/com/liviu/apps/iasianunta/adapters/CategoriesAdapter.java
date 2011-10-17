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

public class CategoriesAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "CategoriesAdapter";
	
	// Data
	private ArrayList<Category>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	public CategoriesAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<Category>();
	}
	
	public CategoriesAdapter add(Category pCategory){
		mItems.add(pCategory);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Category getItem(int position) {
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
			convertView 	= mLf.inflate(R.layout.category_list_item, parent, false);
			vh				= new ViewHolder();
			vh.txtCatName	= (LTextView)convertView.findViewById(R.id.category_name);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();				
			
		vh.txtCatName.setText(mItems.get(position).getName());
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtCatName;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	
	
}
