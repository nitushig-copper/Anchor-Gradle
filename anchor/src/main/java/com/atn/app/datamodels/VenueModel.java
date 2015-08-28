/*
 * @Copyright 2014
 * **/
package com.atn.app.datamodels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.google.android.gms.maps.model.LatLng;

/***
 * represent FourSquare venue
 * */
public class VenueModel extends AtnOfferData {	 
	 //provider 
	 public static final int FOUR_SQUARE = 0;
	 public static final int FACEBOOK = 1;
	
	/**Venue status**/
	
	 //if its not attached with any anchor business
	public static final int NON_ATN_BAR = 0;
	//if its not attached with any anchor business but user favorite
	public static final int NON_ATN_BAR_FAV = 1;
	//if its attached with a anchor business 
	public static final int ATN_BAR_FAV = 2;
	//if its attached with a anchor business and user favorite
	public static final int ATN_BAR = 3;
	
	
	//josn key instagram
	public static final String IG_LOCATION_ID = "id";
	public static final String IG_LAT = "latitude";
	public static final String IG_LNG = "longitude";
	public static final String IG_LOC_NAME = "name";
	public static final String CONANICAL_URL = "canonicalUrl";

	
	private int _id;
	
	private String atnBarId;
	private String venueId;
	private String venueName;
	private String phone;
	private String canonicalURL;

	// location
	private String address;
	private String streetAddress;
	private String lat;
	private String lng;
	
	
	// instagram location id
	private String instagramLocationId;
	private String iGLocName;
	private String iGlat;
	private String iGlng;

	List<IgMedia> instagramMedia;

	private LatLng latLng;
	private LatLng iGlatLng;
	private int atnBarType;
	private int placeProvider;
	private String mPhoto;
	private int commentCount;
	private int reviewCount;
	private float rating;
	private long latestMediaDate;
		//@tranisent

	///reviews.@transient
	private ArrayList<ReviewTag> reviews = null;
	
	
	//@transient //user on search screen distance from current location
	private double distance = 0;
	
	//populate fields from cursor
	public VenueModel(Cursor cursor) {
		
		this();
		this._id = cursor.getInt(cursor.getColumnIndex(Atn.Venue._ID));
		this.venueId = cursor.getString(cursor.getColumnIndex(Atn.Venue.VENUE_ID));
		this.atnBarId = cursor.getString(cursor.getColumnIndex(Atn.Venue.ATN_BAR_ID));
		this.venueName = cursor.getString(cursor.getColumnIndex(Atn.Venue.VENUE_NAME));
		this.phone = cursor.getString(cursor.getColumnIndex(Atn.Venue.CONTACT_PHONE));
		this.canonicalURL = cursor.getString(cursor.getColumnIndex(Atn.Venue.CANONICAL_URL));
		this.address = cursor.getString(cursor.getColumnIndex(Atn.Venue.ADDRESS));
		this.streetAddress = cursor.getString(cursor.getColumnIndex(Atn.Venue.ADDRESS_STREET));
		this.lat = cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT));
		this.lng = cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG));
		this.instagramLocationId = cursor.getString(cursor.getColumnIndex(Atn.Venue.INSTAGRAM_ID));
		this.iGLocName = cursor.getString(cursor.getColumnIndex(Atn.Venue.IG_LOC_NAME));
		this.iGlat = cursor.getString(cursor.getColumnIndex(Atn.Venue.IG_LAT));
		this.iGlng = cursor.getString(cursor.getColumnIndex(Atn.Venue.IG_LNG));
		this.atnBarType =cursor.getInt(cursor.getColumnIndex(Atn.Venue.FOLLOWED)); 
		setVenueCategoryId(cursor.getInt(cursor.getColumnIndex(Atn.Venue.CATEGORY)));
		setVenueSubCategoryId(cursor.getString(cursor.getColumnIndex(Atn.Venue.SUB_CATEGORY)));
		this.placeProvider = cursor.getInt(cursor.getColumnIndex(Atn.Venue.ATN_PLACE_PROVIDER));
		this.mPhoto = cursor.getString(cursor.getColumnIndex(Atn.Venue.PHOTO));
		this.commentCount = cursor.getInt(cursor.getColumnIndex(Atn.Venue.COMMENT_COUNT));
		this.reviewCount = cursor.getInt(cursor.getColumnIndex(Atn.Venue.REVEIW_COUNT));
		this.rating = cursor.getFloat(cursor.getColumnIndex(Atn.Venue.RATING));
		this.latestMediaDate = cursor.getLong(cursor.getColumnIndex(Atn.Venue.LATEST_MEDIA_DATE));
		
	}
	
	public int getId() {
		return _id;
	}
	
	//return content values. only used in search 
	public ContentValues getContentValues() {
		
		ContentValues contentValue = new ContentValues();
		contentValue.put(Atn.Venue.VENUE_ID,this.venueId);
		contentValue.put(Atn.Venue.VENUE_NAME,this.venueName);
		contentValue.put(Atn.Venue.CONTACT,this.phone);
		contentValue.put(Atn.Venue.ADDRESS,this.address);
		contentValue.put(Atn.Venue.ADDRESS_STREET,this.streetAddress);
		contentValue.put(Atn.Venue.LAT,this.lat);
		contentValue.put(Atn.Venue.LNG,this.lng);
		contentValue.put(Atn.Venue.CANONICAL_URL,this.canonicalURL);
		
		contentValue.put(Atn.Venue.ATN_BAR_ID,this.atnBarId);
		contentValue.put(Atn.Venue.FOLLOWED,this.atnBarType);
		
		//instagram info 
		contentValue.put(Atn.Venue.INSTAGRAM_ID,this.instagramLocationId);
		contentValue.put(Atn.Venue.IG_LAT,this.iGlat);
		contentValue.put(Atn.Venue.IG_LNG,this.iGlng);
		contentValue.put(Atn.Venue.IG_LOC_NAME,this.iGLocName);
		
		contentValue.put(Atn.Venue.CATEGORY, getVenueCategoryId());
		contentValue.put(Atn.Venue.SUB_CATEGORY, getVenueSubCategoryId());
		contentValue.put(Atn.Venue.PHOTO, mPhoto);
		contentValue.put(Atn.Venue.COMMENT_COUNT, getCommentCount());
		contentValue.put(Atn.Venue.REVEIW_COUNT, getReviewCount());
		contentValue.put(Atn.Venue.RATING, getRating());
		
		return contentValue;
	}
	

	/**
	 * @return the reviewCount
	 */
	public int getReviewCount() {
		return reviewCount;
	}

	/**
	 * @param reviewCount the reviewCount to set
	 */
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public VenueModel() {
		this.instagramMedia = new ArrayList<IgMedia>();
	}


	public String getAtnBarId() {
		return atnBarId;
	}

	public void setAtnBarId(String atnBarId) {
		this.atnBarId = atnBarId;
	}

	public int getAtnBarType() {
		return atnBarType;
	}


	public void setAtnBarType(int atnBarType) {
		this.atnBarType = atnBarType;
	}


	public int getPlaceProvider() {
		return placeProvider;
	}


	public void setPlaceProvider(int placeProvider) {
		this.placeProvider = placeProvider;
	}


	public String getVenueId() {
		return venueId;
	}

	public void setVenueId(String venueId) {
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

	public String getCanonicalURL() {
		return canonicalURL;
	}

	public void setCanonicalURL(String canonicalURL) {
		this.canonicalURL = canonicalURL;
	}

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

	public String getInstagramLocationId() {
		return instagramLocationId;
	}

	public void setInstagramLocationId(String instagramLocationId) {
		this.instagramLocationId = instagramLocationId;
	}

	public List<IgMedia> getInstagramMedia() {
		return instagramMedia;
	}

	public void setInstagramMedia(List<IgMedia> instagramMedia) {
		this.instagramMedia = instagramMedia;
	}

	public String getiGLocName() {
		return iGLocName;
	}

	public void setiGLocName(String iGLocName) {
		this.iGLocName = iGLocName;
	}

	public String getiGlat() {
		return iGlat;
	}

	public void setiGlat(String iGlat) {
		this.iGlat = iGlat;
	}

	public String getiGlng() {
		return iGlng;
	}

	public void setiGlng(String iGlng) {
		this.iGlng = iGlng;
	}

	public int getBarType() {
		return NON_ATN_BAR;
	}

	public LatLng getLatLng() {
		
		if(latLng==null&&!TextUtils.isEmpty(lat)){
			latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
		}
		
		return latLng;
	}

	public LatLng getiGlatLng() {
		if (iGlatLng == null&&!TextUtils.isEmpty(iGlat)) {
			iGlatLng = new LatLng(Double.valueOf(iGlat), Double.valueOf(iGlng));
		}
		return iGlatLng;
	}

	public void addInstagramMedia(IgMedia instaMediaObj) {
		this.instagramMedia.add(instaMediaObj);
	}

	public void removeInstagraMedia(IgMedia instaMediaObj) {
		this.instagramMedia.remove(instaMediaObj);
	}

	@Override
	public boolean equals(Object o) {
		if(((VenueModel)o).getDataType()==VenueType.FOURSQUARE) {
			return this.venueId.equals(((VenueModel)o).venueId);
		}
		return false;
	}

	@Override
	public int getDataType() {
		return VenueType.FOURSQUARE;
	}

	/**
	 * @return the mPhoto
	 */
	public String getPhoto() {
		return mPhoto;
	}


	/**
	 * @param mPhoto the mPhoto to set
	 */
	public void setPhoto(String mPhoto) {
		this.mPhoto = mPhoto;
	}


	/**
	 * @return the commentCount
	 */
	public int getCommentCount() {
		return commentCount;
	}


	/**
	 * @param commentCount the commentCount to set
	 */
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}


	@Override
	public String toString() {
		return this.venueName+"";
	}


	/**
	 * @return the reviews
	 */
	public ArrayList<ReviewTag> getReviews() {
		return reviews;
	}

	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(ArrayList<ReviewTag> reviews) {
		this.reviews = reviews;
	}

	/**
	 * @return the rating
	 */
	public float getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(float rating) {
		this.rating = rating;
	}

	/**
	 * @return the latestMediaDate
	 */
	public long getLatestMediaDate() {
		return latestMediaDate;
	}

	/**
	 * @param latestMediaDate the latestMediaDate to set
	 */
	public void setLatestMediaDate(long latestMediaDate) {
		this.latestMediaDate = latestMediaDate;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		if(distance==0) {
			distance = AtnUtils.distanceInMiles(this.lat, this.lng);
		}
		return distance;
	}

	
	//sort on distance
	
	//sort on distance
	public static class DistanceComparator implements Comparator<AtnOfferData> {
		@Override
		public int compare(AtnOfferData lhs, AtnOfferData rhs) {
			VenueModel venueLhs = (VenueModel)lhs;
			VenueModel venueRhs = (VenueModel)rhs;
			
			if(venueLhs.getDistance()>venueRhs.getDistance())
				return 1;
			else if(venueLhs.getDistance()<venueRhs.getDistance())
				return -1;
			
			return 0;
		}
	}
	
	
	public static class DateComparator implements Comparator<AtnOfferData> {
		@Override
		public int compare(AtnOfferData lhs, AtnOfferData rhs) {
			VenueModel venueLhs = (VenueModel)lhs;
			VenueModel venueRhs = (VenueModel)rhs;
			
			if (venueLhs.latestMediaDate < venueRhs.latestMediaDate)
			{
			    return 1;
			}

			return venueLhs.latestMediaDate == venueRhs.latestMediaDate ? 1 : 0;
			
			
			
		}
	}

}
