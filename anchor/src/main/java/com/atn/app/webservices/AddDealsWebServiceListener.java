package com.atn.app.webservices;

import com.atn.app.datamodels.AddDealsPostData;

public interface AddDealsWebServiceListener {
	
	public void onSuccess(String result);
	public void onFailed(int errorCode, String errorMessage);
	

}
