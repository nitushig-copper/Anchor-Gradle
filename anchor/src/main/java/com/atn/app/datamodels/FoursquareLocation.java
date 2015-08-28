/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;

@Deprecated
public class FoursquareLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String address;
	private String streetAddress;
	private String lat;
	private String lng;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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
