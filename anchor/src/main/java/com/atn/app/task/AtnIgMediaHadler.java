package com.atn.app.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.atn.app.AtnApp;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnMediaRefreshTask;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.AtnMediaRefreshTask.Status;

public class AtnIgMediaHadler {

	private  static List<VenueLoadListener> venueListenerList;
	private static  List<VenueModel> fourSquareVenueQueqe;
	private static AtnIgMediaHadler atnMediaRefresh = null;
	static{
		fourSquareVenueQueqe = new ArrayList<VenueModel>();
		fourSquareVenueQueqe = Collections.synchronizedList(fourSquareVenueQueqe);
		venueListenerList = new ArrayList<VenueLoadListener>();
		venueListenerList = Collections.synchronizedList(venueListenerList);
	}
	private MediaRefreshTask mediaTaskRef = null;
	
	public interface VenueLoadListener{
		public void onVenueLoad(VenueModel fsVenue); 
	}
	
	private AtnIgMediaHadler(){}
	
	public static AtnIgMediaHadler getInstance(){
		if(atnMediaRefresh==null){
			atnMediaRefresh = new AtnIgMediaHadler();
		}
		return atnMediaRefresh;
	}
	
	
	public void registerListener(VenueLoadListener listener){
		if(!venueListenerList.contains(listener)){
			venueListenerList.add(listener);
		}
	}
	
	public void unRegisterListener(VenueLoadListener listener){
		venueListenerList.remove(listener);
	}
	
	
	public Status getStatus(){
		if(mediaTaskRef==null) return Status.FINISHED;
		return mediaTaskRef.getStatus();
	}
	
	public void loadVenue(VenueModel fsVenue){
		if(!TextUtils.isEmpty(fsVenue.getVenueId())){
			if(!fourSquareVenueQueqe.contains(fsVenue)){
				fourSquareVenueQueqe.add(0, fsVenue);
			}
			load();
		}
	}
	
	public void load(){
		if((mediaTaskRef==null||mediaTaskRef.getStatus()==Status.FINISHED)){
			mediaTaskRef = new MediaRefreshTask();
			mediaTaskRef.execute();
		}
	}
	
	//refresh all atn and non atn favorite bar which have faursaure id/
	private class MediaRefreshTask extends AtnMediaRefreshTask<Void, VenueModel, Boolean>{

		private FsHttpRequest refreshRequest = null;
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			refreshRequest = new FsHttpRequest(AtnApp.getAppContext());
		}
		
		@Override
		public Boolean doInBackground(Void... params) {
			
			//load all venues.
			Atn.Venue.addFsVenueIdsForRefresh(fourSquareVenueQueqe);
			if(fourSquareVenueQueqe.size()!=0&&AtnUtils.isConnectedToInternet()) {
				boolean isAllLoaded = false;
				while (!isAllLoaded) {
					final VenueModel fsVenue = fourSquareVenueQueqe.get(0);
					refreshRequest.refreshVenue(fsVenue);
					fourSquareVenueQueqe.remove(fsVenue);
					publishProgress(fsVenue);
					isAllLoaded = fourSquareVenueQueqe.size()==0;
				}
			}else{
				fourSquareVenueQueqe.clear();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(VenueModel... values) {
			super.onProgressUpdate(values);
			for (VenueLoadListener loadListener : venueListenerList) {
				loadListener.onVenueLoad(values[0]);
			}
		}
		
		@Override
		public void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
	
}
