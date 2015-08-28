package com.atn.app.webservices;

import com.atn.app.webservices.WebserviceType.ServiceType;

public interface BusinessSubscribeWebserviceListener
{
	public void onSuccess(ServiceType serviceType, String businessId);
	public void onFailed(ServiceType serviceType, int errorCode, String errorMessage);
}
