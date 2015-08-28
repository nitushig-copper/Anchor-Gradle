/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;

import org.json.JSONObject;

/*
 * Gcm Notification object
 * **/
public class PushNotificationData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PUSH_DATA = "push_notification_data";

	public static final String PUSH_ID = "id";
	public static final String PUSH_USER_ID = "user_id";
	public static final String PUSH_TITLE = "title";
	public static final String PUSH_DETAIL = "detail";
	public static final String PUSH_TYPE = "type";

	public static final String PROMOTION_ID = "promotion_id";

	public static final String FORUM = "forum";

	public enum PushType {
		FORUM, PROMOTION
	}

	private String id;
	private String userId;
	private String title;
	private String detail;
	private PushType type;
	private String promotionId;

	public PushNotificationData(String data) {
		try {
			JSONObject dataObject = new JSONObject(data);

			if (!dataObject.isNull(PUSH_ID)) {
				id = dataObject.getString(PUSH_ID);
			}
			if (!dataObject.isNull(PUSH_DETAIL)) {
				detail = dataObject.getString(PUSH_DETAIL);
			}
			if (!dataObject.isNull(PUSH_TITLE)) {
				title = dataObject.getString(PUSH_TITLE);
			}
			if (!dataObject.isNull(PUSH_USER_ID)) {
				userId = dataObject.getString(PUSH_USER_ID);
			}
			if (!dataObject.isNull(PUSH_TYPE)) {
				if (dataObject.getString(PUSH_TYPE).equalsIgnoreCase(FORUM)) {
					type = PushType.FORUM;
				} else {
					type = PushType.PROMOTION;
				}
			}
			if (!dataObject.isNull(PROMOTION_ID)) {
				promotionId = dataObject.getString(PROMOTION_ID);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @return the type
	 */
	public PushType getType() {
		return type;
	}

	public String getPromotionId() {
		return promotionId;
	}

}
