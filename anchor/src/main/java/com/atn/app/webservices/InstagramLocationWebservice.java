package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atn.app.datamodels.FoursquareData;
import com.atn.app.datamodels.InstagramLocation;
import com.atn.app.instagram.InstagramAppData;
import com.atn.app.pool.UserDataPool;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to fetch Instagram venue id details using specifed Foursquare location ids. This returns
 * Instagram location details for the successful web service call, otherwise returns an error message to the
 * InstagramLocationServiceListener. You should register InstagramLocationServiceListener first to receive
 * this web service responses.
 * 
 * @author gagan
 *
 */
public class InstagramLocationWebservice extends WebserviceBase
{
	
	//Instagram location api.
	private final static String INSTAGRAM_LOCATION_API = "https://api.instagram.com/v1/locations/search?";
	
	//Local variables for the web service.
	private final String FORSQUARE_ID = "foursquare_v2_id";
	private final String ACCESS_TOKEN = "access_token";
	private final String DISTANCE = "distance";
	
	private final String DISTANCE_VALUE = "500";
	
	//To hold the specified Foursquare data.
	private FoursquareData foursquareData;
	
	private InstagramLocationWebserviceListener mInstagramLocationWebserviceListener;
	
	
	/**
	 * Initializes the web service using specified collection of Foursquare data and registers a web service
	 * listener to listen for web service response/error notifications.
	 * 
	 * @param foursquareData Foursquare data recevied from the FoursquareWebservice response.
	 */
	public InstagramLocationWebservice(FoursquareData foursquareData)
	{
		super(INSTAGRAM_LOCATION_API);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.INSTAGRAM_LOCATION);
		setWebserviceListener(mWebserviceListener);
		
		this.foursquareData = foursquareData;
	}
	
	
	/**
	 * Registers Instagram location web service listener to listen for success/failure notifications from
	 * the server.
	 * 
	 * @param listener InstagramLocationWebserviceListener to register.
	 */
	public void setInstagramLocationWebserviceListener(InstagramLocationWebserviceListener listener)
	{
		mInstagramLocationWebserviceListener = listener;
	}
	
	
	/**
	 * Listens for the web service response whether the web service call is successful or not. The response/error
	 * of the web service call is parsed and returned to the InstagramLocationWebserviceListener.
	 * The success response is parsed from JSON to InstagramLocation and returned to the listener. 
	 */
	WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			
			if(mInstagramLocationWebserviceListener != null)
			{
				if(serviceType == ServiceType.INSTAGRAM_LOCATION)
				{
					mInstagramLocationWebserviceListener.onFailure(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
			
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mInstagramLocationWebserviceListener != null)
			{
				if(serviceType == ServiceType.INSTAGRAM_LOCATION)
				{
					mInstagramLocationWebserviceListener.onSuccess(getInstagramLocationDetail(result));
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mInstagramLocationWebserviceListener != null)
			{
				if(serviceType == ServiceType.INSTAGRAM_LOCATION)
				{
					mInstagramLocationWebserviceListener.onFailure(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mInstagramLocationWebserviceListener != null)
			{
				if(serviceType == ServiceType.INSTAGRAM_LOCATION)
				{
					mInstagramLocationWebserviceListener.onFailure(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
		
	};
	
	
	/**
	 * Returns the parsed InstagramLocation details from the server response that is received in JSON format.
	 * 
	 * @param response JSON response from server.
	 * @return InstagramLocation.
	 */
	public InstagramLocation getInstagramLocationDetail(String response)
	{
		InstagramLocation instagramLocation = null;
		
		try
		{
			JSONArray jsonArray = new JSONArray(response);
			JSONObject dataObject = null;
			
			if(jsonArray.length() > 0)
			{
				dataObject = (JSONObject) jsonArray.get(0);
			}
			
			if(dataObject == null)
			{
				return null;
			}
			
			instagramLocation = new InstagramLocation();
			
			if(!dataObject.isNull(InstagramLocation.VENUE_ID))
			{
				instagramLocation.setInstagramVenueId(dataObject.getString(InstagramLocation.VENUE_ID));
			}
			
			if(!dataObject.isNull(InstagramLocation.VENUE_NAME))
			{
				instagramLocation.setInstagramVenueName(dataObject.getString(InstagramLocation.VENUE_NAME));
			}
			
			if(!dataObject.isNull(InstagramLocation.LOCATION_LAT))
			{
				instagramLocation.setLat(dataObject.getString(InstagramLocation.LOCATION_LAT));
			}
			
			if(!dataObject.isNull(InstagramLocation.LOCATION_LNG))
			{
				instagramLocation.setLng(dataObject.getString(InstagramLocation.LOCATION_LNG));
			}
			
			instagramLocation.setFoursquareData(foursquareData);
			
			return instagramLocation;
			
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse getSynchronousLocationId()
	{
		
		if(foursquareData.getLocationId() != null)
			{
				
				setUrl(INSTAGRAM_LOCATION_API);
				
				try
				{
					ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair(FORSQUARE_ID, foursquareData.getLocationId()));
					
					if(UserDataPool.getInstance().isUserLoggedIn() && UserDataPool.getInstance().getUserDetail().getUserInstagramToken() != null && UserDataPool.getInstance().getUserDetail().getUserInstagramToken().trim().length() > 0)
					{
						nameValuePair.add(new BasicNameValuePair(ACCESS_TOKEN, UserDataPool.getInstance().getUserDetail().getUserInstagramToken().trim()));
					}
					else
					{
						nameValuePair.add(new BasicNameValuePair(InstagramAppData.ID, InstagramAppData.CLIENT_ID));	
					}
					nameValuePair.add(new BasicNameValuePair(DISTANCE, DISTANCE_VALUE));
					
					setPostData(nameValuePair);

				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			
			
		return doRequestSynch();
		
	}
	
	
	/**
	 * Asynchronously calls the web service to get the Instagram location details. Response of this web
	 * service can be captured from web service listener.
	 */
	public void getLocationId()
	{
		if(foursquareData.getLocationId() != null)
			{
				setUrl(INSTAGRAM_LOCATION_API);
				
				try
				{
					ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair(FORSQUARE_ID, foursquareData.getLocationId()));
					
					if(UserDataPool.getInstance().isUserLoggedIn() && UserDataPool.getInstance().getUserDetail().getUserInstagramToken() != null && UserDataPool.getInstance().getUserDetail().getUserInstagramToken().trim().length() > 0)
					{
						nameValuePair.add(new BasicNameValuePair(ACCESS_TOKEN, UserDataPool.getInstance().getUserDetail().getUserInstagramToken().trim()));
					}
					else
					{
						nameValuePair.add(new BasicNameValuePair(InstagramAppData.ID, InstagramAppData.CLIENT_ID));	
					}
					nameValuePair.add(new BasicNameValuePair(DISTANCE, DISTANCE_VALUE));
					
					setPostData(nameValuePair);
					doRequestAsync();

				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
	}

}
