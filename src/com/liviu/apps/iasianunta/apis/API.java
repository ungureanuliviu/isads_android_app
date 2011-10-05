package com.liviu.apps.iasianunta.apis;

import java.io.File;
import java.io.IOException;

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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.liviu.apps.iasianunta.data.Ad;
import com.liviu.apps.iasianunta.data.JSONResponse;
import com.liviu.apps.iasianunta.data.User;
import com.liviu.apps.iasianunta.interfaces.IAdNotifier;
import com.liviu.apps.iasianunta.interfaces.ILoginNotifier;
import com.liviu.apps.iasianunta.interfaces.IUploadNotifier;
import com.liviu.apps.iasianunta.utils.Base64;
import com.liviu.apps.iasianunta.utils.Console;

public class API {	
		
	// Constants
	private final String TAG 				= "API";
	private final String API_URL 			= "http://www.iasianunta.info/API";
	private final String API_UPLOAD_URL 	= "http://www.iasianunta.info/upload.php";
	private final int MSG_LOGIN 			= 1;
	private final int MSG_LOGOUT 			= 2;
	private final int MSG_UPLOAD_DONE 		= 3;
	private final int MSG_AD_ADDED 			= 4;
	
	// Data
	private static API mInstance;
	private HttpClient client; 	
	private ClientConnectionManager	cm;
    private HttpPost post;	  
	private HttpContext httpContext;
	private	HttpParams params;		
	private Handler handler;
	
	// Interfaces
	private ILoginNotifier 	mILoginNotifier;
	private IUploadNotifier mIUploadNotifier;
	private IAdNotifier		mIAdNotifier;
	
	// private constructor
	private API(){		
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
					if(null != mIAdNotifier){
						if(null != msg.obj){
							mIAdNotifier.onAdRemoteAdded(true, (Ad)msg.obj);
						} else{
							mIAdNotifier.onAdRemoteAdded(false, null);
						}
					}
					break;
				default:
					break;
				}
        	};
        };
        
	}
	
	// get a instance of singleton object
	public static API getInstance(){
		if(mInstance != null)
			return mInstance;
		else
			return (mInstance = new API()); 		
	}
	
	private String getCredentials(String userName, String password){		
		return Base64.encodeBytes((userName + ":" + password).getBytes());
	}
		

	private String doRequest(String url, JSONObject jsonParams, String pUserAuth, String pUserPassword){
		Console.debug(TAG, "doRequest: " + url + " params: " + jsonParams.toString().replaceAll(",", "\n") + " userName: " + pUserAuth + " password: " + pUserPassword);
	    try {	       
	        post = new HttpPost(url);       
	        
	        // prepare parameters
	        JSONObject params = jsonParams;
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
	        return apiResponse;

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
					String apiResponse = doRequest(url, params, cUserAuthName, cUserPassword);
					
					if(null != apiResponse){
						User loggedUser = User.getInstance();
						JSONResponse jsonReponse = new JSONResponse(apiResponse);
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
					String apiResponse = doRequest(url, params, cUser.getAuthName(), cUser.getPassword());
					
					if(null != apiResponse){						
						JSONResponse jsonReponse = new JSONResponse(apiResponse);
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
	
	public API setAdNotifier(IAdNotifier pAdNotifier){
		mIAdNotifier = pAdNotifier;
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
					params.put("category_id", "2");
					JSONArray images = new JSONArray();
					for(int i = 0; i < cNewAd.getImages().size(); i++){
						images.put(cNewAd.getImages().get(i).getServerFileInfo());						
					}
					params.put("images", images);
						
					String apiResponse = doRequest(API_URL + "/ads/add/", params, cUserAuth, cUserPassword);
					if(null != apiResponse){
						JSONResponse jsonResponse = new JSONResponse(apiResponse);
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

	// check if the user if connected to Internet
	public boolean isAvailable(Context mContext) {		
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);	
		if(null == cm.getActiveNetworkInfo())
			return false;
		else
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
}
