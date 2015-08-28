/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.database.handler;

/**
 * Represent Anchor business table
 * */
public class BusinessTable {

	public static final String BUSINESS_TABLE = "BUSINESS_TABLE";
	
	public static final String COL_BUSINESS_CITY = "city";
	public static final String COL_BUSINESS_CREATED = "created";
	public static final String COL_BUSINESS_FAVORITED = "favorited";
	public static final String COL_BUSINESS_FB_LINK = "fb_link";
	public static final String COL_BUSINESS_FB_LINK_ID = "fb_link_id";
	public static final String COL_BUSINESS_FS_LINK = "fs_link";
	public static final String COL_BUSINESS_FS_LINK_ID = "fs_link_id";
	public static final String COL_BUSINESS_FS_CAT_ID = "fs_category_id";
	
	public static final String COL_BUSINESS_ID = "business_id";
	public static final String COL_BUSINESS_LAT = "lat";
	public static final String COL_BUSINESS_LOGO = "logo";
	public static final String COL_BUSINESS_LON = "lon";
	public static final String COL_BUSINESS_MODIFIED = "modified";
	public static final String COL_BUSINESS_NAME = "name";
	public static final String COL_BUSINESS_PHONE = "phone";
	public static final String COL_BUSINESS_SHARED = "shared";
	public static final String COL_BUSINESS_STATE = "state";
	public static final String COL_BUSINESS_STATUS = "status";
	public static final String COL_BUSINESS_STREET = "street";
	public static final String COL_BUSINESS_SUBSCRIBED = "subscribed";
	public static final String COL_BUSINESS_ZIP = "zip";
	
	public static final String COL_IS_REGISTERED = "is_registered";
	public static final String create_business_table = "CREATE TABLE " + BUSINESS_TABLE + " ( "
			+ COL_BUSINESS_CITY + " TEXT, "
			+ COL_BUSINESS_CREATED + " TEXT, "
			+ COL_BUSINESS_FAVORITED + " TEXT, "
			+ COL_BUSINESS_FB_LINK + " TEXT, "
			+ COL_BUSINESS_FB_LINK_ID + " TEXT, "
			+ COL_BUSINESS_FS_LINK + " TEXT, "
			+ COL_BUSINESS_FS_LINK_ID + " TEXT, "
			+ COL_BUSINESS_ID + " TEXT PRIMARY KEY, "
			+ COL_BUSINESS_LAT + " TEXT, "
			+ COL_BUSINESS_LOGO + " TEXT, "
			+ COL_BUSINESS_LON + " TEXT, "
			+ COL_BUSINESS_MODIFIED + " TEXT, "
			+ COL_BUSINESS_NAME + " TEXT, "
			+ COL_BUSINESS_PHONE + " TEXT, "
			+ COL_BUSINESS_SHARED + " TEXT, "
			+ COL_BUSINESS_STATE + " TEXT, "
			+ COL_BUSINESS_STATUS + " TEXT, "
			+ COL_BUSINESS_STREET + " TEXT, "
			+ COL_BUSINESS_SUBSCRIBED + " TEXT, "
			+ COL_IS_REGISTERED + " TEXT, "
			+ COL_BUSINESS_FS_CAT_ID + " INTEGER , "
			+ COL_BUSINESS_ZIP + " TEXT )";
	
}
