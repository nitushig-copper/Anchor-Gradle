package com.atn.app.webservices;

import org.json.JSONObject;

import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates web service response that holds the response of a web service call. It checks for the error/response
 * recevied from the web server, parse the response and returns the response as response data or error message
 * to web service listener.
 * 
 * @author gagan
 *
 */
public class WebserviceResponse 
{
	private final String CODE = "code";
	private final String META = "meta";
	private final String RESPONSE = "response";
	private final String ERROR_DETAIL = "errorDetail";
	private final String MESSAGE = "message";
	private final String DATA = "data";
	private final String ERROR_MESSAGE = "error_message";
	
	private final String BUSINESS_TAG = "Businesses";

	private final String MY_DEALS_TAG = "Promotions";
	
	private final String VENUE_TAG = "venue";
	
	private final String CANONICAL_URL_TAG = "canonicalUrl";
	
	private String message;
	private boolean isSuccess;
	private String responseData;
	private boolean isInternetAvaibale = true;
	
	/**
	 * Result code description
	 * Code:"4" = Error
	 * Code:"0" = Success
	 */
	private int resultCode = 4;
	
	public WebserviceResponse(ServiceType serviceType, String jsonResponse)
	{	
		try
		{	
			JSONObject jobj  = new JSONObject(jsonResponse);			
			
			
			switch(serviceType)
			{
			
				case FOURSQUARE:
					resultCode = jobj.getJSONObject(META).getInt(CODE);
					
					if(resultCode == 200) //Success
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).toString();
					}
					else
					{
						isSuccess = false;
						message = jobj.getJSONObject(META).getString(ERROR_DETAIL).toString();
					}
					
					break;
						
			
				case INSTAGRAM_LOCATION:
					
					resultCode = jobj.getJSONObject(META).getInt(CODE);
					
					if(resultCode == 200) //Success
					{
						isSuccess = true;
						responseData = jobj.getJSONArray(DATA).toString();
					}
					else
					{
						isSuccess = false;
						message = jobj.getJSONObject(META).getString(ERROR_MESSAGE).toString();
					}
					break;
					
				
				case INSTAGRAM_MEDIA:
					
					resultCode = jobj.getJSONObject(META).getInt(CODE);
					
					if(resultCode == 200) //Success
					{
						isSuccess = true;
						responseData = jobj.getJSONArray(DATA).toString();
					}
					else
					{
						isSuccess = false;
						message = jobj.getJSONObject(META).getString(ERROR_MESSAGE).toString();
					}
					break;
				
					
				case ATN_REGISTERED_BARS:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).getJSONArray(BUSINESS_TAG).toString();
					}
					
					break;
					
					
				case CONTACT_ATN:
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = "";
						JSONObject jsonOBj = jobj.getJSONObject(RESPONSE).getJSONObject("errors");
						if(!jsonOBj.isNull("full_name")){
							message = jsonOBj.getString("full_name").toString()+"\n ";
						}
						if(!jsonOBj.isNull("email")){
							message = message+jsonOBj.getString("email").toString()+"\n ";
						}
						if(!jsonOBj.isNull("message")){
							message = message+jsonOBj.getString("message").toString();
						}
					}
					else
					{
						isSuccess = true;
						//responseData = jobj.getJSONObject(RESPONSE).toString();
					}
					
					break;
					
				case ADD_DEALS:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).getString(MESSAGE);
					}
					
					break;
					
				case BUSINESS_SUBSCRIBE:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
					
				case BUSINESS_SUBSCRIBE_ALL:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
					
				case BUSINESS_UNSUBSCRIBE:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
					
				case BUSINESS_UNSUBSCRIBE_ALL:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
				
					
				case BUSINESS_FAVORITES:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
					
				case BUSINESS_UNFAVORITES:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
					
				case SHARE_ATN_BARS:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
					}
					
					break;
					
				case FOURSQUARE_LOCATION:
					
					resultCode = jobj.getJSONObject(META).getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).getJSONObject(VENUE_TAG).getString(CANONICAL_URL_TAG);
					}
					break;
				case MY_DEAL:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).getString("Promotions");
					}
					break;
				case REDEEM_PROMOTION:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					
					break;
					
				case CLAIM_PROMOTION:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //UnSuccessful
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					break;
					
				case EXPIRE_PROMOTION:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //UnSuccessful
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else // successful
					{
						isSuccess = true;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					break;
					
					
				default:
					
					resultCode = jobj.getInt(CODE);
					
					if(resultCode == 4) //Error
					{
						isSuccess = false;
						message = jobj.getJSONObject(RESPONSE).getString(MESSAGE).toString();
					}
					else
					{
						isSuccess = true;
						responseData = jobj.getJSONObject(RESPONSE).toString();
					}
					
					break;
			
			}
		}
		catch(Exception ex)
		{
			isSuccess = false;
			message = ex.getMessage();
			ex.printStackTrace();
		}
	}

	public WebserviceResponse()
	{
	}

	public String getErrorMessage() 
	{
		return message;
	}

	public boolean isSuccess() 
	{
		return isSuccess;
	}

	public String getResponseData() 
	{
		return responseData;
	}

	public void setErrorMessage(String message)
	{
		this.message = message;
	}

	public void setSuccess(boolean isSuccess)
	{
		this.isSuccess = isSuccess;
	}

	public boolean isInternetAvaibale()
	{
		return isInternetAvaibale;
	}

	public void setInternetAvaibale(boolean isInternetAvaibale)
	{
		this.isInternetAvaibale = isInternetAvaibale;
	}

}
