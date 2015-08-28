package com.atn.app.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.atn.app.database.handler.DbHandler;
import com.atn.app.pool.UserDataPool;

public class FacebookSession {

	private SharedPreferences sharedPref;
	private static final String SHARED = "Facebook_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";
	private static final String API_EXPIRY_DATE = "expiry_date";
	private final String EMPTY = "";
	
	public FacebookSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param expireToken
	 * @param expiresIn
	 * @param username
	 */
	public void storeAccessToken(String accessToken, String id,String username, String name) {
		Editor editor = sharedPref.edit();
		editor.putString(API_ID, id);
		editor.putString(API_NAME, name);
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.putString(API_USERNAME, username);
		editor.commit();
		UserDataPool.getInstance().getUserDetail().setUserFbToken(accessToken);
		DbHandler.getInstance().loginUser(UserDataPool.getInstance().getUserDetail());
	}

	
	public void storeAccessToken(String accessToken) {
		Editor editor = sharedPref.edit();
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	
	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		Editor editor = sharedPref.edit();
		editor.clear();
		// editor.putString(API_ID, EMPTY);
		// editor.putString(API_NAME, EMPTY);
		editor.putString(API_ACCESS_TOKEN, EMPTY);
		// editor.putString(API_USERNAME, EMPTY);
		editor.commit();
	}

	
	/**
	 * Get user name
	 * 
	 * @return User name
	 */
	public String getUsername() {
		return sharedPref.getString(API_USERNAME, EMPTY);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getId() {
		return sharedPref.getString(API_ID, EMPTY);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return sharedPref.getString(API_NAME, EMPTY);
	}

	
	/**
	 * Get access token
	 * 
	 * @return Access token
	 */
	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, EMPTY);
	}
	
	
	public static String getAccessToken(Context context){
		return context.getSharedPreferences(SHARED, Context.MODE_PRIVATE).getString(API_ACCESS_TOKEN, "");
	}
	
	/**
	 * Returns true if the access token is stored in shared preferences.
	 * 
	 * @return true if available, otherwise returns false.
	 */
	public boolean hasAccessToken() {
		return (getAccessToken() == null || getAccessToken().trim().length() == 0) ? false
				: true;
	}
	
	
	/**
	 * Returns expiry date of token
	 * 
	 * @return
	 */
	public String getExpiryDate() {
		return sharedPref.getString(API_EXPIRY_DATE, EMPTY);
	}
	
	
	
	/**
	 * Returns user name from the current active session.
	 */
//	public void getUserNameFromSession()
//	{
//		Request mFsRequest = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback()
//		{
//			@Override
//			public void onCompleted(GraphUser user, Response response)
//			{
//				if (user != null)
//				{
//					storeAccessToken(Session.getActiveSession().getAccessToken(), user.getId(), user.getName(), user.getUsername());
//					
//					if(fbListener != null)
//					{
//						fbListener.getUserName(user.getUsername());
//					}
//				}
//				else
//				{
//					if(fbListener != null)
//					{
//						fbListener.onFailed();
//					}
//				}
//			}
//		});
//
//		Request.executeBatchAsync(mFsRequest);
//	}
	
}
