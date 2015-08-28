package com.atn.app.webservices;

import java.util.ArrayList;

import com.atn.app.datamodels.FoursquareData;

public interface FoursquareWebServiceListener
{
	public void onSuccess(ArrayList<FoursquareData> foursquareData);
	public void onFailure(int errorCode, String errorMessage);
}
