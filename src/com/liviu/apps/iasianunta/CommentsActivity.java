package com.liviu.apps.iasianunta;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liviu.apps.iasianunta.adapters.CommentsAdapter;
import com.liviu.apps.iasianunta.apis.API;
import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.ICommentsNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.managers.ActivityIdProvider;
import com.liviu.apps.iasianunta.managers.AdsManager;
import com.liviu.apps.iasianunta.ui.LEditText;
import com.liviu.apps.iasianunta.ui.LTextView;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class CommentsActivity extends Activity implements ICommentsNotifier, 
														  OnClickListener{
	// Constants
	private final 			String 	TAG 		= "CommentsActivity";
	public  final static 	int	 	ACTIVITY_ID = ActivityIdProvider.getInstance().getNewId(CommentsActivity.class);
	
	// Data
	private Ad 				mAd;
	private User 			mUser;	
	private AdsManager 		mAdsManager;
	private CommentsAdapter mCommentsAdapter;
	private API				mApi;
	private int				mIndex = 0;
	
	// UI
	private ListView		mLstComments;
	private Button			mButAddComment;
	private LEditText		mEdtxContentComment;
	private ProgressBar		mAddCommentProgress;
	private LTextView		mTxtAddCommentProgress;
	private ProgressBar		mLoadCommentsProgress;
	private LTextView		mTxtLoadCommnetsProgress;
	private LTextView		mTxtNoComments;
	private LTextView		mTxtTitle;
	private LTextView		mTxtContent;
	private LTextView		mTxtCategory;
	private LTextView		mTxtDate;
	private LTextView		mTxtIndex;
	private LTextView		mTxtAuthor;
	private LTextView		mTxtViews;
	private LTextView		txtUserName;
	private Button			mButCall;
	private Button			butLogin;
	// Services
	private InputMethodManager imm; 
	
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
				mIndex = getIntent().getIntExtra("ad_index", 0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        } else{
        	Console.debug(TAG, "no ad specified");
        }
        
        if(null == mAd){        	
        	setResult(RESULT_CANCELED);
        	finish();
        	return;
        }
        
        // we are good to go
        // the ad was received well
        Console.debug(TAG, "RECEIVED AD: " + mAd);             
        
        // Initialize other objects
        mAdsManager 			= new AdsManager(this);
        mApi					= new API();
        mUser 					= User.getInstance();
        mCommentsAdapter		= new CommentsAdapter(this);
        mLstComments			= (ListView)findViewById(R.id.comments_list);
        mButAddComment			= (Button)findViewById(R.id.comments_but_add);
        mEdtxContentComment		= (LEditText)findViewById(R.id.edtx_comment);
        imm						= (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mAddCommentProgress		= (ProgressBar)findViewById(R.id.ad_comment_progress);
        mTxtAddCommentProgress 	= (LTextView)findViewById(R.id.txt_ad_comment_progress);
        mLoadCommentsProgress 	= (ProgressBar)findViewById(R.id.load_comments_progress);
        mTxtLoadCommnetsProgress= (LTextView)findViewById(R.id.txt_load_comments_progress);
        mTxtNoComments			= (LTextView)findViewById(R.id.txt_no_comments);
        mTxtTitle				= (LTextView)findViewById(R.id.ad_title);
        mTxtDate				= (LTextView)findViewById(R.id.ad_date);
        mTxtContent				= (LTextView)findViewById(R.id.ad_content);
        mTxtCategory			= (LTextView)findViewById(R.id.ad_category);
        mTxtIndex				= (LTextView)findViewById(R.id.ad_index);
        mTxtAuthor				= (LTextView)findViewById(R.id.ad_author);
        mTxtViews				= (LTextView)findViewById(R.id.ad_views);
        mButCall				= (Button)findViewById(R.id.ad_but_call);
        txtUserName 			= (LTextView)findViewById(R.id.user_name);
        butLogin 				= (Button)findViewById(R.id.main_but_login);
        
        // check if the use is logged in
        if(mUser.isLoggedIn()){
        	txtUserName.setText("Salut " + mUser.getName());
        	butLogin.setText("Logout");
        } else{
        	// the user is not logged in.
        	// we have to update the UI to reflect this state
        	butLogin.setText("Login");
        }   
        
        mAdsManager.setCommentsNotifier(this);
        mAdsManager.getAllComments(mAd.getId());        
        mTxtTitle.setText(mAd.getTitle());
        mTxtIndex.setText(Integer.toString(mIndex));
        mTxtContent.setText(mAd.getContent());
        mTxtCategory.setText(mAd.getCategoryName());
        mTxtDate.setText(mAd.getFormattedDate());
        mTxtAuthor.setText(mAd.getAuthor());
        mTxtViews.setText(mAd.getViewsCount() + " " + (mAd.getViewsCount() == 1 ? "vizualizare" : "vizualizari"));
        
        if(mAd.getPhone().length() > 0)
        	mButCall.setText(mAd.getPhone().replace("/", ""));
        else
        	mButCall.setVisibility(View.GONE);
        
        mLstComments.setAdapter(mCommentsAdapter);
        
        // Listener
        mButAddComment.setOnClickListener(this);
        mButCall.setOnClickListener(this);
        butLogin.setOnClickListener(this);
	}

	@Override
	public void onCommentLoaded(boolean isSuccess, int pAdId, ArrayList<Comment> pLoadedComments) {
		Console.debug(TAG, "onCommentsLoaded: " + isSuccess + " pAdId: " + pAdId + " " + pLoadedComments);		
		mLoadCommentsProgress.setVisibility(View.GONE);
		mTxtLoadCommnetsProgress.setVisibility(View.GONE);
		if(isSuccess){
			if(pLoadedComments.size() == 0){
				mTxtNoComments.setVisibility(View.VISIBLE);
			} else{
				for(int i = 0; i < pLoadedComments.size(); i++)
					mCommentsAdapter.addItem(pLoadedComments.get(i));
				mCommentsAdapter.notifyDataSetChanged();
			}
		} else{	
			mTxtNoComments.setVisibility(View.VISIBLE);
			mTxtNoComments.setText("Comentariile nu au putut fi preluate. \nVa rugam verificati conexiunea la Internet.");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ad_but_call:
				Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:"+mAd.getPhone().replace("/", "")));
	            startActivity(callIntent);			
				break;
			case R.id.comments_but_add:  
				if(mEdtxContentComment.getText().length() == 0){
					Toast.makeText(CommentsActivity.this, "Comentariul dvs. nu poate fi gol.", Toast.LENGTH_LONG).show();
				}else{
			        if(!Utils.isConnected(this)){
			        	Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
			        	finish();		        	
			        } else{
			        	imm.hideSoftInputFromWindow(mEdtxContentComment.getWindowToken(), 0);		        	
						if(mUser.isLoggedIn()){
							mButAddComment.setVisibility(View.GONE);
							mEdtxContentComment.setVisibility(View.GONE);
							mAddCommentProgress.setVisibility(View.VISIBLE);  
							mTxtAddCommentProgress.setVisibility(View.VISIBLE);
							String commentContent = mEdtxContentComment.getText().toString();
							Comment newComment = new Comment();
							newComment.setContent(commentContent)
								.setAuthor(mUser.getName())
								.setAuthorId(mUser.getId())
								.setTitle(commentContent.length() > 5 ? commentContent.substring(0, 5) : commentContent)
								.setAdId(mAd.getId());
							mAdsManager.addComment(newComment, mUser.getAuthName(), mUser.getPassword());
						} else{
				        	// the user is not logged in
				        	// we have to redirect him to login activity
				        	Intent toLogin = new Intent(CommentsActivity.this, LoginActivity.class);
				        	toLogin.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
				        	startActivity(toLogin);
				        	finish();
						}
			        }
				}
				break;
			case R.id.main_but_login:
				if(!mUser.isLoggedIn()){
					// we have to log in the user so 
					// let's start login activity
					Intent toLoginIntent = new Intent(CommentsActivity.this, LoginActivity.class);
					toLoginIntent.putExtra(LoginActivity.PARENT_ACTIVITY_ID, ACTIVITY_ID);
					startActivity(toLoginIntent);
					finish();
				} else{
					// the user is logged in. 
					// Now, we will log him out.
					mApi.logoutUser(mUser, new ILoginNotifier() {					
							@Override
							public void onLogout(boolean isSuccess, int pUserId) {
								if(isSuccess){
									// logout done
									mUser.logout();
									Toast.makeText(CommentsActivity.this, "Logout success.", Toast.LENGTH_SHORT).show();
									butLogin.setText("Login"); // update the main button
									txtUserName.setText("Holla amigos");
								} else{
									// hmmm.. something went wrong...
									Toast.makeText(CommentsActivity.this, "Logout error.", Toast.LENGTH_SHORT).show(); 
								}
							}					
							@Override
							public void onLogin(boolean isSuccess, User pUser) {
								// nothing here
							}
						});
				}
				break;				

		default:
			break;
		}
	}

	@Override
	public void onCommentAdded(boolean isSuccess, Comment pAddedComment) {
		Console.debug(TAG, "OnCommentAdded: " + isSuccess + " " + pAddedComment);						
		if(isSuccess){
			mEdtxContentComment.setText("");
			mCommentsAdapter.addItem(pAddedComment);
			mCommentsAdapter.notifyDataSetChanged();
			mLstComments.setSelection(mCommentsAdapter.getCount());			
			mTxtNoComments.setVisibility(View.GONE);
			Toast.makeText(CommentsActivity.this, "Comentariul dvs. a fost adaugat.", Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(CommentsActivity.this, "Ne pare rau insa comentariul dvs. nu a putut fi adaugat pt moment.", Toast.LENGTH_LONG).show();
		}
				
		mButAddComment.setVisibility(View.VISIBLE);
		mEdtxContentComment.setVisibility(View.VISIBLE);
		mAddCommentProgress.setVisibility(View.GONE);
		mTxtAddCommentProgress.setVisibility(View.GONE);		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent data = new Intent();
			data.putExtra("new_comments_count", mCommentsAdapter.getCount());
			data.putExtra("ad_id", mAd.getId());
			setResult(Utils.RESULT_CODE_COMMENTS_COUNT_CHANGED, data);
		}
		return super.onKeyDown(keyCode, event);
	}
}
