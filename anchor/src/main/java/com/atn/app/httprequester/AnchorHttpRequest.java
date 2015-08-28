

package com.atn.app.httprequester;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.text.TextUtils;

import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;

public class AnchorHttpRequest  {

	public interface Method {
		int GET = 100;
		int POST = 101;
	}
	
	public interface AnchorHttpResponceListener {
		
		public void onError(Exception ex);
		public void onSuccess(JSONObject jsonObject);
		///save data in db here. only call when mFsRequest 
		public void onSuccessInBackground(JSONObject jsonObject);
	}
	private Context mContext;
	private AnchorHttpResponceListener mResponceListener = null;
	private int mMethod;
	private Uri.Builder mBuilder;
	private HttpAsynchTask mHttpAsynchTask;
	
	private boolean isCanceled = false;
	public boolean isCanceled() {
		return isCanceled;
	}
	public void setAnchorResponceListener(AnchorHttpResponceListener responceListener){
		this.mResponceListener = responceListener;
	}
	
	private ArrayList<NameValuePair> mNameValuesParams = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> mFileParams = new ArrayList<NameValuePair>();
	
	public void addFile(String key,String path) {
		mFileParams.add(new BasicNameValuePair(key, path));
	}
	
	public void addText(String key,String value) {
		mNameValuesParams.add(new BasicNameValuePair(key, value));
	}
	
	public AnchorHttpRequest(Context context) {
		this.mContext = context;
	}

	public AnchorHttpRequest(Context context,Uri.Builder builder,int method,
			AnchorHttpResponceListener responceListener) {
		this.mContext = context.getApplicationContext();
		this.mResponceListener = responceListener;
		this.mBuilder = builder;
		this.mMethod = method;
	}
	
	protected void addParams(ArrayList<NameValuePair> nameValuesParams) {}
	protected void addFileParams(ArrayList<NameValuePair> mFileParams) {}

	public void execute() {
		
		if(mHttpAsynchTask!=null&&mHttpAsynchTask.getStatus()!=Status.FINISHED) {
			mHttpAsynchTask.cancel(true);
		}
		mHttpAsynchTask = new HttpAsynchTask();
		mHttpAsynchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	
	
	public void setCanceled(boolean canceled) {
		this.isCanceled = canceled;
		if(mHttpAsynchTask!=null&&mHttpAsynchTask.getStatus()!=Status.FINISHED){
			mHttpAsynchTask.cancel(true);
		}
		this.mResponceListener = null;
	}
	
	private class HttpAsynchTask extends AsyncTask<Void, Void, JSONObject> {
		private Exception mException = null;
		@Override
		protected JSONObject doInBackground(Void... params) {
			
			HttpUriRequest request = null;
			if(AtnUtils.isConnected(mContext)) {
				
				try {
					switch (mMethod) {
					case Method.GET:
						request = new HttpGet(HttpUtility.preparegetRequest(mBuilder, mNameValuesParams));
						AtnUtils.log(mBuilder.build().toString()+" param"+mNameValuesParams.toString());
						break;
					case Method.POST:
						HttpPost post = new HttpPost(mBuilder.build().toString());
						post.setEntity(HttpUtility.getPostRequestParams(mNameValuesParams, mFileParams).build());
						request = post;
						AtnUtils.log(mBuilder.build().toString()+" param"+mNameValuesParams.toString());
						break;
					default:
						break;
					}
				} catch (Exception e) {
					mException = e;
					AtnUtils.log("AnchorHttpRequestUrl:-"+e.getLocalizedMessage());
				}
				
				///make mFsRequest
				if(request!=null) {
					try {
						String result = HttpUtility.processHttpResponse(HttpManager.executeOnSeperateClient(request));
						AtnUtils.log("AnchorHttpRequestResult:-"+result);
						if(!TextUtils.isEmpty(result)) {
							JSONObject jsonObject = new JSONObject(result);
							if(mResponceListener!=null&&!isCancelled()) {
								mResponceListener.onSuccessInBackground(jsonObject);
							}
							return jsonObject;
						} else {
							mException = new Exception("Http Response Not Ok");
						}
						//set appropriate message in catch
					} catch (ParseException e) {
						e.printStackTrace();
						mException =e;
					} catch (IOException e) {
						e.printStackTrace();
						mException =e;
					} catch (JSONException e) {
						e.printStackTrace();
						mException =e;
					}catch (Exception e) {
						e.printStackTrace();
						mException =e;
					}
				} else {
					mException = new Exception("No Method defined");
				}
			} else {
				mException = new Exception("No Internet");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			if(isCancelled()) return;
			
			if(mResponceListener!=null) {
				if(mException!=null){
					mResponceListener.onError(mException);	
				} else if(result!=null) {
					mResponceListener.onSuccess(result);
				}
			}else if(result!=null){
				//print non listener request.
				AtnUtils.log(result.toString());
			}
		}
	}
}
