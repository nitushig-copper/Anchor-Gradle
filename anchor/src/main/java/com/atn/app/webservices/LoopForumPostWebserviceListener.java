package com.atn.app.webservices;

import com.atn.app.datamodels.LoopForumPostData;


public interface LoopForumPostWebserviceListener
{
	public void onSuccess(LoopForumPostData postdata, String result);
	public void onFailed(int errorCode, String errorMessage);
}
