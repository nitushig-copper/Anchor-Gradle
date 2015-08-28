package com.atn.app.webservices;

import java.util.ArrayList;

import com.atn.app.datamodels.AtnRegisteredVenueData;

public interface AtnVenueWebServiceListener
{
	
	public void onSuccess(ArrayList<AtnRegisteredVenueData> atnVenueData);
	public void onFailed(int errorCode, String errorMessage);
	
}
