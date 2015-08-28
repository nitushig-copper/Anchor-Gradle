package com.atn.app.pool;

import com.atn.app.AtnApp;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.utils.SharedPrefUtils;

/**
 * Creates data pool for the current logged user details. You can access logged-in user details from this data pool.
 *
 */
public class UserDataPool {

	
	private static UserDataPool instance = null;
	private UserDetail loggedUser = null;
	
	private UserDataPool(){}
	
	/**
	 * Instantiate user data pool.
	 * @return
	 */
	public static UserDataPool getInstance() {
		if (instance == null) {
			instance = new UserDataPool();
		}
		return instance;
	}
	
	
	/**
	 * Sets the logged in user details to data pool using specified user details.
	 * 
	 * @param userDetail to add in data pool.
	 */
	public void setUserDetail(UserDetail userDetail) {
		loggedUser = userDetail;
	}
	
	/**
	 * Returns the currently logged-in user details.
	 * 
	 * @return logged-in user details if exists, otherwise returns null.
	 */
	public UserDetail getUserDetail(){
		if(loggedUser==null){
			loggedUser = DbHandler.getInstance().getLoggedInUser();
		}
		return loggedUser;
	}
	
	/**
	 * Returns whether the user is currently logged-in or not.
	 * 
	 * @return true if user is logged-in, otherwise returns false.
	 */
	public boolean isUserLoggedIn() {
		return SharedPrefUtils.isUserLoggedIn(AtnApp.getAppContext());
	}
	
	public void setUserLoggedIn(boolean isLoogedIn){
		SharedPrefUtils.setUserLoggedIn(AtnApp.getAppContext(), isLoogedIn);
	}
	
	
	
	public void updateUser(UserDetail userDetail) {
		
		
	}
	
	
	
}
