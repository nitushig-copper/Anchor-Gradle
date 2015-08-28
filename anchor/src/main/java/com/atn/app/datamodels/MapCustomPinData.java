/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;

/**
 * Class to show the bar name with the bar address.
 * 
 * @author gagan
 * 
 */

public class MapCustomPinData implements Serializable {

	public static final String CUSTOM_DATA = "custom_pin_data";
	public static final String VENUE_ID = "venue_id";
	public static final String VENUE_NAME = "venue_name";
	public static final String VENUE_TYPE = "venue_type";

	/**
	 * default serial id for the custom pin.
	 */
	private static final long serialVersionUID = 1L;

	public enum VenueType {
		ATN_BAR, NON_ATN_BAR, ATN_BAR_WITH_IMAGE
	}

	private String venueId;
	private String imageId;
	private String venueName;
	private String venueAddress;

	private VenueType venueType;

	/**
	 * @return the venueId
	 */
	public String getVenueId() {
		return venueId;
	}

	/**
	 * @return the venueName
	 */
	public String getVenueName() {
		return venueName;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String value) {
		imageId = value;
	}

	/**
	 * @return the venueAddress
	 */
	public String getVenueAddress() {
		return venueAddress;
	}

	/**
	 * @param venueId
	 *            the venueId to set
	 */
	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}

	/**
	 * @param venueName
	 *            the venueName to set
	 */
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	/**
	 * @param venueAddress
	 *            the venueAddress to set
	 */
	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
	}

	/**
	 * @return the venueType
	 */
	public VenueType getVenueType() {
		return venueType;
	}

	/**
	 * @param venueType
	 *            the venueType to set
	 */
	public void setVenueType(VenueType venueType) {
		this.venueType = venueType;
	}

}
