package com.atn.app.utils;

import org.json.JSONObject;

import com.atn.app.constants.ATNConstants;

public class JsonUtils {

	public static final int  ANCHOR_SUCCESS = 0;
	public static final String  RESPONSE = "response";
	public static final String  MESSAGE = "message";
	public static final String  DATA = "data";
	public static final String  CODE = "code";
	

	public static boolean isSuccess(String str){
		try {
			JSONObject jsonObj = new JSONObject(str);
			return jsonObj.getInt(CODE)==0;
		} catch (Exception e) {
			AtnUtils.logE(e);
		}
		return false;
	}
	
	
	public static String getErrorMessage(String str) {
		try {
			JSONObject jsonObj = new JSONObject(str);
			return jsonObj.getJSONObject(RESPONSE).getString(MESSAGE);
		} catch (Exception e) {
			AtnUtils.logE(e);
		}
		return "Unkon Error";
	}
	
	
	public static int resultCode(JSONObject jsonObject) {
		try {
			return jsonObject.getInt(CODE);
		} catch (Exception e) {
			AtnUtils.logE(e);
		}
		return ATNConstants.INVALID_RESULT;
	}
	
	/**
	 * 
	 * */
	public static String getErrorMessage(JSONObject jsonObj) {
		try {
			
			if(!jsonObj.isNull(RESPONSE)) {
				if(!jsonObj.getJSONObject(RESPONSE).isNull(MESSAGE)) {
					return jsonObj.getJSONObject(RESPONSE).getString(MESSAGE);
				} else {
					return "No Message Result Code "+resultCode(jsonObj);
				}
			}
			return "No responce ";
		} catch (Exception e) {
			AtnUtils.logE(e);
		}
		return "Unkon Error";
	}
	
	
	////json keys
	
	public interface CommentKey {
		
		String USER_COMMENT = "UserComment";
		String USER = "User";
		
		String ID = "id";
		String FS_VENUE_ID = "fs_venue_id";
		String COMMENT = "comment";
		String CREATED = "created";
		
		//user detail.
		String USER_ID = "user_id";
		String USER_PIC = "picture";
		String USER_NAME = "userName";
		
		
		
		//upload key
		String TEXT = "text";
	}
	

	public interface VenuePicUpload {
		
		String USER_ID = "user_id";
		String FS_VENUE_ID = "fs_venue_id";
		String NAME = "name";
		String MEDIA_PIC_TO_UPLOAD = "venuePic";
		String ADDRESS = "address";
		String FS_VENUE_LINK = "fs_venue_link";
		String fS_VENUE_CATEGORY = "fs_venue_category";
		String INSTAGRAM_LOCATION_ID = "insta_location_id";
		String LON = "lon";
		String LAT = "lat";
		String DESCRIPTION = "description";
		String TAGS = "tags";
		String FS_VENUE_PICTURE = "fs_venue_picture";
		//for anchr bar
		String BUSINESS_ID = "business_id";
		String FS_ANCHOR_VENUE_ID = "bar_id";
	}
	
	/**
	 * Foursquare venue coming from anchor server,json kyes 
	 * */
	public interface FsVenueAnchorKeys {

		String BUSINESSES = "Businesses";
		String NON_ATN_BUSINESS = "Nonatnbusiness";
		
		String ANCHOR_ID = "id";
		String FS_VENUE_ID = "fs_venue_id";
		String NAME = "name";
		String ADDRESS = "address";
		String FS_VENUE_LINK = "fs_venue_link";
		String fS_VENUE_CATEGORY = "fs_venue_category";
		String INSTAGRAM_LOCATION_ID = "insta_location_id";
		String LON = "lon";
		String LAT = "lat";
		String DESCRIPTION = "description";
		String FAVORITED = "favorited";
		
		String COMMENT_COUNT = "totalComment";
		String REVIEW_COUNT = "totalReview";
		String RATING = "totalRating";
		String PHOTO = "fs_venue_picture";
		
	}
	
	public interface ReviewTagKey {
		
		String HASH_TAG = "HashTags";
		
		String ID = "id";
		String TAG_NAME = "tagName";
		String TAG_TYPE = "tagtype";
		String REVIEW = "review";
		String ANCHOR_CATEGORY_ID = "category_id";

	}
	
	public interface CategoryKeys{
		String CATEGORIES = "categories";
		String USER_ID = "user_id";
	}
	
	public interface UserKey{
		String USER_ID = "user_id";
		String PROFILE_PIC = "profile";
	}
	
	
	public interface AnchorMediaKeys{
		
		String VENUE_PICTURE = "venuePicture";
		String USER_PICTURE = "UserPicture";
		
		String MEDIA_ID = "id";
		String USER_ID = "user_id";
		String FS_VENUE_ID = "fs_venue_id";
		String PICTURE = "picture";
		String CREATED = "created";
		String UNIX_TIME = "unixtime";
		
	}
	
}
