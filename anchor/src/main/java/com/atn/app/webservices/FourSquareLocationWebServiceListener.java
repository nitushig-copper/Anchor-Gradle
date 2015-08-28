package com.atn.app.webservices;


public interface FourSquareLocationWebServiceListener {

	public void onSuccess(String foursquareData);
	public void onFailure(int errorCode, String errorMessage);
	
}
