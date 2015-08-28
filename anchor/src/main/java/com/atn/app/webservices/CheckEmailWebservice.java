package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to check whether the specified email is already registered with ATN or not.
 * Response of this web service is returned to the web service listener.
 * 
 * @author gagan
 *
 */
public class CheckEmailWebservice extends WebserviceBase
{
	
	public static final String CHECK_EMAIL = "/checkemail";

	private String email;
	private String userId = null;

	
	/**
	 * Initializes web service for the specified email.
	 * 
	 * @param email
	 */
	public CheckEmailWebservice(String email)
	{
		super(HttpUtility.BASE_SERVICE_URL + CHECK_EMAIL);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.CHECK_EMAIL);
		
		this.email = email;
	}
	
	
	/**
	 * Initializes web service for the specified email and user id.
	 * 
	 * @param email
	 * @param userId
	 */
	public CheckEmailWebservice(String email, String userId)
	{
		super(HttpUtility.BASE_SERVICE_URL + CHECK_EMAIL);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.CHECK_EMAIL);
		
		this.email = email;
		this.userId = userId;
	}
	
	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse checkSynchronousEmail()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, email));	
			
			if(userId != null)
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userId));
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
	 * Asynchronously calls the web service to check whether email already exists or not. Response of this web
	 * service can be captured from web service listener.
	 */
	public void checkEmail()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, email));	
			
			if(userId != null)
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, userId));
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
