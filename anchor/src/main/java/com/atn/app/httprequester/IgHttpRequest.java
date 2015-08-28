package com.atn.app.httprequester;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.instagram.InstagramAppData;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;

public class IgHttpRequest extends AtnHttpRequest {

		public boolean isFilterOnDateRange = false;
		private int placesProvider = VenueModel.FOUR_SQUARE;
		
		// Instagram location api.
		private static final  String INSTAGRAM_LOCATION_API = "api.instagram.com";
		// Local variables for the web service.
		private static final String IG_V1 = "v1";
		private static final String IG_LOCATIONS = "locations";
		private static final String IG_SERAHC = "search";
		private static final String FORSQUARE_ID = "foursquare_v2_id";
		private static final String FACEBOOK_ID = "facebook_places_id";
		private static final String ACCESS_TOKEN = "access_token";
		private static final String DISTANCE = "distance";
		private static final String DISTANCE_VALUE = "500";
		
		
		public void setPlacesProvider(int placesProvider) {
			this.placesProvider = placesProvider;
		}


		private String getInstagramIdUrl(String venueId) {

			Uri.Builder uri = HttpUtility.buildGetMethod("https",INSTAGRAM_LOCATION_API);
			uri.appendPath(IG_V1).appendPath(IG_LOCATIONS).appendPath(IG_SERAHC);
			
			switch (placesProvider) {
			case VenueModel.FOUR_SQUARE:
				uri.appendQueryParameter(FORSQUARE_ID, venueId);
				break;
			case VenueModel.FACEBOOK:
				uri.appendQueryParameter(FACEBOOK_ID, venueId);
				break;
			default:
				uri.appendQueryParameter(FORSQUARE_ID, venueId);
				break;
			}
			
			uri.appendQueryParameter(DISTANCE, DISTANCE_VALUE);
			if (UserDataPool.getInstance().isUserLoggedIn()&&!TextUtils.isEmpty(UserDataPool.getInstance().getUserDetail().getUserInstagramToken())) {
				uri.appendQueryParameter(ACCESS_TOKEN, UserDataPool.getInstance()
						.getUserDetail().getUserInstagramToken().trim());
			} else {
				uri.appendQueryParameter(InstagramAppData.ID,InstagramAppData.CLIENT_ID);
			}
			return uri.build().toString();
		}

		private static String INSTAGRAM_MEDIA_API = "api.instagram.com";
		
		private  String getIgMediaIdUrl(String igId) {

			Uri.Builder uri = HttpUtility.buildGetMethod("https",INSTAGRAM_MEDIA_API);
			uri.appendPath(IG_V1).appendPath(IG_LOCATIONS).appendPath(igId).appendPath("media").appendPath("recent");
			uri.appendQueryParameter(InstagramAppData.ID,InstagramAppData.CLIENT_ID);
			
//			if(isFilterOnDateRange) {
//				uri.appendQueryParameter("min_timestamp", ""+AtnUtils.getMinTimeStampForIgMedia())
//				  .appendQueryParameter("max_timestamp", ""+AtnUtils.getMaxTimeStampForIgMedia());
//			}
			
			if (!TextUtils.isEmpty(InstagramSession.getToken(getContext()))) {
				uri.appendQueryParameter(ACCESS_TOKEN, InstagramSession.getToken(getContext()));
			}
			
			String ul = uri.build().toString();
			//AtnUtils.log(ul);
			return ul;
		}
	
		
		public IgHttpRequest(Context context) {
			super(context);
		}
		
		
		/**
		 * Fetch instagram id by foursquare id
		 * @fourSv2Id fs venue id
		 * */
		public ContentValues fetchInstagramVenueId(String otherSocialId) {
			
			try {
				String result = executeRequest(new HttpGet(getInstagramIdUrl(otherSocialId)));
				if (isCanceled()) {
					synchResponce.isSuccess = false;
					synchResponce.errorMessage = getError();
					return null;
				}
				
				if (result == null) return null;
				
				JSONObject igLocObj = new JSONObject(result);
				int code = igLocObj.getJSONObject("meta").getInt("code");
				if (code == 200) {
					JSONArray jsonArray = igLocObj.getJSONArray("data");
					if (jsonArray.length() > 0) {
						JSONObject dataObject = jsonArray.getJSONObject(0);
						if (!dataObject.isNull(VenueModel.IG_LOCATION_ID)) {
							ContentValues contentValue = new ContentValues();
							contentValue.put(Atn.Venue.INSTAGRAM_ID,
											dataObject.getString(VenueModel.IG_LOCATION_ID));

							if (!dataObject.isNull(VenueModel.IG_LOC_NAME)) {
								contentValue.put(Atn.Venue.IG_LOC_NAME,
												dataObject.getString(VenueModel.IG_LOC_NAME));
							}

							if (!dataObject.isNull(VenueModel.IG_LAT)) {
								contentValue.put(Atn.Venue.IG_LAT,
										dataObject.getString(VenueModel.IG_LAT));
							}

							if (!dataObject.isNull(VenueModel.IG_LNG)) {
								contentValue.put(Atn.Venue.IG_LNG,
										dataObject.getString(VenueModel.IG_LNG));
							}
							contentValue.put(Atn.Venue.VENUE_ID, otherSocialId);
							return contentValue;
						}
					}
				} else {
					String errorMsg = igLocObj.getJSONObject("meta").getString("error_message");
					AtnUtils.logE("Code:"+code+" Msg:"+errorMsg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		// this synch to db too;
		public int fetchInstgramMedia(final String fourSquareId,String instagramId) {
			int result = FAIL;
			//check if instagram id null the try fetch if not found return fail 
			ContentValues values =null;
			if(TextUtils.isEmpty(instagramId)) {
				values = fetchInstagramVenueId(fourSquareId);
				if(values==null) {
					return FAIL;
				} else {
					instagramId = values.getAsString(Atn.Venue.INSTAGRAM_ID);
				}
			}
			
			String mediaResult = executeRequest(new HttpGet(getIgMediaIdUrl(instagramId)));
			try {
				if (isCanceled()) {
					synchResponce.isSuccess = false;
					synchResponce.errorMessage = getError();
					return FAIL;
				}
				
				if (mediaResult == null) return FAIL;

				JSONObject mediaJsonObj = new JSONObject(mediaResult);
				int code = mediaJsonObj.getJSONObject("meta").getInt("code");
				if (code == 200) {

					List<IgMedia> igMediaNewList = new ArrayList<IgMedia>();
					JSONArray mediaArray = mediaJsonObj.getJSONArray("data");
					
					for (int i = 0; i < mediaArray.length(); i++) {
						JSONObject dataObject = mediaArray.getJSONObject(i);
						IgMedia igMedia = new IgMedia(dataObject, instagramId);
						igMedia.setFourSquareId(fourSquareId);
						igMediaNewList.add(igMedia);
						result = SUCCESS;
					}
					
					Atn.InstagramMedia.deleteNotInMedia(igMediaNewList,instagramId, getContext());
					Atn.InstagramMedia.insertMedia(igMediaNewList, getContext());
					//update date last media image
					if(values==null) {
						values = new ContentValues();
					}
					values.put(Atn.Venue.VENUE_ID, fourSquareId);
					values.put(Atn.Venue.INSTAGRAM_ID, instagramId);
					if(igMediaNewList.size()>0) {
						values.put(Atn.Venue.LATEST_MEDIA_DATE, 
								Integer.valueOf(igMediaNewList.get(0).getCreatedDate()));
					} else {
						values.put(Atn.Venue.LATEST_MEDIA_DATE,0);
					}
					
				} else {
					String errorMsg = mediaJsonObj.getJSONObject("meta").getString("error_message");
					AtnUtils.logE("Code:"+code+" Msg:"+errorMsg);
					result = FAIL;
				}
			} catch (JSONException e) {
				AtnUtils.logE("Media parsing fail"+e.getLocalizedMessage());
				e.printStackTrace();
				result = FAIL;
			}
			if(values!=null) {
				Atn.Venue.updateMediaDate(values, getContext());
			}
			return result;
		}
	
		public boolean fetchInstgramIdForSearchedVenue(VenueModel fsVenueBar) {
			try {
				String result = executeRequest(new HttpGet(getInstagramIdUrl(fsVenueBar.getVenueId())));
				if (isCanceled()) {
					synchResponce.isSuccess = false;
					synchResponce.errorMessage = getError();
					return false;
				}
				
				if (result == null) return false;
				
				JSONObject igLocObj = new JSONObject(result);
				int code = igLocObj.getJSONObject("meta").getInt("code");
				if (code == 200) {
					JSONArray jsonArray = igLocObj.getJSONArray("data");
					if (jsonArray.length() > 0) {
						JSONObject dataObject = jsonArray.getJSONObject(0);
						if (!dataObject.isNull(VenueModel.IG_LOCATION_ID)) {
							fsVenueBar.setInstagramLocationId(dataObject.getString(VenueModel.IG_LOCATION_ID));
							if (!dataObject.isNull(VenueModel.IG_LOC_NAME)) {
								fsVenueBar.setiGLocName(dataObject.getString(VenueModel.IG_LOC_NAME));
							}

							if (!dataObject.isNull(VenueModel.IG_LAT)) {
								fsVenueBar.setiGlat(dataObject.getString(VenueModel.IG_LAT));
							}

							if (!dataObject.isNull(VenueModel.IG_LNG)) {
								fsVenueBar.setiGlng(dataObject.getString(VenueModel.IG_LNG));
							}
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
			//this synch to db too;
			public boolean fetchInstgramMediaSearch(VenueModel fsVenueBar) {

				String mediaResult = executeRequest(new HttpGet(getIgMediaIdUrl(fsVenueBar.getInstagramLocationId())));
				try {
					
					if (isCanceled()) {
						synchResponce.isSuccess = false;
						synchResponce.errorMessage = getError();
						return false;
					}
					
					if (mediaResult == null) return false;

					JSONObject mediaJsonObj = new JSONObject(mediaResult);
					int code = mediaJsonObj.getJSONObject("meta").getInt("code");
					if (code == 200) {
						
						JSONArray mediaArray = mediaJsonObj.getJSONArray("data");
						for (int i = 0; i < mediaArray.length(); i++) {
							IgMedia instaMediaObj = new IgMedia();
							instaMediaObj.setInstagramLocationId(fsVenueBar.getInstagramLocationId());
							
							JSONObject dataObject = mediaArray.getJSONObject(i);
							
							instaMediaObj.setInstagramLocationId(fsVenueBar.getInstagramLocationId());
							if (!dataObject.isNull(IgMedia.IMAGE_ID)) {
								instaMediaObj.setImageId(dataObject.getString(IgMedia.IMAGE_ID));
							}

							if (!dataObject.isNull(IgMedia.IMAGE_TIME))
								instaMediaObj.setCreatedDate(dataObject.getString(IgMedia.IMAGE_TIME));
							

							if (!dataObject.isNull(IgMedia.LIKES)) {
								if (!dataObject.getJSONObject(IgMedia.LIKES).isNull(IgMedia.LIKES_COUNT)) {
									instaMediaObj.setLikesCount(dataObject.getJSONObject(IgMedia.LIKES)
											.getInt(IgMedia.LIKES_COUNT));
								}
							}

							if (!dataObject.isNull(IgMedia.IMAGE_TAGS)) {
								JSONArray hashTagArray = dataObject.getJSONArray(IgMedia.IMAGE_TAGS);
								String hashTag = "";
								for (int j = 0; j < hashTagArray.length(); j++)
									hashTag += "#" + hashTagArray.getString(j) + " ";

								instaMediaObj.setHashTag(hashTag);
							}

							if (!dataObject.isNull(IgMedia.IMAGE)) {
								if (!dataObject.getJSONObject(IgMedia.IMAGE).isNull(IgMedia.IMAGE_LOW_RES)) {
									if (!dataObject.getJSONObject(IgMedia.IMAGE).getJSONObject(IgMedia.IMAGE_LOW_RES).isNull(IgMedia.IMAGE_URL)) {
										instaMediaObj.setImageUrl(dataObject.getJSONObject(IgMedia.IMAGE)
												.getJSONObject(IgMedia.IMAGE_LOW_RES)
												.getString(IgMedia.IMAGE_URL));
									}
								}

								if (!dataObject.getJSONObject(IgMedia.IMAGE).isNull(IgMedia.IMAGE_THUMB)) {
									if (!dataObject.getJSONObject(IgMedia.IMAGE).getJSONObject(IgMedia.IMAGE_THUMB).isNull(IgMedia.IMAGE_URL)) {
										instaMediaObj.setThumbnailUrl(dataObject.getJSONObject(IgMedia.IMAGE)
												.getJSONObject(IgMedia.IMAGE_THUMB)
												.getString(IgMedia.IMAGE_URL));
									}
								}
							}

							if (!dataObject.isNull(IgMedia.MEDIA_LOCATION)) {
								
								JSONObject locOBj = dataObject.getJSONObject(IgMedia.MEDIA_LOCATION);
								if (!locOBj.isNull(IgMedia.LOC_NAME)) {
									instaMediaObj.setLocationName(locOBj.getString(IgMedia.LOC_NAME));
								}
								
								if (!locOBj.isNull(IgMedia.LOC_NAME)) {
									instaMediaObj.setLat(locOBj.getString(IgMedia.LAT));
								}
								
								if (!locOBj.isNull(IgMedia.LOC_NAME)) {
									instaMediaObj.setLng(locOBj.getString(IgMedia.LNG));
								}
							}
							fsVenueBar.addInstagramMedia(instaMediaObj);
						}
						return true;
					} else {
						String errorMsg = mediaJsonObj.getJSONObject("meta").getString("error_message");
						AtnUtils.logE("Code:"+code+" Msg:"+errorMsg);
						return false;
					}
				} catch (JSONException e) {
					AtnUtils.logE("Media parsing fail"+e.getLocalizedMessage());
					e.printStackTrace();
				}
				return false;
			}
}
