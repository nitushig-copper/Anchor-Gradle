/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

/**
 * Anchor busuness model
 * */
public class AtnRegisteredVenueData extends AtnOfferData implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String BUSINESS = "Business";
	public static final String BUSINESS_ID = "id";
	public static final String BUSINESS_NAME = "name";
	public static final String BUSINESS_STREET = "street";
	public static final String BUSINESS_CITY = "city";
	public static final String BUSINESS_STATE = "state";
	public static final String BUSINESS_ZIP = "zip";
	public static final String BUSINESS_LAT = "lat";
	public static final String BUSINESS_LNG = "lon";
	public static final String BUSINESS_PHONE = "phone";
	public static final String BUSINESS_FB_LINK = "fb_link";
	public static final String BUSINESS_FB_LINK_ID = "fb_link_id";
	public static final String BUSINESS_FS_VENUE_LINK = "fs_venue_link";
	public static final String BUSINESS_FS_VENUE_ID = "fs_venue_id";
	public static final String BUSINESS_IMAGE = "logo";
	public static final String BUSINESS_FAVORITE = "favorited";
	public static final String BUSINESS_SUBSCRIBE = "subscribed";
	public static final String USERS_FAVORITES = "Favorites";
	public static final String IS_REGISTERED_VENUE = "isATN";
	
	public static final String VISIT_BUSSINESS_ID = "business_id";

	private String businessId;
	private String businessName;
	private String businessStreet;
	private String businessStatus;
	private String businessCity;
	private String businessState;
	private String businessZip;
	private String businessLat;
	private String businessLng;
	private String businessPhone;
	private String businessFbLink;
	private String businessFbLinkId;
	private String businessFsVenueLink;
	private String businessFsVenueId;
	private String businessImageUrl;
	private String businessModified;
	private String businessCreated;
	private String businessShared;
	private boolean businessFavorited;
	private boolean businessSubscribed;
	private boolean isRegisterdVenue;
	private ArrayList<AtnPromotion> atnPromotionList;
	private boolean isCouponClaimed;
	private VenueModel venueModel;
	private String iGlocId;

	/**
	 * @return the iGlocId
	 */
	public String getiGlocId() {
		return iGlocId;
	}

	/**
	 * @param iGlocId
	 *            the iGlocId to set
	 */
	public void setiGlocId(String iGlocId) {
		this.iGlocId = iGlocId;
	}

	/**
	 * @return the venueModel
	 */
	public VenueModel getFsVenueModel() {
		return venueModel;
	}

	/**
	 * @param venueModel
	 *            the venueModel to set
	 */
	public void setFsVenueModel(VenueModel venueModel) {
		this.venueModel = venueModel;
	}

	private LatLng latLng = null;

	public AtnRegisteredVenueData() {
		atnPromotionList = new ArrayList<AtnPromotion>();
	}

	public boolean isCouponClaimed() {
		return isCouponClaimed;
	}

	public void setCouponClaimed(boolean isCouponClaimed) {
		this.isCouponClaimed = isCouponClaimed;
	}

	public void addPromotion(AtnPromotion atnPromotion) {
		atnPromotionList.add(atnPromotion);
	}

	public void removePromotion(AtnPromotion atnPromotion) {
		atnPromotionList.remove(atnPromotion);
	}

	public AtnPromotion getPromotion(int index) {
		return atnPromotionList.get(index);
	}

	public void addBulkPromotion(ArrayList<AtnPromotion> promotionList) {
		atnPromotionList = promotionList;
	}

	public ArrayList<AtnPromotion> getBulkPromotion() {
		return atnPromotionList;
	}

	/**
	 * @return the businessShared
	 */
	public String getBusinessShared() {
		return businessShared;
	}

	/**
	 * @param businessShared
	 *            the businessShared to set
	 */
	public void setBusinessShared(String businessShared) {
		this.businessShared = businessShared;
	}

	/**
	 * @return the businessStatus
	 */
	public String getBusinessStatus() {
		return businessStatus;
	}

	/**
	 * @param businessStatus
	 *            the businessStatus to set
	 */
	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	/**
	 * @return the businessModified
	 */
	public String getBusinessModified() {
		return businessModified;
	}

	/**
	 * @param businessModified
	 *            the businessModified to set
	 */
	public void setBusinessModified(String businessModified) {
		this.businessModified = businessModified;
	}

	/**
	 * @return the businessCreated
	 */
	public String getBusinessCreated() {
		return businessCreated;
	}

	/**
	 * @param isCreated
	 *            the isCreated to set
	 */
	public void setCreated(String businessCreated) {
		this.businessCreated = businessCreated;
	}

	/**
	 * @return the businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * @return the businessName
	 */
	public String getBusinessName() {
		return businessName;
	}

	/**
	 * @return the businessStreet
	 */
	public String getBusinessStreet() {
		return businessStreet;
	}

	/**
	 * @return the businessCity
	 */
	public String getBusinessCity() {
		return businessCity;
	}

	/**
	 * @return the businessState
	 */
	public String getBusinessState() {
		return businessState;
	}

	/**
	 * @return the businessZip
	 */
	public String getBusinessZip() {
		return businessZip;
	}

	/**
	 * @return the businessLat
	 */
	public String getBusinessLat() {
		return businessLat;
	}

	/**
	 * @return the businessLng
	 */
	public String getBusinessLng() {
		return businessLng;
	}

	/**
	 * @return the businessPhone
	 */
	public String getBusinessPhone() {
		return businessPhone;
	}

	/**
	 * @return the businessFbLink
	 */
	public String getBusinessFacebookLink() {
		return businessFbLink;
	}

	/**
	 * @return the businessFbLinkId
	 */
	public String getBusinessFacebookLinkId() {
		return businessFbLinkId;
	}

	/**
	 * @return the businessFsVenueLink
	 */
	public String getBusinessFoursquareVenueLink() {
		return businessFsVenueLink;
	}

	/**
	 * @return the businessFsVenueId
	 */
	public String getBusinessFoursquareVenueId() {
		return businessFsVenueId;
	}

	/**
	 * @return the businessImageUrl
	 */
	public String getBusinessImageUrl() {
		return businessImageUrl;
	}

	/**
	 * @return the businessFavorited
	 */
	public boolean isFavorited() {
		return businessFavorited;
	}

	/**
	 * @return the businessSubscribed
	 */
	public boolean isSubscribed() {
		return businessSubscribed;
	}

	/**
	 * @param businessId
	 *            the businessId to set
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * @param businessName
	 *            the businessName to set
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	/**
	 * @param businessStreet
	 *            the businessStreet to set
	 */
	public void setBusinessStreet(String businessStreet) {
		this.businessStreet = businessStreet;
	}

	/**
	 * @param businessCity
	 *            the businessCity to set
	 */
	public void setBusinessCity(String businessCity) {
		this.businessCity = businessCity;
	}

	/**
	 * @param businessState
	 *            the businessState to set
	 */
	public void setBusinessState(String businessState) {
		this.businessState = businessState;
	}

	/**
	 * @param businessZip
	 *            the businessZip to set
	 */
	public void setBusinessZip(String businessZip) {
		this.businessZip = businessZip;
	}

	/**
	 * @param businessLat
	 *            the businessLat to set
	 */
	public void setBusinessLat(String businessLat) {
		this.businessLat = businessLat;
	}

	/**
	 * @param businessLng
	 *            the businessLng to set
	 */
	public void setBusinessLng(String businessLng) {
		this.businessLng = businessLng;
	}

	/**
	 * @param businessPhone
	 *            the businessPhone to set
	 */
	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	/**
	 * @param businessFbLink
	 *            the businessFbLink to set
	 */
	public void setBusinessFacebookLink(String businessFbLink) {
		this.businessFbLink = businessFbLink;
	}

	/**
	 * @param businessFbLinkId
	 *            the businessFbLinkId to set
	 */
	public void setBusinessFacebookLinkId(String businessFbLinkId) {
		this.businessFbLinkId = businessFbLinkId;
	}

	/**
	 * @param businessFsVenueLink
	 *            the businessFsVenueLink to set
	 */
	public void setBusinessFoursquareVenueLink(String businessFsVenueLink) {
		this.businessFsVenueLink = businessFsVenueLink;
	}

	/**
	 * @param businessFsVenueId
	 *            the businessFsVenueId to set
	 */
	public void setBusinessFoursquareVenueId(String businessFsVenueId) {
		this.businessFsVenueId = businessFsVenueId;
	}

	/**
	 * @param businessImageUrl
	 *            the businessImageUrl to set
	 */
	public void setBusinessImageUrl(String businessImageUrl) {
		this.businessImageUrl = businessImageUrl;
	}

	/**
	 * @param string
	 *            the isFavorited to set
	 */
	public void setFavorited(boolean data) {
		this.businessFavorited = data;
	}

	/**
	 * @param isSubscribed
	 *            the isSubscribed to set
	 */
	public void setSubscribed(boolean data) {
		this.businessSubscribed = data;
	}

	/**
	 * @return the isRegisterdVenue
	 */
	public boolean isRegisterdVenue() {
		return isRegisterdVenue;
	}

	/**
	 * @param isRegisterdVenue
	 *            the isRegisterdVenue to set
	 */
	public void setRegisterdVenue(boolean isRegisterdVenue) {
		this.isRegisterdVenue = isRegisterdVenue;
	}

	//first check data type and then id
	@Override
	public boolean equals(Object o) {
		if (((AtnOfferData) o).getDataType() == VenueType.ANCHOR) {
			return ((AtnRegisteredVenueData) o).getBusinessId().equals(
					this.getBusinessId());
		}
		return false;
	}

	//return latlng object 
	public LatLng getLatLng() {

		if (this.latLng == null)
			this.latLng = new LatLng(Double.valueOf(this.businessLat),
					Double.valueOf(this.businessLng));

		return latLng;
	}

	@Override
	public int getDataType() {
		return VenueType.ANCHOR;
	}

}
