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
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.AtnBarRequest;
import com.atn.app.provider.Atn;

public class FollowingBarTask implements Runnable{

	private static FollowingBarTask followingBarTask = null;
	private GetBarTask getBarTask = null;
	Thread localDbThread = null;
	private boolean isOnceLoadedFromServer = false;
	private static final List<Handler> listeners = new ArrayList<Handler>();
	
	private FollowingBarTask(){
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
	
	public static FollowingBarTask getInstance() {
		if(followingBarTask==null) {
			followingBarTask = new FollowingBarTask();
		}
		return followingBarTask;
	}
	
	public void loadDataFromServer() {
		if(getBarTask==null||getBarTask.getStatus()==Status.FINISHED) {
			getBarTask = new GetBarTask();
			getBarTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
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
	
	public Status getStatus(){
		if(getBarTask==null) return Status.FINISHED;
		return getBarTask.getStatus();
	}
	
	private class GetBarTask extends AsyncTask<Void, List<AtnOfferData>, List<AtnOfferData>>{

		private String errorMessage = "unKnow Error Occured";
		@Override
		protected List<AtnOfferData> doInBackground(Void... params) {
			List<AtnOfferData> atnBar = null;
			AtnBarRequest newFavRequest = new AtnBarRequest(AtnApp.getAppContext());
			if(newFavRequest.fetchFavorites()) {
				isOnceLoadedFromServer = true;
				atnBar = DbHandler.getInstance().getBulkFavoriteVenueDetails();
				atnBar.addAll(Atn.Venue.getNonAtnVenue());
				return prepareData(atnBar);
			} else {
				errorMessage = newFavRequest.getError();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AtnOfferData> result) {
			super.onPostExecute(result);
			sendMessageTohandlers(result,errorMessage);
			AtnIgMediaHadler.getInstance().load();
		}
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
	
	
	private List<AtnOfferData> prepareData(List<AtnOfferData> regBars){
		
    	List<AtnOfferData> offerData = new ArrayList<AtnOfferData>();
    	for (AtnOfferData atnBar : regBars) {
    		if(atnBar.getDataType()==VenueType.ANCHOR) {
    			AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)atnBar;
    			offerData.add(atnVenueData);
    			if ((atnVenueData.getFsVenueModel() == null)) {
    				offerData.addAll(atnVenueData.getBulkPromotion());
    			}
    		} else {
    			VenueModel venueModel = (VenueModel) atnBar;
    			offerData.add(venueModel);
    		}
   			
		}
    	return offerData;
    }

	
	//fetch data from local
	@Override
	public void run() {
		// Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        List<AtnOfferData> atnBar = DbHandler.getInstance().getBulkFavoriteVenueDetails();
		atnBar.addAll(Atn.Venue.getNonAtnVenue());
		sendMessageTohandlers(prepareData(atnBar), null);
	}
	
	public void cancel(){
		if(getBarTask!=null&&getBarTask.getStatus()!=Status.FINISHED){
			getBarTask.cancel(true);	
		}
	}
	
	
}
