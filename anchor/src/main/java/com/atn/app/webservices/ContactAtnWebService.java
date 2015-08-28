package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to send the user details to ATN when user wants to contact ATN. This sends the user details to ATN
 * and response of this web service can be listen via web service listener.
 * 
 * @author gagan
 *
 */
public class ContactAtnWebService extends WebserviceBase
{

	private static final String CONTACT_API = "/contact";
	
	private UserDetail userDetail;
	
	/**
	 * Initialize web service.
	 */
	public ContactAtnWebService()
	{
		super(HttpUtility.BASE_SERVICE_URL + CONTACT_API);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.CONTACT_ATN);
	}
	
	
	/**
	 * set user details that will be send to ATN server.
	 * @param userDetail
	 */
	public void setUserData(UserDetail userDetail)
	{
		this.userDetail = userDetail;
	}
	
	
	/**
	 * Asynchronously calls the web service to send the user details to ATN. Response of this web service can
	 * be captured from web service listener.
	 */
	public void sendDetails()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userDetail.getUserId(), "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, userDetail.getUserEmail()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_FULL_NAME, URLEncoder.encode(userDetail.getUserFullName(), "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_Message, URLEncoder.encode(userDetail.getUserMessage(), "UTF-8")));
			
			setPostData(nameValuePair);
			doRequestAsync();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Asynchronously calls the web service to send the user details to ATN. Response of this web service can
	 * be captured from web service listener.
	 */
	public WebserviceResponse sendSyncDetails()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_ID, URLEncoder.encode(userDetail.getUserId(), "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, userDetail.getUserEmail()));
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_FULL_NAME, URLEncoder.encode(userDetail.getUserFullName(), "UTF-8")));
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_Message, URLEncoder.encode(userDetail.getUserMessage(), "UTF-8")));
			
			setPostData(nameValuePair);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return doRequestSynch();
	}

}
