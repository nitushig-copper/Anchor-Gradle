package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class PromotionsRedeemWebService extends WebserviceBase
{
  private static final String REDEEM_PROMOTION = "/promotions/redeem";
  
  private PromotionReedemWebServiceListener reedemWebServiceListener;
  
  private String promotionID = null;
  private String userID = null;

  
  public PromotionsRedeemWebService(String userID, String promotionID)
  {
    super(HttpUtility.BASE_SERVICE_URL +  REDEEM_PROMOTION);
    setRequestType(WebserviceBase.RequestType.GET);
    setWebserviceType(WebserviceType.ServiceType.REDEEM_PROMOTION);
    setWebserviceListener(mWebserviceListener);
    
    this.userID = userID;
    this.promotionID = promotionID;
  }
  
  
  public void setReedemWebserviceListener(PromotionReedemWebServiceListener listener)
	{
	  reedemWebServiceListener = listener;
	}
  
  
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(reedemWebServiceListener != null)
			{
				if(serviceType == ServiceType.REDEEM_PROMOTION)
				{
					reedemWebServiceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}

		@Override
		public void onServiceResult(ServiceType serviceType, String result) {
			
			if(reedemWebServiceListener != null)
			{
				if(serviceType == ServiceType.REDEEM_PROMOTION)
				{
					reedemWebServiceListener.onSuccess(result);
				}
			}
			
		}

		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage) {
			
			if(reedemWebServiceListener != null)
			{
				if(serviceType == ServiceType.REDEEM_PROMOTION)
				{
					reedemWebServiceListener.onFailed(errorCode, errorMessage);
				}
			}
			
		}

		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) {
			
			if(reedemWebServiceListener != null)
			{
				if(serviceType == ServiceType.REDEEM_PROMOTION)
				{
					reedemWebServiceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
			
		}
		
	};
	
	
	
  public void redeemPromotions()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("promotion_id", URLEncoder.encode(this.promotionID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	doRequestAsync();
      return;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public WebserviceResponse redeemSynchronousPromotions()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("promotion_id", URLEncoder.encode(this.promotionID, "UTF-8")));
      setPostData(nameValuePair);
      
    }
    catch (Exception localException)
    {
     
    }
    
    return doRequestSynch();
    
  }
}
