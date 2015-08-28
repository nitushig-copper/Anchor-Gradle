package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class BusinessSubscribeWebservice extends WebserviceBase
{

	public static final String BUSINESS_SUBSCRIBE = "/businesses/subscribe";
	public static final String BUSINESS_UNSUBSCRIBE = "/businesses/unsubscribe";
	
	private static final String USER_ID = "user_id";
	private static final String BUSINESS_ID = "business_id";
	
	private BusinessSubscribeWebserviceListener mBusinessSubscribeWebserviceListener;
	
	private String userId;
	private String businessId = "ALL BUSINESSES";
	
	public BusinessSubscribeWebservice() 
	{
		super(BUSINESS_SUBSCRIBE);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.BUSINESS_SUBSCRIBE);
		setWebserviceListener(mWebserviceListener);
		
		this.userId = UserDataPool.getInstance().getUserDetail().getUserId();
	}
	
	
	public void setBusinessSubscribeWebserviceListener(BusinessSubscribeWebserviceListener listener)
	{
		mBusinessSubscribeWebserviceListener = listener;
	}

	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mBusinessSubscribeWebserviceListener != null)
			{
				mBusinessSubscribeWebserviceListener.onFailed(serviceType, WebserviceError.URL_ERROR, ex.getMessage());
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mBusinessSubscribeWebserviceListener != null)
			{
				mBusinessSubscribeWebserviceListener.onSuccess(serviceType, businessId);
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mBusinessSubscribeWebserviceListener != null)
			{
				mBusinessSubscribeWebserviceListener.onFailed(serviceType, errorCode, errorMessage);
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mBusinessSubscribeWebserviceListener != null)
			{
				mBusinessSubscribeWebserviceListener.onFailed(serviceType, WebserviceError.INTERNET_ERROR, ex.getMessage());
			}
		}
		
	};
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse subscribeBusinessSync(String businessId)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_SUBSCRIBE);
			setRequestType(RequestType.GET);
			setWebserviceType(ServiceType.BUSINESS_SUBSCRIBE);
			
			this.businessId = businessId;
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(BUSINESS_ID, URLEncoder.encode(businessId, "UTF-8")));

			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to subscribe ATN Business . Response of this web
	 * service can be captured from web service listener.
	 */
	public void subscribeBusiness(String businessId)
	{
		try
		{
			
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_SUBSCRIBE);
			setRequestType(RequestType.GET);
			setWebserviceType(ServiceType.BUSINESS_SUBSCRIBE);
			
			this.businessId = businessId;
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(BUSINESS_ID, URLEncoder.encode(businessId, "UTF-8")));

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
	 * @return web service response.
	 */
	public WebserviceResponse unsubscribeBusinessSync(String businessId)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_UNSUBSCRIBE);
			setRequestType(RequestType.GET);
			setWebserviceType(ServiceType.BUSINESS_UNSUBSCRIBE);
			
			this.businessId = businessId;
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(BUSINESS_ID, URLEncoder.encode(businessId, "UTF-8")));

			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to unsubscribe ATN Business . Response of this web
	 * service can be captured from web service listener.
	 */
	public void unsubscribeBusiness(String businessId)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_UNSUBSCRIBE);
			setRequestType(RequestType.GET);
			setWebserviceType(ServiceType.BUSINESS_UNSUBSCRIBE);
			
			this.businessId = businessId;
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(BUSINESS_ID, URLEncoder.encode(businessId, "UTF-8")));

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
	 * @return web service response.
	 */
	public WebserviceResponse subscribeAllBusinessSync()
	{
		try
		{
			
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_SUBSCRIBE);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.BUSINESS_SUBSCRIBE_ALL);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to subscribe to all ATN Business . Response of this web
	 * service can be captured from web service listener.
	 */
	public void subscribeAllBusiness()
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_SUBSCRIBE);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.BUSINESS_SUBSCRIBE_ALL);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));

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
	 * @return web service response.
	 */
	public WebserviceResponse unsubscribeAllBusinessSync()
	{
		try
		{
			
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_UNSUBSCRIBE);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.BUSINESS_UNSUBSCRIBE_ALL);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));

			setPostData(nameValuePair);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to unsubscribe ATN Business . Response of this web
	 * service can be captured from web service listener.
	 */
	public void unsubscribeAllBusiness()
	{
		try
		{
			
			setUrl(HttpUtility.BASE_SERVICE_URL + BUSINESS_UNSUBSCRIBE);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.BUSINESS_UNSUBSCRIBE_ALL);
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			nameValuePair.add(new BasicNameValuePair(USER_ID, URLEncoder.encode(userId, "UTF-8")));

			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
}
