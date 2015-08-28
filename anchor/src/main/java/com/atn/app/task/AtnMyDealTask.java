package com.atn.app.task;

import java.util.ArrayList;
import java.util.List;

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

public class AtnMyDealTask implements Runnable{

	private static AtnMyDealTask atnRegTask = null;
	private AtnBarTask atnTsk = null;
	Thread localDbThread = null;
	private boolean isOnceLoadedFromServer = false;
	
	
	public interface AtnRegisterBarTaskListener{
		public void progressUpdate(List<AtnBar> list);
		public void onTaskFinish(List<AtnBar> list);
	}
	
	private static final List<Handler> listeners = new ArrayList<Handler>();
	
	private AtnMyDealTask(){
	}
	
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
	
	public static AtnMyDealTask getInstance(){
		if(atnRegTask==null){
			atnRegTask = new AtnMyDealTask();
		}
		return atnRegTask;
	}
	
	public void loadDataFromServer(){
		if(atnTsk==null||atnTsk.getStatus()==Status.FINISHED){
			atnTsk = new AtnBarTask();
			atnTsk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	
	/**
	 * load data from db and send mFsRequest to server if needed
	 * */
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
	
	
	public Status getStatus(){
		if(atnTsk==null) return Status.FINISHED;
		return atnTsk.getStatus();
	}
	
	private class AtnBarTask extends AsyncTask<Void, List<AtnOfferData>, List<AtnOfferData>>{

		private String errorMessage = "Unknow error";
		
		@Override
		protected List<AtnOfferData> doInBackground(Void... params) {
			AtnBarRequest newMydealRequest = new AtnBarRequest(AtnApp.getAppContext());
			if(newMydealRequest.fetchPromotions()){
				isOnceLoadedFromServer = true;
				return  getMyDealsParsedData(DbHandler.getInstance().getBulkVenueDetails(true));
			}else{
				errorMessage = newMydealRequest.getError();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AtnOfferData> result) {
			super.onPostExecute(result);
			sendMessageTohandlers(result,errorMessage);
		}
	}

	private List<AtnOfferData> getMyDealsParsedData(List<AtnOfferData> venueData ) {
		ArrayList<AtnOfferData> offerData = new ArrayList<AtnOfferData>();
		for (int i = 0; i < venueData.size(); i++) {
			AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData) venueData.get(i);
			if (atnVenueData.getBulkPromotion().size() > 0) {
				offerData.add(atnVenueData);
				offerData.addAll(atnVenueData.getBulkPromotion());
			}
		}
		return offerData;
	}
	
	private void sendMessageTohandlers(List<AtnOfferData> result,String message){
		
		for (Handler handler : listeners) {
			Message messsage = null;
			if (result == null) {
				messsage = Message.obtain(handler, 0, message);
			} else {
				messsage = Message.obtain(handler, 1, result);
			}
			messsage.sendToTarget();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		//db fetch on progress call
		List<AtnOfferData> list = DbHandler.getInstance().getBulkVenueDetails(true);
		sendMessageTohandlers(getMyDealsParsedData(list), null);
	}
	
	
	public void cancel(){
		if(atnTsk!=null&&atnTsk.getStatus()!=Status.FINISHED){
			atnTsk.cancel(true);	
		}
	}
	
}
