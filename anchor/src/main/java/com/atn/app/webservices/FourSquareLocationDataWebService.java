package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.AtnUtils;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a Foursquare web service that is used to fetch all the Foursquare location Data for currently selected ATN bar.
 * 
 *
 *
 */


public class FourSquareLocationDataWebService extends WebserviceBase{

	/**
	 * Foursquare client details that are used to fetch details from Foursquare server.
	 */
	private static final String FOURSQUARE_CATEGORY_ID = "4d4b7105d754a06376d81259";
	private static final String FOURSQUARE_CLIENT_ID = "101PXDN3IRTGUPEDII4OK2WY2CSCNFB5JIXOTS31S4QIAGWM";
	private static final String FOURSQUARE_CLIENT_SECRET = "TJYMLFS4IIWSBE52CKQWER3PO1Q0IDLRK4CJAUHM4XEGNOQ5";

	/**
	 * Foursquare api
	 */
	
	
	/**
	 * Local variable that are used to parse JSON data received from Foursquare server.
	 */
	private final String CLIENT_ID = "client_id";
	private final String CLIENT_SECRET = "client_secret";
	private final String VERSION = "v";
	
	private final static String FOURSQUARE_LOCATION_DATA = "https://api.foursquare.com/v2/venues/";
	static String extra = "?";
	private final String VERSION_VALUE = AtnUtils.getTwoDaysOldDate();//"20140212";
	
	private FourSquareLocationWebServiceListener mFoursquareWebServiceListener;
	
	
	public FourSquareLocationDataWebService(String fourSquareVenueId) {
		super(FOURSQUARE_LOCATION_DATA +fourSquareVenueId+extra);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.FOURSQUARE_LOCATION);
		
		setWebserviceListener(mWebserviceListener);
	}
	
	/**
	 * Registers Foursquare web service listener to listens from Foursquare web server responses.
	 * @param listener
	 */
	public void setFoursquareLocationWebserviceListener(FourSquareLocationWebServiceListener listener)
	{
		mFoursquareWebServiceListener = listener;
	}
	
	
	
	/**
	 * Listens for Foursquare server response. It returns an error message to the listener if it receives an 
	 * error message from server, otherwise it parses the server response that is received in JSON format and
	 * creates a FoursquareData list that is returned to the listener.
	 */
	WebserviceListener mWebserviceListener = new WebserviceListener()
	{
		
		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex)
		{
			if(mFoursquareWebServiceListener != null)
			{
				if(serviceType == ServiceType.FOURSQUARE_LOCATION)
				{
					mFoursquareWebServiceListener.onFailure(WebserviceError.URL_ERROR, ex.getMessage());
				}
			}
		}
		
		@Override
		public void onServiceResult(ServiceType serviceType, String result)
		{
			if(mFoursquareWebServiceListener != null)
			{
				if(serviceType == ServiceType.FOURSQUARE_LOCATION)
				{
					mFoursquareWebServiceListener.onSuccess(result);
				}
			}
		}
		
		@Override
		public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage)
		{
			if(mFoursquareWebServiceListener != null)
			{
				if(serviceType == ServiceType.FOURSQUARE_LOCATION)
				{
					mFoursquareWebServiceListener.onFailure(errorCode, errorMessage);
				}
			}
		}
		
		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex)
		{
			if(mFoursquareWebServiceListener != null)
			{
				if(serviceType == ServiceType.FOURSQUARE_LOCATION)
				{
					mFoursquareWebServiceListener.onFailure(WebserviceError.INTERNET_ERROR, ex.getMessage());
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
	
	public WebserviceResponse getSynchronousLocationId()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(CLIENT_ID, URLEncoder.encode(FOURSQUARE_CLIENT_ID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(CLIENT_SECRET, URLEncoder.encode(FOURSQUARE_CLIENT_SECRET, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(VERSION, URLEncoder.encode(VERSION_VALUE, "UTF-8")));
			setPostData(nameValuePair);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to get the Foursquare location details. Response of this web
	 * service can be captured from web service listener.
	 */
	public void getLocationId()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(CLIENT_ID, URLEncoder.encode(FOURSQUARE_CLIENT_ID, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(CLIENT_SECRET, URLEncoder.encode(FOURSQUARE_CLIENT_SECRET, "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(VERSION, URLEncoder.encode(VERSION_VALUE, "UTF-8")));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	

}
