package com.atn.app.task;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.atn.app.AtnApp;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnBar;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.httprequester.AtnBarRequest;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.location.AtnLocationManager;

public class AtnRegisterBarTask implements Runnable{

	private static AtnRegisterBarTask atnRegTask = null;
	private AtnBarTask atnTsk = null;
	private boolean isOnceLoadedFromServer = false;
	Thread localDbThread = null;
	
	public interface AtnRegisterBarTaskListener{
		public void progressUpdate(List<AtnBar> list);
		public void onTaskFinish(List<AtnBar> list);
	}
	
	private static final List<Handler> listeners = new ArrayList<Handler>();
	
	private AtnRegisterBarTask(){}
	
	public void setLoadFromServer(boolean server){
		isOnceLoadedFromServer = !server;
	}
	
	public void registerListener(Handler listener){
		if(listeners!=null&&!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public void unRegisterListener(Handler listener){
		if(listeners!=null){
			listeners.remove(listener);
		}
	}
	
	public static AtnRegisterBarTask getInstance(){
		if(atnRegTask==null){
			atnRegTask = new AtnRegisterBarTask();
		}
		return atnRegTask;
	}
	
	public void loadDataFromServer(){
		if(atnTsk==null||atnTsk.getStatus()==Status.FINISHED){
			atnTsk = new AtnBarTask();
			atnTsk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	public Status getStatus(){
		if(atnTsk==null) return Status.FINISHED;
		return atnTsk.getStatus();
	}
	
	
	public void refreshDataFromDb(){
		//check if data is not loaded from server once then load
		if(!isOnceLoadedFromServer){
			loadDataFromServer();
		}
		if(localDbThread==null||!localDbThread.isAlive()){
			localDbThread  = new Thread(this);
			localDbThread.start();
		}
	}
	
	private class AtnBarTask extends AsyncTask<Void, List<AtnOfferData>, List<AtnOfferData>>{

		String errorMessage = "unknow error";
		
		@Override
		protected List<AtnOfferData> doInBackground(Void... params) {
			
			//db fetch on progress call
			Location currentLocation = AtnLocationManager.getInstance().getLastLocation();
			if(currentLocation != null) {
				AtnBarRequest newBusinessRequest = new AtnBarRequest(AtnApp.getAppContext());
				if(newBusinessRequest.fetchBusiness()) {
					isOnceLoadedFromServer = true;
					return  filterData(DbHandler.getInstance().getBulkVenueDetails(false));
				}else{
					errorMessage = newBusinessRequest.getError();
				}
			} else {
				errorMessage = "Location not found";
			}
			return  null;
		}
	
		@Override
		protected void onPostExecute(List<AtnOfferData> result) {
			super.onPostExecute(result);
			sendMessageTohandlers(result,errorMessage);
			//AtnIgMediaHadler.getInstance().load();
		}
	}
	
	
	private void sendMessageTohandlers(List<AtnOfferData> result,String message){
		
		for (Handler listnr : listeners) {
			Message messsage = Message.obtain();
			if (result == null) {
				messsage.what = 0;
				messsage.obj = message;
			} else {
				messsage.what = 1;
				messsage.obj = result;
			}
			listnr.sendMessage(messsage);
		}
	}

	///filetr data on radius
	private List<AtnOfferData> filterData(List<AtnOfferData> list) {
		
		Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
		if(currntLoc==null){
			return list;
		}
		
		List<AtnOfferData> atnOfferList = new ArrayList<AtnOfferData>();
		for (AtnOfferData atnOfferData : list) {
			Location businessLoc = new Location("");
			businessLoc.setLatitude(Double.valueOf(((AtnRegisteredVenueData)atnOfferData).getBusinessLat()));
			businessLoc.setLongitude(Double.valueOf(((AtnRegisteredVenueData)atnOfferData).getBusinessLng()));
			if((currntLoc.distanceTo(businessLoc)<FsHttpRequest.RADIUS_VALUE)) {
				atnOfferList.add(atnOfferData);
			}
		}
		
		return atnOfferList;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		List<AtnOfferData> atnBar = DbHandler.getInstance().getBulkVenueDetails(false);
		sendMessageTohandlers(filterData(atnBar), null);
	}
	
	public void cancel(){
		if(atnTsk!=null&&atnTsk.getStatus()!=Status.FINISHED){
			atnTsk.cancel(true);	
		}
	}
	
}
