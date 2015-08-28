package com.atn.app.webservices;

public interface AccessTokenSubscribeWebserviceListener
{
	public void onSuccess(String message);
	public void onFailed(int errorCode, String errorMessage);
}
