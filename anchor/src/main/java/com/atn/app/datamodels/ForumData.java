package com.atn.app.datamodels;

import java.io.Serializable;
import java.util.ArrayList;

import com.atn.app.utils.HttpUtility;

/**
 * Creates data model for the Loop-Forum list and Forum data.
 * 
 * @author gagan
 * 
 */
@Deprecated
public class ForumData implements Serializable {

	/**
	 * Default serialize version id.
	 */
	private static final long serialVersionUID = 1L;

	public static final String FORUM_LIST = "forums";
	public static final String FORUM_TITLE = "Forum";
	public static final String FORUM_DATA = "data";

	public static final String FORUM_ID = "id";
	public static final String FORUM_CHILD = "child";
	public static final String FORUM_MESSAGE = "message";
	public static final String FORUM_USER = "User";
	public static final String FORUM_USER_NAME = "userName";
	public static final String FORUM_USER_ID = "user_id";
	public static final String FORUM_CREATION_TIME = "created";
	public static final String FORUM_MESSAGE_COUNT = "totalCount";
	public static final String IMAGE = "image";

	public static final String FORUM_PUSH_DATA = "forum_push_data";

	/**
	 * Base path for retrieveing images.
	 */

	private String forumId;
	private String userName;
	private String userMessage;
	private String messageTime;
	private String messageCount;
	private String imageUrl;

	private ArrayList<ForumData> forumData;

	private ForumDataType forumType;

	public enum ForumDataType {
		FORUM_LIST, FORUM_DATA, FORUM_ATTACHMENT, FORUM_SEPARATOR
	}

	public ForumData() {
		forumData = new ArrayList<ForumData>();
	}

	/**
	 * @return the forumType
	 */
	public ForumDataType getForumDataType() {
		return forumType;
	}

	/**
	 * @param forumTypethe
	 *            forumType to set
	 */
	public void setForumDataType(ForumDataType forumType) {
		this.forumType = forumType;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String path) {
		this.imageUrl = HttpUtility.VENUES_IMAGE_URL + path;
	}

	/**
	 * @return the forumId
	 */
	public String getForumId() {
		return forumId;
	}

	/**
	 * @param forumId
	 *            the forumId to set
	 */
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	/**
	 * @return the forumData
	 */
	public ArrayList<ForumData> getForumData() {
		return this.forumData;
	}

	/**
	 * @param forumDatathe
	 *            forumData to set
	 */
	public void setForumList(ForumData forumData) {
		this.forumData.add(forumData);
	}

	public void setBulkForumData(ArrayList<ForumData> forumData) {
		for (int i = 0; i < forumData.size(); i++) {
			this.forumData.add(forumData.get(i));
		}
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the userMessage
	 */
	public String getUserMessage() {
		return userMessage;
	}

	/**
	 * @return the messageTime
	 */
	public String getMessageTime() {
		return messageTime;
	}

	/**
	 * @return the messageCount
	 */
	public String getMessageCount() {
		return messageCount;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param userMessage
	 *            the userMessage to set
	 */
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 * @param messageTime
	 *            the messageTime to set
	 */
	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	/**
	 * @param messageCount
	 *            the messageCount to set
	 */
	public void setMessageCount(String messageCount) {
		this.messageCount = messageCount;
	}

}
