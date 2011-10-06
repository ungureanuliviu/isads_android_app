package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.ui.LTextView;

public class TopCateogoriesAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "TopCateogoriesAdapter";
	
	// Data
	private ArrayList<Category>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	public TopCateogoriesAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<Category>();
	}
	
	public TopCateogoriesAdapter addItem(Category pCategory){
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
			convertView 	= (LTextView)mLf.inflate(R.layout.top_category_layout, parent, false);
			vh				= new ViewHolder();
			vh.txtTitle		= (LTextView)convertView;
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();
		
			vh.txtTitle.setText(mItems.get(position).getName());			
			
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView txtTitle;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
}
