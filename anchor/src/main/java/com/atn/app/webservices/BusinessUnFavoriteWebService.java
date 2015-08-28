package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class BusinessUnFavoriteWebService
  extends WebserviceBase
{
  private static final String BUSINESS_UNFAVORITE = "/businesses/unfavorite";
  private String businessID = null;
  private String userID = null;
  
  BusinessUnFavoriteWebServiceListener mBusinessUnFavoriteWebserviceListener;
  
  public BusinessUnFavoriteWebService(String userID, String businessID)
  {
    super(HttpUtility.BASE_SERVICE_URL +BUSINESS_UNFAVORITE );
    setRequestType(WebserviceBase.RequestType.GET);
    setWebserviceType(WebserviceType.ServiceType.BUSINESS_UNFAVORITES);
    setWebserviceListener(this.mWebserviceListener);
    
    this.userID = userID;
    this.businessID = businessID;
  }
  
  /**
	 * Sets listener to listen for the web service response from the server.
	 * @param listener
	 */
  
  public void setBusinessUnFavoriteWebServiceListener(BusinessUnFavoriteWebServiceListener listener)
  {
    this.mBusinessUnFavoriteWebserviceListener = listener;
  }
  
  
	/**
	 * Listens for the web service response from the server.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(mBusinessUnFavoriteWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mBusinessUnFavoriteWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mBusinessUnFavoriteWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mBusinessUnFavoriteWebserviceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mBusinessUnFavoriteWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mBusinessUnFavoriteWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mBusinessUnFavoriteWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mBusinessUnFavoriteWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
  
  public void addToUnFavoriteBusiness()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("business_id", URLEncoder.encode(this.businessID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	doRequestAsync();
      
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public WebserviceResponse addToUnFavoriteSynchronousBusiness()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("business_id", URLEncoder.encode(this.businessID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	
    }
    catch (Exception localException)
    {
      
    }
    return doRequestSynch();
  }
  
  
}
