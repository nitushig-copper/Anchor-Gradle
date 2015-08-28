package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class PromotionClaimWebService extends WebserviceBase
{
	
	public static final String CLAIM_PROMOTION = "/promotions/claim";
	
	private String userID = null;
	private String promotionID = null;
	private String claim_expire_time=null;
	PromotionClaimWebServiceListener mClaimPromotionWebServiceListener;
	
	public PromotionClaimWebService(String userID, String promotionID,String claim_expire_time) {
		super(HttpUtility.BASE_SERVICE_URL + CLAIM_PROMOTION);
		
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.CLAIM_PROMOTION);
		setWebserviceListener(mWebserviceListener);
		
		
		this.userID =  userID;
		this.promotionID = promotionID;
		this.claim_expire_time = claim_expire_time;
	}
	
	/**
	 * Sets listener to listen for the web service response from the server.
	 * @param listener
	 */
	public void setClaimPromotionWebServiceListener(PromotionClaimWebServiceListener listener)
	{
		mClaimPromotionWebServiceListener = listener;
	}
	
	
	/**
	 * Listens for the web service response from the server.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(mClaimPromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.CLAIM_PROMOTION)
				{
					mClaimPromotionWebServiceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mClaimPromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.CLAIM_PROMOTION)
				{
					mClaimPromotionWebServiceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mClaimPromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.CLAIM_PROMOTION)
				{
					mClaimPromotionWebServiceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) 
		{
			if(mClaimPromotionWebServiceListener != null)
			{
				if(serviceType == ServiceType.CLAIM_PROMOTION)
				{
					mClaimPromotionWebServiceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
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
	public WebserviceResponse claimPromotionSynchronously()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PROMOTION_ID, URLEncoder.encode(promotionID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.EXPIRE_TIME, claim_expire_time));
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to claim Promotion . Response of this
	 * web service can be captured from web service listener.
	 */
	
	public void claimPromotion()
	{
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PROMOTION_ID, URLEncoder.encode(promotionID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.EXPIRE_TIME, claim_expire_time));
			setPostData(nameValuePair);
			doRequestAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

}
