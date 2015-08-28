
package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class BusinessRemoveFavoritesWebService   extends WebserviceBase
{
  public static final String BUSINESS_REMOVE_FAVORITES = "/businesses/unfavorite";
  
  private String businessID = null;
  private String userID = null;
  private BusinessFavoriteWebServiceListener mFavoriteBusinessWebserviceListener;
  
  
  
  public BusinessRemoveFavoritesWebService(String userID, String businessID)
  {
    super(HttpUtility.BASE_SERVICE_URL + BUSINESS_REMOVE_FAVORITES);
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
  public void setBusinessUnFavoriteWebServiceListener(BusinessFavoriteWebServiceListener paramBusinessFavoriteWebServiceListener)
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
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
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
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onSuccess(ServiceType.BUSINESS_UNFAVORITES, businessID, result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mFavoriteBusinessWebserviceListener != null)
			{
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
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
				if(serviceType == ServiceType.BUSINESS_UNFAVORITES)
				{
					mFavoriteBusinessWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
  
 
	public void removeBusinessFavorite() {
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID,URLEncoder.encode(this.userID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.BUSINESS_ID,URLEncoder.encode(this.businessID, "UTF-8")));

			setPostData(nameValuePair);
			doRequestAsync();
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

  public WebserviceResponse removeBusinessSynchronousFavorite()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair(UserDetail.BUSINESS_ID, URLEncoder.encode(this.businessID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	
    }
    catch (Exception localException)
    {
      
    }
    
    return doRequestSynch();
    
  }
  
 
}
