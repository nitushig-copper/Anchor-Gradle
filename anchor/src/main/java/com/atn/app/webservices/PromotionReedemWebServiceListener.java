package com.atn.app.webservices;

public interface PromotionReedemWebServiceListener {
	 
	public abstract void onFailed(int errorCode, String errorMessage);
	 public abstract void onSuccess(String message);
	  
	 

}
