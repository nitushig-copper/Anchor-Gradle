/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;
@Deprecated
public class FoursquareData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String VENUE = "venue";
	public static final String VENUE_ID = "id";
	public static final String VENUE_NAME = "name";
	public static final String CONTACT = "contact";
	public static final String CONTACT_PHONE = "phone";
	public static final String LOCATION = "location";
	public static final String ADDRESS = "address";
	public static final String ADDRESS_STREET = "crossStreet";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String CANONICAL_URL = "canonicalUrl";
	public static final String TOTAL_RESULTS = "totalResults";
	public static final String GROUPS = "groups";
	public static final String ITEMS = "items";

	private String venueId;
	private String venueName;
	private String phone;
	private String canonicalURL;
	FoursquareLocation foursquareLocation;
	private int totalResult;

	public String getCanonicalURL() {
		return canonicalURL;
	}

	public void setCanonicalURL(String canonicalURL) {
		this.canonicalURL = canonicalURL;
	}

	public FoursquareData() {
		foursquareLocation = new FoursquareLocation();
	}

	public String getLocationId() {
		return venueId;
	}

	public void setLocationId(String venueId) {
		this.venueId = venueId;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return foursquareLocation.getAddress();
	}

	public void setAddress(String address) {
		foursquareLocation.setAddress(address);
	}

	public String getStreetAddress() {
		return foursquareLocation.getStreetAddress();
	}

	public void setStreetAddress(String streetAddress) {
		foursquareLocation.setStreetAddress(streetAddress);
	}

	public String getLat() {
		return foursquareLocation.getLat();
	}

	public void setLat(String lat) {
		foursquareLocation.setLat(lat);
	}

	public String getLng() {
		return foursquareLocation.getLng();
	}

	public void setLng(String lng) {
		foursquareLocation.setLng(lng);
	}

	/**
	 * @return the totalResult
	 */
	public int getTotalResult() {
		return totalResult;
	}

	/**
	 * @param totalResult
	 *            the totalResult to set
	 */
	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}
}
