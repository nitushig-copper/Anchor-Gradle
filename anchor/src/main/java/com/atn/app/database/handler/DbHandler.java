package com.atn.app.database.handler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.atn.app.AtnApp;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.AtnPromotion.PromotionType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;

/**
 * Creates a helper class that interacts with the database to perform all the
 * required database related operations. You should use only this class to do
 * all the database related operations.
 */
public class DbHandler {
	static final String DB_NAME = "ATN_DB";
	private static final int DB_VERSION = 2;//last 1 at 1.3
	protected static String DROP_TABLE_COMMAND = "DROP TABLE IF EXISTS ";
	private static DbHandler instance = null;
	private static final String TRUE = "1";
	private DatabaseHelper mOpenHelper;
	
	/**
	 * Returns instance of the database helper to perform database related
	 * operations.
	 * 
	 * @return DbHandler instance
	 */

	public static DbHandler getInstance() {
		if (instance == null) {
			instance = new DbHandler();
		}
		return instance;
	}

	private DbHandler() {
		mOpenHelper = new DatabaseHelper(AtnApp.getAppContext());

	}

	private static class DatabaseHelper extends SQLiteOpenHelper {



		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * 
		 * Creates the underlying database with table name and column names
		 * taken from the NotePad class.
		 * 
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(UserTable.create_user_table);
			db.execSQL(BusinessTable.create_business_table);
			db.execSQL(PromotionTable.create_promotion_table);
			db.execSQL(LoginTable.CREATE_TABLE);
		}

		/**
		 * 
		 * Demonstrates that the provider must consider what happens when the
		 * underlying datastore is changed. In this sample, the database is
		 * upgraded the database by destroying the existing data. A real
		 * application should upgrade the database in place.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL(DROP_TABLE_COMMAND + UserTable.USER_TABLE);
			db.execSQL(DROP_TABLE_COMMAND + BusinessTable.BUSINESS_TABLE);
			db.execSQL(DROP_TABLE_COMMAND + PromotionTable.PROMOTION_TABLE);
			db.execSQL(DROP_TABLE_COMMAND + LoginTable.TABLE_NAME);
			
			onCreate(db);
			
			SharedPrefUtils.clearAll(AtnApp.getAppContext());
		}
	}

	private SQLiteDatabase getWritableDatabase() {
		return mOpenHelper.getWritableDatabase();
	}

	private SQLiteDatabase getReadableDatabase() {
		return mOpenHelper.getReadableDatabase();
	}

	/**
	 * Insert user details in database.
	 * 
	 * @param userDetail
	 *            to add in database.
	 */
	public void addUserDetail(UserDetail userDetail) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(UserTable.COL_USER_CREATED, userDetail.getUserCreated());
		values.put(UserTable.COL_USER_DOB, userDetail.getUserDob());
		values.put(UserTable.COL_USER_EMAIL, userDetail.getUserEmail());
		values.put(UserTable.COL_USER_FB_ACCESS_TOKEN,userDetail.getUserFbToken());
		values.put(UserTable.COL_USER_FB_LINK, userDetail.getUserFbLink());
		values.put(UserTable.COL_USER_FB_UID, userDetail.getUserFbUid());
		values.put(UserTable.COL_USER_FIRST_NAME, userDetail.getUserFirstName());
		
		values.put(UserTable.COL_USER_GENDER, userDetail.getUserGender());
		
		values.put(UserTable.COL_USER_ID, userDetail.getUserId());
		values.put(UserTable.COL_USER_MANNUAL_LOGIN,
				userDetail.isUserManualLogin());
		values.put(UserTable.COL_USER_LAST_NAME, userDetail.getUserLastName());
		values.put(UserTable.COL_USER_LOCATION, userDetail.getUserLocation());
		values.put(UserTable.COL_USER_MODIFIED, userDetail.getUserModified());
		values.put(UserTable.COL_USER_POINTS, userDetail.getUserPoints());
		values.put(UserTable.COL_USER_NAME, userDetail.getUserName());

		db.insert(UserTable.USER_TABLE, null, values);
		
	}

	/**
	 * Returns user details of specified user id.
	 * 
	 * @param userId
	 *            to get user details.
	 * @return User details.
	 */
	public UserDetail getUserDetail(String userId) {
		if (userId == null) {
			return null;
		}

		UserDetail userDetail = new UserDetail();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(UserTable.USER_TABLE, null,
				UserTable.COL_USER_ID + " =?", new String[] { userId }, null,
				null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			userDetail.setUserCreated(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_CREATED)));
			userDetail.setUserDob(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_DOB)));
			userDetail.setUserEmail(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_EMAIL)));
			userDetail.setUserFbToken(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_FB_ACCESS_TOKEN)));
			userDetail.setUserFbLink(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_FB_LINK)));
			userDetail.setUserFbUid(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_FB_UID)));
			userDetail.setUserFirstName(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_FIRST_NAME)));

			userDetail.setUserGender(cursor.getInt(cursor.getColumnIndex(LoginTable.COL_USER_GENDER)));
			
			userDetail.setUserId(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_ID)));
			userDetail.setUserLastName(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_LAST_NAME)));
			userDetail.setUserLocation(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_LOCATION)));
			userDetail.setUserModified(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_MODIFIED)));
			userDetail.setUserPoints(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_POINTS)));
			userDetail.setUserName(cursor.getString(cursor.getColumnIndex(UserTable.COL_USER_NAME)));
		}

		

		return userDetail;
	}

	/**
	 * Inserts user details in database when user login.
	 * 
	 * @param userDetail
	 *            to insert in database.
	 */
	public void loginUser(UserDetail userDetail) {
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LoginTable.COL_USER_ID, userDetail.getUserId());
		values.put(LoginTable.COL_USER_NAME, userDetail.getUserName());
		values.put(LoginTable.COL_USER_EMAIL, userDetail.getUserEmail());
		values.put(LoginTable.COL_USER_ADDRESS, userDetail.getUserLocation());
		values.put(LoginTable.COL_USER_PASSWORD, userDetail.getUserPassword());
		values.put(LoginTable.COL_USER_PIC, userDetail.getImageUrl());
		values.put(LoginTable.COL_USER_GENDER, userDetail.getUserGender());
		
		int rowUpadted = db.update(LoginTable.TABLE_NAME, values, LoginTable.COL_USER_ID+" = ?", new String[]{userDetail.getUserId()});
		if(rowUpadted<=0){
			long rowId = db.insert(LoginTable.TABLE_NAME, null, values);
			if(rowId<0){
				AtnUtils.log("Error Occured in insert user object");
			}
			UserDataPool.getInstance().setUserLoggedIn(true);
		}
	}

	/**
	 * Removes user details from database when user log out.
	 * 
	 * @param userId
	 *            to remove user details.
	 */
	public void logoutUser(String userId) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(LoginTable.TABLE_NAME, LoginTable.COL_USER_ID + "=?", new String[] { userId });
	}

	/**
	 * Returns the currently logged-in user details from database.
	 */
	public UserDetail getLoggedInUser() {
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(LoginTable.TABLE_NAME, null, null, null, null,null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			UserDetail userDetail = new UserDetail();
			userDetail.setUserId(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_ID)));
			userDetail.setUserName(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_NAME)));
			userDetail.setUserPassword(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_PASSWORD)));
			userDetail.setUserEmail(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_EMAIL)));
			userDetail.setUserLocation(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_ADDRESS)));
			userDetail.setImageUrl(cursor.getString(cursor.getColumnIndex(LoginTable.COL_USER_PIC)));
			userDetail.setUserGender(cursor.getInt(cursor.getColumnIndex(LoginTable.COL_USER_GENDER)));
			cursor.close();
			return userDetail;
		}
		if(cursor!=null){cursor.close();};
		
		return null;
	}

		public synchronized void deleteBusinessBar(String businessId) {
			
			SQLiteDatabase db = getWritableDatabase();
			db.delete(PromotionTable.PROMOTION_TABLE, PromotionTable.COL_PROMOTION_BUSINESS_ID + " IS ?", new String[] { businessId });
			
		}
		
	
	
	public synchronized void deleteBusinessBar() {
		
		SQLiteDatabase db = getWritableDatabase();
		db.delete(PromotionTable.PROMOTION_TABLE, PromotionTable.COL_PROMOTION_ACCEPTED + " IS NOT ?", new String[] { TRUE });
		
	}

	public synchronized void deletePromotionsBar() {
		
		SQLiteDatabase db = getWritableDatabase();
		db.delete(PromotionTable.PROMOTION_TABLE, PromotionTable.COL_PROMOTION_ACCEPTED + " IS ?", new String[] { TRUE });
	}
	
	public synchronized void insertOrUpdate(ContentValues values) {
		
		AtnRegisteredVenueData atnBar = getAtnBusinessDetail(values.getAsString(BusinessTable.COL_BUSINESS_ID));
		String newFsId = "";
		if(values.containsKey(BusinessTable.COL_BUSINESS_FS_LINK_ID)) {
			newFsId = values.getAsString(BusinessTable.COL_BUSINESS_FS_LINK_ID);
		}
		
		if(atnBar!=null&&!TextUtils.isEmpty(atnBar.getBusinessFoursquareVenueId())&&!newFsId.equals(atnBar.getBusinessFoursquareVenueId())) {
			ContentValues value = new ContentValues();
			value.put(Atn.Venue.VENUE_ID, atnBar.getBusinessFoursquareVenueId());
			value.put(Atn.Venue.FOLLOWED, VenueModel.NON_ATN_BAR);
			Atn.Venue.update(value, AtnApp.getAppContext());
		}
		
		SQLiteDatabase db = getWritableDatabase();
		int rowId = db.update(BusinessTable.BUSINESS_TABLE, values,
				BusinessTable.COL_BUSINESS_ID + " = ? ", 
				new String[] { values.getAsString(BusinessTable.COL_BUSINESS_ID) });
		if(rowId==0){
			db.insert(BusinessTable.BUSINESS_TABLE, null, values);
		}
	}
	
	
	/**
	 * Inserts bulk favorite business details in database. This is used by
	 * following tab to add favorite business in database that is recevied from
	 * web service response.
	 * 
	 * @param userId
	 * @param venueList
	 *            list of business to add in database.
	 */
	public synchronized void addBulkBusinessDetail(String userId, ArrayList<AtnRegisteredVenueData> venueList) {
		
		for (AtnRegisteredVenueData venueData : venueList) {
			this.addBusinessDetail(venueData);
		}
	}

	/**
	 * Add favorite business detail.
	 * 
	 * @param userId to add corresponding business details.
	 * @param venueData to add in database.
	 */
	public synchronized void addBusinessDetail(AtnRegisteredVenueData venueData) {
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BusinessTable.COL_BUSINESS_CITY, venueData.getBusinessCity());
		values.put(BusinessTable.COL_BUSINESS_CREATED,venueData.getBusinessCreated());
		values.put(BusinessTable.COL_BUSINESS_FAVORITED,venueData.isFavorited());
		values.put(BusinessTable.COL_BUSINESS_FB_LINK,venueData.getBusinessFacebookLink());
		values.put(BusinessTable.COL_BUSINESS_FB_LINK_ID,venueData.getBusinessFacebookLinkId());
		values.put(BusinessTable.COL_BUSINESS_FS_LINK,venueData.getBusinessFoursquareVenueLink());
		values.put(BusinessTable.COL_BUSINESS_FS_LINK_ID,venueData.getBusinessFoursquareVenueId());
		values.put(BusinessTable.COL_BUSINESS_ID, venueData.getBusinessId());
		values.put(BusinessTable.COL_BUSINESS_LAT, venueData.getBusinessLat());
		values.put(BusinessTable.COL_BUSINESS_LOGO,venueData.getBusinessImageUrl());
		values.put(BusinessTable.COL_BUSINESS_LON, venueData.getBusinessLng());
		values.put(BusinessTable.COL_BUSINESS_MODIFIED,venueData.getBusinessModified());
		values.put(BusinessTable.COL_BUSINESS_NAME, venueData.getBusinessName());
		values.put(BusinessTable.COL_BUSINESS_PHONE,venueData.getBusinessPhone());
		values.put(BusinessTable.COL_BUSINESS_SHARED,venueData.getBusinessShared());
		values.put(BusinessTable.COL_BUSINESS_STATE,venueData.getBusinessState());
		values.put(BusinessTable.COL_BUSINESS_STATUS,venueData.getBusinessStatus());
		values.put(BusinessTable.COL_BUSINESS_STREET,venueData.getBusinessStreet());
		values.put(BusinessTable.COL_BUSINESS_SUBSCRIBED,venueData.isSubscribed());
		values.put(BusinessTable.COL_BUSINESS_ZIP, venueData.getBusinessZip());
		values.put(BusinessTable.COL_IS_REGISTERED,venueData.isRegisterdVenue());
		values.put(BusinessTable.COL_BUSINESS_FS_CAT_ID,venueData.getVenueCategoryId());
		
		int count = db.update(BusinessTable.BUSINESS_TABLE, values, BusinessTable.COL_BUSINESS_ID+" = ? ", new String[]{venueData.getBusinessId()});
		if(count==0){
			db.insert(BusinessTable.BUSINESS_TABLE, null, values);
		}

		for (int i = 0; i < venueData.getBulkPromotion().size(); i++){
			   addPromotionDetail(venueData.getPromotion(i));
		}
	}

	public synchronized void updateBusinessFavoriteStatus(String businessId, boolean value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BusinessTable.COL_BUSINESS_ID, businessId);
		values.put(BusinessTable.COL_BUSINESS_FAVORITED, value);
		db.update(BusinessTable.BUSINESS_TABLE, values,
				BusinessTable.COL_BUSINESS_ID + " = ?",
				new String[] { businessId });
	}

	/**
	 * Returns all the Atn venue data saved in database.
	 * 
	 * @return list of registered Atn venues.
	 */
	public synchronized ArrayList<AtnOfferData> getBulkVenueDetails(boolean isAccepted) {

		ArrayList<AtnOfferData> atnVenueList = new ArrayList<AtnOfferData>();
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(BusinessTable.BUSINESS_TABLE, null, null,null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				AtnRegisteredVenueData venueData = new AtnRegisteredVenueData();
				venueData.setBusinessCity(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_CITY)));
				venueData.setBusinessFacebookLink(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK)));
				venueData.setBusinessFacebookLinkId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK_ID)));
				venueData.setBusinessFoursquareVenueId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK_ID)));
				venueData.setBusinessFoursquareVenueLink(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK)));
				venueData.setBusinessId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_ID)));
				venueData.setBusinessImageUrl(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LOGO)));
				venueData.setBusinessLat(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LAT)));
				venueData.setBusinessLng(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LON)));
				venueData.setBusinessModified(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_MODIFIED)));
				venueData.setBusinessName(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_NAME)));
				venueData.setBusinessPhone(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_PHONE)));
				venueData.setBusinessShared(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_SHARED)));
				venueData.setBusinessState(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STATE)));
				venueData.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STATUS)));
				venueData.setBusinessStreet(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STREET)));
				venueData.setBusinessZip(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_ZIP)));
				venueData.setVenueCategoryId(cursor.getInt(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_CAT_ID)));

				if (cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FAVORITED)).equalsIgnoreCase(TRUE))
					venueData.setFavorited(true);
				else
					venueData.setFavorited(false);

				if (cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_SUBSCRIBED)).equalsIgnoreCase(TRUE))
					venueData.setSubscribed(true);
				else
					venueData.setSubscribed(false);

				if (cursor.getString(
						cursor.getColumnIndex(BusinessTable.COL_IS_REGISTERED))
						.equalsIgnoreCase(TRUE))
					venueData.setRegisterdVenue(true);
				else
					venueData.setRegisterdVenue(false);
				
				if (isAccepted) {
					List<AtnPromotion> listPro = getBulkPromotionDetail(venueData.getBusinessId(), isAccepted);
					if (listPro != null && listPro.size() > 0) {
						venueData.addBulkPromotion((ArrayList<AtnPromotion>) listPro);
						atnVenueList.add(venueData);
					}
				} else {
					venueData.addBulkPromotion(getBulkPromotionDetail(venueData.getBusinessId(), isAccepted));
					atnVenueList.add(venueData);
				}
			} while (cursor.moveToNext());
		}
		if(cursor!=null){cursor.close();};
		return atnVenueList;
	}

	/**
	 * Returns all user's favorite businesses
	 * 
	 * @return
	 */
	public synchronized ArrayList<AtnOfferData> getBulkFavoriteVenueDetails() {
		ArrayList<AtnOfferData> atnVenueList = new ArrayList<AtnOfferData>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BusinessTable.BUSINESS_TABLE, null, BusinessTable.COL_BUSINESS_FAVORITED+" = ?", new String[]{TRUE}, null, null, null);
		if(cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			do {
				AtnRegisteredVenueData venueData = new AtnRegisteredVenueData();
				venueData.setFavorited(true);
				venueData.setBusinessCity(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_CITY)));
				venueData.setBusinessFacebookLink(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK)));
				venueData.setBusinessFacebookLinkId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK_ID)));
				venueData.setBusinessFoursquareVenueId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK_ID)));
				venueData.setBusinessFoursquareVenueLink(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK)));
				venueData.setBusinessId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_ID)));
				venueData.setBusinessImageUrl(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LOGO)));
				venueData.setBusinessLat(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LAT)));
				venueData.setBusinessLng(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_LON)));
				venueData.setBusinessModified(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_MODIFIED)));
				venueData.setBusinessName(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_NAME)));
				venueData.setBusinessPhone(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_PHONE)));
				venueData.setBusinessShared(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_SHARED)));
				venueData.setBusinessState(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STATE)));
				venueData.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STATUS)));
				venueData.setBusinessStreet(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_STREET)));
				venueData.setBusinessZip(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_ZIP)));
				venueData.setVenueCategoryId(cursor.getInt(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_CAT_ID)));
				
				if(!TextUtils.isEmpty(venueData.getBusinessFoursquareVenueId())){
					venueData.setFsVenueModel(Atn.Venue.getVenue(venueData.getBusinessFoursquareVenueId(), AtnApp.getAppContext()));
				}
				
				if(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_SUBSCRIBED)).equalsIgnoreCase(TRUE))
					venueData.setSubscribed(true);
				else
					venueData.setSubscribed(false);
				
				if(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_IS_REGISTERED)).equalsIgnoreCase(TRUE))
					venueData.setRegisterdVenue(true);
				else
					venueData.setRegisterdVenue(false);
				
				venueData.addBulkPromotion(getBulkPromotionDetail(venueData.getBusinessId(),false));
				atnVenueList.add(venueData);
			
			} while (cursor.moveToNext());
			
			}
		if(cursor!=null){cursor.close();};
		return atnVenueList;
	}

	
	/**
	 * Returns ATN registered venue details using specified ATN registered
	 * business id.
	 * 
	 * @param businessId
	 *            to get ATN venue details.
	 * @return ATN registered venue details.
	 */
	public synchronized AtnRegisteredVenueData getAtnBusinessDetail(String businessId) {
		
		AtnRegisteredVenueData venueData = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BusinessTable.BUSINESS_TABLE, null, BusinessTable.COL_BUSINESS_ID + " = ? OR "+BusinessTable.COL_BUSINESS_FS_LINK_ID+" = ?",
				new String[] { businessId,businessId }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			venueData = new AtnRegisteredVenueData();
			cursor.moveToFirst();

			venueData.setBusinessCity(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_CITY)));
			venueData.setBusinessFacebookLink(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK)));
			venueData.setBusinessFacebookLinkId(cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FB_LINK_ID)));
			venueData.setBusinessFoursquareVenueId(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK_ID)));
			venueData.setBusinessFoursquareVenueLink(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_FS_LINK)));
			venueData.setBusinessId(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_ID)));
			venueData.setBusinessImageUrl(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_LOGO)));
			venueData.setBusinessLat(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_LAT)));
			venueData.setBusinessLng(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_LON)));
			venueData.setBusinessModified(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_MODIFIED)));
			venueData.setBusinessName(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_NAME)));
			venueData.setBusinessPhone(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_PHONE)));
			venueData.setBusinessShared(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_SHARED)));
			venueData.setBusinessState(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_STATE)));
			venueData.setBusinessStatus(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_STATUS)));
			venueData.setBusinessStreet(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_STREET)));
			venueData.setBusinessZip(cursor.getString(cursor
					.getColumnIndex(BusinessTable.COL_BUSINESS_ZIP)));

			venueData.setVenueCategoryId(cursor.getInt(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FS_CAT_ID)));
			
			if (cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_FAVORITED))
					.equalsIgnoreCase(TRUE))
				venueData.setFavorited(true);
			else
				venueData.setFavorited(false);

			if (cursor.getString(cursor.getColumnIndex(BusinessTable.COL_BUSINESS_SUBSCRIBED))
					.equalsIgnoreCase(TRUE))
				venueData.setSubscribed(true);
			else
				venueData.setSubscribed(false);

			if (cursor.getString(cursor.getColumnIndex(BusinessTable.COL_IS_REGISTERED))
					.equalsIgnoreCase(TRUE))
				venueData.setRegisterdVenue(true);
			else
				venueData.setRegisterdVenue(false);

			venueData.addBulkPromotion(getBulkPromotionDetail(venueData.getBusinessId(), false));
		
			if(!TextUtils.isEmpty(venueData.getBusinessFoursquareVenueId())){
				venueData.setFsVenueModel(Atn.Venue.getVenue(venueData.getBusinessFoursquareVenueId(), AtnApp.getAppContext()));
			}			
		}

		if(cursor!=null){cursor.close();};
		return venueData;
	}

	

	public synchronized void insertOrUpdatePromotion(ContentValues values){
		SQLiteDatabase db = getWritableDatabase();
		int count = db.update(PromotionTable.PROMOTION_TABLE,values,
						PromotionTable.COL_PROMOTION_BUSINESS_ID + " = ? AND "+ PromotionTable.COL_PROMOTION_ID + " = ?",
						new String[] {values.getAsString(PromotionTable.COL_PROMOTION_BUSINESS_ID),
										values.getAsString(PromotionTable.COL_PROMOTION_ID) });
		if(count==0){
			db.insert(PromotionTable.PROMOTION_TABLE, null, values);
		}
	}
	
	/**
	 * Inserts promotion detail in database.
	 * 
	 * @param mTipDetail to insert.
	 */
	public synchronized void addPromotionDetail(AtnPromotion promotionDetail) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(PromotionTable.COL_PROMOTION_ACCEPTED, promotionDetail.isAccepted());
		values.put(PromotionTable.COL_PROMOTION_AGE_MAX, promotionDetail.getAgeMax());
		values.put(PromotionTable.COL_PROMOTION_AGE_MIN, promotionDetail.getAgeMin());
		values.put(PromotionTable.COL_PROMOTION_BUSINESS_ID, promotionDetail.getBusinessId());
		values.put(PromotionTable.COL_PROMOTION_COUPON_EXPIRY_DATE, promotionDetail.getCouponExpiryDate());
		values.put(PromotionTable.COL_PROMOTION_CREATED, promotionDetail.getPromotionCreated());
		values.put(PromotionTable.COL_PROMOTION_DETAILS, promotionDetail.getPromotionDetail());
		values.put(PromotionTable.COL_PROMOTION_END_DATE, promotionDetail.getEndDate());
		values.put(PromotionTable.COL_PROMOTION_GROUP_COUNT, promotionDetail.getPromotionGroupCount());
		values.put(PromotionTable.COL_PROMOTION_HIGH_RES_IMAGE, promotionDetail.getPromotionImageLargeUrl());
		values.put(PromotionTable.COL_PROMOTION_ID, promotionDetail.getPromotionId());
		values.put(PromotionTable.COL_PROMOTION_LOW_RES_IMAGE, promotionDetail.getPromotionImageSmallUrl());
		values.put(PromotionTable.COL_PROMOTION_MODIFIED, promotionDetail.getPromotionModified());
		values.put(PromotionTable.COL_PROMOTION_REDEEMED, promotionDetail.isRedeemed());
		values.put(PromotionTable.COL_PROMOTION_SEX, promotionDetail.getSex());
		values.put(PromotionTable.COL_PROMOTION_SHARED, promotionDetail.isShared());
		values.put(PromotionTable.COL_PROMOTION_START_DATE, promotionDetail.getStartDate());
		values.put(PromotionTable.COL_PROMOTION_STATUS, promotionDetail.getPromotionStatus());
		values.put(PromotionTable.COL_PROMOTION_TITLE, promotionDetail.getPromotionTitle());
		values.put(PromotionTable.COL_PROMOTION_TYPE, promotionDetail.getPromotionType().toString());
		values.put(PromotionTable.COL_PROMOTION_LOGO_URL, promotionDetail.getPromotionLogoUrl());
		
		int count = db.update(PromotionTable.PROMOTION_TABLE, values, PromotionTable.COL_PROMOTION_BUSINESS_ID+" = ? AND "+PromotionTable.COL_PROMOTION_ID+" = ?", new String[]{promotionDetail.getBusinessId(),promotionDetail.getPromotionId()});
		if(count==0){
			db.insert(PromotionTable.PROMOTION_TABLE, null, values);
		}
	}

	/**
	 * Add promotion to my deals.
	 * 
	 * @param promotionId to add
	 * @param status to update.
	 */
	
	///TO DO: ROHIT
	public synchronized void updatePromotionAddedStatus(String promotionId, boolean status) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PromotionTable.COL_PROMOTION_ID, promotionId);
		values.put(PromotionTable.COL_PROMOTION_ACCEPTED, status);
		db.update(PromotionTable.PROMOTION_TABLE, values, PromotionTable.COL_PROMOTION_ID + " = ?", new String[] { promotionId });
	}
	
	
	/**
	 * Updates promotion status for specified promotion id.
	 * @param promotionId
	 * @param promotionType
	 */
	public synchronized void updatePromotionStatus(String promotionId, PromotionType promotionType) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PromotionTable.COL_PROMOTION_ID, promotionId);
		values.put(PromotionTable.COL_PROMOTION_TYPE, promotionType.name());
		db.update(PromotionTable.PROMOTION_TABLE, values, PromotionTable.COL_PROMOTION_ID + " = ?", new String[]{promotionId});
	}
	

	public synchronized boolean isPromotionExist(String promotionId){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(PromotionTable.PROMOTION_TABLE, null, PromotionTable.COL_PROMOTION_ID + " = ?", new String[] { promotionId }, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		if(cursor!=null){cursor.close();};
		return false;
	}
	
	/**
	 * Returns promotion details using specified promotionId.
	 * 
	 * @param value promotion id to get promotion details.
	 * @return promotion details.
	 */
	public synchronized AtnPromotion getPromotionDetail(String promotionId) {
		
		AtnPromotion promotionDetail = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(PromotionTable.PROMOTION_TABLE, null, PromotionTable.COL_PROMOTION_ID + " = ?", new String[] { promotionId }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			promotionDetail = new AtnPromotion();
			cursor.moveToFirst();

			if (cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_ACCEPTED)).equalsIgnoreCase(TRUE))
				promotionDetail.setAccepted(true);
			else
				promotionDetail.setAccepted(false);

			promotionDetail.setAgeMax(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_AGE_MAX)));
			promotionDetail.setAgeMin(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_AGE_MIN)));
			promotionDetail.setBusinessId(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_BUSINESS_ID)));
			promotionDetail.setCouponExpiryDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_COUPON_EXPIRY_DATE)));
			promotionDetail.setPromotionCreated(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_CREATED)));
			promotionDetail.setPromotionDetail(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_DETAILS)));
			promotionDetail.setEndDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_END_DATE)));
			promotionDetail.setPromotionGroupCount(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_GROUP_COUNT)));
			promotionDetail.setPromotionId(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_ID)));
			promotionDetail.setPromotionModified(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_MODIFIED)));
			promotionDetail.setPromotionImageSmallUrl(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_LOW_RES_IMAGE)));
			promotionDetail.setStartDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_START_DATE)));

			String redeemTxt = cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_REDEEMED));
			if (redeemTxt!=null&&redeemTxt.equalsIgnoreCase(TRUE))
				promotionDetail.setRedeemed(true);
			else
				promotionDetail.setRedeemed(false);

			promotionDetail.setPromotionImageLargeUrl(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_HIGH_RES_IMAGE)));
			promotionDetail.setSex(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_SEX)));

			promotionDetail.setPromotionTitle(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_TITLE)));
			
			promotionDetail.setPromotionStatus(cursor.getInt(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_STATUS)));

			if (cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_SHARED)) == null)
				promotionDetail.setShared(false);
			else
				promotionDetail.setShared(true);

			String type = cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_TYPE));
			if (type.equals(PromotionType.Event.toString()))
				promotionDetail.setPromotionType(PromotionType.Event);
			else if (type.equals(PromotionType.Offer.toString()))
				promotionDetail.setPromotionType(PromotionType.Offer);

		}
		
		if(cursor!=null){cursor.close();};
		
		return promotionDetail;
	}

	/**
	 * Returns bulk promotion details using specified businessId.
	 * 
	 * @param businessId
	 *            to get promotion details.
	 * @return promotion details.
	 */
	public synchronized ArrayList<AtnPromotion> getBulkPromotionDetail(String businessId, boolean isAccepted) {
		
		ArrayList<AtnPromotion> promotionList = new ArrayList<AtnPromotion>();
		AtnPromotion promotionDetail;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		if (isAccepted) {
			cursor = db.query(PromotionTable.PROMOTION_TABLE, null, PromotionTable.COL_PROMOTION_BUSINESS_ID + " =? AND "
							+ PromotionTable.COL_PROMOTION_ACCEPTED + " = ?", new String[] { businessId, TRUE }, null, null, null);
		} else {
			cursor = db.query(PromotionTable.PROMOTION_TABLE, null, PromotionTable.COL_PROMOTION_BUSINESS_ID + " =?",
					new String[] { businessId }, null, null, null);
		}

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				promotionDetail = new AtnPromotion();
				if (cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_ACCEPTED)).equalsIgnoreCase(TRUE)) {
					promotionDetail.setAccepted(true);
				} else {
					promotionDetail.setAccepted(false);
				}
				promotionDetail.setAgeMax(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_AGE_MAX)));
				promotionDetail.setAgeMin(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_AGE_MIN)));
				promotionDetail.setBusinessId(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_BUSINESS_ID)));
				promotionDetail.setCouponExpiryDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_COUPON_EXPIRY_DATE)));
				promotionDetail.setPromotionCreated(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_CREATED)));
				promotionDetail.setPromotionDetail(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_DETAILS)));
				promotionDetail.setEndDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_END_DATE)));
				promotionDetail.setStartDate(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_START_DATE)));
				promotionDetail.setPromotionGroupCount(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_GROUP_COUNT)));
				promotionDetail.setPromotionId(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_ID)));
				promotionDetail.setPromotionModified(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_MODIFIED)));
				promotionDetail.setPromotionImageSmallUrl(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_LOW_RES_IMAGE)));
				promotionDetail.setPromotionLogoUrl(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_LOGO_URL)));

				String reddem = cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_REDEEMED));
				if (reddem!=null&&reddem.equalsIgnoreCase(TRUE)) {
					promotionDetail.setRedeemed(true);
				} else {
					promotionDetail.setRedeemed(false);
				}

				promotionDetail.setPromotionImageLargeUrl(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_HIGH_RES_IMAGE)));
				promotionDetail.setSex(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_SEX)));
				promotionDetail.setPromotionTitle(cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_TITLE)));
				promotionDetail.setPromotionStatus(cursor.getInt(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_STATUS)));

				if (cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_SHARED)).equalsIgnoreCase(TRUE)) {
					promotionDetail.setShared(false);
				} else {
					promotionDetail.setShared(true);
				}
				
				String type = cursor.getString(cursor.getColumnIndex(PromotionTable.COL_PROMOTION_TYPE));
				if (type.equals(PromotionType.Event.toString())) {
					promotionDetail.setPromotionType(PromotionType.Event);
				} else if (type.equals(PromotionType.Offer.toString())) {
					promotionDetail.setPromotionType(PromotionType.Offer);
				}
				promotionList.add(promotionDetail);
				
			} while (cursor.moveToNext());
		}
		if(cursor!=null){cursor.close();};
		return promotionList;
	}

	
	
	/**
	 * Saves Facebook token of specified user into database.
	 * 
	 * @param userId
	 * @param fbToken
	 *            of the user.
	 */

	public synchronized int updateCouponStatus(String promotionId, int status) {
		SQLiteDatabase db = getWritableDatabase();
		int result = -1;
		ContentValues values = new ContentValues();
		values.put(PromotionTable.COL_PROMOTION_STATUS, status);
		result = db.update(PromotionTable.PROMOTION_TABLE, values,
				PromotionTable.COL_PROMOTION_ID + " = ?",
				new String[] { promotionId });
		return result;
	}

	public synchronized int updatePromotion(ContentValues values, String promotionId) {
		SQLiteDatabase db = getWritableDatabase();
		int result = -1;
		result = db.update(PromotionTable.PROMOTION_TABLE, values,PromotionTable.COL_PROMOTION_ID + " = ?",
				new String[] { promotionId });
		return result;
	}
	
	
	/**
	 * Updates the subsciption status of venue.
	 * 
	 * @param businessId
	 *            to update status.
	 * @param isSubscibed
	 *            subscribe/unsubscribe
	 */
	public synchronized void updateSubscriptionStatus(String businessId, boolean isSubscibed) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(BusinessTable.COL_BUSINESS_SUBSCRIBED, isSubscibed);

		db.update(BusinessTable.BUSINESS_TABLE, values,
				BusinessTable.COL_BUSINESS_ID + " = ?",
				new String[] { businessId });
	}

	public synchronized int getPromotionStatus(String promotionId) {
		int status = 0;
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT " + PromotionTable.COL_PROMOTION_STATUS
				+ " FROM " + PromotionTable.PROMOTION_TABLE + " WHERE "
				+ PromotionTable.COL_PROMOTION_ID + " = " + promotionId;

		Cursor c = db.rawQuery(query, null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			status = c.getInt(c.getColumnIndex(PromotionTable.COL_PROMOTION_STATUS));
		}
		if(c!=null){
			c.close();
		}
		return status;

	}

	/**
	 * Updates the subsciption status of for all ATN registered venues.
	 * 
	 * @param isSubscibed
	 *            to update status.
	 */
	public synchronized void updateSubscriptionStatusForAllBusiness(boolean isSubscibed) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BusinessTable.COL_BUSINESS_SUBSCRIBED, isSubscibed);
		db.update(BusinessTable.BUSINESS_TABLE, values, null, null);
	}

	/**
	 * Updates promotion expire Time for specified promotion id.
	 * @param promotionId
	 * @param expiredTime
	 */
	public synchronized void updatePromotionExpireTime(String promotionId, String expiredTime) {
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PromotionTable.COL_PROMOTION_ID, promotionId);
		values.put(PromotionTable.COL_PROMOTION_COUPON_EXPIRY_DATE, expiredTime);
		db.update(PromotionTable.PROMOTION_TABLE, values, PromotionTable.COL_PROMOTION_ID + " = ?", new String[]{promotionId});
	}
	
	/**
	 * Clears all the database tables when user logout.
	 */
	public void clearDatabase() {

		SQLiteDatabase db = getWritableDatabase();
		db.delete(BusinessTable.BUSINESS_TABLE, null, null);
		db.delete(LoginTable.TABLE_NAME, null, null);
		db.delete(PromotionTable.PROMOTION_TABLE, null, null);
		db.delete(UserTable.USER_TABLE, null, null);
		UserDataPool.getInstance().setUserDetail(null);
	}

}
