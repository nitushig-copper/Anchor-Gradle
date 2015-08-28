package com.atn.app.webservices;

import java.util.ArrayList;

import com.atn.app.datamodels.ForumData;

public interface LoopForumWebserviceListener
{
	public void onSuccess(ArrayList<ForumData> forumData);
	public void onFailed(int errorCode, String errorMessage);
}
