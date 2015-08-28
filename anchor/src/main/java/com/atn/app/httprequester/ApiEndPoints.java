package com.atn.app.httprequester;

public class ApiEndPoints {

	
	//anchor api end points.
	public static final String BUSINESS = "businesses";
	public static final String ANCHOR_BAR_FAVORITE = "favorite";
	public static final String ANCHOR_BAR_UNFAVORITE = "unfavorite";
	
	//non anchor bars
	public static final String FOURSQUARE_BAR_FAVORITE = "favoritebar";
	public static final String FOURSQUARE_BAR_UNFAVORITE = "unfavoritebar";
	
	
	//venue comments end point
	public static final String GET_COMMENTS = "getComment";
	public static final String POST_COMMENT = "postComment";
	
	//add picture on venue
	public static final String ADD_PICTURE_ONVENUE = "postVenuePicture";
	
	
	//get all near by venue from anchor server
	public static final String GET_VENUE_LIST = "venuelist";
	public static final String POST_VENUE_REVIEW = "postVenueReview";
	
	public static final String REMOVE_USER_PIC = "profilePicDelete";
	
	//un favorite multiple venues
	public static final String REMOVE_FAVORITE = "removefavorite";
	
	//update venue visit state on server
	public static final String VENUE_VISIT = "venuevisit";
	
	//update Tips visit state on server
	public static final String TIP_VISIT = "tipvisit";
	
	
}
