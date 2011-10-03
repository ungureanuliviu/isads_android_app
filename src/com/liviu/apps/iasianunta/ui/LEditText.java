package com.liviu.apps.iasianunta.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.liviu.apps.iasianunta.interfaces.OnAttachedImageClick;
import com.liviu.apps.iasianunta.utils.Console;


/**
 * @author Liviu
 * This class create a custom Edittext view
 * which support a image as attachemanet. It is a simple drawable
 * on the right side of view. When this attached image is clicked,
 * the event OnAttachedImageClick will be fired(if one was set)
 */

public class LEditText extends EditText{

	// Constants
	private final String TAG = "LEditText";
	 
	// Data
	private int attachedImageResourceID;
	
	// Listeners
	private com.liviu.apps.iasianunta.interfaces.OnAttachedImageClick onAttachedImageClick;
	
	public LEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		attachedImageResourceID = -1;
	}

	public LEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		attachedImageResourceID = -1;
	}
	
	public LEditText(Context context) {
		super(context);
		attachedImageResourceID = -1;
	}

	/**
	 * In case of the event was set, we will fire it.
	 * Note: the listener should return a value:
	 * in case the listener return true, the event is consumed.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(event.getX() > LEditText.this.getWidth() - 50){
				if(onAttachedImageClick != null){
					onAttachedImageClick.onAttachedImageClick(LEditText.this);
					event.setAction(MotionEvent.ACTION_CANCEL);
				}
				else{
					LEditText.this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Add a listener to attached image
	 * @param a listener which will be triggered when use click on the attached image 
	 * {@link OnAttachedImageClick}
	 * <strong>Note: </strong> If the listener return true, the onTouchEvent will be consumed.
	 */
	public void setOnAttachedImageClickListener(OnAttachedImageClick listener){
		onAttachedImageClick = listener;
	}
	
	/**
	 * Attach a image to this view.
	 * The attached image will be displayed on right side of the view.
	 * @param resourceId
	 * @throws Resources.NotFoundException in case the resouce is not found. 
	 *         This exception is handled internal so no image will be attache but the application won't crash
	 */
	public void setAttachedImageFromResource(int resourceId){
		try{
	    	this.setCompoundDrawablesWithIntrinsicBounds(null, 
	    												 null,
	    												 getResources().getDrawable(resourceId), 
														 null);
	    	attachedImageResourceID = resourceId;
		}
		catch (Resources.NotFoundException e) {
			e.printStackTrace();
			Console.debug(TAG, "Cannot find a resource for id: " + resourceId);			
		}
	}
	
	public int getAttachedImageResourceId(){
		return attachedImageResourceID;
	}
		
}
