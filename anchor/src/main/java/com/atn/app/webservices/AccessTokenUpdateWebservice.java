package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class AccessTokenUpdateWebservice extends WebserviceBase
{

	private static final String UPDATE_ACCESS_TOKEN = "/accesstoken";
	
	private static final String FB_ACCESS_TOKEN = "fb_access_token";
	private static final String FB_TOKEN_EXPIRY = "fb_expiry_date";
	private static final String INS_ACCESS_TOKEN = "ins_access_token";
	
	private static final String USER_ID = "user_id";
	
	private AccessTokenUpdateWebserviceListener mAccessTokenWebserviceListener;
	
	private String userId;
	
	public AccessTokenUpdateWebservice(String userId)
	{
		super(HttpUtility.BASE_SERVICE_URL + UPDATE_ACCESS_TOKEN);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.UPDATE_ACCESS_TOKEN);
		setWebserviceListener(mWebserviceListener);
		
		this.userId = userId;
	}
	
	
	public void setUpdateAccessTokenWebserviceListener(AccessTokenUpdateWebserviceListener listener)
	{
		mAccessTokenWebserviceListener = listener;
	}
	
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mAccessTokenWebserviceListener != null)
			{
				if(serviceType == ServiceType.UPDATE_ACCESS_TOKEN)
				{
					mAccessTokenWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mAccessTokenWebserviceListener != null)
			{
				if(serviceType == ServiceType.UPDATE_ACCESS_TOKEN)
				{
					mAccessTokenWebserviceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mAccessTokenWebserviceListener != null)
			{
				if(serviceType == ServiceType.UPDATE_ACCESS_TOKEN)
				{
					mAccessTokenWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mAccessTokenWebserviceListener != null)
			{
				if(serviceType == ServiceType.UPDATE_ACCESS_TOKEN)
				{
					mAccessTokenWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @param fbAccessToken Facebook access token of the logged-in user
	 * @param fbExpiryDate Facebook access token expiry date (null for optional)
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse updateFacebookTokenSynchronously(String fbAccessToken, String fbExpiryDate)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(FB_ACCESS_TOKEN, URLEncoder.encode(fbAccessToken, "UTF-8")));
			
			if(fbExpiryDate != null)
			{
				nameValuePair.add(new BasicNameValuePair(FB_TOKEN_EXPIRY, URLEncoder.encode(fbExpiryDate, "UTF-8")));
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
	 * Asynchronously calls the web service to update the Facebook access token of the user. Response of this web service can
	 * be captured from web service listener.
	 * 
	 * @param fbAccessToken Facebook access token of the logged-in user
	 * @param fbExpiryDate Facebook access token expiry date (null for optional)
	 */
	public void updateFacebookToken(String fbAccessToken, String fbExpiryDate)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(FB_ACCESS_TOKEN, URLEncoder.encode(fbAccessToken, "UTF-8")));
			
			if(fbExpiryDate != null)
			{
				nameValuePair.add(new BasicNameValuePair(FB_TOKEN_EXPIRY, URLEncoder.encode(fbExpiryDate, "UTF-8")));
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
	 * @param insAccessToken Instagram access token of the logged-in user
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse updateInstagramTokenSynchronously(String insAccessToken)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(INS_ACCESS_TOKEN, URLEncoder.encode(insAccessToken, "UTF-8")));
			
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to update the Instagram access token of the user. Response of this web service can
	 * be captured from web service listener.
	 * 
	 * @param insAccessToken Instagram access token of the logged-in user
	 */
	public void updateInstagramToken(String insAccessToken)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(INS_ACCESS_TOKEN, URLEncoder.encode(insAccessToken, "UTF-8")));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
