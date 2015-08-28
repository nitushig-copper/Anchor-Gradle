package com.atn.app.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.atn.app.AtnApp;
import com.atn.app.constants.ATNConstants;
import com.atn.app.datamodels.AnchorCategory;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.Comment;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.ReviewTag;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.pool.UserDataPool;
import com.atn.app.service.SynchService;
import com.atn.app.task.AtnMyDealTask;
import com.atn.app.task.AtnRegisterBarTask;
import com.atn.app.task.FollowingBarTask;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.HttpUtility.ImageType;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.SharedPrefUtils;


public final class Atn {
	public static final String AUTHORITY = "com.atn.app.ATN";

	/**
	 * The scheme part for this provider's URI
	 */
	private static final String SCHEME = "content://";

	// This class cannot be instantiated
	private Atn() {}
    
    /**
     * Venue table
     */
    public static final class Venue implements BaseColumns {
    	
    	private static final Object mVenueLock = new Object();
    	
    	// This class cannot be instantiated
        private Venue() {}
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "faure_square_vanue";
        
        
        /*
         * Column definitions
         */
        /**
         * Column name for the vanue id of the vanue 
         * <P>Type: TEXT</P>
         */
        
        public static final String ATN_BAR_ID = "atn_bar_id";
        public static final String VENUE_ID = "vanue_id";
    	public static final String VENUE_NAME = "name";
    	public static final String CONTACT = "contact";
    	public static final String CONTACT_PHONE = "phone";
    	public static final String LOCATION = "location";
    	public static final String ADDRESS = "address";
    	public static final String ADDRESS_STREET = "cross_street";
    	public static final String LAT = "lat";
    	public static final String LNG = "lng";
    	public static final String CANONICAL_URL = "canonical_url";
    	public static final String INSTAGRAM_ID = "instagram_id";
    	public static final String IG_LAT = "ig_lat";
    	public static final String IG_LNG = "ig_lng";
    	public static final String IG_LOC_NAME = "ig_name";
    	public static final String FOLLOWED = "followed";
    	public static final String ATN_PLACE_PROVIDER = "atn_place_provider";
    	public static final String CATEGORY = "venue_category";
    	public static final String LATEST_MEDIA_DATE = "lates_media_date";
    	public static final String PHOTO = "photo";
    	public static final String COMMENT_COUNT = "comment_count";
    	public static final String REVEIW_COUNT = "review_count";
    	public static final String RATING = "rating";
    	public static final String SUB_CATEGORY = "venue_sub_category";
        
        
        
        
        /*
         * URI definitions
         */
        /**
         * Path parts for the URIs
         */
        private static final String PATH_VANUES = "/vanues";
        
        /**
         * Path part for the Note ID URI
         */
        private static final String PATH_VANUE_ID = "/vanues/";
        
        private static final String PATH_CLEAN = "/cleandb";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_VANUES);
    	
        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + PATH_VANUE_ID);

		 public static final Uri CONTENT_URI_CLEAN_DB =  Uri.parse(SCHEME + AUTHORITY + PATH_CLEAN);
		
        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_VANUE_ID + "/#");
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = LATEST_MEDIA_DATE+" DESC";
        
       
    	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
    			+ _ID + " INTEGER PRIMARY KEY, "
    			+ VENUE_ID + " TEXT, "
    			+ ATN_BAR_ID + " TEXT, "
    			+ INSTAGRAM_ID + " TEXT, "
    			+ IG_LAT + " TEXT, "
    			+ IG_LNG + " TEXT, "
    			+ IG_LOC_NAME + " TEXT, "
    			+ VENUE_NAME + " TEXT, "
    			+ CONTACT + " TEXT, "
    			+ CONTACT_PHONE + " TEXT, "
    			+ LOCATION + " TEXT, "
    			+ ADDRESS + " TEXT, "
    			+ ADDRESS_STREET + " TEXT, "
    			+ LAT + " TEXT, "
    			+ LNG + " TEXT, "
    			+ FOLLOWED + " INTEGER DEFAULT 0, "
    			+ CANONICAL_URL + " TEXT, "
    			+ PHOTO + " TEXT, "
    			+ ATN_PLACE_PROVIDER + " INTEGER DEFAULT 1, "
    			+ CATEGORY + " INTEGER, "
    			+ LATEST_MEDIA_DATE + " INTEGER DEFAULT 0, "
    			+ REVEIW_COUNT + " INTEGER , "
    			+ RATING + " REAL DEFAULT 0.0 , "
    			+ SUB_CATEGORY + " TEXT, "
    			+ COMMENT_COUNT + " INTEGER )";
    		
    	
		/**
		 * Insert venue in batch
		 * */
		public static void batchOperation(
				ArrayList<ContentProviderOperation> list, Context context) {
			synchronized (mVenueLock) {
				try {
					context.getContentResolver().applyBatch(AUTHORITY, list);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Insert Or update venue
		 * */
		public  static void insertOrUpdate(ContentValues contentValue,Context context) {
			synchronized (mVenueLock) {
				int count = context.getContentResolver().update(CONTENT_URI,
						contentValue, VENUE_ID + " = ?",
						new String[] { contentValue.getAsString(VENUE_ID) });
				if (count <= 0) {
					context.getContentResolver().insert(CONTENT_URI, contentValue);
				}
			}
		}
		///
		public  static void insertOrUpdateFromAnchor(ContentValues contentValue,Context context) {
			synchronized (mVenueLock) {
				int count = updateMediaDate(contentValue, context);
	    		if(count<=0) {
	    			context.getContentResolver().insert(CONTENT_URI, contentValue);
	    		}
			}
		}
		
		public static int update(ContentValues contentValue, Context context) {
			synchronized (mVenueLock) {
				int count = context.getContentResolver().update(CONTENT_URI,
						contentValue, VENUE_ID + " = ?",
						new String[] { contentValue.getAsString(VENUE_ID) });
				return count;
			}
		}
		
		/**
		 * Check if content values have LATEST_MEDIA_DATE date then we try to
		 * update on basis of LATEST_MEDIA_DATE and venue id, otherwise remove the LATEST_MEDIA_DATE and update venue
		 * **/
		public  static int updateMediaDate(ContentValues contentValue, Context context) {
			synchronized (mVenueLock) {
				int count = 0;
				if (contentValue.containsKey(LATEST_MEDIA_DATE)) {
					count = context.getContentResolver().update(CONTENT_URI,contentValue,
									VENUE_ID + " = ? AND " + LATEST_MEDIA_DATE+ " < ?",
									new String[] {contentValue.getAsString(VENUE_ID),
											contentValue.getAsString(LATEST_MEDIA_DATE) });
				}

				if (count <= 0) {
					// remove latest media key
					contentValue.remove(LATEST_MEDIA_DATE);
					count = update(contentValue, context);
				}
				return count;
			}
		}
		
	
		public  static boolean deleteVenue(String fourSquareId,String igLocId,Context context) {
			synchronized (mVenueLock) {
				 context.getContentResolver().delete(CONTENT_URI, VENUE_ID+" = ? AND "+FOLLOWED+" = ?", new String[]{fourSquareId,"0"});
				 context.getContentResolver().delete(Atn.InstagramMedia.CONTENT_URI, Atn.InstagramMedia.INSTAGRAM_ID_REF+" = ?", new String[]{igLocId});
			}
			return true;
		}
		
		public  static VenueModel getVenue(String venueId,Context context) {
			
			VenueModel fSvenue = null;
			Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, VENUE_ID+" = ?", new String[]{venueId}, DEFAULT_SORT_ORDER);
			if(cursor != null&&cursor.getCount()>0){
				cursor.moveToFirst();
				fSvenue = new VenueModel(cursor);
				//if(!TextUtils.isEmpty(fSvenue.getInstagramLocationId())){
					Cursor mediaCursor = context.getContentResolver().query(
							Atn.InstagramMedia.CONTENT_URI, null,
							Atn.InstagramMedia.FOUR_SQUARE_ID + " = ?",
							new String[] { fSvenue.getVenueId() },
							Atn.InstagramMedia.DEFAULT_SORT_ORDER);
					if(mediaCursor != null&&mediaCursor.getCount()>0){
						mediaCursor.moveToFirst();
						do {
							fSvenue.addInstagramMedia(new IgMedia(mediaCursor));
						} while (mediaCursor.moveToNext());
						mediaCursor.close();
					}
				//}
				cursor.close();
			}
			return fSvenue;
		}
		
		public  static int getVenueCategory(String iGLocId,Context context) {
			
			Cursor cursor = context.getContentResolver().query(CONTENT_URI, new String[]{CATEGORY,VENUE_ID}, VENUE_ID+" = ?", new String[]{iGLocId}, DEFAULT_SORT_ORDER);
			if(cursor != null&&cursor.getCount()>0) {
				cursor.moveToFirst();
				return cursor.getInt(cursor.getColumnIndex(CATEGORY));
			}
			return 0;
		}
		
		public static ArrayList<String> followedVenuesId(Context context) {
			
			ArrayList<String> fSvenueList = new ArrayList<String>();
			Cursor cursor = AtnApp.getAppContext()
					.getContentResolver()
					.query(CONTENT_URI, new String[]{Atn.Venue.VENUE_ID,
							  Atn.Venue.INSTAGRAM_ID}, FOLLOWED + " > ? ",
							  new String[] { String.valueOf(VenueModel.NON_ATN_BAR) }, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					fSvenueList.add(cursor.getString(cursor.getColumnIndex(Atn.Venue.VENUE_ID)));
				} while (cursor.moveToNext());
			}
			if (cursor != null) {
				cursor.close();
			}
			return fSvenueList;
		}
		
		public  static List<AtnOfferData> getNonAtnVenue() {

			List<AtnOfferData> fSvenueList = new ArrayList<AtnOfferData>();
			Cursor cursor = AtnApp.getAppContext()
					.getContentResolver()
					.query(CONTENT_URI, null, FOLLOWED + " IS ? ",new String[] { "1" }, DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					VenueModel fsVenue = new VenueModel(cursor);
					fSvenueList.add(fsVenue);
					// if(!TextUtils.isEmpty(fsVenue.getInstagramLocationId())){
					Cursor mediaCursor = AtnApp
							.getAppContext()
							.getContentResolver()
							.query(Atn.InstagramMedia.CONTENT_URI, null,
									Atn.InstagramMedia.FOUR_SQUARE_ID + " = ?",
									new String[] { fsVenue.getVenueId() },
									Atn.InstagramMedia.DEFAULT_SORT_ORDER);
					if (mediaCursor != null && mediaCursor.getCount() > 0) {
						mediaCursor.moveToFirst();
						do {
							fsVenue.addInstagramMedia(new IgMedia(mediaCursor));
						} while (mediaCursor.moveToNext());
					}
					if (mediaCursor != null) {
						mediaCursor.close();
					}
					// }
				} while (cursor.moveToNext());
			}
			if (cursor != null) {
				cursor.close();
			}
			return fSvenueList;
		}
		 
		 /*
		  * Merge venue if insert from search screen
		  * **/
		public static void mergeSearchedVenue(VenueModel venueModel,
				Context context) {
			synchronized (mVenueLock) {
				Cursor cursor = context.getContentResolver().query(CONTENT_URI,null, VENUE_ID + " = ?",
						new String[] { venueModel.getVenueId() },
						DEFAULT_SORT_ORDER);
				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					venueModel.setAtnBarId(cursor.getString(cursor.getColumnIndex(Atn.Venue.ATN_BAR_ID)));
					venueModel.setAtnBarType(cursor.getInt(cursor.getColumnIndex(Atn.Venue.FOLLOWED)));
				}
				if (cursor != null) {
					cursor.close();
				}
			}
		}
			
		 
		 /***
		  * It insert or update search venue in database and also update or insert media
		  * @fsVenueModel venue detail
		  * */
		 public static  void insertOrUpdateSearchedVenue(VenueModel venueModel,Context context) {
			 
			 synchronized (mVenueLock){
				 ContentValues contentValue = venueModel.getContentValues();			 
				 insertOrUpdate(contentValue, context);
				 
				 if(venueModel.getInstagramMedia()!=null&&venueModel.getInstagramMedia().size()<0) {
					 for (IgMedia igMedia : venueModel.getInstagramMedia()) {
							Atn.InstagramMedia.insertOrUpdate(igMedia.getContentValues(), context);
						 }
				 }
			 }
		 }
		 
		 public static boolean isFollowedNonAtnBar(String insID,Context context) {
			 Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, VENUE_ID+" = ? AND "+
					 FOLLOWED+" IS NOT ?", new String[]{insID,VenueModel.NON_ATN_BAR+""}, DEFAULT_SORT_ORDER);
				if(cursor != null&&cursor.getCount()>0){
					return true;
				}
			 if(cursor!=null){cursor.close();}
			return false;
		 }
		 
		 public static String getInstagramId(String insID,Context context) {
			 Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, VENUE_ID+" = ? ", new String[]{insID}, DEFAULT_SORT_ORDER);
				if(cursor != null&&cursor.getCount()>0){
					cursor.moveToFirst();
					String instagramId = cursor.getString(cursor.getColumnIndex(INSTAGRAM_ID));
					cursor.close();
					return instagramId;
				}
			 if(cursor!=null){cursor.close();}
			return null;
		 }
		 
		 public static void addFsVenueIdsForRefresh(List<VenueModel> list) {
				Cursor cursor = AtnApp.getAppContext().getContentResolver().query(CONTENT_URI, null, 
						FOLLOWED+" IS NOT ? ",  new String[]{VenueModel.NON_ATN_BAR+""}, DEFAULT_SORT_ORDER);
				if(cursor != null&&cursor.getCount()>0) {
					cursor.moveToFirst();
					do {
						String venueID = cursor.getString(cursor.getColumnIndex(VENUE_ID));
						String instagramId = cursor.getString(cursor.getColumnIndex(INSTAGRAM_ID));
						VenueModel venueModel = new VenueModel();
						venueModel.setVenueId(venueID);
						venueModel.setInstagramLocationId(instagramId);
						if(!list.contains(venueModel)){
							list.add(venueModel);
						}
					} while (cursor.moveToNext());
				}
				if(cursor!=null){cursor.close();
			}
		 }
    }
    
    /**
     * FaureSquare
     */
    public static class InstagramMedia implements BaseColumns {
    	
    	private static final Object mMediaLock = new Object();
    	
    	// This class cannot be instantiated
        private InstagramMedia(){}
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "instagram_media";
        
        /*
         * URI definitions
         */
        /**
         * Path parts for the URIs
         */
        private static final String PATH_MEDIA = "/igmedia";
        
        /**
         * Path part for the Note ID URI
         */
        private static final String PATH_MEDIA_ID = "/igmedia/";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_MEDIA);
    	
        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + PATH_MEDIA);

        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_MEDIA_ID + "/#");
        
        
        
        /*
         * Column definitions
         */
        /**
         * Column name for the vanue id of the vanue 
         * <P>Type: TEXT</P>
         */
        public static final String MEDIA_ID = "media_id";
        public static final String MEDIA_URL = "media_url";
    	public static final String MEDIA_THUMB_URL = "thumbnail_url";
    	public static final String MEDIA_CREATED_DATE = "created_time";
    	public static final String LAT = "lat";
    	public static final String LNG = "lng";
    	public static final String LIKES_COUNT = "like_count";
    	public static final String INSTAGRAM_ID_REF = "instagram_id_ref";
    	public static final String FOUR_SQUARE_ID = "four_square_id";
    	public static final String LOC_NAME = "loc_name";
    	public static final String IMAGE_TAG = "image_tag";
    	
    	/**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = MEDIA_CREATED_DATE+" DESC";
    	
    	
    	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
    			+ _ID + " INTEGER PRIMARY KEY, "
    			+ MEDIA_ID + " TEXT, "
    			+ INSTAGRAM_ID_REF + " TEXT, "
    			+ FOUR_SQUARE_ID + " TEXT, "
    			+ LOC_NAME + " TEXT, "
    			+ MEDIA_THUMB_URL + " TEXT, "
    			+ MEDIA_CREATED_DATE + " INTEGER, "
    			+ LIKES_COUNT + " INTEGER, "
    			+ LAT + " TEXT, "
    			+ LNG + " TEXT, "
    			+ IMAGE_TAG + " TEXT, "
    			+ MEDIA_URL + " TEXT, "
    			+"UNIQUE (" + MEDIA_ID + ") ON CONFLICT REPLACE )";
    	
    	public  static void insertOrUpdate(ContentValues contentValue,Context context) {
    		synchronized (mMediaLock) {
    			int count = context.getContentResolver().update(
    					CONTENT_URI,
    					contentValue,
    					INSTAGRAM_ID_REF + " = ? AND " + MEDIA_ID + " = ? ",
    					new String[] { contentValue.getAsString(INSTAGRAM_ID_REF),contentValue.getAsString(MEDIA_ID) });
        		if(count<=0) {
        			context.getContentResolver().insert(CONTENT_URI, contentValue);
        		}
			}
    	}	
    	
    	public static void insert(ContentValues contentValue,Context context) {
    		context.getContentResolver().insert(CONTENT_URI, contentValue);
    	}	
    	
    	/**
    	 * It delete all media except these mediaIds
    	 * @mediaIds commaseperate media ids
    	 * @instagramLocId instagram location id
    	 * */
    	public  static int deleteMedia(String mediaIds,String instagramLocId,Context context){
    		synchronized (mMediaLock) {
    			int mMediacount = context.getContentResolver().delete(
    					Atn.InstagramMedia.CONTENT_URI,
    					Atn.InstagramMedia.INSTAGRAM_ID_REF + " = ? AND "+
    					Atn.InstagramMedia.MEDIA_ID +" NOT IN ( ? )",
    					new String[] { instagramLocId,mediaIds});
        		
        		//AtnUtils.log("mMediacount:"+mMediacount);
        		return mMediacount;
    		}
    	}
    	/**
    	 * Delete all media of a venue
    	 * */
    	public  static int deleteAllMedia(String igLocId,Context context) {
    		synchronized (mMediaLock) {
    			int mMediacount = context.getContentResolver().delete(
    					Atn.InstagramMedia.CONTENT_URI,
    					Atn.InstagramMedia.INSTAGRAM_ID_REF + " = ?",
    					new String[] { igLocId });
        		return mMediacount;
    		}
    	}
    	
    	/**
    	 * Delete all media of a venue
    	 * */
    	public  static int deleteNotInMedia(final List<IgMedia> list,String igLocId,Context context) {
    		synchronized (mMediaLock) {
    			int mMediacount = 0;
				if (list != null && list.size() > 0) {
					String mSelection = null;
					String[] mSelectionArgs = null;
					// create filter query IN Clouse
					mSelectionArgs = new String[list.size()+1];
					mSelectionArgs[0] = igLocId;
					StringBuffer qqsnMarks = new StringBuffer();
					qqsnMarks.append("(");
					for (int i = 1; i < list.size()+1; i++) {
						mSelectionArgs[i] = list.get(i-1).getImageId();
						qqsnMarks.append("?,");
					}
					qqsnMarks.deleteCharAt(qqsnMarks.length() - 1);
					qqsnMarks.append(")");
					mSelection = Atn.InstagramMedia.INSTAGRAM_ID_REF+" = ? AND "+Atn.InstagramMedia.MEDIA_ID + " NOT IN " + qqsnMarks.toString();
					mMediacount = context.getContentResolver().delete(Atn.InstagramMedia.CONTENT_URI, mSelection,
							mSelectionArgs);
					
				} else {
    				mMediacount = context.getContentResolver().delete(
        					Atn.InstagramMedia.CONTENT_URI,
        					Atn.InstagramMedia.INSTAGRAM_ID_REF + " = ?",
        					new String[] { igLocId });
    			}
    			return mMediacount;
    		}
    	}
    	
    	//insert or replace media in batch
    	public  static void insertMedia(final List<IgMedia> list,Context context) {

    		synchronized (mMediaLock) {
    			if(list==null||list.size()==0) return;
        		
        		ArrayList<ContentProviderOperation>  operationList = new ArrayList<ContentProviderOperation>();
        		for (IgMedia igMedia : list) {
    				operationList.add(ContentProviderOperation
    						.newInsert(CONTENT_URI)
    						.withValues(igMedia.getContentValues()).build());
    			}
        		try {
    				context.getContentResolver().applyBatch(AUTHORITY, operationList);
    			} catch (RemoteException e) {
    				e.printStackTrace();
    			} catch (OperationApplicationException e) {
    				e.printStackTrace();
    			}
			}
    	} 
    	
    	///insert media from anchor server in batch
    	public static  long insertAnchorMedia(Context context,JSONArray anchorMediaArray,String fsVenueId) {
    		
    		synchronized (mMediaLock){
    			try {
        			ArrayList<ContentProviderOperation>  operationList = new ArrayList<ContentProviderOperation>();
        			long latestMediaTime = 0;
        			for (int i = 0; i < anchorMediaArray.length(); i++) {
            			JSONObject anchorMediaObj = anchorMediaArray.getJSONObject(i);
            			if(!anchorMediaObj.isNull(JsonUtils.AnchorMediaKeys.USER_PICTURE)){
            				JSONObject mediaObj = anchorMediaObj.getJSONObject(JsonUtils.AnchorMediaKeys.USER_PICTURE);
            				String pictureName = mediaObj.getString(JsonUtils.AnchorMediaKeys.PICTURE);
    						long time = TextUtils.isEmpty(mediaObj.getString(JsonUtils.AnchorMediaKeys.UNIX_TIME)) ? 0
    								: mediaObj.getLong(JsonUtils.AnchorMediaKeys.UNIX_TIME);
    						
    						if(i==0) {
    							latestMediaTime = time;
    						}
    						operationList.add(ContentProviderOperation
    										.newInsert(CONTENT_URI)
    										.withValue(MEDIA_ID,mediaObj.getString(JsonUtils.AnchorMediaKeys.MEDIA_ID))
    										.withValue(FOUR_SQUARE_ID,mediaObj.getString(JsonUtils.AnchorMediaKeys.FS_VENUE_ID))
    										.withValue(MEDIA_URL,HttpUtility.getVenueMediaUrl(pictureName, ImageType.S))
    										.withValue(MEDIA_THUMB_URL,HttpUtility.getVenueMediaUrl(pictureName, ImageType.S))
    										.withValue(MEDIA_CREATED_DATE,time).build());
            			}
        			}
        			context.getContentResolver().applyBatch(AUTHORITY, operationList);
        			return latestMediaTime;
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
        		return 0;
    		}
    	}
    }
    
    
    /**
     * FaureSquare @
     */
    @Deprecated
    public static class Forum implements BaseColumns {
    	// This class cannot be instantiated
        private Forum(){}
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "forum_msg";
        
        /*
         * URI definitions
         */
        /**
         * Path parts for the URIs
         */
        private static final String PATH_MEDIA = "/forum";
        
        /**
         * Path part for the Note ID URI
         */
        private static final String PATH_MEDIA_ID = "/forum/";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_MEDIA);
    	
        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_MEDIA);

        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN  = Uri.parse(SCHEME + AUTHORITY + PATH_MEDIA_ID + "/#");
        
   
        /**
         * Column name for the vanue id of the vanue 
         * <P>Type: TEXT</P>
         */
        public static final String FORUM_ID = "forum_id";
        public static final String MESSAGE = "message";
    	public static final String IMAGE_URL = "image_url";
    	public static final String CREATED_DATE = "created_date";
    	public static final String USER_ID = "user_id";
    	public static final String PARENT_FORUM_ID = "parent_forum_id";
    	public static final String USER_NAME = "user_name";
    	public static final String TOTAL_COUNT = "total_count";
    	
    	/**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = FORUM_ID+" DESC";//"strftime('%s', '"+CREATED_DATE+"' ) ASC ";
    	
    	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
    			+ _ID + " INTEGER PRIMARY KEY, "
    			+ FORUM_ID + " INTEGER, "
    			+ MESSAGE + " TEXT, "
    			+ IMAGE_URL + " TEXT, "
    			+ CREATED_DATE + " TEXT, "
    			+ PARENT_FORUM_ID + " INTEGER DEFAULT 0, "
    			+ TOTAL_COUNT + " INTEGER DEFAULT 0, "
    			+ USER_NAME + " TEXT, "
    			+ USER_ID + " INTEGER )";
    	
    	
    	public synchronized static void insertOrUpdate(ContentValues contentValue,Context context){
			int count = context.getContentResolver().update(CONTENT_URI,contentValue,
					 FORUM_ID + " = ? ",
					new String[] {contentValue.getAsString(FORUM_ID) });
    		if(count<=0){
    			context.getContentResolver().insert(CONTENT_URI, contentValue);
    		}
    	}	
    	
    	public synchronized static void insert(ContentValues contentValue,Context context) {
    		Uri uri = context.getContentResolver().insert(CONTENT_URI, contentValue);
    		if(uri!=null){
    			AtnUtils.logE("Uir::"+uri.toString());
    		}
    	}	
    	
    	public synchronized static int deleteAllForumMsg(String igLocId,Context context){
    		int mMediacount = context.getContentResolver().delete(CONTENT_URI, USER_ID+" = ?", new String[]{igLocId});
    		return mMediacount;
    	}
    }
    
    
    public static class Category implements BaseColumns {
    	
    	 private Category(){}
         /**
          * The table name offered by this provider
          */
         public static final String TABLE_NAME = "categories";
         /**
          * Path parts for the URIs
          */
         private static final String PATH_CATEGORY = "/cat";
         /**
          * Path part for the Note ID URI
          */
         private static final String PATH_CATEGORY_ID = "/cat/";
         /**
          * The content:// style URL for this table
          */
         public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
         /**
          * The content URI base for a single note. Callers must
          * append a numeric note id to this Uri to retrieve a note
          */
 		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
         /**
          * The content URI match pattern for a single note, specified by its ID. Use this to match
          * incoming URIs or to construct an Intent.
          */
         public static final Uri CONTENT_ID_URI_PATTERN  = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY_ID + "/#");
    	
         /**
          * Column name for the venue id of the venue 
          * <P>Type: TEXT</P>
          */
         public static final String ANCHOR_CATEGORY_ID = "category_id";
         public static final String NAME = "name";
         //will work as category type
         public static final String PARENT_CATEGORY_ID = "parent_category_id";
         
         //fs
         public static final String FS_CATEGORY_ID = "fs_category_id";
         public static final String FS_NAME = "fs_name";
         //status on off.
         public static final String STATUS = "status";
         
         public static final String CATEGORY_ORDER = "cat_order";
         
     	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
    			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    			+ ANCHOR_CATEGORY_ID + " INTEGER NOT NULL, "
    			+ NAME + " TEXT, "
    			+ PARENT_CATEGORY_ID + " INTEGER NOT NULL, "
    			+ FS_CATEGORY_ID + " TEXT NOT NULL, "
    			+ FS_NAME + " TEXT, "
    			+ STATUS + " INTEGER, "
    			+ CATEGORY_ORDER + " INTEGER DEFAULT 0, "
    			+"UNIQUE (" + ANCHOR_CATEGORY_ID + ") ON CONFLICT IGNORE )";
     	
     	
     	public synchronized static void insert(ContentValues contentValue,Context context) {
    		 context.getContentResolver().insert(CONTENT_URI, contentValue);
    	}	
    	
    	public synchronized static int deleteAllCategory(Context context) {
    		int mMediacount = context.getContentResolver().delete(CONTENT_URI,null, null);
    		return mMediacount;
    	}
    	
    	public  static int getVenueCategory(String categoryId,Context context) {
    		Cursor cursor = context.getContentResolver().query(CONTENT_URI, new String[]{ANCHOR_CATEGORY_ID,PARENT_CATEGORY_ID}, FS_CATEGORY_ID+" = ?", new String[]{categoryId}, null);
    		if(cursor != null&&cursor.getCount()>0) {
    			 cursor.moveToFirst();
    			 int catId = cursor.getInt(cursor.getColumnIndex(PARENT_CATEGORY_ID));
    			 //its parent
    			 if(catId==0) {
    				 catId = cursor.getInt(cursor.getColumnIndex(ANCHOR_CATEGORY_ID));
    			 }
    			 cursor.close();
    			return catId;
			}
    		return ATNConstants.INVALID_RESULT;
    	}
    	
    	
    	public synchronized static void insertBatch(Context context,ArrayList<ContentProviderOperation> categoriesList) {
    		try {
    			context.getContentResolver().applyBatch(AUTHORITY, categoriesList);	
			} catch (Exception e) {
				AtnUtils.logE(e.getLocalizedMessage());
			}
    	}
    	
    	
    	//
    	public  static ArrayList<AnchorCategory> getSearchCatgoryList(Context context , String searchText) {
    		// Mohar 
    		
    		ArrayList<AnchorCategory> categoriesList = new ArrayList<AnchorCategory>();

    		if(searchText.length() <= 0)
    			return categoriesList;
    		
    		Cursor cursor = null;    		
    		cursor = context.getContentResolver().query(CONTENT_URI,null, NAME+" LIKE '%"+searchText+"%'",null, CATEGORY_ORDER+" ASC");

    		if(cursor != null&&cursor.getCount()>0) {
    			 cursor.moveToFirst();

    			 
    			 do {
    				 AnchorCategory anchorCat = new AnchorCategory();
    				 anchorCat.id = cursor.getInt(cursor.getColumnIndex(ANCHOR_CATEGORY_ID));
    				 anchorCat.categoryId = cursor.getInt(cursor.getColumnIndex(PARENT_CATEGORY_ID));
    				 anchorCat.foursquareId = cursor.getString(cursor.getColumnIndex(FS_CATEGORY_ID));
    				 if(anchorCat.categoryId==0) anchorCat.categoryId = anchorCat.id;
    				 
    				anchorCat.name =  cursor.getString(cursor.getColumnIndex(NAME));
    				anchorCat.fourSquareName =  cursor.getString(cursor.getColumnIndex(FS_NAME));
    				anchorCat.status =  cursor.getInt(cursor.getColumnIndex(STATUS));
    				 categoriesList.add(anchorCat);
    				 
				} while (cursor.moveToNext());
    			 cursor.close();
			}
    		return categoriesList;
    	} 
    	
    	
    	//
    	public  static ArrayList<AnchorCategory> populateCategories(Context context,boolean parentOnly) {
    		// Mohar 
    		ArrayList<AnchorCategory> categoriesList = new ArrayList<AnchorCategory>();
    		Cursor cursor = context.getContentResolver().query(CONTENT_URI,null, null,null, null);
    		
    		if(parentOnly)
    			cursor = context.getContentResolver().query(CONTENT_URI,null, PARENT_CATEGORY_ID+" = ? ",new String[]{"0"}, CATEGORY_ORDER+" ASC");
    		else
    			cursor = context.getContentResolver().query(CONTENT_URI,null, null,null, null);
    		
    		if(cursor != null&&cursor.getCount()>0) {
    			 cursor.moveToFirst();
    			 
    			 int catId = cursor.getInt(cursor.getColumnIndex(PARENT_CATEGORY_ID));
    			 //its parent
    			 if(catId==0) {
    				 catId = cursor.getInt(cursor.getColumnIndex(ANCHOR_CATEGORY_ID));
    			 }
 
    			 do {
    				 AnchorCategory anchorCat = new AnchorCategory();
    				 anchorCat.id = cursor.getInt(cursor.getColumnIndex(ANCHOR_CATEGORY_ID));
    				 anchorCat.categoryId = cursor.getInt(cursor.getColumnIndex(PARENT_CATEGORY_ID));
    				 anchorCat.foursquareId = cursor.getString(cursor.getColumnIndex(FS_CATEGORY_ID));
    				 if(anchorCat.categoryId==0) anchorCat.categoryId = anchorCat.id;
    				 
    				 if(parentOnly) {
    					 anchorCat.name =  cursor.getString(cursor.getColumnIndex(NAME));
    					 anchorCat.fourSquareName =  cursor.getString(cursor.getColumnIndex(FS_NAME));
    					 anchorCat.status =  cursor.getInt(cursor.getColumnIndex(STATUS));
    				 }
    				 categoriesList.add(anchorCat);
    				 
				} while (cursor.moveToNext());
    			 cursor.close();
			}
    		return categoriesList;
    	} 
    	
    	
    	/**
    	 * update status....
    	 * **/
    	public static  void updateStatus(Context context, ArrayList<AnchorCategory> categories) {

    		ArrayList<ContentProviderOperation> updateOprList = new ArrayList<ContentProviderOperation>();
    		
    		for (AnchorCategory anchorCategory : categories) {
    			updateOprList.add(ContentProviderOperation
						.newUpdate(CONTENT_URI)
						.withValue(ANCHOR_CATEGORY_ID, anchorCategory.id)
						.withValue(STATUS, anchorCategory.status)
						.withSelection(ANCHOR_CATEGORY_ID + " = ? ",new String[] { String
										.valueOf(anchorCategory.id) }).build());
			}
    		try {
				context.getContentResolver().applyBatch(AUTHORITY, updateOprList);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
    	}
    	
    	
    	public static  String getTopCategoryId(Context context,String anchorCatId){
    		Cursor cursor = context.getContentResolver().query(CONTENT_URI,null, ANCHOR_CATEGORY_ID+" = ?",new String[]{anchorCatId}, null);
    		if(cursor != null&&cursor.getCount()>0) {
   			   cursor.moveToFirst();
   			   String catId = cursor.getString(cursor.getColumnIndex(FS_CATEGORY_ID));
   			   cursor.close();
   			   return catId;
   			}
    		if(cursor!=null) cursor.close();
    		return null;
    	}
    	
    	//insert or update
		public static synchronized void insertOrUpdate(Context context,
				ContentValues contentValues) {
			int count = context.getContentResolver().update(CONTENT_URI,contentValues,
					ANCHOR_CATEGORY_ID + " = ? ",
					new String[] { contentValues.getAsString(ANCHOR_CATEGORY_ID) });
			if (count <= 0) {
				context.getContentResolver().insert(CONTENT_URI,contentValues);
			}
		}
    }
    
    
    public static class ReviewTable implements BaseColumns {
    	
   	 private ReviewTable(){}
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "review_tag";
        /**
         * Path parts for the URIs
         */
        private static final String PATH_CATEGORY = "/review";
        /**
         * Path part for the Note ID URI
         */
        private static final String PATH_CATEGORY_ID = "/review/";
        
        private static final String PATH_REVIEW_LIMIT = "/reviewlimit";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN  = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY_ID + "/#");
   	
        public static final Uri CONTENT_URI_WITH_LIMIT =  Uri.parse(SCHEME + AUTHORITY + PATH_REVIEW_LIMIT);
        
        /**
         * Column name for the venue id of the venue 
         * <P>Type: TEXT</P>
         */
        public static final String TAG_ID = "tag_id";
        public static final String NAME = "name";
        //will work as category type
        public static final String TAG_TYPE = "tag_type";
        
        public static final String REVIEW_COUNT = "review_count";
        public static final String FS_VENUE_ID = "venue_id";
        public static final String ANCHOR_CATEGORY_ID = "anchor_category_id";
        //status on off.
        public static final String STATUS = "status";
        
        public static final String ANCHOR_FS_ID_VALUE = "anchor";
        
    	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
   			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
   			+ TAG_ID + " INTEGER NOT NULL, "
   			+ NAME + " TEXT, "
   			+ TAG_TYPE + " INTEGER, "
   			+ ANCHOR_CATEGORY_ID + " INTEGER, "
   			+ REVIEW_COUNT + " INTEGER, "
   			+ STATUS + " INTEGER, "
   			+ FS_VENUE_ID + " TEXT NOT NULL )";
    	
    	
    	public static synchronized void insertBatch(Context context,ArrayList<ContentProviderOperation> operationsList){
    		
    		try {
    			context.getContentResolver().applyBatch(AUTHORITY, operationsList);
			} catch (Exception e) {
				// TODO: handle exception
			}
    	} 
    	
    	/**
    	 * Update or insert reviews in table.
    	 * ***/
    	public static  synchronized void insertOrUpdate(Context context,JSONArray jsonArray,String fsVenueId) {
    		
    		for (int index = 0; index < jsonArray.length(); index++) {
    			try {
					JSONObject reviewObj = jsonArray.getJSONObject(index);
					ContentValues values = new ContentValues();
					int id = reviewObj.getInt(JsonUtils.ReviewTagKey.ID);
					values.put(TAG_ID, id);
					if (!reviewObj.isNull(JsonUtils.ReviewTagKey.TAG_NAME))
						values.put(NAME, reviewObj.getString(JsonUtils.ReviewTagKey.TAG_NAME));

					if (!reviewObj.isNull(JsonUtils.ReviewTagKey.TAG_TYPE))
						values.put(TAG_TYPE, reviewObj.getInt(JsonUtils.ReviewTagKey.TAG_TYPE));

					if (!reviewObj.isNull(JsonUtils.ReviewTagKey.REVIEW))
						values.put(REVIEW_COUNT, reviewObj.getInt(JsonUtils.ReviewTagKey.REVIEW));
					
					
					if (!reviewObj.isNull(JsonUtils.ReviewTagKey.ANCHOR_CATEGORY_ID))
						values.put(ANCHOR_CATEGORY_ID, reviewObj.getInt(JsonUtils.ReviewTagKey.ANCHOR_CATEGORY_ID));
					
					 values.put(FS_VENUE_ID, fsVenueId);
					 
					 insertOrUpdate( context, values);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
    	}
    	
    	public static synchronized void insertOrUpdate(Context context,ContentValues values) {
    		int row = context.getContentResolver().update(CONTENT_URI,
					values,
					FS_VENUE_ID + " = ? AND " + TAG_ID + " = ?",
					new String[] { values.getAsString(FS_VENUE_ID), values.getAsString(TAG_ID) });
			if(row<=0) {
				context.getContentResolver().insert(CONTENT_URI, values);
			}
    	}
    	
		public static ArrayList<ReviewTag> getVenueTwoReviewTag(Context context, VenueModel venue) {
			return getVenueTwoReviewTag(context, venue.getVenueId());
		}

		public static ArrayList<ReviewTag> getVenueTwoReviewTag(Context context, String venueId){
			ArrayList<ReviewTag> reviews = new ArrayList<ReviewTag>();
			Cursor cursor = context.getContentResolver().query(CONTENT_URI_WITH_LIMIT, null, FS_VENUE_ID + " = ? AND "+REVIEW_COUNT+" > ?",
							new String[] { venueId,String.valueOf(0) },REVIEW_COUNT + " DESC");
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					reviews.add(new ReviewTag(cursor));
				} while (cursor.moveToNext());
			}
			if (cursor != null)
				cursor.close();
			return reviews;
		}
		
    	//fetch review tag for this venue
    	public static List<ReviewTag> getReviewTag(Context context,VenueModel venue) {
    		 ArrayList<ReviewTag> reviews = new ArrayList<ReviewTag>();
    		
    		Cursor cursor = context.getContentResolver().query(CONTENT_URI,null, FS_VENUE_ID+" = ?",new String[]{venue.getVenueId()}, REVIEW_COUNT+" DESC");
    		if(cursor != null&&cursor.getCount()>0) {
   			   cursor.moveToFirst();
				do {
					reviews.add(new ReviewTag(cursor));
				} while (cursor.moveToNext());
   			}
    		
    		if(cursor!=null) cursor.close();
    		
    		//if(reviews.size()<10) {
   				ArrayList<ReviewTag> defaultList = getReviewTagByCat(context,venue.getVenueCategoryId());
   				   if(defaultList!=null&&defaultList.size()>0){
   					   for (ReviewTag reviewTag : defaultList) {
   						   if(!reviews.contains(reviewTag)) {
   							reviewTag.setFsVenueId(venue.getVenueId());
   							reviews.add(reviewTag); 
   						   }
					   }
   				   }
   			  // }
    		
    		Collections.shuffle(reviews);
    		//MOHAR: Remove 10 Tag limits
//    		if(reviews.size()>10) {
//    			return reviews.subList(0, 10);
//    		}
    		return reviews;
    	}
    	
    private static  ArrayList<ReviewTag> getReviewTagByCat(Context context,int categoryId) {
    		
			Cursor cursor = context.getContentResolver().query(
					CONTENT_URI,
					null,
					ANCHOR_CATEGORY_ID + " = ? AND " + FS_VENUE_ID + " = ?",
					new String[] { String.valueOf(categoryId),
							ANCHOR_FS_ID_VALUE }, REVIEW_COUNT + " DESC");
    		if(cursor != null&&cursor.getCount()>0) {
   			   cursor.moveToFirst();
   			   ArrayList<ReviewTag> reviews = new ArrayList<ReviewTag>();
				do {
					reviews.add(new ReviewTag(cursor));
				} while (cursor.moveToNext());
   			   cursor.close();
   			   return reviews;
   			}
    		if(cursor!=null) cursor.close();
    		return null;
    	}
    	
    	//update reviews
    	public static synchronized void insertOrUpdateReview(Context context,ArrayList<ReviewTag> list) {
    		
    		if(list==null) return;
    	
    		for (ReviewTag reviewTag : list) {
    			ContentValues values = new ContentValues();
				values.put(TAG_ID, reviewTag.getTagId());
				values.put(NAME, reviewTag.getName());
				values.put(TAG_TYPE, reviewTag.getType());
				values.put(REVIEW_COUNT, reviewTag.getReviewCount());
				values.put(ANCHOR_CATEGORY_ID, reviewTag.getAnchorCategoryId());
				values.put(FS_VENUE_ID, reviewTag.getFsVenueId());
				insertOrUpdate( context, values); 
			}
    	}
    	
    	public static void deleteDefaultCategoryTags(Context context) {
    		context.getContentResolver().delete(CONTENT_URI, Atn.ReviewTable.FS_VENUE_ID+" = ?", new String[]{Atn.ReviewTable.ANCHOR_FS_ID_VALUE});
    	}
    }
    
    
    
    public static class CommentTable implements BaseColumns {
    	
      	 private CommentTable(){}
           /**
            * The table name offered by this provider
            */
           public static final String TABLE_NAME = "comments";
           /**
            * Path parts for the URIs
            */
           private static final String PATH_CATEGORY = "/comment";
           /**
            * Path part for the Note ID URI
            */
           private static final String PATH_CATEGORY_ID = "/comment/";
           /**
            * The content:// style URL for this table
            */
           public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
           /**
            * The content URI base for a single note. Callers must
            * append a numeric note id to this Uri to retrieve a note
            */
   		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
           /**
            * The content URI match pattern for a single note, specified by its ID. Use this to match
            * incoming URIs or to construct an Intent.
            */
           public static final Uri CONTENT_ID_URI_PATTERN  = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY_ID + "/#");
      	
           /**
            * Column name for the venue id of the venue 
            * <P>Type: TEXT</P>
            */
           public static final String COMMENT_ID = "comment_id";
           public static final String TEXT = "text";
           public static final String USER_ID = "user_id";
           public static final String USER_NAME = "user_name";
           public static final String USER_PHOTO = "user_photo";
           public static final String FS_VENUE_ID = "venue_id";
           public static final String COMMENT_DATE = "comment_date";
           
           
       	public static final String create_table_query = "CREATE TABLE " + TABLE_NAME + " ( "
      			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      			+ COMMENT_ID + " INTEGER NOT NULL, "
      			+ TEXT + " TEXT, "
      			+ USER_ID + " INTEGER NOT NULL, "
      			+ USER_NAME + " TEXT, "
      			+ USER_PHOTO + " TEXT, "
      			+ FS_VENUE_ID + " TEXT NOT NULL, "
      			+ COMMENT_DATE + " INTEGER, "
      			+"UNIQUE (" + COMMENT_ID + ") ON CONFLICT REPLACE )";
       	
       	 
       	/*
       	 * 
       	 * insert or update comment in batch
       	 * ***/
		public static synchronized void insertOrUpdate(Context context, JSONObject jsonObject) {
			try {
				JSONArray commentArray = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONArray(JsonUtils.DATA);
				if(commentArray!=null){
					ArrayList<ContentProviderOperation> oprList = new ArrayList<ContentProviderOperation>();
					for (int i = 0; i < commentArray.length(); i++) {
						
						JSONObject commentUserObj  = commentArray.getJSONObject(i);
						JSONObject commentObject = commentUserObj.getJSONObject(JsonUtils.CommentKey.USER_COMMENT);
						
						if(commentObject==null)continue;
						
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(CONTENT_URI);
						if(!commentObject.isNull(JsonUtils.CommentKey.ID)) {
							builder.withValue(COMMENT_ID, commentObject.getInt(JsonUtils.CommentKey.ID));
						}
						if(!commentObject.isNull(JsonUtils.CommentKey.FS_VENUE_ID)) {
							builder.withValue(FS_VENUE_ID, commentObject.getString(JsonUtils.CommentKey.FS_VENUE_ID));
						}
						if(!commentObject.isNull(JsonUtils.CommentKey.COMMENT)){
							builder.withValue(TEXT, commentObject.getString(JsonUtils.CommentKey.COMMENT));
						}
						if(!commentObject.isNull(JsonUtils.CommentKey.CREATED)){
							builder.withValue(COMMENT_DATE,AtnUtils.convertInMillisecond(commentObject.getString(JsonUtils.CommentKey.CREATED)));
						}
						if(!commentObject.isNull(JsonUtils.CommentKey.USER_ID)){
							builder.withValue(USER_ID, commentObject.getString(JsonUtils.CommentKey.USER_ID));
						}
						
						JSONObject userObject = commentUserObj.getJSONObject(JsonUtils.CommentKey.USER);
						if(userObject!=null) {

							if(!userObject.isNull(JsonUtils.CommentKey.USER_NAME))
								builder.withValue(USER_NAME, userObject.getString(JsonUtils.CommentKey.USER_NAME));
							
							if(!userObject.isNull(JsonUtils.CommentKey.USER_PIC))
								builder.withValue(USER_PHOTO, userObject.getString(JsonUtils.CommentKey.USER_PIC));
						}
						
						oprList.add(builder.build());
					}
					
					if(oprList.size()>0) {
						context.getContentResolver().applyBatch(AUTHORITY, oprList);
					}
				}
				
			} catch (Exception e) {
			  AtnUtils.logE(e.getLocalizedMessage());
			}
		}
		
		//insert single object
		public static synchronized void insertComment(Context context, ContentValues values) {
			context.getContentResolver().insert(CONTENT_URI, values);
		}
		
	///load all comment for given venue id.
		public static  ArrayList<Comment> getAllComment(Context context,String fsVenueId){
					
			ArrayList<Comment> comments = new ArrayList<Comment>();
			Cursor cursor = context.getContentResolver().query(CONTENT_URI,null, FS_VENUE_ID+" = ?",new String[]{fsVenueId}, COMMENT_DATE+ " ASC ");
    		if(cursor != null&&cursor.getCount()>0) {
   			   cursor.moveToFirst();
				do {
					comments.add(new Comment(cursor));
				} while (cursor.moveToNext());
   			   cursor.close();
   			}
    		if(cursor!=null) cursor.close();
			return comments;
		}

	}
       
  
    public static void  cleanDatabase(Context context) {
    	
    	
    	AtnRegisterBarTask.getInstance().cancel();
    	FollowingBarTask.getInstance().cancel();
    	AtnMyDealTask.getInstance().cancel();
    	SharedPrefUtils.cleanAppPref(context);
    	
    	
    	
    	//context.getContentResolver().delete(Atn.Venue.CONTENT_URI, null,null);
    	//context.getContentResolver().delete(Atn.InstagramMedia.CONTENT_URI, null,null);
    	
    	//context.getContentResolver().delete(Atn.Forum.CONTENT_URI, null,null);
    	SynchService.IS_RUNNING = false;
    }

    
    public static void cleanFourSquareDb(Context context) {
    	
    	context.stopService(new Intent(context,SynchService.class));
    	//non atn venue need to be reload
    	if(UserDataPool.getInstance().isUserLoggedIn()) {
    		context.getContentResolver().delete(Atn.Venue.CONTENT_URI, Atn.Venue.FOLLOWED+" IS ?",new String[]{""+VenueModel.NON_ATN_BAR});
    	} else {
    		context.getContentResolver().delete(Atn.Venue.CONTENT_URI, null,null);	
    	}
    	//delete all media .
    	context.getContentResolver().delete(Atn.InstagramMedia.CONTENT_URI, null,null);
    	SynchService.IS_RUNNING = false;
    }
    
}
