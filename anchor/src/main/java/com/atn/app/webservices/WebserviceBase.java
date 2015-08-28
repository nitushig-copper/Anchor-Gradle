package com.atn.app.webservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates a base for the web service to handle all the web service calls and response/errors. This should be
 * extended by the child classes to make a call to web service and listen for the web service responses.
 * You should specify the mFsRequest type as get/post to make a web service call. 
 * 
 * @author gagan
 *
 */
public class WebserviceBase {
	
	/**
	 * Defines web service mFsRequest type.
	 * @author gagan
	 *
	 */
	public enum RequestType {
		GET, POST
	}

	
	/**
	 * Error decleration for the web services.
	 */
	public static int CONNECTION_TIMEOUT = 20 * 1000;
	public static int SOCKET_TIMEOUT = 20 * 1000;
	public static final String IS_SESSION_TIMED_OUT = "is_session_timed_out";
	
	/**
	 * Base url of web service.
	 */

	
	
	private static int NOTIFY_HANDLER_SUCCESS = 1;
	private static int NOTIFY_HANDLER_FAIL = -1;
	
	private URL  url;
	private String urlString;
	private boolean isUrlSet = false;
	private WebserviceListener listener;	
	private RequestType requestType = RequestType.GET;
	
	private boolean isUploadFile;
	private String uploadFilePath;
	private String fileKey;
	
	private boolean isExecuting = false;
	private boolean parllelExecutionAllowed = false;
	
	private ArrayList<NameValuePair> nameValuePairs;
	
	private ServiceType webServiceType;
	
	private Handler callbackHandler =null;
	
	
	/**
	 * Allows the parallel execution of web services using specified value.
	 * 
	 * @param parllelAllowed true to allow parallel execution of web services.
	 */
	public void setParllelExecution(boolean parllelAllowed) {
		parllelExecutionAllowed = parllelAllowed;
	}
	
	/**
	 * Initializes the post data for the POST web service call.
	 * 
	 * @param nameValuePairs post data value.
	 */
	public void setPostData(ArrayList<NameValuePair> nameValuePairs) {
		this.nameValuePairs = nameValuePairs;
	}
	
	
	/**
	 * Registers a web service listener to listen for web service response/error notifications.
	 * @param listener
	 */
	public void setWebserviceListener(WebserviceListener listener) {
		this.listener = listener;
	}
	
	
	/**
	 * Sets the web service mFsRequest type.
	 * 
	 * @param rType Request type GET/POST.
	 */
	public void setRequestType(RequestType rType) {
		requestType = rType;
	}
	
	
	/**
	 * Sets the type of web service. This is used to differenciate web service response/error when response/erros
	 * from multiple web services are received.
	 * 
	 * @param webserviceType
	 */
	public void setWebserviceType(ServiceType webserviceType) {
		this.webServiceType = webserviceType;
	}
	
	
	/**
	 * Initializes the web service base. 
	 * 
	 * @param url to initialize.
	 */
	public WebserviceBase(String url) {
		try {
			this.url = new URL(url);
			urlString = url;
			isUrlSet = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			isUrlSet = false;

			if (listener != null) {
				listener.onSetUrlError(webServiceType, ex);
			}
		}

	}
	
	
	/**
	 * Sets the new url to make a web service call.
	 * 
	 * @param url
	 */
	public void setUrl(String url)
	{
		try 
		{			
			this.url = new URL(url);
			urlString = url;
			isUrlSet = true;
		}
		catch (MalformedURLException e) 
		{		
			e.printStackTrace();
			isUrlSet = false;
			
			if(listener != null)
			{
				listener.onSetUrlError(webServiceType, e);
			}
		}
	}
	
	
	/**
	 * Creates a call back handler to handle web service responses/errors received from the server. This sends
	 * the success/failure/error notifications to the corresponsing web service listener.
	 */
	private void createHandler()
	{
		
		if(callbackHandler==null)
		{
			callbackHandler = new Handler(new Handler.Callback() 
			{
				
				@Override
				public boolean handleMessage(Message msg) 
				{
					isExecuting = false;
					
					if(msg.what == NOTIFY_HANDLER_SUCCESS)
					{
						WebserviceResponse response = (WebserviceResponse) msg.obj;
						
						if(listener != null)
						{
							if(response.isSuccess())
							{								
								listener.onServiceResult(webServiceType, response.getResponseData());
							}
							else
							{					
								WebserviceError error = new WebserviceError(response.getErrorMessage());
								listener.onServiceError(webServiceType, error.getErrorCode(), error.getErrorMessage());
							}
						}	
						
						if(!response.isSuccess())
						{							
							WebserviceError error = new WebserviceError(response.getErrorMessage());
							
							if(error.getErrorCode() == WebserviceError.SESSION_EXPIRED)
							{
								Bundle bendle = new Bundle();
								bendle.putBoolean(IS_SESSION_TIMED_OUT, true);
							}
						}
					}
					else if(msg.what == NOTIFY_HANDLER_FAIL)
					{
						if(listener != null)
						{	
							listener.onServiceError(webServiceType, WebserviceError.UNKNOWN_ERROR, ((Exception)msg.obj).getMessage());
						}
					}
					return false;
				}
			});	
		}
	}
	
	
	
	/**
	 * Sets the file location to send the specified file with key to the server.
	 * 
	 * @param key secure key.
	 * @param path path of the file.
	 */
	public void setFile(String key,String path)
	{
		isUploadFile = true;
		uploadFilePath = path;
		fileKey = key;
	}
	
	
	/**
	 * Initializes the web services parameters. This checks for the web service type either it is GET or POST
	 * web service and then initializes the parameters accordingly.
	 */
	private void generateAuthParams()
	{	
		if(requestType == RequestType.GET)
		{
			String urlQuery = url.getQuery();
			
			if(urlQuery != null)
			{
				if(!urlQuery.contains(HttpUtility.API_KEY_FIELD))
				{
					urlString = urlString + "&" + HttpUtility.API_KEY_FIELD + "=" + HttpUtility.API_KEY_VALUE;
				}
				
				if(!urlQuery.contains(HttpUtility.CLIENT_KEY_FIELD))
				{
					urlString = urlString + "&" + HttpUtility.CLIENT_KEY_FIELD + "=" + HttpUtility.CLIENT_KEY_VALUE;
				}
				
				int paramsLength =nameValuePairs!=null?nameValuePairs.size():0;
				for (int i = 0; i < paramsLength; i++) {
					urlString = urlString + "&"+ nameValuePairs.get(i).getName();
					urlString = urlString + "="+ nameValuePairs.get(i).getValue();
				}
			}
			else
			{
				urlString = urlString + "?" + HttpUtility.API_KEY_FIELD + "=" + HttpUtility.API_KEY_VALUE + "&" + HttpUtility.CLIENT_KEY_FIELD + "=" + HttpUtility.CLIENT_KEY_VALUE;
				
				int paramsLength =nameValuePairs!=null?nameValuePairs.size():0;
				
				for (int i = 0; i < paramsLength; i++) {
					urlString = urlString + "&"+ nameValuePairs.get(i).getName();
					urlString = urlString + "="+ nameValuePairs.get(i).getValue();
				}
			}
			
			try {
				// urlString = URLEncoder.encode(urlString,"UTF-8");
				urlString = urlString.replace(" ", "%20");
				setUrl(urlString);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if(requestType == RequestType.POST) {
			if (nameValuePairs == null) {
				nameValuePairs = new ArrayList<NameValuePair>();
			}
			nameValuePairs.add(new BasicNameValuePair(HttpUtility.API_KEY_FIELD,HttpUtility.API_KEY_VALUE));
			nameValuePairs.add(new BasicNameValuePair(HttpUtility.CLIENT_KEY_FIELD,HttpUtility.CLIENT_KEY_VALUE));		
		}
	}
	
	
	/**
	 * This makes a web service call asynchronously. This first checks for the Internet connectivity and then
	 * initializes the call back handler and web service parameters and then asynchronously make a web 
	 * service mFsRequest. It also creates web service response to listen for the response/errors received from
	 * the server.
	 */
	protected void doRequestAsync()
	{
		createHandler();
		
		if(!parllelExecutionAllowed && isExecuting)
			return;
		
		if(!AtnUtils.isConnectedToInternet())
		{
			if(listener!=null)
			{
				listener.onNoInternet(webServiceType, new  Exception(AtnApp.getAppContext()
						.getResources().getString(R.string.no_internet_available)));
			}
			
			return;
		}
		
		if(!isUrlSet)
		{
			if(listener != null)
				listener.onSetUrlError(webServiceType, new Exception("URL not set"));
			
			return;
		}
		
		generateAuthParams();
		
		new Thread(new Runnable() {
			@Override
			public void run() {			
				try {	
					isExecuting = true;
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
					HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
					DefaultHttpClient client = new DefaultHttpClient(httpParameters);
					
					HttpResponse response = null;
					
					if(requestType == RequestType.GET ) {
						HttpGet getRequest = new HttpGet(urlString);
						getRequest.setHeader("Content-type", "text/plain");
						getRequest.setHeader("Accept", "text/plain");	
						response = client.execute(getRequest);
					} else if(requestType == RequestType.POST) {
						
						HttpPost postRequest = new HttpPost(urlString);						
						if(nameValuePairs != null) {	
							int paramsLength = nameValuePairs.size();
							MultipartEntityBuilder multiPart = MultipartEntityBuilder.create();
							
							for (int i = 0; i < paramsLength; i++) {
								multiPart.addTextBody(nameValuePairs.get(i).getName(), nameValuePairs.get(i).getValue());
								AtnUtils.log(nameValuePairs.get(i).getName()+"   "+nameValuePairs.get(i).getValue());
							}

							if (isUploadFile) {
								multiPart.addPart(fileKey, new FileBody(new File(uploadFilePath)));
							}
							postRequest.setEntity(multiPart.build());
						}
						
						response = client.execute(postRequest);
					}
				     
				     Log.i("URL :::",""+urlString);
				    
				     InputStream content = response.getEntity().getContent();
				     BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				     
				     String result = "";
				     String s = "";
				     while ((s = buffer.readLine()) != null){
				        result += s;
				     }
				     
				     result = new String(result.getBytes(), "UTF-8");
				     Log.d("result::", ""+result);
				     WebserviceResponse serviceResponse = new WebserviceResponse(webServiceType, result);
				     
				     Message msg = new Message();
				     msg.what = NOTIFY_HANDLER_SUCCESS;
				     msg.obj = serviceResponse;				     
				     callbackHandler.sendMessage(msg);
				     
				} catch(Exception ex) {
					 Message msg = new Message();
				     msg.what = NOTIFY_HANDLER_FAIL;
				     msg.obj = ex;				     
				     callbackHandler.sendMessage(msg);
				}
			}
			
		}).start();
	}

	
	/**
	 * This makes a web service call synchronously and returns the web service response. This first checks 
	 * for the Internet connectivity and then initializes the call back handler and web service parameters
	 * and then synchronously make a web service mFsRequest. It also creates web service response to listen for
	 * the response/errors received from the server.<p>
	 * 
	 * Note: This should be called from the background thread otherwise it will throw NetworkOnMainThread
	 * exception.
	 */
	protected WebserviceResponse doRequestSynch()
	{
		WebserviceResponse serviceResponse = null;
		
		generateAuthParams();
		
		try
		{	
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
			
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse response = null;
			 Log.i("URL :::",""+urlString);
			if(requestType == RequestType.GET )
			{
				HttpGet getRequest = new HttpGet(urlString.replace(" ", "%20"));
				getRequest.setHeader("Content-type", "text/plain");
				getRequest.setHeader("Accept", "text/plain");	
				response = client.execute(getRequest);
				
			} else if(requestType == RequestType.POST) {
				
				HttpPost postRequest = new HttpPost(urlString);						
				
				if(nameValuePairs != null) {
					int paramsLength = nameValuePairs.size();
					MultipartEntityBuilder multiPart = MultipartEntityBuilder.create();
					for (int i = 0; i < paramsLength; i++) {
						multiPart.addTextBody(nameValuePairs.get(i).getName(),
								nameValuePairs.get(i).getValue());
					}
					if (isUploadFile) {
						multiPart.addPart(fileKey, new FileBody(new File(uploadFilePath)));
					}
					postRequest.setEntity(multiPart.build());
				}
				response = client.execute(postRequest);
			}
			
			
		   HttpEntity httpEntity = response.getEntity();
		   String  result = EntityUtils.toString(httpEntity);
		   serviceResponse = new WebserviceResponse(webServiceType, result);
		   
		   if(!serviceResponse.isSuccess())
		   {			    
				WebserviceError error = new WebserviceError(serviceResponse.getErrorMessage());
			
				if(error.getErrorCode()==WebserviceError.SESSION_EXPIRED)
				{
					//SESSION EXPIRED
				}   
		   }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();			
			serviceResponse = new WebserviceResponse();
			serviceResponse.setSuccess(false);
			serviceResponse.setErrorMessage(ex.getLocalizedMessage());	
		}
		 return serviceResponse;
	}
	
}
