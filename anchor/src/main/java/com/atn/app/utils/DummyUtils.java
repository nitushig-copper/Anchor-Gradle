package com.atn.app.utils;

import android.location.Location;

public class DummyUtils {

	
	
	public static Location getLocation() {
		Location targetLocation = new Location("");//provider name is unecessary
	    targetLocation.setLatitude(32.8206645d);//your coords of course
	    targetLocation.setLongitude(-96.7313396d);
	    return targetLocation;
	}
	
	
	
	
}
