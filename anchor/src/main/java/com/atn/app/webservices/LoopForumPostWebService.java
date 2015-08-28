package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.LoopForumPostData;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class LoopForumPostWebService extends WebserviceBase
{
	private static final String FORUM_POST_API = "/forum";
	private LoopForumPostWebserviceListener forumPostWebserviceListener;
	private LoopForumPostData postData;
	
	public LoopForumPostWebService() {
		super(HttpUtility.BASE_SERVICE_URL + FORUM_POST_API);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.FORUM_POST);
		setWebserviceListener(mWebserviceListener);
	}
	
	
	public void setForumPostWebserviceListener(LoopForumPostWebserviceListener listener)
	{
		forumPostWebserviceListener = listener;
	}
	
	
	public void setForumPostData(LoopForumPostData data)
	{
		this.postData = data;
	}
	
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(forumPostWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_POST)
				{
					forumPostWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(forumPostWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_POST)
				{
					forumPostWebserviceListener.onSuccess(postData, result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(forumPostWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_POST)
				{
					forumPostWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(forumPostWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_POST)
				{
					forumPostWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
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
	public WebserviceResponse getSynchronousResponse()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.USER_ID, postData.getUserId()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.DATE, postData.getMessageDate()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.LAT, postData.getlatitude()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.LNG, postData.getlongitude()));
			
			if(postData.getMessage() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.USER_TEXT, postData.getMessage()));
			}
			
			if(postData.getImageUrl() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.IMAGE, postData.getImageUrl()));
				setFile(LoopForumPostData.IMAGE, postData.getImageUrl());
			}
			
			if(postData.getForumId() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.FORUM_ID, postData.getForumId()));
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
	 * Asynchronously calls the web service to get the registered ATN venues. Response of this web service can
	 * be captured from web service listener.
	 */
	public void getResponse()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.USER_ID, postData.getUserId()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.DATE, postData.getMessageDate()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.LAT, postData.getlatitude()));
			nameValuePair.add(new BasicNameValuePair(LoopForumPostData.LNG, postData.getlongitude()));
			
			if(postData.getMessage() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.USER_TEXT, postData.getMessage()));
			}
			
			if(postData.getImageUrl() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.IMAGE, postData.getImageUrl()));
				setFile(LoopForumPostData.IMAGE, postData.getImageUrl());
			}
			
			if(postData.getForumId() != null)
			{
				nameValuePair.add(new BasicNameValuePair(LoopForumPostData.FORUM_ID, postData.getForumId()));
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
