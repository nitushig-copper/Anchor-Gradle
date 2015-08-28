/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.database.handler;

public class PromotionTable
{
	public static final String PROMOTION_TABLE = "PROMOTION_TABLE";
	
	public static final String COL_PROMOTION_ACCEPTED = "accepted";
	public static final String COL_PROMOTION_AGE_MAX = "age_max";
	public static final String COL_PROMOTION_AGE_MIN = "age_min";
	public static final String COL_PROMOTION_BUSINESS_ID = "business_id";
	public static final String COL_PROMOTION_COUPON_EXPIRY_DATE = "coupon_expiry_date";
	public static final String COL_PROMOTION_CREATED = "created";
	public static final String COL_PROMOTION_DETAILS = "details";
	public static final String COL_PROMOTION_END_DATE = "end_date";
	public static final String COL_PROMOTION_GROUP_COUNT = "group_count";
	public static final String COL_PROMOTION_ID = "promotion_id";
	public static final String COL_PROMOTION_MODIFIED = "modified";
	public static final String COL_PROMOTION_LOW_RES_IMAGE = "non_retina_image_url";
	public static final String COL_PROMOTION_STATUS = "promotion_status";
	public static final String COL_PROMOTION_REDEEMED = "redeemed";
	public static final String COL_PROMOTION_HIGH_RES_IMAGE = "retina_image_url";
	public static final String COL_PROMOTION_SEX = "sex";
	public static final String COL_PROMOTION_SHARED = "shared";
	public static final String COL_PROMOTION_START_DATE = "start_date";
	public static final String COL_PROMOTION_TITLE = "title";
	public static final String COL_PROMOTION_TYPE = "type";
	public static final String COL_PROMOTION_LOGO_URL = "logo_url";
	
	public static final String create_promotion_table = "CREATE TABLE " + PROMOTION_TABLE + " ( "
			+ COL_PROMOTION_ACCEPTED + " TEXT, "
			+ COL_PROMOTION_AGE_MAX + " TEXT, "
			+ COL_PROMOTION_AGE_MIN + " TEXT, "
			+ COL_PROMOTION_BUSINESS_ID + " TEXT, "
			+ COL_PROMOTION_COUPON_EXPIRY_DATE + " TEXT, "
			+ COL_PROMOTION_CREATED + " TEXT, "
			+ COL_PROMOTION_DETAILS + " TEXT, "
			+ COL_PROMOTION_END_DATE + " TEXT, "
			+ COL_PROMOTION_GROUP_COUNT + " TEXT, "
			+ COL_PROMOTION_ID + " TEXT, "
			+ COL_PROMOTION_MODIFIED + " TEXT, "
			+ COL_PROMOTION_LOW_RES_IMAGE + " TEXT, "
			+ COL_PROMOTION_STATUS + " INTEGER, "
			+ COL_PROMOTION_REDEEMED + " TEXT, "
			+ COL_PROMOTION_HIGH_RES_IMAGE + " TEXT, "
			+ COL_PROMOTION_LOGO_URL + " TEXT, "
			+ COL_PROMOTION_SEX + " TEXT, "
			+ COL_PROMOTION_SHARED + " TEXT, "
			+ COL_PROMOTION_START_DATE + " TEXT, "
			+ COL_PROMOTION_TITLE + " TEXT, "
			+ COL_PROMOTION_TYPE + " TEXT ) ";
}
