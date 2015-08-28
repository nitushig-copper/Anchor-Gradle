package com.atn.app.webservices;



public class WebserviceError
{

	public static final int URL_ERROR = -1;
	public static final int INTERNET_ERROR = -2;
	
	public static final int SESSION_EXPIRED = 1002;
	public static final int UNKNOWN_ERROR = -3036;
	

	private int errorCode = 4;
	private String errorMessage;
	
	public WebserviceError(String errorMessage)
	{	
		this.errorMessage = errorMessage;
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	

	@Override
	public String toString() 
	{			
		return getErrorMessage();
	}
}
