/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

import com.atn.app.provider.Atn;

import android.database.Cursor;

/**
 * venue Tag model
 * */
public class ReviewTag {

	// /josn keys
	public static final String HASH_TAGS = "HashTags";
	public static final String ID = "id";
	public static final String TAG_NAME = "tagName";
	public static final String TAG_TYPE = "tagtype";
	public static final String REVIEW = "review";

	public interface ViewType {
		int NORMAL = 0;
		int DONE = 1;
		int CANCEL = 2;
		// /total
	}

	//
	private int tagId;
	private String name;
	private int reviewCount;
	private int type;
	private String fsVenueId;
	private int anchorCategoryId;

	private int viewType = 0;

	/***/
	public ReviewTag(Cursor cursor) {
		tagId = cursor.getInt(cursor.getColumnIndex(Atn.ReviewTable.TAG_ID));
		name = cursor.getString(cursor.getColumnIndex(Atn.ReviewTable.NAME));
		reviewCount = cursor.getInt(cursor
				.getColumnIndex(Atn.ReviewTable.REVIEW_COUNT));
		type = cursor.getInt(cursor.getColumnIndex(Atn.ReviewTable.TAG_TYPE));
		fsVenueId = cursor.getString(cursor
				.getColumnIndex(Atn.ReviewTable.FS_VENUE_ID));
		anchorCategoryId = cursor.getInt(cursor
				.getColumnIndex(Atn.ReviewTable.ANCHOR_CATEGORY_ID));
	}

	public ReviewTag() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the tagId
	 */
	public int getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the reviewCount
	 */
	public int getReviewCount() {
		return reviewCount;
	}

	/**
	 * @param reviewCount
	 *            the reviewCount to set
	 */
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the fsVenueId
	 */
	public String getFsVenueId() {
		return fsVenueId;
	}

	/**
	 * @param fsVenueId
	 *            the fsVenueId to set
	 */
	public void setFsVenueId(String fsVenueId) {
		this.fsVenueId = fsVenueId;
	}

	/**
	 * @return the anchorCategoryId
	 */
	public int getAnchorCategoryId() {
		return anchorCategoryId;
	}

	/**
	 * @param anchorCategoryId
	 *            the anchorCategoryId to set
	 */
	public void setAnchorCategoryId(int anchorCategoryId) {
		this.anchorCategoryId = anchorCategoryId;
	}

	/**
	 * @return the viewType
	 */
	public int getViewType() {
		return viewType;
	}

	/**
	 * @param viewType
	 *            the viewType to set
	 */
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return ((ReviewTag) o).tagId == this.tagId;
	}

}
