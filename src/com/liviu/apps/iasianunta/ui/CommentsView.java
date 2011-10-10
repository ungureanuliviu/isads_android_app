package com.liviu.apps.iasianunta.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.utils.Console;

public class CommentsView extends RelativeLayout implements OnClickListener{
	

	// Constants
	private final String TAG = "CommentsView";
	
	// Data
	private ArrayList<Comment> mItems;
	private LayoutInflater 		mLf;
	private RelativeLayout		mLayout;
	
	// Listeners
	private OnClickListener mOnClickListener;
	
	public CommentsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems  = new ArrayList<Comment>();
	}

	public CommentsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems  = new ArrayList<Comment>();
	}
	
	public CommentsView(Context context) {
		super(context);		
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems  = new ArrayList<Comment>();
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {	
		mOnClickListener = l;
	}
	
	public CommentsView addComment(Comment pComment){
		if(null == pComment)
			return this;
		RelativeLayout newCommentHolder = (RelativeLayout)mLf.inflate(R.layout.ad_comment, mLayout, false);			
		
		newCommentHolder.setTag(mItems.size());
		newCommentHolder.setId(mItems.size() + 1);				
		
		float[] hsv = new float[3];
		int darkerBgColor = Color.parseColor("#cacaca");
		/*
		int darkerBgColor = bgColor;
		Color.colorToHSV(darkerBgColor, hsv);
		hsv[2] *= 0.7f; // value component
		darkerBgColor = Color.HSVToColor(hsv); */
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		if(mItems.size() > 0){
			params.addRule(RelativeLayout.BELOW, mItems.size());		
		} else{
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		}
		params.rightMargin = 1;				
		((LTextView)newCommentHolder.findViewById(R.id.ad_comment_title)).setText(pComment.getTitle());
		newCommentHolder.setTag(mItems.size());		

		mItems.add(pComment);
		addView(newCommentHolder, params);
		
		newCommentHolder.setOnClickListener(this);
		
		return this;
	}

	@Override
	public void onClick(View view) {
		int position = ((Integer)view.getTag()).intValue();
		Comment comment = mItems.get(position);	
		Console.debug(TAG, "clicked commet: " + comment);
		// trigger listener
		if(null != mOnClickListener)
			mOnClickListener.onClick(view);
	}

	public void clear() {
		removeAllViews();
	}
	
	public View getView(int position){
		if(position < getChildCount())
			return getChildAt(position);
		else
			return null;
	}
}
