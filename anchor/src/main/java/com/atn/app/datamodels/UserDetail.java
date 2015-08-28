/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;

/**
 * Creates and holds all the user details that is used with user management
 * class to call web services.
 * 
 */
public class UserDetail {

	public static final String USER = "user";
	public static final String ID = "id";

	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "userName";
	public static final String USER_FULL_NAME = "full_name";
	public static final String USER_FIRST_NAME = "first_name";
	public static final String USER_Message = "message";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String NEW_PASSWORD = "newPassword";
	public static final String GENDER = "sex";
	public static final String ADDRESS = "address";
	public static final String DEVICE_TOKEN = "apns_token";
	public static final String INSTAGRAM_ACCESS_TOKEN = "ins_access_token";
	public static final String FB_ACCESS_TOKEN = "fb_access_token";
	public static final String FB_EXPIRY_DATE = "fb_expiry_date";
	// added for Share Promotions
	public static final String PROMOTION_ID = "promotion_id";
	public static final String BUSINESS_ID = "business_id";
	// added for push notifications.
	public static final String DEVICE_TYPE = "device_type";
	public static final String DEVICE_TYPE_VALUE = "1";
	public static final String PROFILE_PIC = "profile";
	public static final String REMOVE_PIC = "is_remove_image";

	public static final String PICTURE_JSON_KEY = "picture";

	private final static String NULL_EXCEPTION = "data is null";
	public static final String EXPIRE_TIME = "ptime";

	private String userId = null;
	private String userName = null;
	private String userFullName = null;
	private String userMessage = null;

	private String userEmail = null;
	private String userPassword = null;
	private String userAddress = null;
	private static String deviceToken = null;

	//Gender type 
	public interface GenderType {
		int MALE = 1;
		int FEMALE = 0;
	}

	private int genderType = GenderType.MALE;
	private String userCreated = null;
	private String userDob = null;
	private String userFbToken = null;
	private String userFbTokenExpiry = null;
	private String userInsToken = null;
	private String userFbLink = null;
	private String userFbUid = null;
	private String userFirstName = null;
	private boolean isUserManualLogin = false;
	private String userLastName = null;
	private String userLocation = null;
	private String userModified = null;
	private String userPoints = null;

	private String imageUrl = null;

	private boolean isRemovePicture = false;

	/**
	 * @return the isRemovePicture
	 */
	public boolean isRemovePicture() {
		return isRemovePicture;
	}

	/**
	 * @param isRemovePicture
	 *            the isRemovePicture to set
	 */
	public void setRemovePicture(boolean isRemovePicture) {
		this.isRemovePicture = isRemovePicture;
	}

	/**
	 * @return the userCreated
	 */
	public String getUserCreated() {
		return userCreated;
	}

	/**
	 * @return the userDob
	 */
	public String getUserDob() {
		return userDob;
	}

	/**
	 * @return the userFbToken
	 */
	public String getUserFbToken() {
		return userFbToken;
	}

	/**
	 * @return the userFbLink
	 */
	public String getUserFbLink() {
		return userFbLink;
	}

	/**
	 * @return the userFbTokenExpiry
	 */
	public String getUserFbTokenExpiry() {
		return userFbTokenExpiry;
	}

	/**
	 * @return the userInsToken
	 */
	public String getUserInstagramToken() {
		return userInsToken;
	}

	/**
	 * @param userFbTokenExpiry
	 *            the userFbTokenExpiry to set
	 */
	public void setUserFbTokenExpiry(String userFbTokenExpiry) {
		this.userFbTokenExpiry = userFbTokenExpiry;
	}

	/**
	 * @param userInsToken
	 *            the userInsToken to set
	 */
	public void setUserInstagramToken(String userInsToken) {
		this.userInsToken = userInsToken;
	}

	/**
	 * @return the userFbUid
	 */
	public String getUserFbUid() {
		return userFbUid;
	}

	/**
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * @return the userLastName
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * @return the userLocation
	 */
	public String getUserLocation() {
		return userLocation;
	}

	/**
	 * @return the userModified
	 */
	public String getUserModified() {
		return userModified;
	}

	/**
	 * @return the userPoints
	 */
	public String getUserPoints() {
		return userPoints;
	}

	/**
	 * @param userCreated
	 *            the userCreated to set
	 */
	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	/**
	 * @param userDob
	 *            the userDob to set
	 */
	public void setUserDob(String userDob) {
		this.userDob = userDob;
	}

	/**
	 * @param userFbToken
	 *            the userFbToken to set
	 */
	public void setUserFbToken(String userFbToken) {
		this.userFbToken = userFbToken;
	}

	/**
	 * @param userFbLink
	 *            the userFbLink to set
	 */
	public void setUserFbLink(String userFbLink) {
		this.userFbLink = userFbLink;
	}

	/**
	 * @param userFbUid
	 *            the userFbUid to set
	 */
	public void setUserFbUid(String userFbUid) {
		this.userFbUid = userFbUid;
	}

	/**
	 * @param userFirstName
	 *            the userFirstName to set
	 */
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	/**
	 * @param userLastName
	 *            the userLastName to set
	 */
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	/**
	 * @param userLocation
	 *            the userLocation to set
	 */
	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	/**
	 * @param userModified
	 *            the userModified to set
	 */
	public void setUserModified(String userModified) {
		this.userModified = userModified;
	}

	/**
	 * @param userPoints
	 *            the userPoints to set
	 */
	public void setUserPoints(String userPoints) {
		this.userPoints = userPoints;
	}

	/**
	 * @return the isUserManualLogin
	 */
	public boolean isUserManualLogin() {
		return isUserManualLogin;
	}

	/**
	 * @param isUserManualLogin
	 *            the isUserManualLogin to set
	 */
	public void setUserManualLogin(boolean isUserManualLogin) {
		this.isUserManualLogin = isUserManualLogin;
	}

	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * @return the userMessage
	 */
	public String getUserMessage() {
		return userMessage;
	}

	/**
	 * @param userFullName
	 *            the userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/**
	 * @param userMessage
	 *            the userMessage to set
	 */
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 * Returns the user id if not null, otherwise throws null pointer exception.
	 * 
	 * @return user id.
	 */
	public String getUserId() {
		if (userId != null) {
			return userId;
		}
		return null;
	}

	/**
	 * Sets the user id using specified user id.
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Returns the user name if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return userName user name.
	 */
	public String getUserName() {
		if (userName == null) {
			throw new NullPointerException(NULL_EXCEPTION);
		}
		return userName;
	}

	/**
	 * Sets the user name using specified user name.
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns the user email if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return userEmail email of user.
	 */
	public String getUserEmail() {
		if (userEmail == null) {
			throw new NullPointerException(NULL_EXCEPTION);
		}
		return userEmail;
	}

	/**
	 * Sets the user email using specified user email.
	 * 
	 * @param userEmail
	 */
	public void setUserEmail(String userEmail) {
		if (userEmail.contains("%40")) {
			userEmail = userEmail.replace("%40", "@");
		}

		this.userEmail = userEmail;
	}

	/**
	 * Returns the user password if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return userPassword password of user.
	 */
	public String getUserPassword() {
		if (userPassword == null) {
			throw new NullPointerException(NULL_EXCEPTION);
		}
		return userPassword;
	}

	/**
	 * Sets the user password using specified password.
	 * 
	 * @param userPassword
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * Returns the user gender if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return userGender password of user.
	 */
	public int getUserGender() {
		return genderType;
	}

	/**
	 * Sets the gender using specified gender.
	 * 
	 * @param userGender
	 */
	public void setUserGender(int userGender) {
		this.genderType = userGender;
	}

	/**
	 * Returns the user address if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return userAddress location of user.
	 */
	public String getUserAddress() {
		if (userAddress != null) {
			return userAddress;
		}
		return null;
	}

	/**
	 * Sets the address using specified address.
	 * 
	 * @param userAddress
	 */
	public void setUserAddress(String userAddress) {
		if (userAddress.contains("%2C")) {
			userAddress = userAddress.replace("%2C", ",");
		}

		if (userAddress.contains("+")) {
			userAddress = userAddress.replace("+", " ");
		}

		this.userAddress = userAddress;
	}

	/**
	 * Returns the device token if not null, otherwise throws null pointer
	 * exception.
	 * 
	 * @return deviceToken device token.
	 */
	public static String getDeviceToken() {
		if (deviceToken == null) {
			deviceToken = "null";
			// throw new NullPointerException(NULL_EXCEPTION);
		}
		return deviceToken;
	}

	/**
	 * Sets the device token using specified token.
	 * 
	 * @param deviceToken
	 *            .
	 */
	public void setDeviceToken(String deviceToken) {
		UserDetail.deviceToken = deviceToken;
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
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
