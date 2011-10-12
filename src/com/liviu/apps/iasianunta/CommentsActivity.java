package com.liviu.apps.iasianunta;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.CommentsAdapter;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ICommentsNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class CommentsActivity extends Activity implements ICommentsNotifier{
	// Constants
	private final 			String 	TAG 		= "CommentsActivity";
	public  final static 	int	 	ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(CommentsActivity.class);
	
	// Data
	private Ad 				mAd;
	private User 			mUser;	
	private AdsManager 		mAdsManager;
	private CommentsAdapter mCommentsAdapter;
	
	// UI
	private ListView		mLstComments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow(); 
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);              
        setContentView(R.layout.comments_layout);
        
       
        if(getIntent().getStringExtra(Utils.TRANSPORT_KEY) != null){
        	// we have an ad
        	Console.debug(TAG, "we have a new ad: " + getIntent().getStringExtra(Utils.TRANSPORT_KEY));
        	try {
				JSONObject json = new JSONObject(getIntent().getStringExtra(Utils.TRANSPORT_KEY));
				mAd = new Ad(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        } else{
        	Console.debug(TAG, "no ad specified");
        }
        
        if(null == mAd){
        	Toast.makeText(CommentsActivity.this, "Internal error[1].", Toast.LENGTH_SHORT).show();
        	setResult(RESULT_CANCELED);
        	finish();
        	return;
        }
        
        // we are good to go
        // the ad was received well
        Console.debug(TAG, "RECEIVED AD: " + mAd);
        
        // Initialize other objects
        mAdsManager 		= new AdsManager(this);
        mUser 				= User.getInstance();
        mCommentsAdapter	= new CommentsAdapter(this);
        mLstComments		= (ListView)findViewById(R.id.comments_list);
        
        mAdsManager.setCommentsNotifier(this);
        mAdsManager.getAllComments(mAd.getId());        
        
        mLstComments.setAdapter(mCommentsAdapter);
	}

	@Override
	public void onCommentLoaded(boolean isSuccess, int pAdId, ArrayList<Comment> pLoadedComments) {
		Console.debug(TAG, "onCommentsLoaded: " + isSuccess + " pAdId: " + pAdId + " " + pLoadedComments);
		if(isSuccess){
			for(int i = 0; i < pLoadedComments.size(); i++)
				mCommentsAdapter.addItem(pLoadedComments.get(i));
			mCommentsAdapter.notifyDataSetChanged();
		}
	}
	
	
}
