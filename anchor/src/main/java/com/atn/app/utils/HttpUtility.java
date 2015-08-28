package com.atn.app.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.provider.Atn;
import com.google.android.gms.maps.model.LatLng;

public class HttpUtility {
	
	//public static final String BASE_SERVICE_URL = "http://accessthenight.com/atn_v3.0/dev/api";//@Deprecated
	//for development ...For old Previous server
	//public static String BASE_PATH = "http://accessthenight.com/atn_v3.0/dev";
	
	public interface ImageType {
		String L = "l";
		String M = "m";
		String S = "s";
	}
	
	/**
	 * Required parameters for the web services.
	 */
	public static final String API_KEY_FIELD = "api_key";
	public static final String API_KEY_VALUE = "E6nV1VDzIJ6E";
	public static final String CLIENT_KEY_FIELD = "client";
	public static final String CLIENT_KEY_VALUE = "atn";
	
	public static String APP_SCHEME = "http";
	//public static String APP_AUTHORITY = "accessthenight.com";
	public static String APP_AUTHORITY = "anchorapp.co";//"64.235.54.200";//anchorapp.co
	//public static String APP_VERSION = "atn_v3.0";

	//http://64.235.54.200/prod/api
	private static String APP_API = "api";
	
	//used in dev only
	private static String APP_VERSION = "anchor";
	
	//private static String BUILD_TYPE = "dev";
	//for prod
	
//	private static String BUILD_TYPE = "prod"; // old 
//	private static String BUILD_TYPE = "prod_v2.0";
	private static String BUILD_TYPE = "DashboardV1.0";
	
		
	/**
	 * UnComment this for Development 
	 * */
	//public static String BASE_PATH = APP_SCHEME+"://"+APP_AUTHORITY+"/"+APP_VERSION+"/"+BUILD_TYPE;//"http://64.235.54.200/anchor/dev";
	//live
	public static String BASE_PATH = APP_SCHEME+"://"+APP_AUTHORITY;
	
	/**
	 * UnComment this for Production 
	 * */
	//public static String BASE_PATH =  APP_SCHEME+"://"+APP_AUTHORITY+"/"+APP_VERSION+"/"+BUILD_TYPE;
	
	public static final String BASE_SERVICE_URL = buildBaseUri().toString();
	public static final String USER_IMAGE_URL =  buildUserImageUri().toString()+"/";//BASE_PATH+"/files/users/";
	public static final String VENUES_IMAGE_URL = buildVenueImageUri().toString()+"/";//BASE_PATH+"/files/venues/";
	
	
	/**
     * Builds an HTTP GET mFsRequest for the specified API method. The returned mFsRequest
     * contains the web service path, and the query parameter for the specified method.
     *
     * @return A Uri.Builder containing the GET path.
     */
	public static Uri.Builder buildGetMethod(String host, String path) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(host);
        builder.authority(path);
        
        return builder;
    }
	
	private  static Uri.Builder buildAppUri(){
		Uri.Builder builder = buildGetMethod(APP_SCHEME, APP_AUTHORITY);//http://anchorapp.co
		//live
		builder.appendPath(BUILD_TYPE);
		//http://anchorapp.co/prod
		return builder;
	} 
	
	public static Uri.Builder buildUserImageUri() {
		return buildAppUri().appendPath("files").appendPath("users");
	}
	
	public static Uri.Builder buildVenueImageUri() {
		return buildAppUri().appendPath("files").appendPath("venues");
	}
	
	public static Uri.Builder buildBaseUri() {
		return buildAppUri().appendPath(APP_API);
	}
	
	public static Uri.Builder buildGetMethodWithAppParams() {
		
		Uri.Builder builder = buildBaseUri();
		builder.appendQueryParameter(API_KEY_FIELD, API_KEY_VALUE)
			   .appendQueryParameter(CLIENT_KEY_FIELD, CLIENT_KEY_VALUE);
		
		return builder;
	}
	
	public static MultipartEntityBuilder buildPostMethodWithAppParams() {
		MultipartEntityBuilder multiPart = MultipartEntityBuilder.create();
		multiPart.addTextBody(API_KEY_FIELD,API_KEY_VALUE);
		multiPart.addTextBody(CLIENT_KEY_FIELD,CLIENT_KEY_VALUE);
		return multiPart;
	}
	
	
	/**
	 * Parse HttpResponce
	 * @responce HttpResponce
	 * */
	public static String processHttpResponse(HttpResponse response) throws ParseException, IOException {
		HttpEntity entity = null;
		String result = null;
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			entity = response.getEntity();
			HttpEntity httpEntity = response.getEntity();
			 result = EntityUtils.toString(httpEntity);
			if (entity != null)
				entity.consumeContent();
		}
		return result;
	}
	
	
	public static HttpParams getHttpParams() {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, false);
		HttpProtocolParams.setUserAgent(params, "ATN/1.1");

		return params;
	}
	
	
	public static String getUserImageUrl(String imgName,String imageType ) {
		return USER_IMAGE_URL+imageType+"/"+imgName;
	}
	
	public static String getVenueMediaUrl(String imgName,String imageType ){
		return VENUES_IMAGE_URL+ImageType.S+"/"+imgName;
	}
	
	///create post mFsRequest params and file upload.
	public static MultipartEntityBuilder getPostRequestParams(ArrayList<NameValuePair> nameValueParam,ArrayList<NameValuePair> nameValueFile){
		MultipartEntityBuilder  multiPart = buildPostMethodWithAppParams();
		
		if (nameValueParam != null) {
			for (int i = 0; i < nameValueParam.size(); i++) {
				multiPart.addTextBody(nameValueParam.get(i).getName(),
						nameValueParam.get(i).getValue());
			}
		}

		if (nameValueFile != null) {
			for (int i = 0; i < nameValueFile.size(); i++) {
				multiPart.addPart(nameValueFile.get(i).getName(), new FileBody(
						new File(nameValueFile.get(i).getValue())));
			}
		}
	
		return multiPart;
	}
	
	public static String preparegetRequest(Uri.Builder builder,
			ArrayList<NameValuePair> nameValueParam) {

		if (nameValueParam != null) {
			for (int i = 0; i < nameValueParam.size(); i++) {
				builder.appendQueryParameter(nameValueParam.get(i).getName(),
						nameValueParam.get(i).getValue());
			}
		}
		return builder.build().toString();
	}
	
	
	
	/**
	 * Method return static google map url for download static map image.
	 * @param latLng center of map 
	 * @param width, width of image
	 * @param height, height of image 
	 * */
	
	public static String getStaticMapUrl(LatLng latLng,int width,int height){
		Uri.Builder builder = buildGetMethod(APP_SCHEME, "maps.googleapis.com");
		builder.appendPath("maps").appendPath("api").appendPath("staticmap");
		//center
		builder.appendQueryParameter("center",latLng.latitude + "," + latLng.longitude)
				.appendQueryParameter("zoom", String.valueOf(16))
				.appendQueryParameter("size", width + "x+" + height);
		return builder.build().toString();
	}

	//add app params that are required for every call
	public static void addAppParams( Map<String,String> params) {
		 params.put(API_KEY_FIELD,API_KEY_VALUE);
		 params.put(CLIENT_KEY_FIELD,CLIENT_KEY_VALUE);
	}
	
	//add venue params in request 
	public static AnchorHttpRequest addVenueParams(Context context,AnchorHttpRequest anchorRequest,VenueModel venue) {
		
		String mCategoryId = Atn.Category.getTopCategoryId(context, String.valueOf(venue.getVenueCategoryId()));
		
		anchorRequest.addText(JsonUtils.VenuePicUpload.FS_VENUE_ID, venue.getVenueId());
		anchorRequest.addText(JsonUtils.VenuePicUpload.NAME, HttpUtility.encode(venue.getVenueName()));
		anchorRequest.addText(JsonUtils.VenuePicUpload.LAT, venue.getLat());
		anchorRequest.addText(JsonUtils.VenuePicUpload.LON, venue.getLng());
		
		if(!TextUtils.isEmpty(venue.getAddress()))
			anchorRequest.addText(JsonUtils.VenuePicUpload.ADDRESS, HttpUtility.encode(venue.getAddress()));
        
        if(!TextUtils.isEmpty(venue.getCanonicalURL()))
        	anchorRequest.addText(JsonUtils.VenuePicUpload.FS_VENUE_LINK,HttpUtility.encode(venue.getCanonicalURL()));
        
        if(!TextUtils.isEmpty(venue.getPhoto()))
        	anchorRequest.addText(JsonUtils.VenuePicUpload.FS_VENUE_PICTURE,HttpUtility.encode(venue.getPhoto()));
        
        if(!TextUtils.isEmpty(venue.getInstagramLocationId()))
        	anchorRequest.addText(JsonUtils.VenuePicUpload.INSTAGRAM_LOCATION_ID,venue.getInstagramLocationId());
        
        if(!TextUtils.isEmpty(mCategoryId))
        	anchorRequest.addText(JsonUtils.VenuePicUpload.fS_VENUE_CATEGORY,mCategoryId);
		
        return anchorRequest;
	}
	
	
	//encode text in utf-8
	public static String encode(String text) {
//		try {
//			return URLEncoder.encode(text, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return text;
	}
	
}
