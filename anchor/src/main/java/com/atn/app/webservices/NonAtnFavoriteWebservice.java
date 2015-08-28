package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.atn.app.datamodels.NonAtnVenueData;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;

public class NonAtnFavoriteWebservice extends WebserviceBase
{
	
	private static final String FAVORITE_API = "/favoritebar";
	private static final String UNFAVORITE_API = "/unfavoritebar";
	
	private NonAtnFavoriteWebserviceListener mFavoriteWebserviceListener;
	private NonAtnVenueData venueData;
	private ServiceType nonAtnFavServiceType;
	
	public NonAtnFavoriteWebservice()
	{
		super(HttpUtility.BASE_SERVICE_URL + FAVORITE_API);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.NON_ATN_FAVORITE);
		setWebserviceListener(mWebserviceListener);
		
		venueData = new NonAtnVenueData();
	}
	
	
	public void setNonAtnFavoriteWebserviceListener(NonAtnFavoriteWebserviceListener listener)
	{
		mFavoriteWebserviceListener = listener;
	}
	
	
	private WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) 
		{
			if(serviceType == ServiceType.NON_ATN_FAVORITE || serviceType == ServiceType.NON_ATN_UNFAVORITE)
			{
				if(mFavoriteWebserviceListener != null)
				{
					mFavoriteWebserviceListener.onFailed(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(serviceType == ServiceType.NON_ATN_FAVORITE || serviceType == ServiceType.NON_ATN_UNFAVORITE)
			{
				if(mFavoriteWebserviceListener != null)
				{
					mFavoriteWebserviceListener.onSuccess(nonAtnFavServiceType, getParsedData(result));
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(serviceType == ServiceType.NON_ATN_FAVORITE || serviceType == ServiceType.NON_ATN_UNFAVORITE)
			{
				if(mFavoriteWebserviceListener != null)
				{
					mFavoriteWebserviceListener.onFailed(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(serviceType == ServiceType.NON_ATN_FAVORITE || serviceType == ServiceType.NON_ATN_UNFAVORITE)
			{
				if(mFavoriteWebserviceListener != null)
				{
					mFavoriteWebserviceListener.onFailed(WebserviceError.INTERNET_ERROR, ex.getMessage());
				}
			}
		}
	};
	
	
	
	/**
	 * Returns non-registerd venue details with venue id that is favorited. These details is used to store venue
	 * details in database and same details will be used to unfavorite venue or delete venue from database.
	 * 
	 * @param response to parse.
	 * @return NonAtnVenueData with venue id.
	 */
	private NonAtnVenueData getParsedData(String response)
	{
		try
		{
			JSONObject dataObject = new JSONObject(response);
			
			if(!dataObject.isNull(NonAtnVenueData.VENUE_ID))
			{
				venueData.setVenueId(dataObject.getString(NonAtnVenueData.VENUE_ID));
			}
			
			return venueData;
		}
		catch (JSONException e) 
		{
			return null;
		}
	}
	
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse setFavoriteVenueSync(NonAtnVenueData venueData)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + FAVORITE_API);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.NON_ATN_FAVORITE);
			nonAtnFavServiceType = ServiceType.NON_ATN_FAVORITE;
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.USER_ID, venueData.getUserId()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_NAME, venueData.getVenueName()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_LAT, venueData.getVenueLat()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_LNG, venueData.getVenueLng()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_ADDRESS, venueData.getVenueAddress()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.FS_VENUE_ID, venueData.getVenueFsId()));
			
			if(venueData.getVenueDescription() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_DESCRIPTION, venueData.getVenueDescription()));
			
			if(venueData.getVenueFsLink() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.FS_VENUE_LINK, venueData.getVenueFsLink()));

			if(venueData.getInstaLocId() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.INSTA_LOCATION_ID, venueData.getInstaLocId()));
			
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
	public void setFavoriteVenue(NonAtnVenueData venueData)
	{
		try
		{
			setUrl(HttpUtility.BASE_SERVICE_URL + FAVORITE_API);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.NON_ATN_FAVORITE);
			
			nonAtnFavServiceType = ServiceType.NON_ATN_FAVORITE;

			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.USER_ID, venueData.getUserId()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_NAME, venueData.getVenueName()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_LAT, venueData.getVenueLat()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_LNG, venueData.getVenueLng()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_ADDRESS, venueData.getVenueAddress()));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.FS_VENUE_ID, venueData.getVenueFsId()));
			
			if(venueData.getVenueDescription() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_DESCRIPTION, venueData.getVenueDescription()));
			
			if(venueData.getVenueFsLink() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.FS_VENUE_LINK, venueData.getVenueFsLink()));

			if(venueData.getInstaLocId() != null)
				nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.INSTA_LOCATION_ID, venueData.getInstaLocId()));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse setUnFavoriteVenueSync(String userId, String venueId)
	{
		try
		{
			venueData.setVenueId(venueId);
			
			setUrl(HttpUtility.BASE_SERVICE_URL + UNFAVORITE_API);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.NON_ATN_UNFAVORITE);
			
			nonAtnFavServiceType = ServiceType.NON_ATN_UNFAVORITE;
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.USER_ID, userId));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_ID, venueId));
			
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
	public void setUnFavoriteVenue(String userId, String venueId)
	{
		try {
			venueData.setVenueId(venueId);

			setUrl(HttpUtility.BASE_SERVICE_URL + UNFAVORITE_API);
			setRequestType(RequestType.POST);
			setWebserviceType(ServiceType.NON_ATN_UNFAVORITE);

			nonAtnFavServiceType = ServiceType.NON_ATN_UNFAVORITE;

			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.USER_ID,userId));
			nameValuePair.add(new BasicNameValuePair(NonAtnVenueData.VENUE_ID,venueId));

			setPostData(nameValuePair);
			doRequestAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
