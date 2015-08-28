package com.atn.app.httprequester;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;

import com.atn.app.constants.ATNConstants;
import com.atn.app.datamodels.AnchorCategory;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.db.customloader.FsVanueLoader;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.SharedPrefUtils;

public class FsHttpRequest extends AtnHttpRequest {

	private ArrayList<AnchorCategory> categoryList = null;
	private IgHttpRequest igMediaRequest;
	public boolean isForTravel = false;
	public IgHttpRequest getIgMediaRequester() {
		return igMediaRequest;
	}
	
	private int screenWidth = 320; //default
	/**
	 * Foursquare client details that are used to fetch details from Foursquare
	 * server.
	 */
	
	private static final String FOURSQUARE_CLIENT_ID = "101PXDN3IRTGUPEDII4OK2WY2CSCNFB5JIXOTS31S4QIAGWM";
	private static final String FOURSQUARE_CLIENT_SECRET = "TJYMLFS4IIWSBE52CKQWER3PO1Q0IDLRK4CJAUHM4XEGNOQ5";

	private static final String V2 = "v2";
	private static final String VANUES = "venues";
	private static final String EXPLORE = "explore";
	private final  static String FOURSQUARE_API = "api.foursquare.com";
	private static final String SEARCH = "search";
	public static  final  String SERACH_RADIUS_VALUE = "32190";
	private static final String LIMIT = "limit";
	private static final String LIMIT_VALUE = "10";
	
	private final static String CATEGORIES = "categories";
	
	private final static String RADIUS = "radius";
	private final static String CLIENT_ID = "client_id";
	private final static String LOCALE = "locale";
	private final static String CLIENT_SECRET = "client_secret";
	private final static String AREA_LIMIT = "limit";
	private final static String LAT_LNG = "ll";
	private final static String VERSION = "v";
	private final static String SECTION = "section";
	private final static String OFFSET = "offset";

	/**
	 * Local variable's data
	 */
	public final static int RADIUS_VALUE = 8047;
	private final static String LOCALE_VALUE = "en";
	private final static int AREA_LIMIT_VALUE = 50;
	private final String VERSION_VALUE = AtnUtils.getTwoDaysOldDate();// "20140212";
	

	private Location mLocation = null;
	private int offSet;
	private int totalResult;

	
	    ///json venue keys
	    //json keys for foursaure
		
		public static final int CODE_SUCCESS_VALUE = 200;
		public static final String CODE = "code";
		public static final String META = "meta";
		public static final String RESPONSE = "response";
	
		public static final String VENUE = "venue";
		public static final String VENUE_ID = "id";
		public static final String VENUE_NAME = "name";
		public static final String CONTACT = "contact";
		public static final String CONTACT_PHONE = "phone";
		public static final String LOCATION = "location";
		public static final String ADDRESS = "address";
		public static final String ADDRESS_STREET = "crossStreet";
		public static final String LAT = "lat";
		public static final String LNG = "lng";
		public static final String TOTAL_RESULTS = "totalResults";
		public static final String GROUPS = "groups";
		public static final String ITEMS = "items";
		
		///optional
		public static final String FORMATTED_ADDRESS = "formattedAddress";
		
	
		///for image
		public static final String PHOTOS = "photos";
		public static final String COUNT = "count";
		public static final String PREFIX = "prefix";
		public static final String SUFFIX = "suffix";
		
		
		private String getSectionValue() {
			//return "Transportation Services";
			//,Nightlife Spot,Food,Outdoors & Recreation,Arts & Entertainment,
			return "Travel & Transport,Outdoors & recreation,Nightlife Spot, Food, Event, Arts & Entertainment";
		}
		
	//foursqaure apis
	private String getFourSquareUrl() {
		String latlng = new DecimalFormat("#.0000000").format(mLocation
				.getLatitude())
				+ ","
				+ new DecimalFormat("#.0000000").format(mLocation.getLongitude());

		Uri.Builder uri = HttpUtility.buildGetMethod("https", FOURSQUARE_API);
		uri.appendPath(V2).appendPath(VANUES).appendPath(EXPLORE);

		uri.appendQueryParameter(SECTION, getSectionValue())
				.appendQueryParameter(RADIUS, RADIUS_VALUE+"")
				.appendQueryParameter(CLIENT_ID, FOURSQUARE_CLIENT_ID)
				.appendQueryParameter(CLIENT_SECRET, FOURSQUARE_CLIENT_SECRET)
				.appendQueryParameter(AREA_LIMIT, String.valueOf(AREA_LIMIT_VALUE))
				.appendQueryParameter(LAT_LNG, latlng)
				.appendQueryParameter(VERSION, VERSION_VALUE)
				.appendQueryParameter(OFFSET, offSet + "")
				.appendQueryParameter("venuePhotos", 1 + "");
		String tr = uri.build().toString();
		AtnUtils.log(tr);
		return tr;
	}

	private String getFourSquareSearchUrl(String query) {
		
		String latlng = new DecimalFormat("#.0000000").format(mLocation
				.getLatitude())
				+ ","
				+ new DecimalFormat("#.0000000").format(mLocation.getLongitude());

		Uri.Builder uri = HttpUtility.buildGetMethod("https", FOURSQUARE_API);
		uri.appendPath(V2).appendPath(VANUES).appendPath(SEARCH);

		uri.appendQueryParameter("query", query)
				.appendQueryParameter(RADIUS, SERACH_RADIUS_VALUE)
				.appendQueryParameter(CLIENT_ID, FOURSQUARE_CLIENT_ID)
				.appendQueryParameter(CLIENT_SECRET, FOURSQUARE_CLIENT_SECRET)
				.appendQueryParameter(LOCALE, LOCALE_VALUE)
				.appendQueryParameter(LAT_LNG, latlng)
				.appendQueryParameter(VERSION, VERSION_VALUE)
				.appendQueryParameter(LIMIT, LIMIT_VALUE)
				.appendQueryParameter("venuePhotos", 1 + "");
		
		String uriSTr = uri.build().toString();
		AtnUtils.log(uriSTr);
		return uriSTr;
	}
	
	
	/**
	 * FourSquare Venue Request for travel and transport
	 * We are using query param for fetching travel venues.
	 * ***/
	//foursqaure apis
		private String getFourSquareUrlForTarvel() {
			String latlng = new DecimalFormat("#.0000000").format(mLocation
					.getLatitude())
					+ ","
					+ new DecimalFormat("#.0000000").format(mLocation.getLongitude());

			Uri.Builder uri = HttpUtility.buildGetMethod("https", FOURSQUARE_API);
			uri.appendPath(V2).appendPath(VANUES).appendPath(EXPLORE);

			uri.appendQueryParameter("query", "travel")
					.appendQueryParameter(RADIUS, RADIUS_VALUE+"")
					.appendQueryParameter(CLIENT_ID, FOURSQUARE_CLIENT_ID)
					.appendQueryParameter(CLIENT_SECRET, FOURSQUARE_CLIENT_SECRET)
					.appendQueryParameter(AREA_LIMIT, String.valueOf(AREA_LIMIT_VALUE))
					.appendQueryParameter(LAT_LNG, latlng)
					.appendQueryParameter(VERSION, VERSION_VALUE)
					.appendQueryParameter(OFFSET, offSet + "")
					.appendQueryParameter("venuePhotos", 1 + "");
			String tr = uri.build().toString();
			AtnUtils.log("For Tarvel:+"+tr);
			return tr;
		}
	
	
	//return url for fetch Canonical url for venue
	private String getCanonicalUrl(String fourSquareId){
		
		Uri.Builder uri = HttpUtility.buildGetMethod("https", FOURSQUARE_API);
		uri.appendPath(V2).appendPath(VANUES).appendPath(fourSquareId);
		uri.appendQueryParameter(VERSION, VERSION_VALUE)
				.appendQueryParameter(CLIENT_ID, FOURSQUARE_CLIENT_ID)
				.appendQueryParameter(CLIENT_SECRET, FOURSQUARE_CLIENT_SECRET);
		String tr = uri.build().toString();
		AtnUtils.log(tr);
		
		return tr;
	}

	
	
	//
	public FsHttpRequest(Context context) {
		super(context);
		igMediaRequest = new IgHttpRequest(context);
		igMediaRequest.setPlacesProvider(VenueModel.FOUR_SQUARE);
		screenWidth = SharedPrefUtils.getScreenWith(getContext());
	}

	//
	public SynchResponce synchVanues(Location location) {
		isForTravel = false;
		igMediaRequest.isFilterOnDateRange = true;
		this.mLocation = location;
		return processRequest(false);
	}
	
	//
	public SynchResponce synchTarvelVanues(Location location) {
		igMediaRequest.isFilterOnDateRange = true;
		isForTravel = true;
		offSet = 0;
		totalResult = 0;
		this.mLocation = location;
		return processRequest(false);
	}

	
	///get result code
	private int resultCode(JSONObject jsonObj){
		try {
			return  jsonObj.getJSONObject(META).getInt(CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ATNConstants.INVALID_RESULT;
	}
	
	/**
	 * Refresh all media 
	 * */
	public SynchResponce refreshVenues() { 
		igMediaRequest.isFilterOnDateRange = true;
		
		Cursor cursor = getContext().getContentResolver().query(
				Atn.Venue.CONTENT_URI,
				new String[] { Atn.Venue.VENUE_ID,
						Atn.Venue.INSTAGRAM_ID,
						Atn.Venue.LAT,
						Atn.Venue.LNG,
						Atn.Venue.FOLLOWED}, null, null, Atn.Venue.DEFAULT_SORT_ORDER);
		
		//Atn.Venue.FOLLOWED+" <= ?", new String[]{VenueModel.NON_ATN_BAR_FAV+""}
		Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
		//check current location
		if(currntLoc==null) {
			synchResponce.errorMessage = "Current Location not found!";
			synchResponce.isSuccess = false;
			return synchResponce;
		}
		
		Location meLoc = new Location("");
		
		if (cursor != null && cursor.getCount() > 0) {
			ArrayList<String> venueIdToDelete = new ArrayList<String>();
			cursor.moveToFirst();
			do {
				
				if(isCanceled()) {
					cursor.close();
					synchResponce.errorMessage = "Canceled";
					synchResponce.isSuccess = false;
					return synchResponce;	
				}
				
				meLoc.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT))));
				meLoc.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG))));
				String fourSquareId = cursor.getString(cursor.getColumnIndex(Atn.Venue.VENUE_ID));
				
				//TODO: Rohit Delete from database.
				//delete if not in range and not favorite, not associate with any Anchor Bar  
				if((currntLoc.distanceTo(meLoc)>FsHttpRequest.RADIUS_VALUE) && 
						(cursor.getInt(cursor.getColumnIndex(Atn.Venue.FOLLOWED))==VenueModel.NON_ATN_BAR) ) {
					AtnUtils.log("Delete:"+fourSquareId);
					venueIdToDelete.add(fourSquareId);
					continue;
				}
				
				String iGLocId = cursor.getString(cursor.getColumnIndex(Atn.Venue.INSTAGRAM_ID));
				igMediaRequest.fetchInstgramMedia(fourSquareId,iGLocId);
				
			} while (cursor.moveToNext());
			
			if(venueIdToDelete.size()>0) {
				//removed followed venues
				venueIdToDelete.removeAll(Atn.Venue.followedVenuesId(getContext()));				
				ArrayList<ContentProviderOperation> venuesToBeDelete = new ArrayList<ContentProviderOperation>();
				
				for (String veneuId : venueIdToDelete) {

					venuesToBeDelete.add(ContentProviderOperation
							.newDelete(Atn.Venue.CONTENT_URI)
							.withSelection(Atn.Venue.VENUE_ID + " = ? ",
									new String[] { veneuId }).build());
					
					venuesToBeDelete.add(ContentProviderOperation
							.newDelete(Atn.InstagramMedia.CONTENT_URI)
							.withSelection(Atn.InstagramMedia.FOUR_SQUARE_ID + " = ? ",
									new String[] { veneuId }).build());
				}
				Atn.Venue.batchOperation(venuesToBeDelete, getContext());
			}
			
		} else {
			synchResponce.errorMessage = "nothing to refresh";
		}
		if(cursor!=null) cursor.close();
		return synchResponce;
	}

	private SynchResponce processRequest(boolean increaseOffset) {
		if (offSet > totalResult || (!synchResponce.isSuccess) || isCanceled()) {
			return synchResponce;
		}
		if (increaseOffset) {
			offSet += AREA_LIMIT_VALUE;
		}
		
		return synchFourSquareVenue(isForTravel?executeRequest(new HttpGet(getFourSquareUrlForTarvel())):executeRequest(new HttpGet(getFourSquareUrl())));
	}

	///save or update foursqaure venues.
	private SynchResponce synchFourSquareVenue(String response) {

		if (response == null || isCanceled()) {
			synchResponce.errorMessage = getError();
			synchResponce.isSuccess = false;
			return synchResponce;
		}

		try {
			JSONObject jsonObject = new JSONObject(response);
			if (resultCode(jsonObject) == CODE_SUCCESS_VALUE) {
				jsonObject = jsonObject.getJSONObject(RESPONSE);
				totalResult = jsonObject.getInt(TOTAL_RESULTS);
				JSONArray groups = jsonObject.getJSONArray(GROUPS);
				
				for (int groupIndex = 0; groupIndex < groups.length(); groupIndex++) {
					JSONObject group = groups.getJSONObject(groupIndex);
					JSONArray items = group.getJSONArray(ITEMS);
					for (int itemIndex = 0; itemIndex < items.length(); itemIndex++) {
						
						if (isCanceled()) {
							synchResponce.isSuccess = false;
							synchResponce.errorMessage = getError();
							break;
						}
						
						JSONObject item = items.getJSONObject(itemIndex);
						JSONObject venue = item.getJSONObject(VENUE);
						
						if (!venue.isNull(VENUE_ID)) {
							
							String fVenueId = venue.getString(VENUE_ID);
							String subCateID = "";
							int categoryId = ATNConstants.INVALID_RESULT;
							if(!venue.isNull(CATEGORIES)) {
								JSONArray catArray = venue.getJSONArray(CATEGORIES);
								if(catArray.length()>0) {
									subCateID = catArray.getJSONObject(0).getString("id");
									categoryId = getCategoryId(subCateID);
								}
							}
							
							if(categoryId==ATNConstants.INVALID_RESULT) continue; 
							
								ContentValues venueContent = new ContentValues();
						
								venueContent.put(Atn.Venue.VENUE_ID,fVenueId);
								venueContent.put(Atn.Venue.CATEGORY,categoryId);
								venueContent.put(Atn.Venue.SUB_CATEGORY,subCateID);
								
								

								if (!venue.isNull(VENUE_NAME))
									venueContent.put(Atn.Venue.VENUE_NAME,venue.getString(VENUE_NAME));

								if (!venue.isNull(CONTACT)) {
									if (!venue.getJSONObject(CONTACT).isNull(CONTACT_PHONE)) {
										venueContent.put(Atn.Venue.CONTACT,
														venue.getJSONObject(CONTACT).getString(CONTACT_PHONE));
									}
								}

								if (!venue.isNull(LOCATION)) {
									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS)) {
										venueContent.put(Atn.Venue.ADDRESS,venue.getJSONObject(LOCATION)
																.getString(ADDRESS));
									} else if(!venue.getJSONObject(LOCATION).isNull(FORMATTED_ADDRESS)) {
										String address = venue.getJSONObject(LOCATION).getString(FORMATTED_ADDRESS); 
										if (!TextUtils.isEmpty(address)) {
											address = address.replace("[", "").replace("]", "").replace("\"", "");
											venueContent.put(Atn.Venue.ADDRESS,address);
										}
									}

									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS_STREET)) {
									venueContent.put(Atn.Venue.ADDRESS_STREET,
													venue.getJSONObject(LOCATION)
													.getString(ADDRESS_STREET));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LAT)) {
										venueContent.put(Atn.Venue.LAT,venue.getJSONObject(LOCATION).getString(LAT));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LNG)) {
										venueContent.put(Atn.Venue.LNG,venue.getJSONObject(LOCATION).getString(LNG));
									}
								}
								 
								///save single photo.
								if(!venue.isNull(PHOTOS)&&venue.getJSONObject(PHOTOS).getInt(COUNT)>0) {
									if(!venue.getJSONObject(PHOTOS).isNull(GROUPS)&&venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).length()>0){
										JSONObject picGroup = venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).getJSONObject(0).getJSONArray(ITEMS).getJSONObject(0);
										String url = picGroup.getString(PREFIX)+screenWidth+picGroup.getString(SUFFIX);
										venueContent.put(Atn.Venue.PHOTO,url);
									}
								}
							 venueContent.put(Atn.Venue.ATN_PLACE_PROVIDER,VenueModel.FOUR_SQUARE);
							 Atn.Venue.insertOrUpdate(venueContent, getContext());
							 //InstagramImageLoader.loader.addVenue(fVenueId,null);
						}
					}
				}
				processRequest(true);
			}else{
				setErrorFromJson(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			synchResponce.errorMessage = e.getLocalizedMessage();
			synchResponce.isSuccess = false;
		}
		return synchResponce;
	}

	//populate category id one and return anchor category id.
	private int getCategoryId(String fourSquareId) {
		if(categoryList==null) {
			categoryList = Atn.Category.populateCategories(getContext(),false);
		}
		for (AnchorCategory anchorCategory : categoryList) {
			if(anchorCategory.foursquareId.equals(fourSquareId)){
				return anchorCategory.categoryId;
			}
		}
		return ATNConstants.INVALID_RESULT;
	}
	
	//only update instagram id not whole object
	public boolean refreshVenue(VenueModel venueModel) {
		igMediaRequest.isFilterOnDateRange = false;
		int result = igMediaRequest.fetchInstgramMedia(venueModel.getVenueId(),
				venueModel.getInstagramLocationId());
		return result!=FAIL;
	}
	
	//four sqaure search venues by name 
	public ArrayList<VenueModel> searchVenueByName(Location location,String query) {
		mLocation = location;
		return parseSearchResponse(executeOnSeperateClient(new HttpGet(getFourSquareSearchUrl(query))));
	}
	
	private ArrayList<VenueModel> parseSearchResponse(String response) {

		AtnUtils.log(response);
		ArrayList<VenueModel> listOfBars = null;
		
		if (response == null || isCanceled()) {
			synchResponce.errorMessage = getError();
			synchResponce.isSuccess = false;
			return null;
		}

		try {
			JSONObject jsonObject = new JSONObject(response);
			if (resultCode(jsonObject)==CODE_SUCCESS_VALUE) {
				
				jsonObject = jsonObject.getJSONObject("response");
				JSONArray items = jsonObject.getJSONArray("venues");
				
					if(items.length()>0) {
						listOfBars = new ArrayList<VenueModel>();
					} else {
						synchResponce.errorMessage = "Venues not found";
						synchResponce.isSuccess = false;
					}
					for (int itemIndex = 0; itemIndex < items.length(); itemIndex++) {
						
						if (isCanceled()) {
							synchResponce.isSuccess = false;
							synchResponce.errorMessage = getError();
							break;
						}
						
						VenueModel fsVenueBar = new VenueModel();
						JSONObject venue = items.getJSONObject(itemIndex);
						if (!venue.isNull(VENUE_ID)) {
							String fVenueId = venue.getString(VENUE_ID);
							String subCateId="";
							int atnCatId = ATNConstants.INVALID_RESULT;
							if(!venue.isNull(CATEGORIES)) {
								JSONArray catArray = venue.getJSONArray(CATEGORIES);
								if(catArray.length()>0) {
									subCateId = catArray.getJSONObject(0).getString("id");
									atnCatId = Atn.Category.getVenueCategory(subCateId,getContext());
								}
							}
							
							if(atnCatId== ATNConstants.INVALID_RESULT) continue;
							
							fsVenueBar.setVenueSubCategoryId(subCateId);
							fsVenueBar.setVenueCategoryId(atnCatId);
							fsVenueBar.setVenueId(fVenueId);
							fsVenueBar.setPlaceProvider(VenueModel.FOUR_SQUARE);

								if (!venue.isNull(VENUE_NAME))
									fsVenueBar.setVenueName(venue.getString(VENUE_NAME));

								if (!venue.isNull(CONTACT)) {
									if (!venue.getJSONObject(CONTACT).isNull(CONTACT_PHONE)) {
										fsVenueBar.setPhone(venue.getJSONObject(CONTACT)
																.getString(CONTACT_PHONE));
									}
								}

								if (!venue.isNull(LOCATION)) {
									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS)) {
										fsVenueBar.setAddress(venue.getJSONObject(LOCATION)
																.getString(ADDRESS));
									} else if(!venue.getJSONObject(LOCATION).isNull(FORMATTED_ADDRESS)) {
										String address = venue.getJSONObject(LOCATION).getString(FORMATTED_ADDRESS); 
										if (!TextUtils.isEmpty(address)) {
											address = address.replace("[", "").replace("]", "").replace("\"", "");
											fsVenueBar.setAddress(address);
										}
									}

									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS_STREET)) {
										fsVenueBar.setStreetAddress(venue.getJSONObject(LOCATION)
																.getString(ADDRESS_STREET));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LAT)) {
										fsVenueBar.setLat(venue.getJSONObject(LOCATION)
												.getString(LAT));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LNG)) {
										fsVenueBar.setLng(venue.getJSONObject(LOCATION)
												.getString(LNG));
									}
								}
								///save single photo.
								if(!venue.isNull(PHOTOS)&&venue.getJSONObject(PHOTOS).getInt(COUNT)>0) {
									if(!venue.getJSONObject(PHOTOS).isNull(GROUPS)&&venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).length()>0){
										JSONObject picGroup = venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).getJSONObject(0).getJSONArray(ITEMS).getJSONObject(0);
										String url = picGroup.getString(PREFIX)+screenWidth+picGroup.getString(SUFFIX);
										fsVenueBar.setPhoto(url);
									}
								}
								
								//cal distance
								fsVenueBar.getDistance();
								
								listOfBars.add(fsVenueBar);
						}
					}
			} else {
				synchResponce.errorMessage = "Venues not found";
				synchResponce.isSuccess = false;
				setErrorFromJson(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			synchResponce.errorMessage = e.getLocalizedMessage();
			synchResponce.isSuccess = false;
		}
		return listOfBars;
	}
	

		//Fetch Canonical url....
	public String fetchCanonicalUrl(final String fourSuareId) {

		String result = executeOnSeperateClient(new HttpGet(getCanonicalUrl(fourSuareId)));
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (resultCode(jsonObject)==CODE_SUCCESS_VALUE) {
					if(!jsonObject.isNull(RESPONSE)&&!jsonObject.getJSONObject(RESPONSE).isNull(VENUE)){
						JSONObject responce = jsonObject.getJSONObject(RESPONSE).getJSONObject(VENUE);
						if(!responce.isNull(VenueModel.CONANICAL_URL)){
							String url = responce.getString(VenueModel.CONANICAL_URL);
							ContentValues values = new ContentValues();
							values.put(Atn.Venue.CANONICAL_URL, url);
							values.put(Atn.Venue.VENUE_ID, fourSuareId);
							Atn.Venue.update(values, getContext());
							return url;
						}
						this.error  = "URL Not Found!";
					}
					this.error  = "Venue Not Found!";
				} else {
					setErrorFromJson(jsonObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.error = e.getLocalizedMessage();
			}
		}
		return null;
	}

	/*
	 * pass json object will set error from that
	 * ***/
	private void setErrorFromJson(JSONObject jsonObject) throws JSONException {
	
		if (!jsonObject.getJSONObject(META).isNull("errorType")) {
			this.error = this.error+jsonObject.getJSONObject(META).getString("errorType");
		}
		if (!jsonObject.getJSONObject(META).isNull("errorDetail")) {
			this.error = this.error+" \n "+jsonObject.getJSONObject(META).getString("errorDetail");
		}
		AtnUtils.log(this.error);
	}
	
	@Override
	public void setCanceled(boolean isCanceled) {
		super.setCanceled(isCanceled);
		igMediaRequest.setCanceled(isCanceled);
	}
	
	///synch with anchor server.
	public void tryToSynchWithAnchorServer() {
		
		Location mLocation = AtnLocationManager.getInstance().getLastLocation();
		if(isCanceled()|| mLocation==null) return;
		
		 Uri.Builder builder = HttpUtility.buildGetMethodWithAppParams().appendPath(ApiEndPoints.GET_VENUE_LIST);
		 builder.appendQueryParameter("lat", new DecimalFormat("#.0000000").format(mLocation.getLatitude()));
		 builder.appendQueryParameter("lon", new DecimalFormat("#.0000000").format(mLocation.getLongitude()));
//		 builder.appendQueryParameter("min_timestamp", ""+AtnUtils.getMinTimeStampForIgMedia())
//		  .appendQueryParameter("max_timestamp", ""+AtnUtils.getMaxTimeStampForIgMedia());
		 String result = executeRequest(new HttpGet(builder.build().toString()));
		
		if (!TextUtils.isEmpty(result)) {
			
			try {
				JSONObject venuesListObject = new JSONObject(result);
				if(JsonUtils.resultCode(venuesListObject)==JsonUtils.ANCHOR_SUCCESS && !venuesListObject.isNull(JsonUtils.RESPONSE)) {
					JSONArray fsVenueList = venuesListObject.getJSONObject(
							JsonUtils.RESPONSE).getJSONArray(JsonUtils.FsVenueAnchorKeys.BUSINESSES);
					if(fsVenueList!=null) {
						for (int i = 0; i < fsVenueList.length(); i++) {
							
							JSONObject anchorFsBar = fsVenueList.getJSONObject(i);
							
							JSONObject businessObj = anchorFsBar
									.getJSONObject(JsonUtils.FsVenueAnchorKeys.NON_ATN_BUSINESS);
							
							ContentValues values = new ContentValues();
							String fsVenueId = businessObj.getString(JsonUtils.FsVenueAnchorKeys.FS_VENUE_ID);
							values.put(Atn.Venue.VENUE_ID, fsVenueId);
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.ANCHOR_ID))
								values.put(Atn.Venue.ATN_BAR_ID, businessObj.getString(JsonUtils.FsVenueAnchorKeys.ANCHOR_ID));
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.NAME))
								values.put(Atn.Venue.VENUE_NAME, businessObj.getString(JsonUtils.FsVenueAnchorKeys.NAME));
							
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.INSTAGRAM_LOCATION_ID))
								values.put(Atn.Venue.INSTAGRAM_ID, businessObj.getString(JsonUtils.FsVenueAnchorKeys.INSTAGRAM_LOCATION_ID));
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.LAT))
								values.put(Atn.Venue.LAT, businessObj.getString(JsonUtils.FsVenueAnchorKeys.LAT));
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.LON))
								values.put(Atn.Venue.LNG, businessObj.getString(JsonUtils.FsVenueAnchorKeys.LON));
							
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.PHOTO))
								values.put(Atn.Venue.PHOTO, businessObj.getString(JsonUtils.FsVenueAnchorKeys.PHOTO));
							
							if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.ADDRESS))
								values.put(Atn.Venue.ADDRESS, businessObj.getString(JsonUtils.FsVenueAnchorKeys.ADDRESS));
							
							
							if (!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.fS_VENUE_CATEGORY))
								values.put(Atn.Venue.CATEGORY,Atn.Category.getVenueCategory(businessObj.getString(JsonUtils.FsVenueAnchorKeys.fS_VENUE_CATEGORY),
												getContext()));
							
							
							int commentCount = anchorFsBar
									.isNull(JsonUtils.FsVenueAnchorKeys.COMMENT_COUNT) ? 0
									: anchorFsBar.getInt(JsonUtils.FsVenueAnchorKeys.COMMENT_COUNT);
							
							values.put(Atn.Venue.COMMENT_COUNT, commentCount);
							
							int reviewCount = anchorFsBar
									.isNull(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT) ? 0
									: anchorFsBar.getInt(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT);
							values.put(Atn.Venue.REVEIW_COUNT, reviewCount);
							
							double rating = anchorFsBar
									.isNull(JsonUtils.FsVenueAnchorKeys.RATING) ? 0
									: anchorFsBar.getDouble(JsonUtils.FsVenueAnchorKeys.RATING);
							values.put(Atn.Venue.RATING, rating);
							
							if(!anchorFsBar.isNull(JsonUtils.ReviewTagKey.HASH_TAG)) {
								JSONArray hashTagArray = anchorFsBar.getJSONArray(JsonUtils.ReviewTagKey.HASH_TAG);
								if(hashTagArray!=null&&hashTagArray.length()>0) {
									Atn.ReviewTable.insertOrUpdate(getContext(), hashTagArray, fsVenueId);	
								}
							}
							
							long lastMediaDate = 0;
							//insert pictures
							if(!anchorFsBar.isNull(JsonUtils.AnchorMediaKeys.VENUE_PICTURE)) {
								JSONArray pictureArray = anchorFsBar.getJSONArray(JsonUtils.AnchorMediaKeys.VENUE_PICTURE);
								if(pictureArray!=null&&pictureArray.length()>0) {
									lastMediaDate = Atn.InstagramMedia.insertAnchorMedia(getContext(), pictureArray, fsVenueId);	
								}
							}
							
							if(lastMediaDate>0) {
								values.put(Atn.Venue.LATEST_MEDIA_DATE, lastMediaDate);
							}
							Atn.Venue.insertOrUpdate(values, getContext());
						}
					}
				}
			} catch (Exception e) {
				AtnUtils.log(e.getLocalizedMessage());
			}
		} 
	}
	
	
	///fetch single venue info
	
	public SynchResponce fetchVenueInfo(VenueModel fsVenueBar) {
		
		String result = executeOnSeperateClient(new HttpGet(getCanonicalUrl(fsVenueBar.getVenueId())));
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (resultCode(jsonObject)==CODE_SUCCESS_VALUE) {
					if(!jsonObject.isNull(RESPONSE)&&!jsonObject.getJSONObject(RESPONSE).isNull(VENUE)){
						JSONObject venue = jsonObject.getJSONObject(RESPONSE).getJSONObject(VENUE);
						
							String fVenueId = venue.getString(VENUE_ID);
							
							int atnCatId = ATNConstants.INVALID_RESULT;
							if(!venue.isNull(CATEGORIES)) {
								JSONArray catArray = venue.getJSONArray(CATEGORIES);
								if(catArray.length()>0) {
									atnCatId =Atn.Category.getVenueCategory(catArray.getJSONObject(0).getString("id"),getContext()); 
								}
							}
							
							
							if(!venue.isNull(VenueModel.CONANICAL_URL)) {
								String url = venue.getString(VenueModel.CONANICAL_URL);
								fsVenueBar.setCanonicalURL(url);
							}
							
							fsVenueBar.setVenueCategoryId(atnCatId);
							fsVenueBar.setVenueId(fVenueId);
							fsVenueBar.setPlaceProvider(VenueModel.FOUR_SQUARE);

								if (!venue.isNull(VENUE_NAME))
									fsVenueBar.setVenueName(venue.getString(VENUE_NAME));

								if (!venue.isNull(CONTACT)) {
									if (!venue.getJSONObject(CONTACT).isNull(CONTACT_PHONE)) {
										fsVenueBar.setPhone(venue.getJSONObject(CONTACT)
																.getString(CONTACT_PHONE));
									}
								}

								if (!venue.isNull(LOCATION)) {
									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS)) {
										fsVenueBar.setAddress(venue.getJSONObject(LOCATION)
																.getString(ADDRESS));
									} else if(!venue.getJSONObject(LOCATION).isNull(FORMATTED_ADDRESS)) {
										String address = venue.getJSONObject(LOCATION).getString(FORMATTED_ADDRESS); 
										if (!TextUtils.isEmpty(address)) {
											address = address.replace("[", "").replace("]", "").replace("\"", "");
											fsVenueBar.setAddress(address);
										}
									}

									if (!venue.getJSONObject(LOCATION).isNull(ADDRESS_STREET)) {
										fsVenueBar.setStreetAddress(venue.getJSONObject(LOCATION)
																.getString(ADDRESS_STREET));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LAT)) {
										fsVenueBar.setLat(venue.getJSONObject(LOCATION)
												.getString(LAT));
									}

									if (!venue.getJSONObject(LOCATION).isNull(LNG)) {
										fsVenueBar.setLng(venue.getJSONObject(LOCATION)
												.getString(LNG));
									}
								}
								///save single photo.
								if(!venue.isNull(PHOTOS)&&venue.getJSONObject(PHOTOS).getInt(COUNT)>0) {
									if(!venue.getJSONObject(PHOTOS).isNull(GROUPS)&&venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).length()>0){
										JSONObject picGroup = venue.getJSONObject(PHOTOS).getJSONArray(GROUPS).getJSONObject(0).getJSONArray(ITEMS).getJSONObject(0);
										String url = picGroup.getString(PREFIX)+screenWidth+picGroup.getString(SUFFIX);
										fsVenueBar.setPhoto(url);
									}
								}
						
					}
					
				} else {
					setErrorFromJson(jsonObject);
					synchResponce.isSuccess = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.error = e.getLocalizedMessage();
				synchResponce.isSuccess = false;
			}
			}else{
				synchResponce.isSuccess = false;
			}
		return synchResponce;
	}
	
}
