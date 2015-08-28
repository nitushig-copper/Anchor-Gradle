/**
 * Copyright (C) 2014 CopperMobile Pvt. Ltd. 
 * 
 * */
package com.atn.app.datamodels;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.google.android.gms.maps.model.LatLng;

/*
 * Simple class represent instagram media object.	
 * **/
public class IgMedia {

	// json keys
	public static final String LIKES = "likes";
	public static final String LIKES_COUNT = "count";
	public static final String IMAGE = "images";
	public static final String IMAGE_URL = "url";
	public static final String IMAGE_ID = "id";
	public static final String IMAGE_LOW_RES = "standard_resolution";  //low_resolution
	public static final String IMAGE_THUMB = "thumbnail";
	public static final String IMAGE_TIME = "created_time";
	public static final String IMAGE_TAGS = "tags";
	public static final String MEDIA_LOCATION = "location";
	public static final String LAT = "latitude";
	public static final String LNG = "longitude";
	public static final String LOC_NAME = "name";
	
	
	private int likesCount;
	private String imageId;
	private String imageUrl;
	private String thumbnailUrl;
	private String createdDate;
	private String locationName;
	private String lat;
	private String lng;

	// instagram location
	private String instagramLocationId;
	private String fourSquareId;
	
	private LatLng latLng = null;
	private String hashTag = null;
	
	//parsing media json object
	public IgMedia(JSONObject dataObject,String instagramId) {
		
		try {
				this.instagramLocationId = instagramId;
				if (!dataObject.isNull(IgMedia.IMAGE_ID)) {
					this.imageId = dataObject.getString(IgMedia.IMAGE_ID);
				}

				if (!dataObject.isNull(IgMedia.IMAGE_TIME))
					this.createdDate = dataObject.getString(IgMedia.IMAGE_TIME);
					
				if (!dataObject.isNull(IgMedia.LIKES)) {
					if (!dataObject.getJSONObject(IgMedia.LIKES).isNull(IgMedia.LIKES_COUNT)) {
						this.likesCount = dataObject.getJSONObject(IgMedia.LIKES).getInt(IgMedia.LIKES_COUNT);
					}
				}

				if (!dataObject.isNull(IgMedia.IMAGE_TAGS)) {
					JSONArray hashTagArray = dataObject.getJSONArray(IgMedia.IMAGE_TAGS);
					String hashTag = "";
					for (int j = 0; j < hashTagArray.length(); j++)
						hashTag += "#" + hashTagArray.getString(j) + " ";

					this.hashTag = hashTag;
				}

				if (!dataObject.isNull(IgMedia.IMAGE)) {
					if (!dataObject.getJSONObject(IgMedia.IMAGE).isNull(IMAGE_LOW_RES)) {
						if (!dataObject.getJSONObject(IgMedia.IMAGE)
								.getJSONObject(IMAGE_LOW_RES)
								.isNull(IgMedia.IMAGE_URL)) {
							this.imageUrl = dataObject.getJSONObject(IgMedia.IMAGE)
									.getJSONObject(IMAGE_LOW_RES)
									.getString(IgMedia.IMAGE_URL);
						}
					}

					if (!dataObject.getJSONObject(IgMedia.IMAGE).isNull(IgMedia.IMAGE_THUMB)) {
						if (!dataObject.getJSONObject(IgMedia.IMAGE).getJSONObject(IgMedia.IMAGE_THUMB).isNull(IgMedia.IMAGE_URL)) {
							this.thumbnailUrl = dataObject.getJSONObject(IgMedia.IMAGE).getJSONObject(IgMedia.IMAGE_THUMB)
									.getString(IgMedia.IMAGE_URL);
						}
					}
				}

				if (!dataObject.isNull(IgMedia.MEDIA_LOCATION)) {
					JSONObject locOBj = dataObject.getJSONObject(IgMedia.MEDIA_LOCATION);
					if (!locOBj.isNull(IgMedia.LOC_NAME)) {
						this.locationName = locOBj.getString(IgMedia.LOC_NAME);
					}
					if (!locOBj.isNull(IgMedia.LOC_NAME)) {
						this.lat = locOBj.getString(IgMedia.LAT);
					}
					if (!locOBj.isNull(IgMedia.LOC_NAME)) {
						this.lng = locOBj.getString(IgMedia.LNG);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IgMedia(JSONObject dataObject) {
		
		try {
			
				//MOHAR: set Custom Id because it is same as in Instagram images
				if (!dataObject.isNull(IgMedia.IMAGE_ID)) {
					this.imageId = "tips"+dataObject.getString(IgMedia.IMAGE_ID);
				}

				if (!dataObject.isNull("start_date")) {
					long time = AtnUtils.convertInMillisecond(dataObject.getString("start_date")) / 1000L;
					this.createdDate = String.valueOf(time);
				}
					
				if (!dataObject.isNull("image_nonretina")) {
					this.thumbnailUrl = dataObject.getString("image_nonretina");
						
				}
				if (!dataObject.isNull("image_retina")) {
					this.imageUrl = dataObject.getString("image_retina");
						
				}

				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//populate from cursor
	public IgMedia(Cursor cursor) {
		
		this.imageId = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.MEDIA_ID));
		this.imageUrl = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.MEDIA_URL));
		this.thumbnailUrl = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.MEDIA_THUMB_URL));
		this.createdDate = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.MEDIA_CREATED_DATE));
		this.locationName = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.LOC_NAME));
		this.lat = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.LAT));
		this.lng = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.LNG));
		this.instagramLocationId = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.INSTAGRAM_ID_REF));
		this.fourSquareId = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.FOUR_SQUARE_ID));
		this.likesCount = cursor.getInt(cursor.getColumnIndex(Atn.InstagramMedia.LIKES_COUNT));
		this.hashTag = cursor.getString(cursor.getColumnIndex(Atn.InstagramMedia.IMAGE_TAG));
		
	}
	
	//return  ContentValues for this object.
	public ContentValues getContentValues() {
		
		ContentValues mediaValue = new ContentValues();
	
		mediaValue.put(Atn.InstagramMedia.INSTAGRAM_ID_REF, instagramLocationId);
		mediaValue.put(Atn.InstagramMedia.FOUR_SQUARE_ID, fourSquareId);
		mediaValue.put(Atn.InstagramMedia.MEDIA_ID, imageId);
		mediaValue.put(Atn.InstagramMedia.MEDIA_CREATED_DATE,createdDate);
		mediaValue.put(Atn.InstagramMedia.LIKES_COUNT, likesCount);
		mediaValue.put(Atn.InstagramMedia.IMAGE_TAG, hashTag);
		mediaValue.put(Atn.InstagramMedia.MEDIA_URL, imageUrl);
		mediaValue.put(Atn.InstagramMedia.MEDIA_THUMB_URL, thumbnailUrl);
		mediaValue.put(Atn.InstagramMedia.LOC_NAME,locationName);
		mediaValue.put(Atn.InstagramMedia.LAT, lat);
		mediaValue.put(Atn.InstagramMedia.LNG, lng);
		
		return mediaValue;
	}
	
	
	public IgMedia() {}

	public int getLikesCount() {
		return likesCount;
	}
	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getInstagramLocationId() {
		return instagramLocationId;
	}
	public void setInstagramLocationId(String instagramLocationId) {
		this.instagramLocationId = instagramLocationId;
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
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	
	public LatLng getLatLng() {
		if (latLng == null)
			latLng = new LatLng(Double.valueOf(this.lat),
					Double.valueOf(this.lat));
		return latLng;
	}
	
	public String getHashTag() {
		return hashTag;
	}

	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.imageId.equals(((IgMedia)o).imageId);
	}

	public String getFourSquareId() {
		return fourSquareId;
	}

	public void setFourSquareId(String fourSquareId) {
		this.fourSquareId = fourSquareId;
	}
	
}
