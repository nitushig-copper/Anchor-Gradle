package com.atn.app.webservices;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a web service to register a new user with the ATN.
 * Response of this web service is returned to the web service listener.
 * 
 * @author gagan
 *
 */
public class SignUpWebservice extends WebserviceBase {

	private static final String REGISTER = "/register";
	
	private String deviceToken = null;
	
	private String userName;
	private String email;
	private String password;
	private int sex;
	private String address;
	private String accessToken;
	private String profilePic;
	
	/**
	 * Initializes web service for the specified user details.
	 * 
	 * @param userName
	 * @param email
	 * @param password
	 * @param sex
	 * @param address
	 */
	public SignUpWebservice(String userName, String email, String password, int sex, String address, String deviceToken,String profilePic)
	{
		super(HttpUtility.BASE_SERVICE_URL + REGISTER);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.REGISTER);
		
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.sex = sex;
		this.address = address;
		this.deviceToken = deviceToken;
		this.profilePic = profilePic;
	}
	
	
	/**
	 * Initializes web service for the specified user details.
	 * 
	 * @param userName
	 * @param email
	 * @param password
	 * @param sex
	 * @param address
	 * @param accessToken Instagram access token.
	 */
	public SignUpWebservice(String userName, String email, String password, int sex, String address, String deviceToken, String accessToken,String profilePic)
	{
		super(HttpUtility.BASE_SERVICE_URL + REGISTER);
		setRequestType(RequestType.POST);
		setWebserviceType(ServiceType.REGISTER);
		
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.sex = sex;
		this.address = address;
		this.deviceToken = deviceToken;
		this.accessToken = accessToken;
		this.profilePic = profilePic;
	}

	
	/**
	 * Return synchronous web service response from the server. This should be called within the background
	 * thread.
	 * 
	 * @return web service response.
	 */
	public WebserviceResponse registerSynchronousUser()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userName));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, email));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD, password));
			nameValuePair.add(new BasicNameValuePair(UserDetail.GENDER, sex + ""));
			
			if(!TextUtils.isEmpty(address)){
				nameValuePair.add(new BasicNameValuePair(UserDetail.ADDRESS, address));	
			}
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TOKEN, deviceToken));
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TYPE, UserDetail.DEVICE_TYPE_VALUE));
			
			if(!TextUtils.isEmpty(profilePic)){
				//nameValuePair.add(new BasicNameValuePair(UserDetail.PROFILE_PIC, profilePic));
				setFile(UserDetail.PROFILE_PIC, profilePic);
			}
			
			if (accessToken != null) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.INSTAGRAM_ACCESS_TOKEN, accessToken));
			}
						
			setPostData(nameValuePair);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return doRequestSynch();
	}
	
	
	
	/**
	 * Asynchronously calls the web service to send the user details to ATN service to register user.
	 * Response of this web service can be captured from web service listener.
	 */
	public void registerUser()
	{
		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair(UserDetail.USER_NAME, userName));	
			nameValuePair.add(new BasicNameValuePair(UserDetail.EMAIL, email));
			nameValuePair.add(new BasicNameValuePair(UserDetail.PASSWORD, password));
			nameValuePair.add(new BasicNameValuePair(UserDetail.GENDER, sex + ""));
			if(!TextUtils.isEmpty(address)){
				nameValuePair.add(new BasicNameValuePair(UserDetail.ADDRESS, address));	
			}
			
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TOKEN, deviceToken));
			nameValuePair.add(new BasicNameValuePair(UserDetail.DEVICE_TYPE, UserDetail.DEVICE_TYPE_VALUE));
			
			if(!TextUtils.isEmpty(profilePic)) {
				//nameValuePair.add(new BasicNameValuePair(UserDetail.PROFILE_PIC, profilePic));
				setFile(UserDetail.PROFILE_PIC, profilePic);
			}
			
			if(accessToken != null) {
				nameValuePair.add(new BasicNameValuePair(UserDetail.INSTAGRAM_ACCESS_TOKEN, accessToken));
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
