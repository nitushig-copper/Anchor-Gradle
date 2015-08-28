/**
 * COPPERMOBILE PVT LTD.
 * 
 * */
package com.atn.app.datamodels;

import com.atn.app.provider.Atn;

import android.database.Cursor;

/**
 * Comment Model
 * */
public class Comment {

	private int commentId;
	private String mCommentText;
	private int mUserId;
	private String mUserIconUrl;
	private String mUserName;

	//populate comment from cursor
	public Comment(Cursor cursor) {
		commentId = cursor.getInt(cursor
				.getColumnIndex(Atn.CommentTable.COMMENT_ID));
		mCommentText = cursor.getString(cursor
				.getColumnIndex(Atn.CommentTable.TEXT));
		mUserName = cursor.getString(cursor
				.getColumnIndex(Atn.CommentTable.USER_NAME));
		mUserId = cursor
				.getInt(cursor.getColumnIndex(Atn.CommentTable.USER_ID));
		mUserIconUrl = cursor.getString(cursor
				.getColumnIndex(Atn.CommentTable.USER_PHOTO));
	}

	public Comment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the commentId
	 */
	public int getCommentId() {
		return commentId;
	}

	/**
	 * @param commentId
	 *            the commentId to set
	 */
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	/**
	 * @return the mCommentText
	 */
	public String getCommentText() {
		return mCommentText;
	}

	/**
	 * @param mCommentText
	 *            the mCommentText to set
	 */
	public void setCommentText(String mCommentText) {
		this.mCommentText = mCommentText;
	}

	/**
	 * @return the mUserId
	 */
	public int getUserId() {
		return mUserId;
	}

	/**
	 * @param mUserId
	 *            the mUserId to set
	 */
	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}

	/**
	 * @return the mUserIconUrl
	 */
	public String getUserIconUrl() {
		return mUserIconUrl;
	}

	/**
	 * @param mUserIconUrl
	 *            the mUserIconUrl to set
	 */
	public void setUserIconUrl(String mUserIconUrl) {
		this.mUserIconUrl = mUserIconUrl;
	}

	/**
	 * @return the mUserName
	 */
	public String getUserName() {
		return mUserName;
	}

	/**
	 * @param mUserName
	 *            the mUserName to set
	 */
	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

}
