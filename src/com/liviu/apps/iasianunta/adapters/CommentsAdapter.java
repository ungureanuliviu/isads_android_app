package com.liviu.apps.iasianunta.adapters;

import java.util.ArrayList;

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
import com.liviu.apps.iasianunta.data.AdView;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.ui.CommentsView;
import com.liviu.apps.iasianunta.ui.LTextView;

public class CommentsAdapter extends BaseAdapter{
	// Constants
	private final String TAG = "CommentsAdapter";
	
	// Data
	private ArrayList<Comment>	mItems;
	private LayoutInflater 		mLf;
	private Context 			mContext;
	
	// Listeners
	private OnClickListener 	mOnClickListener;
	
	public CommentsAdapter(Context pContext) {
		mContext = pContext;
		mLf 	 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems	 = new ArrayList<Comment>();
	}
	
	public CommentsAdapter addItem(Comment pComment){
		mItems.add(pComment);
		return this;
	}	
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Comment getItem(int position) {
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
			convertView 	= mLf.inflate(R.layout.ad_comment, parent, false);
			vh				= new ViewHolder();
			vh.txtAuthor	= (LTextView)convertView.findViewById(R.id.ad_comment_author);
			vh.txtTitle		= (LTextView)convertView.findViewById(R.id.ad_comment_title);
			vh.txtDate		= (LTextView)convertView.findViewById(R.id.ad_comment_date);
			vh.txtContent	= (LTextView)convertView.findViewById(R.id.ad_comment_content);
						
			convertView.setTag(vh);
		} else
			vh = (ViewHolder)convertView.getTag();				
			
		vh.txtTitle.setText(mItems.get(position).getTitle());
		vh.txtAuthor.setText(mItems.get(position).getAuthor());
		vh.txtDate.setText(mItems.get(position).getFormattedDate());
		vh.txtContent.setText(mItems.get(position).getContent());
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtTitle;
		public LTextView	txtDate;
		public LTextView	txtAuthor;
		public LTextView	txtContent;
	}

	public void removeAt(int position) {
		try{
			mItems.remove(position);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public CommentsAdapter setOnClickListener(OnClickListener pListener) {
		mOnClickListener = pListener;
		return this;
	}
	
	
}
