package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.AddDealsPostData;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class AddDealsWebService extends WebserviceBase{

	public static final String ADD_DEALS = "/promotions/accept";
	
	private AddDealsWebServiceListener dealsPostWebServiceListener;
	
	public AddDealsWebService()
	{
		super(HttpUtility.BASE_SERVICE_URL + ADD_DEALS);
		
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.ADD_DEALS);
		setWebserviceListener(mWebserviceListener);
	}
	
	public void setDealsPostWebserviceListener(AddDealsWebServiceListener listener)
	{
		dealsPostWebServiceListener = listener;
	}
	
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(dealsPostWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					dealsPostWebServiceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}

		@Override
		public void onServiceResult(ServiceType serviceType, String result) {
			
			if(dealsPostWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					dealsPostWebServiceListener.onSuccess(result);
				}
			}
			
		}

		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage) {
			
			if(dealsPostWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					dealsPostWebServiceListener.onFailed(errorCode, errorMessage);
				}
			}
			
		}

		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) {
			
			if(dealsPostWebServiceListener != null)
			{
				if(serviceType == ServiceType.ADD_DEALS)
				{
					dealsPostWebServiceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
			
		}
		
	};
	
	
	public void addMyDealsData(String promotionId)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			// hardCoded for Testing
			
			nameValuePair.add(new BasicNameValuePair(AddDealsPostData.USER_ID, UserDataPool.getInstance().getUserDetail().getUserId()));
			nameValuePair.add(new BasicNameValuePair(AddDealsPostData.PROMOTION_ID, promotionId));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	
}
