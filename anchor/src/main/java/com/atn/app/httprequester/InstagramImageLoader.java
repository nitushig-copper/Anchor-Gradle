package com.atn.app.httprequester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.atn.app.AtnApp;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.provider.Atn;
import com.atn.app.service.SynchService;

public class InstagramImageLoader implements Runnable {

	public static final InstagramImageLoader loader = new InstagramImageLoader();
	private List<String[]> venueIdAndInstagramId = Collections.synchronizedList(new ArrayList<String[]>());
	private Thread myThread = null;
	private IgHttpRequest igMediaRequest= null;
	public boolean isRunning = false;
	private InstagramImageLoader() {
		igMediaRequest = new IgHttpRequest(AtnApp.getAppContext());
		igMediaRequest.setPlacesProvider(VenueModel.FOUR_SQUARE);
	}
	
	public void addVenue(String venueId,String instagramId) {
		
		if (!TextUtils.isEmpty(venueId)) {
			venueIdAndInstagramId.add(TextUtils.isEmpty(instagramId)?new String[]{venueId}:new String[]{venueId,instagramId});
			if(myThread==null||!myThread.isAlive()){
				myThread = new Thread(loader);
				myThread.start();
			}
		}
	}

	public void cancel() {
		if(myThread!=null&&myThread.isAlive()) {
			venueIdAndInstagramId.clear();
			try {
				myThread.interrupt();
			 } catch (Exception e) {
			}
		}
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (venueIdAndInstagramId.size()>0) {
			final String[] venueAndInstagramId = venueIdAndInstagramId.remove(0);
			if(venueAndInstagramId.length==2) {
				igMediaRequest.fetchInstgramMedia(venueAndInstagramId[0],venueAndInstagramId[1]);	
			} else {
				igMediaRequest.fetchInstgramMedia(venueAndInstagramId[0],Atn.Venue.getInstagramId(venueAndInstagramId[0], AtnApp.getAppContext()));	
			}
		}
		isRunning = false;
		SynchService.sendBroadCast(AtnApp.getAppContext(), SynchService.SUCCESS, "Synch success");
	}
}
