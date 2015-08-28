package com.atn.app.webservices;

import com.atn.app.webservices.WebserviceType.ServiceType;

public interface BusinessFavoriteWebServiceListener
{
	 public abstract void onFailed(int errorCode, String errorMessage);
	 public abstract void onSuccess(ServiceType serviceType, String businessId, String message);	  
}
