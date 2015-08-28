package com.atn.app.httprequester;

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
import android.text.TextUtils;

import com.atn.app.database.handler.BusinessTable;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.database.handler.PromotionTable;
import com.atn.app.datamodels.AtnBar;
import com.atn.app.datamodels.AtnPromotion.PromotionType;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.ReviewTag;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;

public class AtnBarRequest extends AtnHttpRequest{

	public AtnBarRequest(Context context) {
		super(context);
	}

	///uri fetch promotion
	private String getPromotionsUrl(){
		
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("user");
		uri.appendPath("promotions");
		uri.appendQueryParameter("user_id", UserDataPool.getInstance().getUserDetail().getUserId());
		
		String url = uri.build().toString();
		AtnUtils.log(url);
		return url;
	}
	

	private String getFavoritesUrl(){
		
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("user");
		uri.appendPath("favorites");
		uri.appendQueryParameter("user_id", UserDataPool.getInstance().getUserDetail().getUserId());
		
		String url = uri.build().toString();
		AtnUtils.log(url);
		return url;
	}
	
	
	private String getBusinessUrl(){
		
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("businesses");

		Location currentLocation = AtnLocationManager.getInstance().getLastLocation();
		if(currentLocation!=null) {
			uri.appendQueryParameter("lat", String.valueOf(currentLocation.getLatitude()));
			uri.appendQueryParameter("lon", String.valueOf(currentLocation.getLongitude()));
		}
		uri.appendQueryParameter("radius", String.valueOf(20));
		
		if(UserDataPool.getInstance().isUserLoggedIn()){
			uri.appendQueryParameter("user_id", UserDataPool.getInstance().getUserDetail().getUserId());	
		}
		String url = uri.build().toString();
		AtnUtils.log(url);
		return url;
	}
	
	private String getBarByIdUrl(String businessId){
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("businesses").appendPath("view");
		uri.appendQueryParameter("user_id", UserDataPool.getInstance().getUserDetail().getUserId())
			.appendQueryParameter("business_id", businessId);
		String url = uri.build().toString();
		//AtnUtils.log(""+url);
		return url;
	}
	
	private static final String BUSINESSES = "Businesses";
	private static final String FAVORITES = "Favorites";
	private static final String PROMOTIONS = "Promotions";
	
	public boolean fetchBusiness() {
		return parseAndSaveBar(executeOnSeperateClient(new HttpGet(
				getBusinessUrl())), BUSINESSES);
	}
	
	public boolean fetchFavorites() {
		return parseAndSaveBar(executeOnSeperateClient(new HttpGet(
				getFavoritesUrl())), FAVORITES);
	}
	
	
	public boolean fetchPromotions() {
		return parseAndSaveBar(executeOnSeperateClient(new HttpGet(
				getPromotionsUrl())), PROMOTIONS);
	}

	private String jsonKey = "";
	private boolean parseAndSaveBar(String result,String jsonKey){
		this.jsonKey = jsonKey;
		try {
			if(getResponseCode(result)==0){
				JSONObject jsonOBj = new JSONObject(result).getJSONObject(JsonUtils.RESPONSE);
				if(!jsonOBj.isNull(jsonKey)) {
					clearData(jsonKey);
					JSONArray businessArray = jsonOBj.getJSONArray(jsonKey);
						for (int i = 0; i < businessArray.length(); i++) {
							saveBarInDb(businessArray.getJSONObject(i));
						}
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e.getLocalizedMessage();
		}
		return false;
	}
	
	private void clearData(String jsonKey){
		
		if(jsonKey.equals(BUSINESSES)) {
			//DbHandler.getInstance().deleteBusinessBar();
		}else if(jsonKey.equals(PROMOTIONS)){
			DbHandler.getInstance().deletePromotionsBar();
		}
	}
	
	private  int getResponseCode(String result) throws JSONException{
		if(result!=null){
			JSONObject jsonOBj = new JSONObject(result);
			if(!jsonOBj.isNull(JsonUtils.CODE)){
				int code =jsonOBj.getInt(JsonUtils.CODE);
				if(code!=0){
					this.error = AtnUtils.getErrorDetailFromCode(code);
				}
				return code;
			}
		}
		return INVALIDE_CODE;
	}
	
	
	public boolean fetchBarById(String businessId) {
		String result = executeOnSeperateClient(new HttpGet(getBarByIdUrl(businessId)));
		if (result != null) {
			try {
				if(getResponseCode(result)==0) {
					JSONObject jsonOBj = new JSONObject(result);
					saveBarInDb(jsonOBj.getJSONObject(JsonUtils.RESPONSE));
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.error = e.getLocalizedMessage();
			}
		} else {
			error = "result not found";
		}
		return false;
	}
	
	

	private static final String BUSISENSS = "Business";
	private static final String PROMOTION = "Promotion";
	
	private static final String BUSINESS_ID = "id";
	private static final String BUSINESS_NAME = "name";
	private static final String BUSINESS_STREET = "street";
	private static final String BUSINESS_CITY = "city";
	private static final String BUSINESS_STATE = "state";
	private static final String BUSINESS_ZIP = "zip";
	private static final String BUSINESS_LAT = "lat";
	private static final String BUSINESS_LNG = "lon";
	private static final String BUSINESS_PHONE = "phone";
	private static final String BUSINESS_FB_LINK = "fb_link";
	private static final String BUSINESS_FB_LINK_ID = "fb_link_id";
	private static final String BUSINESS_FS_VENUE_LINK = "fs_venue_link";
	private static final String BUSINESS_FS_VENUE_ID = "fs_venue_id";
	private static final String BUSINESS_IMAGE = "logo";
	private static final String BUSINESS_FAVORITE = "favorited";
	private static final String BUSINESS_SUBSCRIBE = "subscribed";
	private static final String IS_REGISTERED_VENUE = "isATN";
	private static final String BUSINESS_STATUS = "status";
	private static final String BUSINESS_ADDRESS = "address";
	private static final String MODIFIED_DATE = "modified";
	private static final String FS_VENUE_CATEGORY_ID = "fs_venue_category";
	
	//for non atn
	private static final String INSTAGRAM_LOC_ID = "insta_location_id";
	

	private void saveBarInDb(JSONObject businessObject) throws Exception{
		
		JSONObject businessObj = businessObject.getJSONObject(BUSISENSS);
		if(businessObj!=null){
			//default value is true;
			boolean isAtnBar = true;
			int atnBarType = AtnBar.NON_ATN_BAR_FAV;
			
			if (!businessObj.isNull(IS_REGISTERED_VENUE)) {
				isAtnBar = businessObj.getBoolean(IS_REGISTERED_VENUE);
			}
			
			if(isAtnBar) {
				
				ContentValues businessValues = new  ContentValues();
				if (!businessObj.isNull(BUSINESS_ID)) {
					businessValues.put(BusinessTable.COL_BUSINESS_ID,
										businessObj.getString(BUSINESS_ID));
					//remove all promotion first.
					if(this.jsonKey.equals(BUSINESSES)){
						DbHandler.getInstance().deleteBusinessBar(businessObj.getString(BUSINESS_ID));
					}
				}

				if (!businessObj.isNull(BUSINESS_NAME)) {
					businessValues.put(BusinessTable.COL_BUSINESS_NAME,
										businessObj.getString(BUSINESS_NAME));
				}
				
				if (!businessObj.isNull(BUSINESS_STREET)) {
					businessValues.put(BusinessTable.COL_BUSINESS_STREET,
										businessObj.getString(BUSINESS_STREET));
				}
				
				if (!businessObj.isNull(BUSINESS_CITY)) {
					businessValues.put(BusinessTable.COL_BUSINESS_CITY, businessObj
							.getString(BUSINESS_CITY));
				}

				if (!businessObj.isNull(BUSINESS_STATE)) {
					businessValues.put(BusinessTable.COL_BUSINESS_STATE,
							businessObj.getString(BUSINESS_STATE));
				}
				
				if (!businessObj.isNull(BUSINESS_ZIP)) {
					businessValues.put(BusinessTable.COL_BUSINESS_ZIP,
							businessObj.getString(BUSINESS_ZIP));

				}
				
				if (!businessObj.isNull(BUSINESS_LAT)) {
					businessValues.put(BusinessTable.COL_BUSINESS_LAT,
							businessObj.getString(BUSINESS_LAT));
				}
				
				if (!businessObj.isNull(BUSINESS_LNG)) {
					businessValues.put(BusinessTable.COL_BUSINESS_LON, businessObj
							.getString(BUSINESS_LNG));

				}
				
				if (!businessObj.isNull(BUSINESS_PHONE)) {
					businessValues.put(BusinessTable.COL_BUSINESS_PHONE,
							businessObj.getString(BUSINESS_PHONE));
				}

				if (!businessObj.isNull(BUSINESS_FB_LINK)) {
					businessValues.put(BusinessTable.COL_BUSINESS_FB_LINK,
									businessObj.getString(BUSINESS_FB_LINK));
				}
				
				if (!businessObj.isNull(BUSINESS_FB_LINK_ID)) {
					businessValues.put(BusinessTable.COL_BUSINESS_FB_LINK_ID,
							businessObj.getString(BUSINESS_FB_LINK_ID));
				}
				
				if (!businessObj.isNull(BUSINESS_FS_VENUE_LINK)) {
					businessValues.put(BusinessTable.COL_BUSINESS_FS_LINK,businessObj.getString(BUSINESS_FS_VENUE_LINK));
				}
				
				if (!businessObj.isNull(BUSINESS_IMAGE)) {
					businessValues.put(BusinessTable.COL_BUSINESS_LOGO,businessObj.getString(BUSINESS_IMAGE));
				}
				
				if (!businessObj.isNull(BUSINESS_STATUS)) {
					businessValues.put(BusinessTable.COL_BUSINESS_STATUS,AtnUtils.getInt(businessObj
									.getString(BUSINESS_STATUS)));
				}
				
				
				if (!businessObj.isNull(MODIFIED_DATE)) {
					businessValues.put(BusinessTable.COL_BUSINESS_MODIFIED,businessObj.getString(MODIFIED_DATE));
				}
				
				if(!businessObj.isNull(BUSINESS_FAVORITE)){
					atnBarType = AtnBar.ATN_BAR_FAV;
					businessValues.put(BusinessTable.COL_BUSINESS_FAVORITED,true);
				}else{
					businessValues.put(BusinessTable.COL_BUSINESS_FAVORITED,false);
					atnBarType = AtnBar.ATN_BAR;
				}
				
				if(!businessObj.isNull(FS_VENUE_CATEGORY_ID)) {
					businessValues.put(BusinessTable.COL_BUSINESS_FS_CAT_ID, Atn.Category
						.getVenueCategory(businessObj.getString(FS_VENUE_CATEGORY_ID),
								getContext()));
				}
				
				businessValues.put(BusinessTable.COL_BUSINESS_SUBSCRIBED,!businessObj.isNull(BUSINESS_SUBSCRIBE)?true:false);
				businessValues.put(BusinessTable.COL_IS_REGISTERED,isAtnBar);
				
				/**
				 * After parsing data check whether there are any promotions available or not. If promotions
				 * are available then parse the promotion details and add promotion details to the specified
				 * ATN venue.
				 */
				List<IgMedia> igMediaNewList = new ArrayList<IgMedia>();

				long latestMediaTime = 0;
				
				if(!businessObject.isNull(PROMOTION)) {
					JSONArray promotionArray = businessObject.getJSONArray(PROMOTION);
					if (promotionArray.length() > 0) {
						for (int j = 0; j < promotionArray.length(); j++) {
							
							JSONObject promotionObj = promotionArray.getJSONObject(j);
							savePromotion(promotionObj);

							// save Promotion as Media image for showing in Happening now screen if it contain image URL
							if (!promotionObj.isNull("image_nonretina")) {
								
								IgMedia igMedia = new IgMedia(promotionObj);
								igMedia.setFourSquareId(businessObj.getString(BUSINESS_FS_VENUE_ID));
								igMediaNewList.add(igMedia);
								long time = AtnUtils.convertInMillisecond(promotionObj.getString("start_date")) / 1000L;

    						   if(latestMediaTime < time)
    							   latestMediaTime = time;
							
							}
						}
					}

					// save in database
					Atn.InstagramMedia.deleteNotInMedia(igMediaNewList,businessObj.getString(BUSINESS_ID), getContext());
					Atn.InstagramMedia.insertMedia(igMediaNewList, getContext());				}
				
				////check for foursquare id if found then load media
				//MOHAR: Add Media latest time
				String fourSuareId = saveNonAtnBar(businessObject,businessObj, atnBarType,latestMediaTime);
				if(fourSuareId!=null) {
					businessValues.put(BusinessTable.COL_BUSINESS_FS_LINK_ID,fourSuareId);
				}
				
				
				
				DbHandler.getInstance().insertOrUpdate(businessValues);
				
			} else {
				//MOHAR: Add Media latest time as 0
				saveNonAtnBar(businessObject,businessObj, atnBarType,0);
			}
			
		} else {
			throw new  Exception("business not found");
		}
	}

	private String saveNonAtnBar(JSONObject businessObject,JSONObject businessObj,int atnBarType, long latestMediaTime) throws JSONException {
		
		String fourSquareId = null;
		if (!businessObj.isNull(BUSINESS_FS_VENUE_ID)) {
			
				fourSquareId = businessObj.getString(BUSINESS_FS_VENUE_ID);
				ContentValues contentValue = new ContentValues();
				contentValue.put(Atn.Venue.VENUE_ID, fourSquareId);
				
				if (!businessObj.isNull(BUSINESS_ID)) {
					contentValue.put(Atn.Venue.ATN_BAR_ID, businessObj.getString(BUSINESS_ID));
				}
				
				if (!businessObj.isNull(BUSINESS_NAME)) {
					contentValue.put(Atn.Venue.VENUE_NAME,businessObj.getString(BUSINESS_NAME));
				}
				
				if (!businessObj.isNull(BUSINESS_ADDRESS)) {
					contentValue.put(Atn.Venue.ADDRESS_STREET, businessObj.getString(BUSINESS_ADDRESS));
				}
				
				if (!businessObj.isNull(BUSINESS_ADDRESS)) {
					contentValue.put(Atn.Venue.ADDRESS,  businessObj.getString(BUSINESS_ADDRESS));
				}

				
				if (!businessObj.isNull(BUSINESS_LAT)) {
					contentValue.put(Atn.Venue.LAT, businessObj.getString(BUSINESS_LAT));
				}
				
				if (!businessObj.isNull(BUSINESS_LNG)) {
					contentValue.put(Atn.Venue.LNG, businessObj.getString(BUSINESS_LNG));
				}
				
				if (!businessObj.isNull(BUSINESS_PHONE)) {
					contentValue.put(Atn.Venue.CONTACT_PHONE,businessObj.getString(BUSINESS_PHONE));
				}
				
				if (!businessObj.isNull(BUSINESS_FS_VENUE_LINK)) {
					contentValue.put(Atn.Venue.CANONICAL_URL,businessObj.getString(BUSINESS_FS_VENUE_LINK));
				}
				
				if(!businessObj.isNull(INSTAGRAM_LOC_ID)) {
					contentValue.put(Atn.Venue.INSTAGRAM_ID,businessObj.getString(INSTAGRAM_LOC_ID));
				}
				
				if (!businessObj.isNull(BUSINESS_IMAGE)) {
					contentValue.put(Atn.Venue.PHOTO,businessObj.getString(BUSINESS_IMAGE));
				}
				
				if(!businessObj.isNull(JsonUtils.FsVenueAnchorKeys.PHOTO))
					contentValue.put(Atn.Venue.PHOTO, businessObj.getString(JsonUtils.FsVenueAnchorKeys.PHOTO));
				
				if(!businessObj.isNull(FS_VENUE_CATEGORY_ID)) {
					contentValue.put(Atn.Venue.CATEGORY, Atn.Category
						.getVenueCategory(businessObj.getString(FS_VENUE_CATEGORY_ID),
								getContext()));
				}
				
				//MOHAR: Add Latest Media Time
				if(latestMediaTime> 0)
					contentValue.put(Atn.Venue.LATEST_MEDIA_DATE, latestMediaTime);

				
				///save comment count ,review count , rating
				int commentCount = businessObject
						.isNull(JsonUtils.FsVenueAnchorKeys.COMMENT_COUNT) ? 0
						: businessObject.getInt(JsonUtils.FsVenueAnchorKeys.COMMENT_COUNT);
				
				contentValue.put(Atn.Venue.COMMENT_COUNT, commentCount);
				
				int reviewCount = businessObject
						.isNull(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT) ? 0
						: businessObject.getInt(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT);
				contentValue.put(Atn.Venue.REVEIW_COUNT, reviewCount);
				
				double rating = businessObject
						.isNull(JsonUtils.FsVenueAnchorKeys.RATING) ? 0
						: businessObject.getDouble(JsonUtils.FsVenueAnchorKeys.RATING);
				contentValue.put(Atn.Venue.RATING, rating);
				
				
				////save tags
				if(!businessObject.isNull(ReviewTag.HASH_TAGS)) {
					JSONArray josnArray = businessObject.getJSONArray(ReviewTag.HASH_TAGS);
					Atn.ReviewTable.insertOrUpdate(getContext(), josnArray, fourSquareId);
				}
				
				if(!businessObject.isNull(JsonUtils.AnchorMediaKeys.VENUE_PICTURE)) {
					JSONArray pictureArray = businessObject.getJSONArray(JsonUtils.AnchorMediaKeys.VENUE_PICTURE);
					if(pictureArray!=null&&pictureArray.length()>0) {
						Atn.InstagramMedia.insertAnchorMedia(getContext(), pictureArray, fourSquareId);	
					}
				}
				contentValue.put(Atn.Venue.FOLLOWED, atnBarType);
				Atn.Venue.insertOrUpdate(contentValue, getContext());
		}
		return fourSquareId;
	}
	
	//json keys promotions
	private static final String PROMOTION_ID = "id";
	private static final String PROMOTION_BUSINESS_ID = "business_id";
	private static final String PROMOTION_TITLE = "title";
	private static final String PROMOTION_DETAIL = "details";
	private static final String PROMOTION_IS_GROUP = "is_group";
	private static final String PROMOTION_TYPE = "type";
	private static final String PROMOTION_START_DATE = "start_date";
	private static final String PROMOTION_END_DATE = "end_date";
	private static final String PROMOTION_LOGO = "logo";
	private static final String PROMOTION_STATUS = "status";
	private static final String PROMOTION_SHARED = "shared";
	private static final String PROMOTION_ACCEPTED = "accepted";
	private static final String PROMOTION_IMAGE_SMALL = "image_nonretina";
	private static final String PROMOTION_IMAGE_LARGE = "image_retina";
	private static final String PROMOTION_EXPIRE_TIME = "count_time";
	
	
	private void savePromotion(JSONObject promotionObject) throws JSONException {
//fsfsfs
		ContentValues values = new ContentValues();
		
		if (!promotionObject.isNull(PROMOTION_ID)) {
			values.put(PromotionTable.COL_PROMOTION_ID,
					promotionObject.getString(PROMOTION_ID));

		}

		if (!promotionObject.isNull(PROMOTION_BUSINESS_ID)) {
			values.put(PromotionTable.COL_PROMOTION_BUSINESS_ID,
					promotionObject.getString(PROMOTION_BUSINESS_ID));
		}

		if (!promotionObject.isNull(PROMOTION_EXPIRE_TIME)) {
			values.put(PromotionTable.COL_PROMOTION_COUPON_EXPIRY_DATE,
					promotionObject.getString(PROMOTION_EXPIRE_TIME));
		}

		if (!promotionObject.isNull(PROMOTION_TITLE)) {
			values.put(PromotionTable.COL_PROMOTION_TITLE,
					promotionObject.getString(PROMOTION_TITLE));
		}

		if (!promotionObject.isNull(PROMOTION_DETAIL)) {
			values.put(PromotionTable.COL_PROMOTION_DETAILS,
					promotionObject.getString(PROMOTION_DETAIL));

		}

		if (!promotionObject.isNull(PROMOTION_TYPE)) {
			int type = promotionObject.getInt(PROMOTION_TYPE);
			switch (type) {
			case 2:
				values.put(PromotionTable.COL_PROMOTION_TYPE,
						PromotionType.Event.toString());
				break;
			default:
				values.put(PromotionTable.COL_PROMOTION_TYPE,
						PromotionType.Offer.toString());
				break;
			}
		}

		if (!promotionObject.isNull(PROMOTION_IS_GROUP)) {
			int isGroup = AtnUtils.getInt(promotionObject
					.getString(PROMOTION_IS_GROUP));
			values.put(PromotionTable.COL_PROMOTION_GROUP_COUNT, isGroup);
		}

		if (!promotionObject.isNull(PROMOTION_START_DATE)) {
			values.put(PromotionTable.COL_PROMOTION_START_DATE,
					promotionObject.getString(PROMOTION_START_DATE));
		}

		if (!promotionObject.isNull(PROMOTION_END_DATE)) {
			values.put(PromotionTable.COL_PROMOTION_END_DATE,
					promotionObject.getString(PROMOTION_END_DATE));

		}

	
		if (!promotionObject.isNull(PROMOTION_LOGO)) {
			values.put(PromotionTable.COL_PROMOTION_LOGO_URL,
					promotionObject.getString(PROMOTION_LOGO));
		}

		/**
		 * Check the promotion's current status whether it is
		 * opened/shared/claimed/redeemed or expired.
		 */
		if (!promotionObject.isNull(PROMOTION_STATUS)) {
			values.put(PromotionTable.COL_PROMOTION_STATUS, AtnUtils
					.getInt(promotionObject.getString(PROMOTION_STATUS)));
		}

		values.put(PromotionTable.COL_PROMOTION_SHARED,!promotionObject.isNull(PROMOTION_SHARED) ? true : false);

		values.put(PromotionTable.COL_PROMOTION_ACCEPTED, (!promotionObject.isNull(PROMOTION_ACCEPTED) && !TextUtils
				.isEmpty(promotionObject.getString(PROMOTION_ACCEPTED))));

		/**
		 * Check the device DPI type and then set the image url of promotion.
		 */
		
		if (!promotionObject.isNull(PROMOTION_IMAGE_SMALL)) {
			values.put(PromotionTable.COL_PROMOTION_LOW_RES_IMAGE,promotionObject.getString(PROMOTION_IMAGE_SMALL));
		}
		
		if (!promotionObject.isNull(PROMOTION_IMAGE_LARGE)) {
			values.put(PromotionTable.COL_PROMOTION_HIGH_RES_IMAGE,promotionObject.getString(PROMOTION_IMAGE_LARGE));
		}
		
		DbHandler.getInstance().insertOrUpdatePromotion(values);
	}
	
	
	private String getSharePromotionUrl(String promotionId){
		
		Uri.Builder uri = HttpUtility.buildGetMethodWithAppParams();
		uri.appendPath("promotions");
		uri.appendPath("share");
		uri.appendQueryParameter("promotion_id", promotionId);
		uri.appendQueryParameter("user_id", UserDataPool.getInstance().getUserDetail().getUserId());
		
		return uri.build().toString();
	}
	
	///
	public boolean sharePromotion(String promotionId) {
		
		String result = executeOnSeperateClient(new HttpGet(getSharePromotionUrl(promotionId)));
		try {
			if(getResponseCode(result)==0){
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			error = e.getLocalizedMessage();
		}
		return false;
	}
}
