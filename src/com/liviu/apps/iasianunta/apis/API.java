package com.liviu.apps.iasianunta.apis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.AdImage;
import com.liviu.apps.iasianunta.data.Alert;
import com.liviu.apps.iasianunta.data.Category;
import com.liviu.apps.iasianunta.data.Comment;
import com.liviu.apps.iasianunta.data.JSONResponse;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdsNotifier;
import com.liviu.apps.iasianunta.interfaces.ICategoryNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.interfaces.IUploadNotifier;
import com.liviu.apps.iasianunta.utils.Base64;
import com.liviu.apps.iasianunta.utils.Console;
import com.liviu.apps.iasianunta.utils.Utils;

public class API {	
				
	// Constants
	private final String TAG 						= "API";
	private final String API_URL 					= "http://www.iasianunta.info/API";
	private final String API_UPLOAD_URL 			= "http://www.iasianunta.info/upload.php";
	private final int MSG_LOGIN 					= 1;
	private final int MSG_LOGOUT 					= 2;
	private final int MSG_UPLOAD_DONE 				= 3;
	private final int MSG_AD_ADDED 					= 4;
	private final int MSG_GET_CATEGORIES_DONE 		= 5;
	private final int MSG_ADS_DOWNLOADED 			= 6;
	
	// Data
	private HttpClient client; 	
	private ClientConnectionManager	cm;
    private HttpPost post;	  
	private HttpContext httpContext;
	private	HttpParams params;		
	private Handler handler;
	
	// Interfaces
	private ILoginNotifier 		mILoginNotifier;
	private IUploadNotifier 	mIUploadNotifier;
	private IAdsNotifier		mIAdsNotifier;
	private ICategoryNotifier	mICategoryNotifier;
	
	// private constructor
	public API(){		
		params 		= new BasicHttpParams();
        httpContext = new BasicHttpContext();
        
        ConnManagerParams.setMaxTotalConnections(params, 300);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);                          
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register( new Scheme("http", PlainSocketFactory.getSocketFactory(), 80) );
       
        cm 			= new ThreadSafeClientConnManager(params, schemeRegistry);
        client 		= new DefaultHttpClient(cm, params);
        
        
        handler = new Handler(){
        	public void handleMessage(android.os.Message msg) {
        		switch (msg.what) {
				case MSG_LOGIN:
					if(mILoginNotifier != null){
						if(msg.obj != null){
							mILoginNotifier.onLogin(true, (User)msg.obj);
						} else{
							mILoginNotifier.onLogin(false, null);
						}
					}
					break;
				case MSG_LOGOUT:
					if(mILoginNotifier != null){
						if(msg.arg1 > -1){
							mILoginNotifier.onLogout(true, msg.arg1);
						} else{
							mILoginNotifier.onLogout(true, -1);
						}
					}
					break;
				case MSG_UPLOAD_DONE:
					if(mIUploadNotifier != null){
						if(msg.obj != null){
							Object[] objs = (Object[]) msg.obj;
							Console.debug(TAG, "uri : " + objs[1] + " " + objs[0]);
							mIUploadNotifier.onImageUploaded(true, (Uri)objs[1], (String)objs[0]);
						} else{
							mIUploadNotifier.onImageUploaded(false, null, null);
						}
					}
					break;
				case MSG_AD_ADDED:
					if(null != mIAdsNotifier){
						if(null != msg.obj){
							mIAdsNotifier.onAdRemoteAdded(true, (Ad)msg.obj);
						} else{
							mIAdsNotifier.onAdRemoteAdded(false, null);
						}
					}
					break;
				case MSG_GET_CATEGORIES_DONE:
					if(null != mICategoryNotifier){
						if(null != msg.obj){
							mICategoryNotifier.onCategoriesSyncronized(true, (ArrayList<Category>)msg.obj);
						} else{
							mICategoryNotifier.onCategoriesSyncronized(false, null);
						}
					}
					break;
				case MSG_ADS_DOWNLOADED:
					if(null != mIAdsNotifier){
						if(null != msg.obj){
							JSONObject jsonRespnse = (JSONObject)msg.obj;
							int categoryId;
							try {
								categoryId = jsonRespnse.getInt("category_id");
								int adsPerPage 	= jsonRespnse.getInt("ads_per_page");
								int page	    = jsonRespnse.getInt("page");
								ArrayList<Ad> adsList = (ArrayList<Ad>)jsonRespnse.get("ads_list");
								mIAdsNotifier.onAdsLoaded(true, categoryId, page, adsPerPage, adsList);								
							} catch (JSONException e) {
								e.printStackTrace();
								mIAdsNotifier.onAdsLoaded(false, -1, -1, -1, null);
							}
						} else{
							mIAdsNotifier.onAdsLoaded(false, -1, -1, -1, null);
						}
					}
					break;
				default:
					break;
				}
        	};
        };
        
	}
	
	private String getCredentials(String userName, String password){		
		return Base64.encodeBytes((userName + ":" + password).getBytes());
	}
		

	private JSONResponse doRequest(String url, JSONObject jsonParams, String pUserAuth, String pUserPassword){
		Console.debug(TAG, "doRequest: " + url + " params: " + (null != jsonParams ? jsonParams.toString().replaceAll(",", "\n") : null) + " userName: " + pUserAuth + " password: " + pUserPassword);
	    try {	       
	        post = new HttpPost(url);       
	        
	        // prepare parameters
	        JSONObject params = null;
	        if(null == jsonParams)
	        	params = new JSONObject();
	        else
	        	params = jsonParams;
	        
	        params.put("client", "android"); // set the client
            StringEntity en = new StringEntity(params.toString());	
            Console.debug(TAG, "params: " + params);
	        post.setEntity(en);
	        post.setHeader("Accept", "application/json");
	        post.setHeader("Content-type", "application/json");
	        
	        if(pUserAuth != null && pUserPassword != null)
	        	post.addHeader("Authorization","Basic "+ getCredentials(pUserAuth, pUserPassword));	        
	        
            HttpResponse responsePOST = client.execute(post, httpContext);  
            HttpEntity resEntity = responsePOST.getEntity();
            String apiResponse = EntityUtils.toString(resEntity);
            Console.debug(TAG, "API RESPONSE: " + apiResponse);
            JSONResponse response = new JSONResponse(apiResponse);
	        return response;

	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    	return null;
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return null;
	    } catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public API login(String pUserAuthName, String pUserPassword, ILoginNotifier pLoginCallback){
		mILoginNotifier = pLoginCallback;
		final String cUserAuthName = pUserAuthName;
		final String cUserPassword = pUserPassword;
		
		Thread tLogin = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_LOGIN;							
				try {
					// set login parameters
					String url = API_URL + "/session/login/";
					JSONObject params = new JSONObject();
					
					params.put("user_name", cUserAuthName);
					params.put("user_password", cUserPassword);
					JSONResponse jsonReponse = doRequest(url, params, cUserAuthName, cUserPassword);
					
					if(null != jsonReponse){
						User loggedUser = User.getInstance();						
						if(jsonReponse.isSuccess()){
							JSONObject userJson = jsonReponse.getJSONObject("user");
							if(null != userJson){ 								
								loggedUser.setName(userJson.getString("name"));		
								loggedUser.setAuthName(cUserAuthName);
								loggedUser.setPassword(cUserPassword);
								loggedUser.setActivationCode(userJson.getString("activate_code"));
								loggedUser.setLoginStatus(userJson.getInt("is_logged_in") == 1 ? true : false);
								loggedUser.setActiveStatus(userJson.getInt("is_active") == 1 ? true : false);
								loggedUser.setEmail(userJson.getString("email"));		
								loggedUser.setId(userJson.getInt("id"));
								msg.obj = loggedUser;
								handler.sendMessage(msg);
							}else{
								handler.sendMessage(msg);
							}
						}else{
							handler.sendMessage(msg);	
						}						
					} else{
						handler.sendMessage(msg);
					}										
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				}							
			}
		});		
		tLogin.start();
		return this;
	}

	public API logoutUser(User pUser, ILoginNotifier pLoginCallback){		
		mILoginNotifier = pLoginCallback;
		if(!pUser.isLoggedIn()){
			Console.debug(TAG, "[logout]:  the user is not logged int " + pUser);
			mILoginNotifier.onLogout(false, pUser.getId());
			return this;
		}
		final User cUser = pUser;		
				
		Thread tLogout = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_LOGOUT;		
				msg.arg1 = -1;
				try {
					// set login parameters
					String url = API_URL + "/session/logout/";
					JSONObject params = new JSONObject();
					
					params.put("user_id", cUser.getId());					
					JSONResponse jsonReponse = doRequest(url, params, cUser.getAuthName(), cUser.getPassword());
					
					if(null != jsonReponse){												
						if(jsonReponse.isSuccess()){
							int userId = jsonReponse.getInt("user_id");
							msg.arg1 = userId;
							handler.sendMessage(msg);
						}else{
							handler.sendMessage(msg);	
						}						
					} else{
						handler.sendMessage(msg);
					}										
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				}							
			}
		});		
		tLogout.start();
		return this;
	}

	public void uploadImage(Uri uri, Context context) {
		final Uri cUri = uri;
		final Context cContext = context;		
		if(uri.toString().length() == 0){
			handler.sendEmptyMessage(MSG_UPLOAD_DONE);
			return;
		}
		
		Thread tUpload = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_UPLOAD_DONE;				
											
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(API_UPLOAD_URL);
				 
				try {
				  String filePath = getRealPathFromURI(cUri, cContext);		
				  if(null == filePath){
					  // the user tried to download a image from a remote server
					  // but the internet connection was down.
					  Console.debug(TAG, "filePath is null");
					  handler.sendMessage(msg);
					  return;
				  }
				  File imgFile = new File(filePath);
				  
				  Console.debug(TAG, "Upload file: " + imgFile  + " length: " + imgFile.length());
				  
				  MultipartEntity entity = new MultipartEntity();				  
				  entity.addPart("type", new StringBody("photo"));
				  entity.addPart("data", new FileBody(imgFile));				  
				  httppost.setEntity(entity);

				  // make the request
				  HttpResponse response = httpclient.execute(httppost);
				  
				  // get response
				  String jsonResponse = EntityUtils.toString(response.getEntity());
				  
				  // prepare the response
				  msg.obj = new Object[]{jsonResponse, cUri};
				  handler.sendMessage(msg);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				}		
			}
		});
		tUpload.start();
	}				
	
	public API setUploadNotifier(IUploadNotifier pINotifier){
		mIUploadNotifier = pINotifier;
		return this;
	}
	
	public API setAdNotifier(IAdsNotifier pAdNotifier){
		mIAdsNotifier = pAdNotifier;
		return this;
	}
	
	public API setCategoryNotifier(ICategoryNotifier pICategoryNotifier){
		mICategoryNotifier = pICategoryNotifier;
		return this;
	}
	
	private synchronized String getRealPathFromURI(Uri contentUri, Context ctx) {
		if(contentUri != null){
	        String[] proj = { MediaStore.Images.Media.DATA };
	        Cursor cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
		} else{
			return null;
		}
    }
	
	 /*		/API/ads/add/
	 *		App a new ad into system
	 *		params:
	 *				title: the title of the new ad [required]
	 *				content: the conente of the new ad [required]
	 *				price: the price [optional]
	 *				address: the address(if it is the case) [optional]
	 *				category_id: the id of the catergory where this comment will be added [required]
	 *				phone: ad owner's phone [required]
	 *				email: ad owner's email [optional]
	 *				user_id: the current user id [required]
	 *				source: the source of this ad (iasianunta.info, Android app, etc)
	 *
	 *		response: {"is_success":1,"ad":{"id":852,"title":"teasdgds","content":"dgsgds","price":"32","address":"dsdgsdgs","cat_id":"4","user_id":"1","comments":[],"total_comments":0,"views":0,"source":"iasianunta.info","email":"smartliviu@gmail.com"}}
	 */	
	public API addNewAd(Ad pNewAd, int pUserId, String pUserAuth, String pUserPassword){
		if(null == pNewAd){
			handler.sendEmptyMessage(MSG_AD_ADDED);
			return this;
		}
		
		// set constants
		final Ad cNewAd 			= pNewAd;
		final int cUserId 			= pUserId;
		final String cUserAuth 		= pUserAuth;
		final String cUserPassword 	= pUserPassword;
		
		Thread tAdd = new Thread(new Runnable() {			
			@Override
			public void run() {					
				try {
					JSONObject params 	= new JSONObject();
					Message msg		 	= new Message();
					msg.what			= MSG_AD_ADDED;
					
					params.put("title", cNewAd.getTitle());
					
					params.put("content", cNewAd.getContent());
					params.put("price", cNewAd.getPrice());
					params.put("address", cNewAd.getAddress());
					params.put("phone", cNewAd.getPhone());
					params.put("email", cNewAd.getEmail());
					params.put("user_id", cUserId);
					params.put("source", cNewAd.getSource());
					params.put("category_id", cNewAd.getCategoryId());					
					JSONArray images = new JSONArray();
					for(int i = 0; i < cNewAd.getImages().size(); i++){
						images.put(cNewAd.getImages().get(i).getServerFileInfo());						
					}
					params.put("images", images);
						
					JSONResponse jsonResponse = doRequest(API_URL + "/ads/add/", params, cUserAuth, cUserPassword);
					if(null != jsonResponse){						
						if(jsonResponse.isSuccess()){
							cNewAd.setId(jsonResponse.getJSONObject("ad").getInt("id"));
							//cNewAd.setDate(jsonResponse.getJSONArray("ad").getLong("date"));
							msg.obj = cNewAd;
							handler.sendMessage(msg);
						} else{
							handler.sendMessage(msg);
						}
					} else{
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(MSG_AD_ADDED);
				}
			}
		});
		tAdd.start();
		return this;
	}

	public API getAllCategories() {
		Thread tGetAllCategories = new Thread(new Runnable() {			
			@Override
			public void run() {				
				JSONResponse jsonResponse = doRequest(API_URL + "/categories/get_all/", null, null, null);
				Message msg 		= new Message();
				msg.what = MSG_GET_CATEGORIES_DONE;
				
				try {					 
					if(null != jsonResponse && jsonResponse.isSuccess()){						
						ArrayList<Category> categories = new ArrayList<Category>();
						JSONArray catArray = jsonResponse.getJSONArray("categories");
						
						for(int i = 0; i < catArray.length(); i++){
							try{
								categories.add(new Category(catArray.getJSONObject(i).getInt("id"),
															catArray.getJSONObject(i).getString("name")));
							}catch (JSONException e) {
								e.printStackTrace();
							}
						}					
						
						msg.obj = categories;
						handler.sendMessage(msg);
						
					}else{
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				}
			}
		});
		tGetAllCategories.start();
		return this;
	}

	public API getAds(int pCategoryId, int pPage, int pAdsPerPage) {
		final int cCategoryId  	= pCategoryId;
		final int cPage 		= pPage;
		final int cAdsPerPage 	= pAdsPerPage;
		
		Thread tGetAds = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what 	= MSG_ADS_DOWNLOADED;
				
				try {
					JSONObject params = new JSONObject();
					params.put("page", cPage);
					params.put("category_id", cCategoryId);
					params.put("ads_per_page", cAdsPerPage);
					JSONResponse jsonResponse = doRequest(API_URL + "/ads/get_all/", params, null, null);										
					if(jsonResponse.isSuccess()){						
						JSONObject data = new JSONObject();
						data.put("category_id", cCategoryId);
						data.put("page", cPage);
						data.put("ads_per_page", cAdsPerPage);
						
						ArrayList<Ad> adsList 	= new ArrayList<Ad>();
						JSONArray adsJSonArray 	= jsonResponse.getJSONArray("ads");
						for(int i = 0; i < adsJSonArray.length(); i++){
							JSONObject jAd = adsJSonArray.getJSONObject(i);
							Ad ad = new Ad();
							ad.setId(jAd.getInt("id"))
							  .setAddress(jAd.isNull("address") 	== true ? null : jAd.getString("address"))
							  .setCategoryId(jAd.isNull("cat_id") 	== true ? null : jAd.getInt("cat_id"))
							  .setContent(jAd.isNull("content") 	== true ? null : jAd.getString("content"))
							  .setDate(jAd.isNull("date") 			== true ? -1 : jAd.getLong("date")* 1000)
							  .setEmail(jAd.isNull("email")	 		== true ? null : jAd.getString("email"))
							  .setPhone(jAd.isNull("phone") 		== true ? null : jAd.getString("phone"))
							  .setSource(jAd.isNull("source") 		== true ? null : jAd.getString("source"))
							  .setTitle(jAd.isNull("title") 		== true ? null : jAd.getString("title"))							  
							  .setUserId(jAd.isNull("user_id") 		== true ? -1 : jAd.getInt("user_id"))
							  .setViewsCount(jAd.isNull("views") 	== true ? 0 : jAd.getInt("views"))
							  .setAuthor(jAd.isNull("user_name") 	== true ? null : jAd.getString("user_name"))
							  .setFormattedDate(jAd.isNull("date")  == true ? "" : Utils.formatDate(jAd.getLong("date") * 1000, "d MMM yyyy"))
							  .setCategoryName(jAd.isNull("cat_name") == true ? null : jAd.getString("cat_name"))
							  .adComment(new Comment().setTitle("testing1"))
							  .adComment(new Comment().setTitle("testing2"))
							  .adComment(new Comment().setTitle("testing3"))
							  .adComment(new Comment().setTitle("testing4"))
							  .adComment(new Comment().setTitle("testing5"))
							  .adComment(new Comment().setTitle("testing6"))
							  .adComment(new Comment().setTitle("testing7"))
							  .setTotalComments(jAd.isNull("total_comments") == true ? 0 : jAd.getInt("total_comments"));
								
							JSONArray jAdImages = jAd.getJSONArray("images");
							for(int imgIndex = 0; imgIndex < jAdImages.length(); imgIndex++){
								AdImage img = new AdImage(null, jAdImages.getJSONObject(imgIndex));
								ad.addImage(img);
							}
							
							adsList.add(ad);							
						}
						
						data.put("ads_list", adsList);
						msg.obj = data;
						handler.sendMessage(msg);
					} else{
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendMessage(msg);
				}				
			}
		});
		tGetAds.start();
		
		return this;
	}

	public synchronized Bitmap downloadThImage(String pImgUrl, Options options) {
		Bitmap 	bmImg;		
		URL 	myFileUrl =	null; 
		
		try {
			myFileUrl= new URL(pImgUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is 	= conn.getInputStream();
			bmImg 			= BitmapFactory.decodeStream(is, null, options);
			
			is.close();
			conn.disconnect();			
			return bmImg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}

	public synchronized ArrayList<Comment> getComments(int cAdId) {			
		try {
			JSONObject params = new JSONObject();
			params.put("ad_id", cAdId);
			JSONResponse jsonResponse = doRequest(API_URL + "/comments/get_all/", params, null, null);
			if(null != jsonResponse){
				if(jsonResponse.isSuccess()){
					JSONArray jComments = jsonResponse.getJSONArray("comments");
					ArrayList<Comment> commentsList = new ArrayList<Comment>();
					for(int i = 0; i < jComments.length(); i++){
						Comment 	comment	 = new Comment();
						JSONObject 	jComment = jComments.getJSONObject(i); 
						comment.setId(jComment.getInt("id"))
							   .setTitle(jComment.getString("title"))
							   .setContent(jComment.getString("content"))
							   .setAdId(jComment.getInt("ad_id"))
							   .setAuthorId(jComment.getInt("owner_user_id"))
							   .setDate(jComment.getLong("date") * 1000)
							   .setFormattedDate(Utils.formatDate(jComment.getLong("date") * 1000, "d MMM yyyy HH:mm:ss"))
							   .setAuthor(jComment.getString("user_name"))
							   .setRating(jComment.getInt("rating"));
						commentsList.add(comment);
					}
					return commentsList;
				}else{
					return null;
				}
			}else{
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}		
	}

	public Comment addComment(Comment pComment, String pUserAuth, String pUserPassword) {		
		try {
			JSONObject params = new JSONObject();
			params.put("ad_id", pComment.getAdId());
			params.put("title", pComment.getTitle());
			params.put("content", pComment.getContent());
			params.put("owner_user_id", pComment.getAuthorId());
			JSONResponse jsonResponse = doRequest(API_URL + "/comments/add/", params, pUserAuth, pUserPassword);
			if(jsonResponse != null && jsonResponse.isSuccess()){
				int newAdId = jsonResponse.getJSONObject("comment").getInt("id");
				pComment.setId(newAdId);
				return pComment;
			} else{
				return null;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return null;
	}

	public synchronized ArrayList<Alert> getAllAlerts(int pUserId, String pUserAuth, String pUserPassword) {
		try{
			JSONObject 			paramas = new JSONObject();
			ArrayList<Alert> 	alerts 	= new ArrayList<Alert>(); 
			
			paramas.put("user_id", pUserId);
			
			JSONResponse jsonResponse = doRequest(API_URL + "/alerts/get_all/",paramas, pUserAuth, pUserPassword);			
			if(null != jsonResponse){
				JSONArray jAlertsArray = jsonResponse.getJSONArray("alerts");
				for(int i = 0; i < jAlertsArray.length(); i++){
					JSONObject jAlert = jAlertsArray.getJSONObject(i);
					Alert newAlert	  = new Alert();
					
					newAlert.setId(jAlert.getInt("id"))
							.setTitle(jAlert.getString("title"))
							.setUserId(jAlert.getInt("user_id"))
							.setAddedDate(jAlert.getLong("added_date") * 1000)
							.setLastCheckedDate(jAlert.getLong("last_checked_date") * 1000)
							.setCategoryId(jAlert.getInt("cat_id"));
					JSONArray jFilters = jAlert.getJSONArray("filters");
					for(int j = 0; j < jFilters.length(); j++)
						newAlert.addFilter(jFilters.getString(j));
					
					alerts.add(newAlert);
				}				
				return alerts;
			} else{
				return null;
			}			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}	
}
