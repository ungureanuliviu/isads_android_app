package com.liviu.apps.iasianunta.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Liviu
 * This class create a custom TevtView with a default font
 */

public class LTextView extends TextView{

	// constants
	private final 	String 		TAG = "LTextView";	
	private 		Typeface	typeface;
	
	public LTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);	
	}

	public LTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);	
	}
	
	public LTextView(Context context) {
		super(context);
		init(context);		
	}	
	
	private void init(Context pContext){
		if(getTypeface() == typeface.DEFAULT_BOLD){			
			typeface = Typeface.createFromAsset(pContext.getAssets(), "fonts/VAGROUN.TTF");		
		}
		else
			typeface = Typeface.createFromAsset(pContext.getAssets(), "fonts/VAGRON.TTF");
		
		if(typeface != null)
			setTypeface(typeface);			
	}
}
