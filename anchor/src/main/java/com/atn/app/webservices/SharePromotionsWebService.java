package com.atn.app.webservices;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.atn.app.utils.HttpUtility;

public class SharePromotionsWebService   extends WebserviceBase
{
  
	private static final String SHARE_PROMOTION = "/promotions/share";
	
	private String promotionID = null;
	private String userID = null;
  
  public SharePromotionsWebService(String userID, String promotionID)
  {
    super(HttpUtility.BASE_SERVICE_URL+SHARE_PROMOTION);
    setRequestType(WebserviceBase.RequestType.GET);
    setWebserviceType(WebserviceType.ServiceType.SHARE_PROMOTION);
    this.userID = userID;
    this.promotionID = promotionID;
  }
  
  public void sharePromotions()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("promotion_id", URLEncoder.encode(this.promotionID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	doRequestAsync();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public WebserviceResponse shareSynchronousPromotions()
  {
    try
    {
    	ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("user_id", URLEncoder.encode(this.userID, "UTF-8")));
    	nameValuePair.add(new BasicNameValuePair("promotion_id", URLEncoder.encode(this.promotionID, "UTF-8")));
    	
    	setPostData(nameValuePair);
    	
    }
    catch (Exception localException)
    {
      
    }
    
    return doRequestSynch();
  }
}
