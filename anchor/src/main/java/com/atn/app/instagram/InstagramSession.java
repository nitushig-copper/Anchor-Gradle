package com.atn.app.instagram;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.atn.app.AtnApp;
import com.atn.app.httprequester.HttpManager;
import com.atn.app.instagram.InstagramDialog.InstagramLoginListener;
import com.atn.app.utils.HttpUtility;

/**
 * Manage access token and user name. Uses shared preferences to store access
 * token and user name.
 * 
 */
public class InstagramSession {
	
	private static final String SHARED = "Instagram_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";
	private final String EMPTY = "";
	
	public static final String ID = "client_id";
	public static final String SECRET = "client_secret";
	
	
	
	private static final String CLIENT_ID = "235419e34a3e4618919921a0a0fbe659"; 
	public static final String CALLBACK_URL = "ig235419e34a3e4618919921a0a0fbe659://authorize";

	
//	private static final String CLIENT_ID = "8adcef21f0314290b25c565e2cbf75a3"; // old ID
	//private static final String CLIENT_SECRET = "f9223403933f4f5fa88544891a155ebe";  //MOHAR: already commented
//	public static final String CALLBACK_URL = "ig8adcef21f0314290b25c565e2cbf75a3://authorize";

	
	
	public static String getInstagramLoginUrl(){
		
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("https");
		uri.authority("api.instagram.com");
		uri.appendPath("oauth").appendPath("authorize");

		uri.appendQueryParameter(ID, CLIENT_ID)
				.appendQueryParameter("redirect_uri", CALLBACK_URL)
				.appendQueryParameter("response_type", "token");
		 return uri.build().toString();
	}
	
	public static String getInstagramUserInfoUrl(String userId,String accessToken){
		//https://api.instagram.com/v1/users/1574083/?access_token=ACCESS-TOKEN
		Uri.Builder uri = new Uri.Builder();
		uri.scheme("https");
		uri.authority("api.instagram.com");
		uri.appendPath("v1").appendPath("users").appendPath(userId);
		uri.appendQueryParameter("access_token", accessToken);
		 return uri.build().toString();
	}
	
	public static String storeUserInfo(Context context,JSONObject jsonOBj) throws JSONException {
		
		String userName = "";
		Editor editor = context.getSharedPreferences(SHARED,Context.MODE_PRIVATE).edit();
//		if(!jsonOBj.isNull("id")) {
//			editor.putString(API_USERNAME, jsonOBj.getString("id"));	
//		}
//		
		if(!jsonOBj.isNull("username")){
			editor.putString(API_USERNAME, jsonOBj.getString("username"));	
		}
		
		if(!jsonOBj.isNull("full_name")){
			userName = jsonOBj.getString("full_name");
			editor.putString(API_NAME, jsonOBj.getString("full_name"));
		}
		editor.commit();
		return userName;
	}

	
	public static void storeAccessToken(Context context,String accessToken) {
		Editor editor = context.getSharedPreferences(SHARED,Context.MODE_PRIVATE).edit();;
	    editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	
	/**
	 * Reset access token and user name
	 */
	public static void resetAccessToken(Context context) {
		Editor editor = context.getSharedPreferences(SHARED,Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static String getName(Context context) {
		return context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
				.getString(API_NAME, "");
	}

	public String getUsername(Context context) {
		return context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
				.getString(API_USERNAME, EMPTY);
	}

	public static String getToken(Context context) {
		return context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
				.getString(API_ACCESS_TOKEN, "");
	}

	
	public interface InstagramUserInfoListner{
		public void onLoaduserInfo(boolean isSuccess,String message);
	}
	
	
	public static void showInstagramDialog(FragmentManager fm,InstagramLoginListener mListener){
		
		 	FragmentTransaction ft = fm.beginTransaction();
	        Fragment prev = fm.findFragmentByTag("dialog");
	        if (prev != null) {
	            ft.remove(prev);
	        }
	        //ft.addToBackStack(null);
	        // Create and show the dialog.
	        InstagramDialog   mDialog  = InstagramDialog.newInstance();
	        mDialog.setOAuthDialogListener(mListener);
	        mDialog.show(ft, "dialog");
	}
	
	
	private static FetchUserInfo fetcher = null;
	public  static void loadUserInfo(InstagramUserInfoListner listner) {
		if(fetcher==null||fetcher.getStatus()==Status.FINISHED) {
			fetcher = new  FetchUserInfo();
			fetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		if(fetcher!=null){
			fetcher.setListener(listner);	
		}
	}
	
	
	public static class FetchUserInfo extends AsyncTask<Void, Void, Boolean>{

		private InstagramUserInfoListner listener;
		private String message = "Unable to load user info";
		public void setListener(InstagramUserInfoListner listener) {
			this.listener = listener;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			String accessToken = InstagramSession.getToken(AtnApp.getAppContext());
			if(!TextUtils.isEmpty(accessToken)){
				String[] userId = accessToken.split("\\.");
				try {
					String responce = HttpUtility.processHttpResponse(HttpManager
									.execute(new HttpGet(getInstagramUserInfoUrl(userId[0],
													accessToken))));
					JSONObject jsonObj = new JSONObject(responce);
					if(!jsonObj.isNull("meta")&&jsonObj.getJSONObject("meta").getInt("code")==200) {
						if(!jsonObj.isNull("data")) {
							message = InstagramSession.storeUserInfo(AtnApp.getAppContext(), jsonObj.getJSONObject("data"));
							return true;
						} else {
							message = responce;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					message = e.getLocalizedMessage();
				}
			}
			return false;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(this.listener !=null){
				this.listener.onLoaduserInfo(result, message);
			}
		}
	}
}