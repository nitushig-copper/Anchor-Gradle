package com.atn.app.webservices;

public interface AtnVenueShareWebserviceListener
{
	public void onSuccess(String message);
	public void onFailed(int errorCode, String errorMessage);
}
