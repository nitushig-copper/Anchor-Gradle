package com.atn.app.process.usermanagement;

import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Listens for user management web services response notification. If the service executes successfully then
 * it returns response in its onSuccess method, otherwise it returns an error message in its onError method.
 * 
 * @author gagan
 *
 */
public interface UserManagementListener
{
	/**
	 * Notifies an error message when a user management web service is not executed properly or when it has
	 * an error message from the server as response.
	 * 
	 * @param errorMessage from the server
	 */
	public void onError(ServiceType serviceType, int errorCode, String errorMessage);
	
	/**
	 * Notifies with a response message that web service is executed successfully. This response message is
	 * returned from the server.
	 * 
	 * @param response from the server.
	 */
	public void onSuccess(ServiceType serviceType, String response);
	
}
