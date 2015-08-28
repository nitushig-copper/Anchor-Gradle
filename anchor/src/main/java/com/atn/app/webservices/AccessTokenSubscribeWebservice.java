package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class AccessTokenSubscribeWebservice extends WebserviceBase
{
	private static final String SUBSCRIBE_API = "/apns/subscribe";
	private static final String UNSUBSCRIBE_API = "/apns/unsubscribe";
	
	private final String USER_ID = "user_id";
	private final String DEVICE_TOKEN = "apns_token";
	
	private AccessTokenSubscribeWebserviceListener mAccessTokenSubscribeWebserviceListener;
	
	private ServiceType mServiceType;
	private String userId;
	private String deviceToken;
	
	/**
	 * Initialize web service.
	 */
	public AccessTokenSubscribeWebservice(String userId, String token)
	{
		super(HttpUtility.BASE_SERVICE_URL + SUBSCRIBE_API);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.SUBSCRIBE_ACCESS_TOKEN);
		setWebserviceListener(mWebserviceListener);
		
		this.userId = userId;
		this.deviceToken = token;
	}
	
	
	/**
	 * Set service type whether it is subscribe or unsubscribe web service.
	 * 
	 * @param type web service type.
	 */
	public void setServiceType(ServiceType type)
	{
		mServiceType = type;
	}
	
	
	/**
	 * Registers web service listener to listen for web service responses.
	 * 
	 * @param listener to register.
	 */
	public void setAccessTokenSubscribeWebserviceListener(AccessTokenSubscribeWebserviceListener listener)
	{
		mAccessTokenSubscribeWebserviceListener = listener;
	}
	

	/**
	 * Listens for web service response and send the response to the 
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mAccessTokenSubscribeWebserviceListener != null)
			{
				if(mServiceType == serviceType)
				{
					mAccessTokenSubscribeWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mAccessTokenSubscribeWebserviceListener != null)
			{
				if(mServiceType == serviceType)
				{
					mAccessTokenSubscribeWebserviceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mAccessTokenSubscribeWebserviceListener != null)
			{
				if(mServiceType == serviceType)
				{
					mAccessTokenSubscribeWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mAccessTokenSubscribeWebserviceListener != null)
			{
				if(mServiceType == serviceType)
				{
					mAccessTokenSubscribeWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
		
	};
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse doProcessSync()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(DEVICE_TOKEN, URLEncoder.encode(deviceToken, "UTF-8")));
			
			if(mServiceType == ServiceType.SUBSCRIBE_ACCESS_TOKEN)
			{
				setUrl(HttpUtility.BASE_SERVICE_URL + SUBSCRIBE_API);
			}
			else if(mServiceType == ServiceType.UNSUBSCRIBE_ACCESS_TOKEN)
			{
				setUrl(HttpUtility.BASE_SERVICE_URL + UNSUBSCRIBE_API);
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
	 * Asynchronously calls the web service to subscribe/unsubscribe device token. Response of this web service can
	 * be captured from web service listener.
	 */
	public void doProcess()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(DEVICE_TOKEN, URLEncoder.encode(deviceToken, "UTF-8")));
			
			if(mServiceType == ServiceType.SUBSCRIBE_ACCESS_TOKEN)
			{
				setUrl(HttpUtility.BASE_SERVICE_URL + SUBSCRIBE_API);
			}
			else if(mServiceType == ServiceType.UNSUBSCRIBE_ACCESS_TOKEN)
			{
				setUrl(HttpUtility.BASE_SERVICE_URL + UNSUBSCRIBE_API);
			}
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
}
