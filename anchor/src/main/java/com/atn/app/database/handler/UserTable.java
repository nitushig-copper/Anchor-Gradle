/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.database.handler;

public class UserTable
{

	public static final String USER_TABLE = "USER_TABLE";
	
	public static final String COL_USER_CREATED = "created";
	public static final String COL_USER_DOB = "date_of_birth";
	public static final String COL_USER_EMAIL = "email";
	public static final String COL_USER_FB_ACCESS_TOKEN = "fb_access_token";
	public static final String COL_USER_FB_LINK = "fb_link";
	public static final String COL_USER_FB_UID = "fb_uid";
	public static final String COL_USER_FIRST_NAME = "first_name";
	public static final String COL_USER_GENDER = "gender";
	public static final String COL_USER_ID = "user_id";
	public static final String COL_USER_MANNUAL_LOGIN = "isManualLogin";
	public static final String COL_USER_LAST_NAME = "last_name";
	public static final String COL_USER_LOCATION = "location";
	public static final String COL_USER_MODIFIED = "modified";
	public static final String COL_USER_POINTS = "points";
	public static final String COL_USER_NAME = "user_name";
	
	
	public static final String create_user_table = "CREATE TABLE " + USER_TABLE + " ( "
			+ COL_USER_CREATED + " TEXT, "
			+ COL_USER_DOB + " TEXT, "
			+ COL_USER_EMAIL + " TEXT, "
			+ COL_USER_FB_ACCESS_TOKEN + " TEXT, "
			+ COL_USER_FB_LINK + " TEXT, "
			+ COL_USER_FB_UID + " TEXT, "
			+ COL_USER_FIRST_NAME + " TEXT, "
			+ COL_USER_GENDER + " INTEGER DEFAULT 0, "
			+ COL_USER_ID + " TEXT PRIMARY KEY, "
			+ COL_USER_MANNUAL_LOGIN + " BOOLEAN, "
			+ COL_USER_LAST_NAME + " TEXT, "
			+ COL_USER_LOCATION + " TEXT, "
			+ COL_USER_MODIFIED + " TEXT, "
			+ COL_USER_POINTS + " TEXT, "
			+ COL_USER_NAME + " TEXT )";
	
}
