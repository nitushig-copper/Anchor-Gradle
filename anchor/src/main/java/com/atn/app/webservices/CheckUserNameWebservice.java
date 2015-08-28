package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to check whether the specified user name is already registered with ATN or not.
 * Response of this web service is returned to the web service listener.
 * 
 * @author gagan
 *
 */
public class CheckUserNameWebservice extends WebserviceBase
{

	public static final String CHECK_USER = "/checkuser";

	private String userName;
	private String userId = null;
	
	
	/**
	 * Initializes web service for the specified userName.
	 * 
	 * @param userName
	 */
	public CheckUserNameWebservice(String userName)
	{
		super(HttpUtility.BASE_SERVICE_URL + CHECK_USER);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.CHECK_USER);
		
		this.userName = userName;
	}
	
	
	/**
	 * Initializes web service for the specified user name and user id.
	 * @param user name
	 * @param userId
	 */
	public CheckUserNameWebservice(String userName, String userId)
	{
		super(HttpUtility.BASE_SERVICE_URL + CHECK_USER);
		setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.CHECK_USER);
		this.userName = userName;
		this.userId = userId;
	}

	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse checkSynchronousUser()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userName));	
			
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
	 * Asynchronously calls the web service to check whether user name already exists or not. Response of this web
	 * service can be captured from web service listener.
	 */
	public void checkUser()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userName));	
			
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
