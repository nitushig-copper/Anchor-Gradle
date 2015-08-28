package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates web service to share specified registered ATN business id on Facebook.
 * 
 * @author gagan
 *
 */
public class AtnVenueShareWebservice extends WebserviceBase
{
	private static final String SHARE_API = "/businesses/share"; 
	private static final String USER_ID = "user_id";
	private static final String BUSINESS_ID = "business_id";
	
	private AtnVenueShareWebserviceListener mAtnVenueShareWebserviceListener;
	private String userId, businessId;
	
	
	/**
	 * Initialize web service.
	 * 
	 * @param userId of the current logged-in user.
	 * @param businessId to share on Facebook.
	 */
	public AtnVenueShareWebservice(String userId, String businessId) {
		super(HttpUtility.BASE_SERVICE_URL + SHARE_API);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.SHARE_ATN_BARS);
		setWebserviceListener(mWebserviceListener);
		this.userId = userId;
		this.businessId = businessId;
	}
	
	
	/**
	 * Sets listener to listen for the web service response from the server.
	 * @param listener
	 */
	public void setAtnVenueShareWebServiceListener(AtnVenueShareWebserviceListener listener)
	{
		mAtnVenueShareWebserviceListener = listener;
	}
	
	
	/**
	 * Listens for the web service response from the server.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(mAtnVenueShareWebserviceListener != null)
			{
				if(serviceType == ServiceType.SHARE_ATN_BARS)
				{
					mAtnVenueShareWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mAtnVenueShareWebserviceListener != null)
			{
				if(serviceType == ServiceType.SHARE_ATN_BARS)
				{
					mAtnVenueShareWebserviceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mAtnVenueShareWebserviceListener != null)
			{
				if(serviceType == ServiceType.SHARE_ATN_BARS)
				{
					mAtnVenueShareWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mAtnVenueShareWebserviceListener != null)
			{
				if(serviceType == ServiceType.SHARE_ATN_BARS)
				{
					mAtnVenueShareWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
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
	public WebserviceResponse shareSynchronousVenue()
	{
		try
		{
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
	 * Asynchronously calls the web service to share the registered ATN venues. Response of this web service can
	 * be captured from web service listener.
	 */
	public void shareVenue()
	{
		try
		{
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
	
	
}
