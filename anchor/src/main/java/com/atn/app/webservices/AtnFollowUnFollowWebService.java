package com.atn.app.webservices;

import com.atn.app.utils.HttpUtility;

public class AtnFollowUnFollowWebService extends WebserviceBase{

	private final String USER_ID = "user_id";
	private final String BUSINESS_ID = "business_id";
	
	private final static String ATN_BUSINESS_ = "/businesses";
	
	
	public AtnFollowUnFollowWebService(String userId, String businessId) 
	{
		super(HttpUtility.BASE_SERVICE_URL + ATN_BUSINESS_);
		/*setRequestType(RequestType.GET);
		setWebserviceType(ServiceType.ATN_REGISTERED_BARS);
		setWebserviceListener(mWebserviceListener);
		
		
		*/
	}
	
}
