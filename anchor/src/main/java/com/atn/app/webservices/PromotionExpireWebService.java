package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class PromotionExpireWebService extends WebserviceBase{

	public static final String EXPIRE_PROMOTION = "/promotions/expire";
	
	
	private String userID = null;
	private String promotionID = null;
	
	PromotionExpireWebServiceListener mExpirePromotionWebServiceListener;
	
	public PromotionExpireWebService(String userID, String promotionID) {
		super(HttpUtility.BASE_SERVICE_URL + EXPIRE_PROMOTION);
		
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.EXPIRE_PROMOTION);
		setWebserviceListener(mWebserviceListener);
		
		this.userID =  userID;
		this.promotionID = promotionID;
		
		
	}

	/**
	 * Sets listener to listen for the web service response from the server.
	 * @param listener
	 */
	public void setExpirePromotionWebServiceListener(PromotionExpireWebServiceListener listener)
	{
		mExpirePromotionWebServiceListener = listener;
	}
	
	/**
	 * Listens for the web service response from the server.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(mExpirePromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.EXPIRE_PROMOTION)
				{
					mExpirePromotionWebServiceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mExpirePromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.EXPIRE_PROMOTION)
				{
					mExpirePromotionWebServiceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mExpirePromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.EXPIRE_PROMOTION)
				{
					mExpirePromotionWebServiceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mExpirePromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.EXPIRE_PROMOTION)
				{
					mExpirePromotionWebServiceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
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
	public WebserviceResponse expireSynchronousPromotions()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PROMOTION_ID, URLEncoder.encode(promotionID, "UTF-8")));
			
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to EXPIRE Promotion . Response of this
	 * web service can be captured from web service listener.
	 */
	
	public void expirePromotions()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PROMOTION_ID, URLEncoder.encode(promotionID, "UTF-8")));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}
