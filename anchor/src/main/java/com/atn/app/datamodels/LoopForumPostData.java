/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;
@Deprecated
public class LoopForumPostData {

	public static final String USER_ID = "user_id";
	public static final String USER_TEXT = "text";
	public static final String DATE = "date";
	public static final String LAT = "lat";
	public static final String LNG = "lon";
	public static final String FORUM_ID = "forum_id";
	public static final String IMAGE = "image";

	private String userId;
	private String message;
	private String messageDate;
	private String latitude;
	private String longitude;
	private String imageUrl;

	private String forumId;

	/**
	 * @return the forumId
	 */
	public String getForumId() {
		return forumId;
	}

	/**
	 * @param forumId
	 *            the forumId to set
	 */
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the messageDate
	 */
	public String getMessageDate() {
		return messageDate;
	}

	/**
	 * @return the latitude
	 */
	public String getlatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getlongitude() {
		return longitude;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param messageDate
	 *            the messageDate to set
	 */
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setlatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setlongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
