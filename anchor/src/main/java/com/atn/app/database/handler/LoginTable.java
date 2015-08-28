/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.database.handler;

public class LoginTable {
	public static final String TABLE_NAME = "ATN_Login_table";
	
	public static final String COL_USER_ID = "user_id";
	public static final String COL_USER_NAME = "user_name";
	public static final String COL_USER_PASSWORD = "user_password";
	public static final String COL_USER_EMAIL = "user_email";
	public static final String COL_USER_ADDRESS = "user_address";
	public static final String COL_USER_GENDER = "user_gender";
	public static final String COL_USER_PIC = "user_pic";
	
	public static final String CREATE_TABLE = "CREATE TABLE  " + TABLE_NAME + " ( "
			+ COL_USER_ID + " TEXT , "
			+ COL_USER_NAME + " TEXT , "
			+ COL_USER_PASSWORD + " TEXT ,"
			+ COL_USER_EMAIL + " TEXT ,"
			+ COL_USER_ADDRESS + " TEXT ,"
			+ COL_USER_PIC + " TEXT ,"
			+ COL_USER_GENDER + " TEXT )";
	
}
