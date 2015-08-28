/**
 * @Copyright CopperMobile 2014
 * */
package com.atn.app.datamodels;
//implemented by VenueModel and Atn Busness class
public interface AtnBar {
	
	public static final int NON_ATN_BAR = 0;
	public static final int NON_ATN_BAR_FAV = 1;
	public static final int ATN_BAR_FAV = 2;
	public static final int ATN_BAR = 3;
	
	public int getBarType();
}
