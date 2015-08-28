/**
 * @Copyright Coppermobile 2014.
 * 
 * **/

package com.atn.app.utils;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.text.TextUtils;

import com.atn.app.datamodels.AnchorCategory;
import com.atn.app.pool.UserDataPool;

/**
 * utility class for shared preferences.
 * **/
public class SharedPrefUtils {

	public static final String ANCHOR_PREF = "ATN_APP_PREF";
	private static final String CATEGORY_PREF = "CATEGORY_PREF";
	//location pref
	private static final String LOCATION_SHARED_PREF = "location_shared_preferences";

	// check user login status
	private static final String IS_USER_LOGGEDIN = "IS_USER_LOGGEDIN";

	// /set true if FS Venue loaded once success fully so that on press refresh,
	// we just need refresh venue media data from instagram api. otherwise load
	// fresh data.
	public static final String IS_LAST_DATA_LOAD_SUCCESS = "IS_LAST_DATA_LOAD_SUCCESS";

	//timestamp category load from server
	public static final String CATEGORY_LOAD_DATE = "CATEGORY_LOAD_DATE";

	//true if all category selected in filter 
	public static final String IS_ALL_SELECTED = "IS_ALL_SELECTED";
	//save comma separated Filter category ids
	public static final String SELECTED_CATEGORY_IDS= "SELECTED_CATEGORY_IDS";

	public static final String LAST_CATEGORY_LOAD_TIME= "LAST_CATEGORY_LOAD_TIME";

	// Search Text Key
	public static final String SEARCH_TEXT_VALUE_KEY= "SEARCH_TEXT_VALUE_KEY";

	// happening now selected state
	public static final String HAPPENING_NOW_SORT_STATE= "HAPPENING_NOW_SORT_STATE";

	//save true if user login first time to show help screen
	public static final String IS_USER_LOGIN_FIRST_TIME= "IS_USER_LOGIN_FIRST_TIME";
	//check if user press first time browsing button To show filter screen once
	public static final String IS_FIRST_TIME_BROWSING= "IS_FIRST_TIME_BROWSING";


	private static final String LAST_LAT = "last_lat_value";
	private static final String LAST_LNG = "last_lng_value";


	//save screen with for foursquare images
	private static final String SCREEN_WIDTH = "screen_width";

	///get SharedPreferences object by name .
	private static SharedPreferences getPreferenceByName(Context context,String name){
		try {
			return context.getSharedPreferences(name, 0);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * save user loggin status
	 * @param context application context
	 * @param isLoggedIn true if user logged in otherwise false.
	 * */
	public static void setUserLoggedIn(Context context,boolean isLoggedIn){
		getPreferenceByName(context,ANCHOR_PREF).edit()
		.putBoolean(IS_USER_LOGGEDIN, isLoggedIn).commit();
	}


	/**
	 * Get user login status
	 * */
	public static boolean isUserLoggedIn(Context context) {
		return getPreferenceByName(context,ANCHOR_PREF).getBoolean(IS_USER_LOGGEDIN, false);
	}

	/**
	 * save foursquare data fetch status
	 * @param context application context
	 * @param isSuccess true if data loaded successfully if false then user can reload data on refresh
	 * */
	public static void setFoursquareVenueStatus(Context context,boolean isSuccess) {
		getPreferenceByName(context,ANCHOR_PREF).edit()
		.putBoolean(IS_LAST_DATA_LOAD_SUCCESS, isSuccess).commit();
	}

	/**
	 * Get foursquare data status
	 * */
	public static boolean fSVenueLoadStatus(Context context) {
		return getPreferenceByName(context, ANCHOR_PREF).getBoolean(
				IS_LAST_DATA_LOAD_SUCCESS, false);
	}

	/*
	 * Return sharedpreference for category
	 * **/
	public static SharedPreferences getCategoryPref(Context context) {
		return context.getSharedPreferences(CATEGORY_PREF, 0);
	}

	/**
	 * Return true if category loaded date is 4 days later.to load category again.
	 * We load categories on every fourth day
	 * */
	public static boolean isLoadCategories(Context context) {
		SharedPreferences pref = getPreferenceByName(context,CATEGORY_PREF);
		long lng = pref.getLong(CATEGORY_LOAD_DATE, 0);
		long different = System.currentTimeMillis() - lng;
		long days = TimeUnit.MILLISECONDS.toDays(different);

		if(days >= 4) {
			return true;
		}
		return false;
	}

	/**
	 * Save category loaded date 
	 * **/
	public static void saveCategoryLoadDate(Context context) {
		SharedPreferences pref = getPreferenceByName(context, CATEGORY_PREF);
		pref.edit().putLong(CATEGORY_LOAD_DATE, System.currentTimeMillis())
		.commit();
	}

	/**
	 * Return true if all filter category is selected.
	 * */
	public static boolean isAllCategoriesSelected(Context context) {
		SharedPreferences prefence= getPreferenceByName(context, CATEGORY_PREF);
		if(prefence != null)
		{
			return prefence.getBoolean(IS_ALL_SELECTED, true);
		}
		
		return true;
	}

	/**
	 * Save all category selected status
	 * */
		public static void setAllCategorySelected(Context context,
											  boolean allSelected) {
		getPreferenceByName(context, CATEGORY_PREF).edit()
				.putBoolean(IS_ALL_SELECTED, allSelected).commit();
	}

	/**
	 * Save selected category id for filter query
	 * @param category list
	 * */
	public static void saveFilterQuery(Context context,ArrayList<AnchorCategory> list) {

		if (list==null) return;

		boolean isAllSelected = true;
		StringBuilder filterStr = new StringBuilder();
		for (AnchorCategory anchorCategory : list) {

			if (anchorCategory.status == 1)
				filterStr.append(anchorCategory.id+",");
			else
				isAllSelected = false;
		}

		setAllCategorySelected(context, isAllSelected);

		//remove last comma.
		if(filterStr.length()>0) {
			filterStr.deleteCharAt(filterStr.length()-1);
		}

		getPreferenceByName(context, CATEGORY_PREF).edit()
		.putString(SELECTED_CATEGORY_IDS, filterStr.toString())
		.commit();

		///try to load on server;
		if(UserDataPool.getInstance().isUserLoggedIn()) {
			AnchorCategory.saveCategoryOnServer(context, filterStr.toString());
		}
	}

	//return filter ids
	public static String getFilterCategories(Context context) {
		return getPreferenceByName(context, CATEGORY_PREF).getString(SELECTED_CATEGORY_IDS, "");
	}

	//return screen width for foursuare venue pic
	public static int getScreenWith(Context context) {
		return getPreferenceByName(context, ANCHOR_PREF).getInt(SCREEN_WIDTH, 320);
	}

	//save screen width for creating foursquare venue image url
	public static void saveScreenWidth(Context context,int screenWidth) {
		getPreferenceByName(context, ANCHOR_PREF).edit().putInt(SCREEN_WIDTH, screenWidth).commit();
	}

	/*
	 * Set user first time login status
	 * **/
	public static void setUserFirstTimeLoginStatus(Context context,boolean isSuccess) {
		getPreferenceByName(context,ANCHOR_PREF).edit()
		.putBoolean(IS_USER_LOGIN_FIRST_TIME, isSuccess).commit();
	}

	//get user first time login status for showing help screen
	public static boolean isUserLoginFirstTime(Context context) {
		return getPreferenceByName(context, ANCHOR_PREF).getBoolean(
				IS_USER_LOGIN_FIRST_TIME, true);
	}

	/**
	 * return first time browsing status
	 * **/
	public static boolean isUserFirstTimeBrowsing(Context context) {
		return getPreferenceByName(context, ANCHOR_PREF).getBoolean(
				IS_FIRST_TIME_BROWSING, true);
	}

	/**
	 * set first time browsing status
	 * **/
	public static void setUserFirstTimeBrowsing(Context context,boolean isFirstBrowsing){
		getPreferenceByName(context,ANCHOR_PREF).edit()
		.putBoolean(IS_FIRST_TIME_BROWSING, isFirstBrowsing).commit();
	}

	/**
	 * @param context application context
	 * @param lat current location latitude
	 * @param lng current location longitude
	 * */
	public static void saveLocation(Context context,double lat,double lng) {
		final SharedPreferences locationPref = getPreferenceByName(context,LOCATION_SHARED_PREF);
		locationPref.edit().putString(LAST_LAT, String.valueOf(lat)).commit();
		locationPref.edit().putString(LAST_LNG, String.valueOf(lng)).commit();
	}

	/**
	 * return null  if location is not stored in preferences otherwise return location object
	 * */
	public static Location getLastLocation(Context context) {
		final SharedPreferences locationPref = getPreferenceByName(context,LOCATION_SHARED_PREF);
		String lat = locationPref.getString(LAST_LAT, null);
		String lng = locationPref.getString(LAST_LNG, null);
		if (!TextUtils.isEmpty(lat)&&!TextUtils.isEmpty(lng)) {
			Location currentLocation = new Location("");
			currentLocation.setLatitude(Double.parseDouble(lat));
			currentLocation.setLongitude(Double.parseDouble(lng));
			return currentLocation;
		}
		return  null;
	}

	//check weather we need to reload  if date is 4 days ago or not exist
	public static boolean isLoadCategory(Context context) {

		SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
		long lng = pref.getLong(LAST_CATEGORY_LOAD_TIME, 0);
		long different = System.currentTimeMillis() - lng;
		long days = TimeUnit.MILLISECONDS.toDays(different);
		if(days>=4){
			return true;
		}
		return false;
	}
	//save date on which category are loaded
	public static void saveCategoryDate(Context context) { 
		SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
		Editor edit = pref.edit();
		edit.putLong(LAST_CATEGORY_LOAD_TIME, System.currentTimeMillis());
		edit.commit();
	}

	//clear last location preference
	public static void clearLastLocation(Context context) {
		getPreferenceByName(context,LOCATION_SHARED_PREF).edit().clear().commit();
	}

	//clear application preference.
	public static void cleanAppPref(Context context) {
		getPreferenceByName(context, ANCHOR_PREF).edit().clear().commit();
	}


	public static void clearAll(Context context){
		getPreferenceByName(context,LOCATION_SHARED_PREF).edit().clear().commit();
		getPreferenceByName(context, ANCHOR_PREF).edit().clear().commit();
		getPreferenceByName(context, CATEGORY_PREF).edit().clear().commit();
	}

	//save date on which category are loaded
	public static void saveSearchText(Context context,String searchText) { 
		SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
		Editor edit = pref.edit();
		edit.putString(SEARCH_TEXT_VALUE_KEY, searchText);
		edit.commit();
	}

	//save date on which category are loaded
	public static String getSearchText(Context context) { 
		SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
		return pref.getString(SEARCH_TEXT_VALUE_KEY, "");

	}
	//save date on which category are loaded
	public static void clearSearchText(Context context) { 
		SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
		Editor edit = pref.edit();
		edit.putString(SEARCH_TEXT_VALUE_KEY, "");
		edit.commit();

	}		

	
	//save date on which category are loaded
		public static void setSelectedState(Context context,Boolean isNearBy) { 
			SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
			Editor edit = pref.edit();
			edit.putBoolean(HAPPENING_NOW_SORT_STATE, isNearBy);
			edit.commit();

		}
		//save date on which category are loaded
		public static Boolean getSelectedState(Context context) { 
			SharedPreferences pref = context.getSharedPreferences(CATEGORY_PREF, 0);
			return pref.getBoolean(HAPPENING_NOW_SORT_STATE, false);

		}		

}
