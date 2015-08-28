package com.atn.app.webservices;

import com.atn.app.datamodels.AtnFollowUnFollowData;

public interface AtnFollowUnFollowWebListener {

	public void onSuccess(AtnFollowUnFollowData followUnfollowBar);
	public void onFailure(int errorCode, String errorMessage);
	
}
