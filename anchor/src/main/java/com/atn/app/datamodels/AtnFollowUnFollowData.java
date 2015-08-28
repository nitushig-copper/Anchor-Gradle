package com.atn.app.datamodels;

import java.io.Serializable;

public class AtnFollowUnFollowData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String BUSINESS_ID = "business_id";
	public static String USER_ID = "user_id";

	private String businessId;
	private String userId;

	public static String getBUSINESS_ID() {
		return BUSINESS_ID;
	}

	public static void setBUSINESS_ID(String bUSINESS_ID) {
		BUSINESS_ID = bUSINESS_ID;
	}

	public static String getUSER_ID() {
		return USER_ID;
	}

	public static void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
