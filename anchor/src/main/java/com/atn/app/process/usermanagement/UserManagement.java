package com.atn.app.process.usermanagement;

import com.atn.app.datamodels.UserDetail;
import com.atn.app.webservices.CheckEmailWebservice;
import com.atn.app.webservices.CheckUserNameWebservice;
import com.atn.app.webservices.ForgotPasswordRecoveryWebservice;
import com.atn.app.webservices.SignInWebservice;
import com.atn.app.webservices.SignUpWebservice;
import com.atn.app.webservices.UpdateUserProfileWebservice;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceListener;
import com.atn.app.webservices.WebserviceType.ServiceType;

/**
 * Manages all the user releated operations like sign-in/sign-up/forgot password
 * recovery/change password details/ update password details etc. Response of
 * all these opetations can be listen from the usermanagement listener.
 * 
 * @author gagan
 * 
 */
public class UserManagement {

	public static final String LOGGED_USER = "logged_in_user";

	private UserManagementListener mUserManagementListener;

	/**
	 * Registers user management listener to listens for user operation
	 * notifications.
	 * 
	 * @param listener
	 *            to listens for notifications.
	 */
	public void setUserManagementListener(UserManagementListener listener) {
		mUserManagementListener = listener;
	}

	/**
	 * Unregisters user management listener to avoid listening user operation
	 * notifications.
	 */
	public void removeUserManagementListener() {
		mUserManagementListener = null;
	}

	/**
	 * Creates a web service to check whether specified user name already exists
	 * or not.
	 * 
	 * @param userName
	 *            to check
	 */
	public void checkUserExists(String userName) {
		CheckUserNameWebservice checkUserService = new CheckUserNameWebservice(
				userName);
		checkUserService.setWebserviceListener(mWebserviceListener);
		checkUserService.checkUser();
	}

	/**
	 * Creates a web service to check whether specified user name already exists
	 * or not.
	 * 
	 * @param userName
	 *            to check
	 * @param userId
	 *            user id of user
	 */
	public void checkUserExists(String userName, String userId) {
		CheckUserNameWebservice checkUserService = new CheckUserNameWebservice(
				userName, userId);
		checkUserService.setWebserviceListener(mWebserviceListener);
		checkUserService.checkUser();
	}

	/**
	 * Creates a web service to check whether specified email already exists or
	 * not.
	 * 
	 * @param email
	 *            to check.
	 */
	public void checkEmailExists(String email) {
		CheckEmailWebservice checkEmailService = new CheckEmailWebservice(email);
		checkEmailService.setWebserviceListener(mWebserviceListener);
		checkEmailService.checkEmail();
	}

	/**
	 * Creates a web service to check whether specified email already exists or
	 * not.
	 * 
	 * @param email
	 *            to check.
	 * @param userId
	 *            user id of user
	 * 
	 */
	public void checkEmailExists(String email, String userId) {
		CheckEmailWebservice checkEmailService = new CheckEmailWebservice(
				email, userId);
		checkEmailService.setWebserviceListener(mWebserviceListener);
		checkEmailService.checkEmail();
	}

	/**
	 * Creates a web service to do login using specified user name and password
	 * 
	 * @param userName
	 *            of the user.
	 * @param password
	 *            of the user
	 */
	public void doSignIn(String userName, String password) {
		SignInWebservice signInService = new SignInWebservice(userName,
				password);
		signInService.setWebserviceListener(mWebserviceListener);
		signInService.loginUser();
	}

	/**
	 * Creates a web service to do login using specified user name, password and
	 * device token
	 * 
	 * @param userName
	 *            of the user.
	 * @param password
	 *            of the user
	 * @param deviceToken
	 *            of the device.
	 */
	public void doSignIn(String userName, String password, String deviceToken) {
		SignInWebservice signInService = new SignInWebservice(userName,
				password, deviceToken);
		signInService.setWebserviceListener(mWebserviceListener);
		signInService.loginUser();
	}

	/**
	 * Creates a web service to create user profile using specified user name,
	 * email. password, gender and address.
	 * 
	 * @param userName
	 *            of the user
	 * @param email
	 *            of the user
	 * @param password
	 *            of the user
	 * @param sex
	 *            of the user, 1=MALE, 2=FEMALE
	 * @param address
	 *            of the user
	 * @param deviceToken
	 *            device id
	 */
	public void doSignUp(String userName, String email, String password,
			int sex, String address, String deviceToken,String profilePic) {
		SignUpWebservice signUpService = new SignUpWebservice(userName, email,
				password, sex, address, deviceToken,profilePic);
		signUpService.setWebserviceListener(mWebserviceListener);
		signUpService.registerUser();
	}

	/**
	 * Creates a web service to create user profile using specified user name,
	 * email. password, gender, address and Instagram access token.
	 * 
	 * @param userName
	 *            of the user
	 * @param email
	 *            of the user
	 * @param password
	 *            of the user
	 * @param sex
	 *            of the user, 1=MALE, 2=FEMALE
	 * @param address
	 *            of the user
	 * @param deviceToken
	 *            device id
	 * @param accessToken
	 *            Instagram access token
	 */
	public void doSignUp(String userName, String email, String password,
			int sex, String address, String deviceToken, String accessToken,String profilePic) {
		SignUpWebservice signUpService = new SignUpWebservice(userName, email,
				password, sex, address, deviceToken, accessToken,profilePic);
		signUpService.setWebserviceListener(mWebserviceListener);
		signUpService.registerUser();
	}

	/**
	 * Creates a web service to update user profile using specified user
	 * details.
	 * 
	 * @param userDetail
	 *            to update.
	 */
	public void doProfileUpdate(UserDetail userDetail) {
		UpdateUserProfileWebservice updateProfileService = new UpdateUserProfileWebservice(userDetail);
		updateProfileService.setWebserviceListener(mWebserviceListener);
		updateProfileService.updateProfile();
	}

	/**
	 * Creates a web service to recover password for specified user name. This
	 * will send an email to the email address of the specified user name (if
	 * user name is a valid ATN user).
	 * 
	 * @param userName
	 *            to recover password.
	 */
	public void doPasswordRecovery(String userName) {
		ForgotPasswordRecoveryWebservice passwordRecoveryService = new ForgotPasswordRecoveryWebservice(
				userName);
		passwordRecoveryService.setWebserviceListener(mWebserviceListener);
		passwordRecoveryService.recoverForgotPassword();
	}

	/**
	 * Creates a web service to change password for specified user.
	 * 
	 * @param userDetail
	 *            to change password.
	 * @param oldPassword
	 *            of user.
	 * @param newPassword
	 *            of user.
	 */
	public void changePassword(UserDetail userDetail, String oldPassword,
			String newPassword) {
		UpdateUserProfileWebservice updateProfileService = new UpdateUserProfileWebservice(
				userDetail);
		updateProfileService.setWebserviceListener(mWebserviceListener);
		updateProfileService.updatePassword(oldPassword, newPassword);
	}

	/**
	 * Listens for the user all management related web service response.
	 */
	private WebserviceListener mWebserviceListener = new WebserviceListener() {

		@Override
		public void onSetUrlError(ServiceType webserviceType, Exception ex) {
			if (mUserManagementListener != null) {
				mUserManagementListener.onError(webserviceType,
						WebserviceError.URL_ERROR, ex.getMessage());
			}
		}

		@Override
		public void onServiceResult(ServiceType webserviceType, String result) {

			if (mUserManagementListener != null) {
				mUserManagementListener.onSuccess(webserviceType, result);
			}
		}

		@Override
		public void onServiceError(ServiceType webserviceType, int errorCode,
				String errorMessage) {
			if (mUserManagementListener != null) {
				mUserManagementListener.onError(webserviceType, errorCode,
						errorMessage);
			}
		}

		@Override
		public void onNoInternet(ServiceType webserviceType, Exception ex) {
			if (mUserManagementListener != null) {
				mUserManagementListener.onError(webserviceType,
						WebserviceError.INTERNET_ERROR, ex.getMessage());
			}
		}

	};

}
