package com.atn.app.webservices;

import com.atn.app.webservices.WebserviceType.ServiceType;


public interface WebserviceListener
{
	public void onSetUrlError(ServiceType serviceType, Exception ex);
	public void onServiceResult(ServiceType serviceType,String result);
	public void onServiceError(ServiceType serviceType, int errorCode, String errorMessage);
	public void onNoInternet(ServiceType serviceType, Exception ex);

}
