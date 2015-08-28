package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to recover the forgot password.
 * Response of this web service is returned to the web service listener.
 * 
 * @author gagan
 *
 */
public class ForgotPasswordRecoveryWebservice extends WebserviceBase
{

	private static final String FORGOT_PASSWORD = "/forgot";

	private String dataValue;
	
	
	/**
	 * Initializes web service for the specified value.
	 * 
	 * @param value
	 */
	public ForgotPasswordRecoveryWebservice(String value)
	{
		super(HttpUtility.BASE_SERVICE_URL + FORGOT_PASSWORD);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.PASSWORD_RECOVERY);
		
		this.dataValue = value;
	}

	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse recoverForgotPasswordSynchronously()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			if(isEmail(dataValue))
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, dataValue));
			}
			else
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, dataValue));	
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
	 * Asynchronously calls the web service to send the password details using specified user name. Response of
	 * this web service can be captured from web service listener.
	 */
	public void recoverForgotPassword()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			
			if(isEmail(dataValue))
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, dataValue));
			}
			else
			{
				nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, dataValue));	
			}
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Checks whether specified value is email or not.
	 * 
	 * @param value to check.
	 * @return true if value is email, otherwise returns false.
	 */
	private boolean isEmail(String value)
	{
		if(value.contains("@") && value.contains("."))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
