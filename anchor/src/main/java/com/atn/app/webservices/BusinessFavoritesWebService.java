package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class BusinessFavoritesWebService extends WebserviceBase
{
  public static final String BUSINESS_FAVORITES = "/businesses/favorite";
  
  private String businessID = null;
  private String userID = null;
  private BusinessFavoriteWebServiceListener mFavoriteBusinessWebserviceListener;
  
  
  
  public BusinessFavoritesWebService(String userID, String businessID) {
    super(HttpUtility.BASE_SERVICE_URL +BUSINESS_FAVORITES);
    setRequestType(WebserviceBase.RequestType.GET);
    setWebserviceType(WebserviceType.ServiceType.BUSINESS_FAVORITES);
    setWebserviceListener(this.mWebserviceListener);
    
    this.userID = userID;
    this.businessID = businessID;
  }
  
  /**
	 * Sets listener to listen for the web service response from the server.
	 * @param listener
	 */
  public void setBusinessFavoriteWebServiceListener(BusinessFavoriteWebServiceListener paramBusinessFavoriteWebServiceListener)
  {
    this.mFavoriteBusinessWebserviceListener = paramBusinessFavoriteWebServiceListener;
  }
  
  
	/**
	 * Listens for the web service response from the server.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(mFavoriteBusinessWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_FAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mFavoriteBusinessWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_FAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onSuccess(ServiceType.BUSINESS_FAVORITES, businessID, result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mFavoriteBusinessWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_FAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mFavoriteBusinessWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_FAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
  
 
  public void addBusinessFavorite()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, this.userID));
    	nameValuePair.add(new BasicNameValuePair(UserDetail.BUSINESS_ID, this.businessID));
    	
    	setPostData(nameValuePair);
    	doRequestAsync();
      return;
    }
    catch (Exception localException)
    {
    }
  }
  
  public WebserviceResponse addBusinessSynchronousFavorite()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, this.userID));
    	nameValuePair.add(new BasicNameValuePair(UserDetail.BUSINESS_ID, this.businessID));
    	
    	setPostData(nameValuePair);
    	
    }
    catch (Exception localException)
    {
      
    }
    
    return doRequestSynch();
    
  }
  
 
}
