/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;
import com.google.android.gms.maps.model.LatLng;

@Deprecated
public class InstagramLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String VENUE_ID = "id";
	public static final String VENUE_NAME = "name";
	public static final String LOCATION_LAT = "latitude";
	public static final String LOCATION_LNG = "longitude";

	private String instagramLocationId;
	private String instagramVenueName;
	private String lat;
	private String lng;

	private FoursquareData foursquareData;

	public LatLng getLatLng() {
		try {
			return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * @return the foursquareData
	 *//*
	public FoursquareData getFoursquareData() {
		return foursquareData;
	}

	*//**
	 * @param foursquareData
	 *            the foursquareData to set
	 */
	public void setFoursquareData(FoursquareData foursquareData) {
		this.foursquareData = foursquareData;
	}

	public String getInstagramVenueId() {
		return instagramLocationId;
	}

	public void setInstagramVenueId(String instagramLocationId) {
		this.instagramLocationId = instagramLocationId;
	}

	public String getInstagramVenueName() {
		return instagramVenueName;
	}

	public void setInstagramVenueName(String instagramVenueName) {
		this.instagramVenueName = instagramVenueName;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

}
