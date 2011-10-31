package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.data.AlertView;
import com.liviu.apps.iasianunta.ui.LTextView;

public class AlertsAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "AlertsAdapter";
	
	// Data
	private ArrayList<AlertView>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	private int					mLastSelectedPosition;
	private int 				mSelectedColor;
	
	// Listeners
	private OnClickListener 	mOnClickListener;
	
	public AlertsAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<AlertView>();
		mLastSelectedPosition = -1;
		mSelectedColor = Color.parseColor("#dfdfdf");
	}
	
	public AlertsAdapter addItem(Alert pAlert){
		mItems.add(new AlertView(pAlert));
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public AlertView getItem(int position) {
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
			convertView 	= mLf.inflate(R.layout.alert_list_item, parent, false);
			vh				= new ViewHolder();			
			vh.txtTitle		= (LTextView)convertView.findViewById(R.id.lst_alert_title);
			vh.txtDate		= (LTextView)convertView.findViewById(R.id.lst_alert_added_date);
			vh.txtAdsText	= (LTextView)convertView.findViewById(R.id.lst_alert_txt_ads);
			vh.txtTotalAds	= (LTextView)convertView.findViewById(R.id.lst_alert_ads_count);
			vh.txtLastCheckedDate = (LTextView)convertView.findViewById(R.id.lst_alert_last_checked_date);						
			vh.buttonsLayout= (RelativeLayout)convertView.findViewById(R.id.lst_alert_actions_holder);
			vh.butRemove	= (Button)convertView.findViewById(R.id.lst_alert_remove);
			vh.butOpen		= (Button)convertView.findViewById(R.id.lst_alert_open);
			
			if(null != mOnClickListener){
				vh.butRemove.setTag(position);
				vh.butOpen.setTag(position);
				vh.butRemove.setOnClickListener(mOnClickListener);	
				vh.butOpen.setOnClickListener(mOnClickListener);				
			}
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();				
			
		vh.butRemove.setTag(position);
		vh.butOpen.setTag(position);
		
		vh.txtTitle.setText(mItems.get(position).getAlert().getTitle());
		vh.txtDate.setText("Adaugat: " +mItems.get(position).getAlert().getFormattedAddedDate());
		vh.txtLastCheckedDate.setText("Ultima notificare: " + mItems.get(position).getAlert().getFormattedLastCheckedDate());
		
		if(mItems.get(position).isSelected()){
			vh.buttonsLayout.setVisibility(View.VISIBLE);
			convertView.setBackgroundColor(mSelectedColor);
		} else{
			vh.buttonsLayout.setVisibility(View.GONE);
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		vh.txtTotalAds.setText(Integer.toString(mItems.get(position).getAlert().getTotalAdsSinceLastCheck()));
		if(mItems.get(position).getAlert().getTotalAdsSinceLastCheck() == 1){
			vh.txtAdsText.setText("anunt  ");
		} else{
			vh.txtAdsText.setText("anunturi");
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtTitle;
		public LTextView	txtDate;
		public LTextView	txtLastCheckedDate;
		public LTextView	txtTotalAds;
		public LTextView	txtAdsText;
		public RelativeLayout buttonsLayout;
		public Button		  butRemove;
		public Button		  butOpen;
		
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public AlertsAdapter setOnClickListener(OnClickListener pListener) {
		mOnClickListener = pListener;
		return this;
	}
	
	public void setSelection(int position, boolean selectionValue){
		try{
			if(-1 != mLastSelectedPosition)
				mItems.get(mLastSelectedPosition).setSelectedValue(false);
			
			mItems.get(position).setSelectedValue(selectionValue);
			mLastSelectedPosition = position;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}  

	public boolean removeItemByAlertId(int pAlertId) {
		for(int i = 0; i < mItems.size(); i++){
			if(mItems.get(i).getAlert().getId() == pAlertId){
				mItems.remove(i);
				mLastSelectedPosition = 0;
				return true;
			}
		}		
		return false;
	}	
}
