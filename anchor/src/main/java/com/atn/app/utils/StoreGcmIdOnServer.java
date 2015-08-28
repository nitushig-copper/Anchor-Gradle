/**
 * @Copyright Coppermobile 2014.
 * */
package com.atn.app.utils;


import android.app.Activity;

import com.atn.app.AtnApp;
import com.atn.app.pool.UserDataPool;
import com.atn.app.webservices.SignInWebservice;
import com.atn.app.webservices.WebserviceResponse;
import com.coppermobile.gcm.GcmHandler;
import com.coppermobile.gcm.GcmHandler.RegistrationGcm;


/**
 * helper class for Store GCM Id on server if its not stored yet.
 * **/
public class StoreGcmIdOnServer implements RegistrationGcm {
	//singleton object
	private static StoreGcmIdOnServer singletonInstance = null;
	//return true if task is running
	private boolean isRunnning = false;
	//set weather GCM iD is stored on server or not.
	private static boolean isGcmIdStored = false;
	
	public static boolean isGcmIdStored() {
		return isGcmIdStored;
	}

	public static synchronized void setGcmIdStored(boolean isGcmIdStored) {
		StoreGcmIdOnServer.isGcmIdStored = isGcmIdStored;
	}

	//stop creating other objects.
	private  StoreGcmIdOnServer() {}
	
	//return singleton object.
	public static StoreGcmIdOnServer getInstance() {
		if (null == singletonInstance) {
			synchronized (StoreGcmIdOnServer.class) {
				if (null == singletonInstance) {
					singletonInstance = new StoreGcmIdOnServer();
				}
			}
		}
		return singletonInstance;
	}
	

	//call to store GCM Id On server.
	public void storeGcmIdOnServer(Activity activity) {
		if(!isRunnning) {
			isRunnning = true;
			new GcmHandler(activity, this);
		}
	}
	
	@Override
	public void gcmRegistered(boolean isSusscess, String deviceId) {
		isRunnning = isSusscess;
		if(isSusscess) { 
			StoreHelper serverStore = new StoreHelper(deviceId);
			serverStore.start();
		}
	}

	//worker thread for storing GCM Id on Server
	private class StoreHelper extends Thread {
		
		private String gcmId = null;
		public StoreHelper(String gcmId) {
			this.gcmId = gcmId;
		}
		
		@Override
		public void run() {
			//store if user logged in.
	      if(UserDataPool.getInstance().isUserLoggedIn()) {
	    	  
	    	  final String userName = UserDataPool.getInstance().getUserDetail().getUserName();
	    	  final String password = UserDataPool.getInstance().getUserDetail().getUserPassword();
	    	  SignInWebservice signWeb = new  SignInWebservice(userName,password,this.gcmId); 
	    	  WebserviceResponse responce = signWeb.loginSynchronousUser();
	    	  final boolean isStored = responce.isSuccess()&&UserDataPool.getInstance().isUserLoggedIn();
	    	  GcmHandler.setRegIdStoreOnServer(AtnApp.getAppContext(), isStored);
	    	  setGcmIdStored(isStored);
	    	  isRunnning = false;
	      }
		  super.run();
		}
	}
}
