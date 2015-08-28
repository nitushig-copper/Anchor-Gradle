package com.atn.app.webservices;

import com.atn.app.datamodels.InstagramLocation;

public interface InstagramLocationWebserviceListener
{
	public void onSuccess(InstagramLocation instagramLocation);
	public void onFailure(int errorCode, String errorMessage);
	
}
