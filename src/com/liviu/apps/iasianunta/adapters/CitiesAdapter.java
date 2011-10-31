package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.City;
import com.liviu.apps.iasianunta.ui.LTextView;

public class CitiesAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "CitiesAdapter";
	
	// Data
	private ArrayList<City>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	public CitiesAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<City>();
	}
	
	public CitiesAdapter add(City pCity){
		mItems.add(pCity);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public City getItem(int position) {
		try{
			return mItems.get(position);
		} catch (IndexOutOfBoundsException e) {
			return new City(1, "iasi");
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
			convertView 	= mLf.inflate(R.layout.city_list_item, parent, false);
			vh				= new ViewHolder();
			vh.txtName		= (LTextView)convertView.findViewById(R.id.city_name);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();				
			
		vh.txtName.setText(mItems.get(position).getName());
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtName;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	
	
}
