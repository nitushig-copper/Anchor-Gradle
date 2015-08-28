package com.atn.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.atn.app.httprequester.AtnHttpRequest.SynchResponce;
import com.atn.app.httprequester.CategoriesFetchRequest;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;

public class SynchService extends IntentService {

	public  static final String ACTION_SERVICE = "com.atn.app.service.SYNCH_SERVICE";
	
	public static final String STATUS = "STATUS";
	public static final String MESSAGE = "MESSAGE";
	//start loading data
	public static final int START = 100;
	//fail to load data
	public static final int FAIL = 101;
	//data loaded successfully
	public static final int SUCCESS = 102;
	
	public static final int CATEGORY_LAODED = 103;
	
	public interface Command {
		String COMMAND = "COMMEND";
		int RELOAD_VENUE = 1000;
		int REFRESH_MEDIA = 1001;
	}
	
	public static  boolean IS_RUNNING = false;
	FsHttpRequest mFsRequest = null;
	
	
	public SynchService() {
		super("synchservice");
	}

	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		if(mFsRequest!=null) {
			mFsRequest.setCanceled(true);
		}
		return super.onStartCommand(intent, flags, startId);
    }
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		IS_RUNNING = true;
		CategoriesFetchRequest fetchCategoryRequest = new CategoriesFetchRequest(getApplicationContext());
		//if(SharedPrefUtils.isLoadCategory(getApplicationContext())&&AtnUtils.isConnected(getApplicationContext())) {
			fetchCategoryRequest.loadCategories();
			sendBroadCast(this,CATEGORY_LAODED,"Category Loaded");
		//}
		//load default review
		fetchCategoryRequest.loadAllCatReview();
		
		final int command =  intent.getIntExtra(Command.COMMAND, Command.RELOAD_VENUE);
		if(AtnUtils.isConnected(getApplicationContext())) {
			
			if(AtnLocationManager.getInstance().getLastLocation()!=null) { 
				SynchResponce responce = null;
				mFsRequest = new FsHttpRequest(getApplicationContext());
				sendBroadCast(this,START,"Start mFsRequest");
				
				mFsRequest.tryToSynchWithAnchorServer();
				if (command ==  Command.RELOAD_VENUE ) {
					SharedPrefUtils.setFoursquareVenueStatus(getApplicationContext(), false);
					responce = mFsRequest.synchVanues(AtnLocationManager.getInstance().getLastLocation());
					responce = mFsRequest.synchTarvelVanues(AtnLocationManager.getInstance().getLastLocation());
				} 
				
				AtnUtils.log("Load Media Image Start");
				responce = mFsRequest.refreshVenues();
				AtnUtils.log("Load Media Image End");
				SharedPrefUtils.setFoursquareVenueStatus(getApplicationContext(),
						responce == null ? false : responce.isSuccess);
				
				//data loaded
				SynchService.IS_RUNNING = false;
				if(responce!=null&&responce.isSuccess) {
					sendBroadCast(this,SUCCESS,"Synch success");
				} else if(responce!=null&&!mFsRequest.isCanceled()) {
					sendBroadCast(this,FAIL,""+responce.errorMessage);
				}
			} else {
				SynchService.IS_RUNNING = false;
				sendBroadCast(this,FAIL,"Location Not Found");
			}
		} else {
			SynchService.IS_RUNNING = false;
			sendBroadCast(this,FAIL,"FAIL: Connection Not Available");
		}
		SynchService.IS_RUNNING = false;
	}

	@Override
	public void onDestroy() {
		if (mFsRequest != null) {
			mFsRequest.setCanceled(true);
		}
		super.onDestroy();
	}
	
	
	public  static void  sendBroadCast(Context context,int status,String message){
		Intent intent = new Intent(ACTION_SERVICE);
		intent.putExtra(STATUS, status);
		intent.putExtra(MESSAGE, message);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
