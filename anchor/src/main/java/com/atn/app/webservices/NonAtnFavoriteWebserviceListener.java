package com.atn.app.webservices;

import com.atn.app.datamodels.NonAtnVenueData;
import com.atn.app.webservices.WebserviceType.ServiceType;


public interface NonAtnFavoriteWebserviceListener 
{
	public void onSuccess(ServiceType serviceType, NonAtnVenueData venueData);
	public void onFailed(int errorCode, String errorMessage);
}
