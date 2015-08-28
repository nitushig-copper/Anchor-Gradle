package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atn.app.datamodels.ForumData;
import com.atn.app.datamodels.ForumData.ForumDataType;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class LoopForumWebservice extends WebserviceBase {

	private static final String FORUM_API = "/forum/list";
	private final String LAT = "lat";
	private final String LNG = "lon";
	
	private String lat, lng;
	
	private LoopForumWebserviceListener mForumWebserviceListener;
	
	/**
	 * Initialize web service
	 * 
	 * @param lat latitude
	 * @param lng longitude
	 */
	public LoopForumWebservice(String lat, String lng)
	{
		super(HttpUtility.BASE_SERVICE_URL + FORUM_API);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.FORUM_LIST);
		setWebserviceListener(mWebserviceListener);
		this.lat = lat;
		this.lng = lng;
	}
	
	
	public void setLoopForumWebServiceListener(
			LoopForumWebserviceListener listener) {
		mForumWebserviceListener = listener;
	}
	
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mForumWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_LIST)
				{
					mForumWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mForumWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_LIST)
				{
					mForumWebserviceListener.onSuccess(getForumDataList(result));
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mForumWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_LIST)
				{
					mForumWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mForumWebserviceListener != null)
			{
				if(serviceType == ServiceType.FORUM_LIST)
				{
					mForumWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
	
	
	private ArrayList<ForumData> getForumDataList(String response) {
		ArrayList<ForumData> forumDataList = new ArrayList<ForumData>();
		
		ForumData forumData = null;
		
		try
		{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject forumObject = jsonObject.getJSONObject(ForumData.FORUM_LIST); 
			JSONArray dataArray = forumObject.getJSONArray(ForumData.FORUM_DATA);
			
			for(int i=0; i<dataArray.length(); i++)
			{
				JSONObject dataObject = dataArray.getJSONObject(i);
				
				forumData = new ForumData();
				
				if(!dataObject.isNull(ForumData.FORUM_TITLE))
				{
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_ID))
					{
						forumData.setForumId(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_ID));
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_MESSAGE))
					{
						forumData.setUserMessage(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_MESSAGE));
						
						forumData.setForumDataType(ForumDataType.FORUM_LIST);
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.IMAGE))
					{
						forumData.setImageUrl(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.IMAGE));
						
						forumData.setForumDataType(ForumDataType.FORUM_ATTACHMENT);
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_CREATION_TIME))
					{
						String creationDate = dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_CREATION_TIME);
						
						String timeBefore = AtnUtils.getDaysDifference(creationDate);
						
						forumData.setMessageTime(timeBefore);
					}
				}
				
				
				if (!dataObject.isNull(ForumData.FORUM_USER)) {
					if (!dataObject.getJSONObject(ForumData.FORUM_USER).isNull(ForumData.FORUM_USER_NAME)) {
						forumData.setUserName(dataObject.getJSONObject(ForumData.FORUM_USER).getString(ForumData.FORUM_USER_NAME));
					}
				}
				
				if (!dataObject.isNull(ForumData.FORUM_MESSAGE_COUNT)) {
					forumData.setMessageCount(dataObject.getString(ForumData.FORUM_MESSAGE_COUNT));
				}
				
				JSONArray childArray = dataObject.getJSONArray(ForumData.FORUM_CHILD);
				forumData.setBulkForumData(getForumData(childArray));
				forumDataList.add(forumData);
				ForumData separatorData = new ForumData();
				separatorData.setForumDataType(ForumDataType.FORUM_SEPARATOR);
				separatorData.setMessageCount(forumData.getMessageCount());
				separatorData.setMessageTime(forumData.getMessageTime());
				forumDataList.add(separatorData);
			}
		}
		catch(JSONException ex)
		{
			ex.printStackTrace();
		}
		
		return forumDataList;
	}
	
	
	private ArrayList<ForumData> getForumData(JSONArray dataArray)
	{
		ArrayList<ForumData> forumDataList = new ArrayList<ForumData>();
		
		ForumData forumData = null;
		
		for(int i=0; i<dataArray.length(); i++) {
			try
			{
				JSONObject dataObject = dataArray.getJSONObject(i);
				forumData = new ForumData();
				if(!dataObject.isNull(ForumData.FORUM_TITLE)) {
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_ID))
					{
						forumData.setForumId(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_ID));
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_MESSAGE))
					{
						forumData.setUserMessage(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_MESSAGE));
						
						forumData.setForumDataType(ForumDataType.FORUM_DATA);
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.IMAGE))
					{
						forumData.setImageUrl(dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.IMAGE));
						
						forumData.setForumDataType(ForumDataType.FORUM_ATTACHMENT);
					}
					
					if(!dataObject.getJSONObject(ForumData.FORUM_TITLE).isNull(ForumData.FORUM_CREATION_TIME))
					{
						String creationDate = dataObject.getJSONObject(ForumData.FORUM_TITLE).getString(ForumData.FORUM_CREATION_TIME);
						
						String timeBefore = AtnUtils.getDaysDifference(creationDate);
						
						forumData.setMessageTime(timeBefore);
					}
				}
				
				if(!dataObject.isNull(ForumData.FORUM_USER))
				{
					if(!dataObject.getJSONObject(ForumData.FORUM_USER).isNull(ForumData.FORUM_USER_NAME))
					{
						forumData.setUserName(dataObject.getJSONObject(ForumData.FORUM_USER).getString(ForumData.FORUM_USER_NAME));
					}
				}
				
				if(!dataArray.getJSONObject(i).isNull(ForumData.FORUM_MESSAGE_COUNT))
				{
					forumData.setMessageCount(dataArray.getJSONObject(i).getString(ForumData.FORUM_MESSAGE_COUNT));
				}
				
				forumDataList.add(forumData);
			}
			catch(JSONException ex)
			{
				ex.printStackTrace();
			}
		}
		
		return forumDataList;
	}
	
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse getSynchronousForumList(int from) {
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(LAT, URLEncoder.encode(lat, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(LNG, URLEncoder.encode(lng, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair("from", URLEncoder.encode(String.valueOf(from), "UTF-8")));

			setPostData(nameValuePair);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to get the registered ATN venues. Response of this web service can
	 * be captured from web service listener.
	 */
	public void getForumList()
	{
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(LAT, URLEncoder.encode(lat, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(LNG, URLEncoder.encode(lng, "UTF-8")));

			setPostData(nameValuePair);
			doRequestAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
