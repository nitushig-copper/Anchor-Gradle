package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;

public class ShareBusinessWebService
  extends WebserviceBase
{
  private static final String SHARE_BUSINESS = "/businesses/share";
  private String businessID = null;
  private String userID = null;
  
  public ShareBusinessWebService(String userID, String businessID)
  {
    super(HttpUtility.BASE_SERVICE_URL+SHARE_BUSINESS);
    
    setRequestType(WebserviceBase.RequestType.GET);
    setWebserviceType(WebserviceType.ServiceType.SHARE_BUSINESS);
    this.userID = userID;
    this.businessID = businessID;
  }
  
  public void shareBusinesses()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
		nameValuePair.add(new BasicNameValuePair("business_id", URLEncoder.encode(this.businessID, "UTF-8")));
		
		setPostData(nameValuePair);
		doRequestAsync();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public WebserviceResponse shareSynchronousBusinesses()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("business_id", URLEncoder.encode(this.businessID, "UTF-8")));
    	
    	setPostData(nameValuePair);
      
    }
    catch (Exception localException)
    {
      
    }
    
    return doRequestSynch();
  }
}
