/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;
//
public class AddDealsPostData {

	public static final String USER_ID = "user_id";
	public static final String PROMOTION_ID = "promotion_id";
	public static final String AUTH_TOKEN = "auth_token";
	public static final String API_KEY = "api_key";

	private String userId;
	private String promotionId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

}
