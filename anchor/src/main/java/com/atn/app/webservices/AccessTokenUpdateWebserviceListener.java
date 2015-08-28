package com.atn.app.webservices;

public interface AccessTokenUpdateWebserviceListener
{
	public void onSuccess(String message);
	public void onFailed(int errorCode, String errorMessage);
}
