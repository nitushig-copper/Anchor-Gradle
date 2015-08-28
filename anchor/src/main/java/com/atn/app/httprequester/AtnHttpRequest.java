package com.atn.app.httprequester;

import org.apache.http.client.methods.HttpUriRequest;
import android.content.Context;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;

public abstract class AtnHttpRequest {
	
	public static final int FAIL = -1;
	public static final int SUCCESS = 1;
	public static final int INVALIDE_CODE = -99;
	
	public class SynchResponce {
		public boolean isSuccess = true;
		public String errorMessage = "";
		
	}

	public SynchResponce synchResponce = null;

	private boolean isCanceled = false;
	private Context mContext;
	protected String error;

	public void setCanceled(boolean isCanceled) {
		error = "canceled";
		this.isCanceled = isCanceled;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public String getError() {
		return error;
	}
	
	public SynchResponce getResponse(){
		return synchResponce;
	}

	public AtnHttpRequest(Context context) {
		this.mContext = context.getApplicationContext();
		synchResponce = new SynchResponce();
	}
	
	public Context getContext() {
		return this.mContext;
	}
	
	
	/**
	 * Execute Get And post mFsRequest
	 * HttpClient is shared 
	 * */
	protected String executeRequest(HttpUriRequest request) {
		String result = null;
		try {
			//AtnUtils.log("AtnHttpRequestUrl:-"+request.getURI().toString());
			if (isCanceled) {
				error = "Request is canceled";
			} else if(AtnUtils.isConnected(mContext)) {
				result = HttpUtility.processHttpResponse(HttpManager.execute(request));
			} else {
				error = "Connection Not Available";
			}
		} catch (Exception e) {
			error = e.getLocalizedMessage();
		}
		//AtnUtils.log("AtnHttpRequestResult:-"+result);
		return result;
	}
	
	/**
	 * Execute Get And post mFsRequest
	 * HttpClient is not shared 
	 * */
	protected String executeOnSeperateClient(HttpUriRequest request) {
		String result = null;
		try {
			if (isCanceled) {
				error = "Request is canceled";
				synchResponce.isSuccess = false;
			} else if(AtnUtils.isConnected(mContext)) {
				result = HttpUtility.processHttpResponse(HttpManager.executeOnSeperateClient(request));
				synchResponce.isSuccess = true;
			} else {
				error = "Internet is not available.";
				synchResponce.isSuccess = false;
			}
		} catch (Exception e) {
			error = e.getLocalizedMessage();
			synchResponce.isSuccess = false;
		}
		return result;
	}
}
