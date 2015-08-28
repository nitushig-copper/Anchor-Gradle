package com.atn.app.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.atn.app.AtnApp;

public class ProcessImageTask extends AtnTask<String, Void, String> {

	private ProcessImageListener listener;
	private Uri uri = null;
	
	public interface ProcessImageListener {
		public void onProcessImage(String path);
	}
	
	public void setProcessImageListener(ProcessImageListener listener) {
		this.listener = listener;
	}

	public ProcessImageTask(ProcessImageListener listener){
		setProcessImageListener(listener);
	}
	public ProcessImageTask( Uri uri,ProcessImageListener listener){
		setProcessImageListener(listener);
		this.uri = uri;
	}
	
	
	@Override
	public String doInBackground(String... params) {
		
		String path = null;
		if(this.uri!=null) {
			path = AtnImageUtils.getBitmapFromUri(uri,AtnApp.getAppContext());
			if(TextUtils.isEmpty(path)) {
				path = AtnUtils.getRealPathFromURI(AtnApp.getAppContext(),uri);
			}
			
		}else if(params.length>0){
			path = params[0];
		}
		
		if(!TextUtils.isEmpty(path)&&AtnImageUtils.rorateIfNeededAndSave(path)) {
			return path;
		}
		return path;
	}

	@Override
	public void onPostExecute(String result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onProcessImage(result);
		}
	}
}
