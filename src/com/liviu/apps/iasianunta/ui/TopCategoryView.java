package com.liviu.apps.iasianunta.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.liviu.apps.iasianunta.R;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.utils.Console;

public class TopCategoryView extends HorizontalScrollView implements OnClickListener{
	

	// Constants
	private final String TAG = "TopCategoryView";
	
	// Data
	private ArrayList<CategoryView> mItems;
	private LayoutInflater 		mLf;
	private RelativeLayout		mLayout;
	
	// Listeners
	private OnClickListener mOnClickListener;
	
	public TopCategoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = new RelativeLayout(context);
		mItems  = new ArrayList<CategoryView>();
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		addView(mLayout, lParams);
	}

	public TopCategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = new RelativeLayout(context);
		mItems  = new ArrayList<CategoryView>();
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		addView(mLayout, lParams);
	}
	
	public TopCategoryView(Context context) {
		super(context);		
		mLf 	= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = new RelativeLayout(context);
		mItems  = new ArrayList<CategoryView>();
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		addView(mLayout, lParams);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {	
		mOnClickListener = l;
	}
	
	public TopCategoryView addCategory(Category pCategory, int width, int bgColor){
		if(null == pCategory)
			return this;
		LTextView newCategoryView = (LTextView)mLf.inflate(R.layout.top_category_layout, mLayout, false);		
		
		newCategoryView.setTag(mItems.size());
		newCategoryView.setId(mItems.size() + 1);				
		
		float[] hsv = new float[3];
		int darkerBgColor = bgColor;
		Color.colorToHSV(darkerBgColor, hsv);
		hsv[2] *= 0.9f; // value component
		darkerBgColor = Color.HSVToColor(hsv);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, LayoutParams.FILL_PARENT);
		if(mItems.size() > 0){
			params.addRule(RelativeLayout.RIGHT_OF, mItems.size());
		} else{
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		}
		newCategoryView.setText(pCategory.getName());
		newCategoryView.setTag(mItems.size());
		newCategoryView.setBackgroundColor(darkerBgColor);
		mItems.add(new CategoryView(pCategory, bgColor, darkerBgColor));
		mLayout.addView(newCategoryView, params);
		
		newCategoryView.setOnClickListener(this);
		
		return this;
	}

	@Override
	public void onClick(View view) {
		int position = ((Integer)view.getTag()).intValue();
		CategoryView catView = mItems.get(position);
		if(catView.isSelected()){
			Console.debug(TAG, "1");
			view.setBackgroundColor(catView.getDefaultColor());
		} else{
			Console.debug(TAG, "2");
			view.setBackgroundColor(catView.getSelectedColor());
		}
		mItems.get(position).setSelected(!catView.isSelected());
		
		// trigger listener
		if(null != mOnClickListener)
			mOnClickListener.onClick(view);
	}	
}
