package com.atn.app.httprequester;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.atn.app.datamodels.VenueModel;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
//not in used
public class FbHttpRequest extends AtnHttpRequest {

	private IgHttpRequest igMediaRequest;
	private Location mLocation = null;
	
	
	public FbHttpRequest(Context context) {
		super(context);
		synchResponce = new SynchResponce();
		igMediaRequest = new IgHttpRequest(context);
		igMediaRequest.setPlacesProvider(VenueModel.FACEBOOK);
	}

	//keys
	private final  static String FACEBOOK_API = "graph.facebook.com";
	
	private static final String V2 = "v2.0";
	private static final String TYPE = "type";
	private static final String SEARCH = "search";
	public static  final  String DISTANCE = "distance";
	private static final String LIMIT_KEY = "limit";
	private static final String CENTER = "center";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String Q = "q";
	
	//values
	private static final int LIMIT_VALUE = 20;
	public static  final  String DISTANCE_VALUE = "8047";
	private static final String TYPE_VALUE = "place";
	private static final String SEARCH_DISTANCE = "32190";
	private static final int SEARCH_LIMIT_VALUE = 1000;
	
	
	////venues json keys
	//json keys for foursaure
		public static final String DATA = "data";
		public static final String VENUE_ID = "id";
		public static final String VENUE_NAME = "name";
		
		public static final String LOCATION = "location";
		public static final String STREET = "street";
		public static final String CITY = "city";
		public static final String STATE = "state";
		public static final String COUNTRY = "country";
		public static final String ZIP = "zip";
		public static final String LAT = "latitude";
		public static final String LNG = "longitude";
	
	private String getVenuesUrl() {
		
		String latlng = new DecimalFormat("#.0000000").format(mLocation
				.getLatitude())
				+ ","
				+ new DecimalFormat("#.0000000").format(mLocation.getLongitude());
		
		Uri.Builder uri = HttpUtility.buildGetMethod("https", FACEBOOK_API);
		uri.appendPath(V2).appendPath(SEARCH);
		uri.appendQueryParameter(LIMIT_KEY, String.valueOf(LIMIT_VALUE))
				.appendQueryParameter(TYPE, TYPE_VALUE)
				.appendQueryParameter(CENTER, latlng)
				.appendQueryParameter(DISTANCE, DISTANCE_VALUE)
				.appendQueryParameter(ACCESS_TOKEN, FacebookSession.getAccessToken(getContext()));
		String tr = uri.build().toString();
		AtnUtils.log(tr);
		return tr;
	}
	
	
	private String getVenuesSearchUrl(String query) {
		
		String latlng = new DecimalFormat("#.0000000").format(mLocation
				.getLatitude())
				+ ","
				+ new DecimalFormat("#.0000000").format(mLocation.getLongitude());
		
		Uri.Builder uri = HttpUtility.buildGetMethod("https", FACEBOOK_API);
		uri.appendPath(V2).appendPath(SEARCH);
		uri.appendQueryParameter(LIMIT_KEY, String.valueOf(SEARCH_LIMIT_VALUE))
				.appendQueryParameter(TYPE, TYPE_VALUE)
				.appendQueryParameter(CENTER, latlng)
				.appendQueryParameter(DISTANCE, SEARCH_DISTANCE)
				.appendQueryParameter(ACCESS_TOKEN, FacebookSession.getAccessToken(getContext()))
				.appendQueryParameter(Q, query);
		String tr = uri.build().toString();
		AtnUtils.log(tr);
		return tr;
	}
	
	
	public SynchResponce synchVanues(Location location) {
		igMediaRequest.isFilterOnDateRange = true;
		this.mLocation = location;
		return processRequest(executeRequest(new HttpGet(getVenuesUrl())));
	}


	private SynchResponce processRequest(String response) {
		
		if (response == null || isCanceled()) {
			synchResponce.errorMessage = getError();
			synchResponce.isSuccess = false;
			return synchResponce;
		}

		try {
			
			JSONObject jsonObject = new JSONObject(response);
			if(!jsonObject.isNull(DATA)) {
				
				JSONArray dataJsonArray = jsonObject.getJSONArray(DATA);
				for (int index = 0; index < dataJsonArray.length(); index++) {
					
					if (isCanceled()) {
						synchResponce.isSuccess = false;
						synchResponce.errorMessage = getError();
						return synchResponce;
					}
					
					JSONObject venue = dataJsonArray.getJSONObject(index);
					if (!venue.isNull(VENUE_ID)) {
						
						String fVenueId = venue.getString(VENUE_ID);
						ContentValues vanueContent = igMediaRequest.fetchInstagramVenueId(fVenueId);
						 int result = FAIL;
						 if(vanueContent!=null) {
							 result = igMediaRequest.fetchInstgramMedia(fVenueId,vanueContent.getAsString(Atn.Venue.INSTAGRAM_ID));
						 }
						 
						if (result!=FAIL) {
							
							vanueContent.put(Atn.Venue.VENUE_ID,fVenueId);
							vanueContent.put(Atn.Venue.INSTAGRAM_ID, vanueContent.getAsString(Atn.Venue.INSTAGRAM_ID));

							if (!venue.isNull(VENUE_NAME))
								vanueContent.put(Atn.Venue.VENUE_NAME,venue.getString(VENUE_NAME));

							if (!venue.isNull(LOCATION)) {
								
								JSONObject locObj = venue.getJSONObject(LOCATION);
								StringBuffer address = new StringBuffer();
								
								if(!locObj.isNull(STREET)){
									address.append(locObj.getString(STREET));
								}
								
								if(!locObj.isNull(ZIP)){
									address.append(" "+locObj.getString(ZIP));
								}
								vanueContent.put(Atn.Venue.ADDRESS_STREET,address.toString());
								address.setLength(0);
								
								if(!locObj.isNull(CITY))
									address.append(locObj.getString(CITY));
								
								if(!locObj.isNull(STATE))
									address.append(" "+locObj.getString(STATE));
								
								if(!locObj.isNull(STATE))
									address.append(" "+locObj.getString(STATE));
									
								if(!locObj.isNull(COUNTRY))
									address.append(" "+locObj.getString(COUNTRY));
								
								if (address.length()>0) {
									vanueContent.put(Atn.Venue.ADDRESS,address.toString());
								}

								if (!locObj.isNull(LAT)) {
									vanueContent.put(Atn.Venue.LAT,locObj.getString(LAT));
								}

								if (!locObj.isNull(LNG)) {
									vanueContent.put(Atn.Venue.LNG,locObj.getString(LNG));
								}
							}
							vanueContent.put(Atn.Venue.ATN_PLACE_PROVIDER,VenueModel.FACEBOOK);
							Atn.Venue.insertOrUpdate(vanueContent, getContext());
							
						} else {
							AtnUtils.logE("media not found "+" fVenueId " + fVenueId + " iGLocationId" + fVenueId);
						}
					}
				}
				// call next
				if (!jsonObject.isNull("paging")
						&& !jsonObject.getJSONObject("paging").isNull("next")) {
					return processRequest(executeRequest(new HttpGet(jsonObject.getJSONObject("paging").getString("next"))));
				}
			} else if(!jsonObject.isNull("error")) {
				
				JSONObject errObj = jsonObject.getJSONObject("error");
				synchResponce.errorMessage = errObj.getString("message");
				synchResponce.errorMessage = synchResponce.errorMessage+"\n "+errObj.getString("type");
				synchResponce.errorMessage = synchResponce.errorMessage+"\n "+errObj.getString("code");
				synchResponce.isSuccess = false;
				
			} else {
				synchResponce.errorMessage = "unknow error";
				synchResponce.isSuccess = false;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			synchResponce.errorMessage = e.getLocalizedMessage();
			synchResponce.isSuccess = false;
		}
		return synchResponce;
	}
	
	
	
	//four sqaure search venues by name 
		public List<VenueModel> searchVenueByName(Location location,String query) {
			mLocation = location;
			return parseSearchResponse(executeOnSeperateClient(new HttpGet(getVenuesSearchUrl(query))));
		}
		
		private List<VenueModel> parseSearchResponse(String response) {

			List<VenueModel> listOfBars = null;
			
			if (response == null || isCanceled()) {
				synchResponce.errorMessage = getError();
				synchResponce.isSuccess = false;
				return null;
			}

			try {
				
				JSONObject jsonObject = new JSONObject(response);
				if(!jsonObject.isNull(DATA)) {
					listOfBars = new ArrayList<VenueModel>();
					JSONArray dataJsonArray = jsonObject.getJSONArray(DATA);
					for (int index = 0; index < dataJsonArray.length(); index++) {
						
						if (isCanceled()) {
							synchResponce.isSuccess = false;
							synchResponce.errorMessage = getError();
							break;
						}
						
						JSONObject venue = dataJsonArray.getJSONObject(index);
						if (!venue.isNull(VENUE_ID)) {
							
							VenueModel venueModel = new VenueModel();
							venueModel.setVenueId(venue.getString(VENUE_ID));
							venueModel.setPlaceProvider(VenueModel.FACEBOOK);
							
							if(igMediaRequest.fetchInstgramIdForSearchedVenue(venueModel)){
								igMediaRequest.fetchInstgramMediaSearch(venueModel);
								Atn.Venue.mergeSearchedVenue(venueModel, getContext());
								if (!venue.isNull(VENUE_NAME))
									venueModel.setVenueName(venue.getString(VENUE_NAME));
								
								if (!venue.isNull(LOCATION)) {
									
									JSONObject locObj = venue.getJSONObject(LOCATION);
									StringBuffer address = new StringBuffer();
									if(!locObj.isNull(STREET)) {
										address.append(locObj.getString(STREET));
									}
									if(!locObj.isNull(ZIP)) {
										address.append(" "+locObj.getString(ZIP));
									}
									venueModel.setStreetAddress(address.toString());
									address.setLength(0);
									
									if(!locObj.isNull(CITY))
										address.append(locObj.getString(CITY));
									
									if(!locObj.isNull(STATE))
										address.append(" "+locObj.getString(STATE));
									
									if(!locObj.isNull(STATE))
										address.append(" "+locObj.getString(STATE));
										
									if(!locObj.isNull(COUNTRY))
										address.append(" "+locObj.getString(COUNTRY));
									
									if (address.length()>0) {
										venueModel.setAddress(address.toString());
									}

									if (!locObj.isNull(LAT)) {
										venueModel.setLat(locObj.getString(LAT));
									}

									if (!locObj.isNull(LNG)) {
										venueModel.setLng(locObj.getString(LNG));
									}
								}
								listOfBars.add(venueModel);
							}
						}
						//search limit is 10
						if(listOfBars.size()==10){
							break;
						}
					}
					
				} else if(!jsonObject.isNull("error")) {
					
					JSONObject errObj = jsonObject.getJSONObject("error");
					synchResponce.errorMessage = errObj.getString("message");
					synchResponce.errorMessage = synchResponce.errorMessage+"\n "+errObj.getString("type");
					synchResponce.errorMessage = synchResponce.errorMessage+"\n "+errObj.getString("code");
					synchResponce.isSuccess = false;
					
				} else {
					synchResponce.errorMessage = "unknow error";
					synchResponce.isSuccess = false;
				}

			} catch (JSONException e) {
				e.printStackTrace();
				synchResponce.errorMessage = e.getLocalizedMessage();
				synchResponce.isSuccess = false;
			}
			return listOfBars;
		}
	
}
