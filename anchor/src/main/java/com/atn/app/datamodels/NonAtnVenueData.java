/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

/**
 * Creates data model for non-registerd ATN venue. This is used to call
 * favorite/unfavorite web service for non-ATN venues.
 * 
 */
@Deprecated 
public class NonAtnVenueData {

	public static final String USER_ID = "user_id";
	public static final String VENUE_NAME = "name";
	public static final String VENUE_LAT = "lat";
	public static final String VENUE_LNG = "lon";
	public static final String VENUE_ADDRESS = "address";
	public static final String FS_VENUE_ID = "fs_venue_id";
	public static final String VENUE_ID = "bar_id";
	public static final String VENUE_DESCRIPTION = "description";
	public static final String FS_VENUE_LINK = "fs_venue_link";
	public static final String INSTA_LOCATION_ID = "insta_location_id";

	private String userId;
	private String venueName;
	private String venueDescription;
	private String venueLat;
	private String venueLng;
	private String venueAddress;
	private String venueFsId;
	private String venueId;
	private String venueFsLink;
	private String instaLocId;

	/**
	 * @return the instaLocId
	 */
	public String getInstaLocId() {
		return instaLocId;
	}

	/**
	 * @param instaLocId
	 *            the instaLocId to set
	 */
	public void setInstaLocId(String instaLocId) {
		this.instaLocId = instaLocId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the venueName
	 */
	public String getVenueName() {
		return venueName;
	}

	/**
	 * @return the venueDescription
	 */
	public String getVenueDescription() {
		return venueDescription;
	}

	/**
	 * @return the venueLat
	 */
	public String getVenueLat() {
		return venueLat;
	}

	/**
	 * @return the venueLng
	 */
	public String getVenueLng() {
		return venueLng;
	}

	/**
	 * @return the venueAddress
	 */
	public String getVenueAddress() {
		return venueAddress;
	}

	/**
	 * @return the venueFsId
	 */
	public String getVenueFsId() {
		return venueFsId;
	}

	/**
	 * @return the venueId
	 */
	public String getVenueId() {
		return venueId;
	}

	/**
	 * @return the venueFsLink
	 */
	public String getVenueFsLink() {
		return venueFsLink;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @param venueName
	 *            the venueName to set
	 */
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	/**
	 * @param venueDescription
	 *            the venueDescription to set
	 */
	public void setVenueDescription(String venueDescription) {
		this.venueDescription = venueDescription;
	}

	/**
	 * @param venueLat
	 *            the venueLat to set
	 */
	public void setVenueLat(String venueLat) {
		this.venueLat = venueLat;
	}

	/**
	 * @param venueLng
	 *            the venueLng to set
	 */
	public void setVenueLng(String venueLng) {
		this.venueLng = venueLng;
	}

	/**
	 * @param venueAddress
	 *            the venueAddress to set
	 */
	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
	}

	/**
	 * @param venueFsId
	 *            the venueFsId to set
	 */
	public void setVenueFsId(String venueFsId) {
		this.venueFsId = venueFsId;
	}

	/**
	 * @param venueId
	 *            the venueId to set
	 */
	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}

	/**
	 * @param venueFsLink
	 *            the venueFsLink to set
	 */
	public void setVenueFsLink(String venueFsLink) {
		this.venueFsLink = venueFsLink;
	}

}
