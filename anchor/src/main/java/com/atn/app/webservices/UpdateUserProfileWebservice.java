package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.datamodels.UserDetail.GenderType;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates web service to update user profile details using specified user details.
 * @author gagan
 *
 */
public class UpdateUserProfileWebservice extends WebserviceBase
{
	private static final String UPDATE_PROFILE_API = "/profile";
	private final static String CHANGE_PASSWORD_API = "/changepassword";
	
	private UserDetail userDetail;
	
	
	/**
	 * Initializes web service to update user profile.
	 * @param userDetail
	 */
	public UpdateUserProfileWebservice(UserDetail userDetail)
	{
		super(HttpUtility.BASE_SERVICE_URL + UPDATE_PROFILE_API);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.UPDATE_USER_PROFILE);
		
		this.userDetail = userDetail;
		
		if (userDetail.getUserFbToken() == null) {
			userDetail.setUserFbToken("");
		}
		
		if (userDetail.getUserFbTokenExpiry() == null) {
			userDetail.setUserFbTokenExpiry("");
		}
		
		if (userDetail.getUserInstagramToken() == null) {
			userDetail.setUserInstagramToken("");
		}
	}
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse updateProfleSynch()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userDetail.getUserId()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userDetail.getUserName()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, userDetail.getUserEmail()));
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.GENDER, String.valueOf(userDetail.getUserGender())));
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.ADDRESS, userDetail.getUserAddress()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.FB_ACCESS_TOKEN, userDetail.getUserFbToken()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.FB_EXPIRY_DATE, userDetail.getUserFbTokenExpiry()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.INSTAGRAM_ACCESS_TOKEN, userDetail.getUserInstagramToken()));
			
			if(userDetail.isRemovePicture()) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.REMOVE_PIC, "1"));
			}
			
			if(!TextUtils.isEmpty(userDetail.getImageUrl())) {
				setFile(UserDetail.PROFILE_PIC, userDetail.getImageUrl());
			}
			
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to check whether user name already exists or not. Response of this web
	 * service can be captured from web service listener.
	 */
	public void updateProfile() {
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userDetail.getUserId()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userDetail.getUserName()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, userDetail.getUserEmail()));
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.GENDER, String.valueOf(userDetail.getUserGender())));
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.ADDRESS, userDetail.getUserAddress()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.FB_ACCESS_TOKEN, userDetail.getUserFbToken()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.FB_EXPIRY_DATE, userDetail.getUserFbTokenExpiry()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.INSTAGRAM_ACCESS_TOKEN, userDetail.getUserInstagramToken()));
			
			if(userDetail.isRemovePicture()) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.REMOVE_PIC, "1"));
			}
			
			if(!TextUtils.isEmpty(userDetail.getImageUrl())) {
				setFile(UserDetail.PROFILE_PIC, userDetail.getImageUrl());
			}
			
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @param oldPassword old password of user.
	 * @param newPassword new password of user.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse updatePasswordSynch(String oldPassword, String newPassword)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + CHANGE_PASSWORD_API); 
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.CHANGE_PASSWORD);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userDetail.getUserId()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD, oldPassword));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.NEW_PASSWORD, newPassword));
			
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to change the user password using specified data. Response of this web
	 * service can be captured from web service listener.
	 * 
	 * @param oldPassword old password of user.
	 * @param newPassword new password of user.
	 */
	public void updatePassword(String oldPassword, String newPassword)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + CHANGE_PASSWORD_API); 
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.CHANGE_PASSWORD);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userDetail.getUserId()));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD, oldPassword));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.NEW_PASSWORD, newPassword));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
}
