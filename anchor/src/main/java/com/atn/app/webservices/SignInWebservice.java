package com.atn.app.webservices;

import android.util.Log;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to login the user.
 * Response of this web service is returned to the web service listener.
 * 
 * @author gagan
 *
 */
public class SignInWebservice extends WebserviceBase
{
	public static final String LOGIN_IN = "/loginapp";

	private String userName;
	private String password;
	
	private String deviceToken = null;
	
	
	/**
	 * Initializes web service for the specified userName and password.
	 * 
	 * @param userName
	 * @param password
	 */
	public SignInWebservice(String userName, String password)
	{
		super(HttpUtility.BASE_SERVICE_URL + LOGIN_IN);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.LOGIN);
		
		this.userName = userName;
		this.password = password;
	}
	
	
	/**
	 * Initializes web service for the specified userName, password and deviceToken.
	 * 
	 * @param userName
	 * @param password
	 * @param deviceToken
	 * 
	 */
	public SignInWebservice(String userName, String password, String deviceToken) {
		super(HttpUtility.BASE_SERVICE_URL + LOGIN_IN);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.LOGIN);
		this.userName = userName;
		this.password = password;
		this.deviceToken = deviceToken;
	}
	

	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse loginSynchronousUser()
	{
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME,userName));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD,password));
			if (deviceToken != null) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TOKEN, deviceToken));
			}
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TYPE,UserDetail.DEVICE_TYPE_VALUE));


			Log.e("Nitushi App Details :: ", userName + ", " + password + ", " + deviceToken + ", " + UserDetail.DEVICE_TYPE_VALUE);

			setPostData(nameValuePair);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	/**
	 * Asynchronously calls the web service to sign in the user. Response of this web service can be captured
	 * from web service listener.
	 */
	public void loginUser() {
		try {
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			Log.e("Nitushi userName", userName);
			Log.e("Nitushi password", password);
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME,userName));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD,password));
			if (deviceToken != null) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TOKEN, deviceToken));
			}
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TYPE,UserDetail.DEVICE_TYPE_VALUE));
			setPostData(nameValuePair);
			doRequestAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
