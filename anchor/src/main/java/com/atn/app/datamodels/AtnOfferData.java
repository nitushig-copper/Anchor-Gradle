/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

//abstract class represent a bar (Could be anchor or FourSquare, tips)
public abstract class AtnOfferData {

	//type of data
	public interface VenueType {
		int ANCHOR = 10;
		int FOURSQUARE = 11;
		int TIPS = 12;
	}

	//foursquare venue category id
	protected int venueCategoryId;
	protected String venueSubCategoryId;

	public int getVenueCategoryId() {
		return venueCategoryId;
	}

	public void setVenueCategoryId(int venueCategoryId) {
		this.venueCategoryId = venueCategoryId;
	}
	
	
	public String getVenueSubCategoryId() {
		return venueSubCategoryId;
	}

	public void setVenueSubCategoryId(String venueSubCategoryId) {
		this.venueSubCategoryId = venueSubCategoryId;
	}
	
	

	/**
	 * @return the dataType
	 */
	public abstract int getDataType();
}
