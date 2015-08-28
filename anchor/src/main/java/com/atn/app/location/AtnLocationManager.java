package com.atn.app.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.location.LocationServiceErrorMessages.ErrorDialogFragment;
import com.atn.app.service.SynchService;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationClient;

/**
 * Creates a location manager to get the current location. If it failed to get the current location then it
 * sends the connection failed error to ConnectionFailedListener.
 * 
 *
 */
public class AtnLocationManager implements LocationListener,ConnectionCallbacks,
    OnConnectionFailedListener{
	
	private static AtnLocationManager instance = null;
	//private  LocationClient mLocationClient;
	private  GoogleApiClient mGoogleApiClient;
	 // A mFsRequest to connect to Location Services
    //private LocationRequest mLocationRequest;
	//Location manager to check for location services whether services are enabled or not.
	private LocationManager mLocationManager;
	private Activity activity = null;
	
	//set this if as location found and service is not started then it will start service automatically
	public boolean shouldStartService = false;

	
	 // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS)         
           // .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	private AtnLocationManager() {
		shouldStartService = true;
		clearCachedLocation();
		mLocationManager = (LocationManager) AtnApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		/*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
//        mLocationClient = new LocationClient(AtnApp.getAppContext(), this, this);
//        mLocationClient.connect();
        
		mGoogleApiClient = new GoogleApiClient.Builder(AtnApp.getAppContext())
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
		
		mGoogleApiClient.connect();
	}
	
	/**
	 * Returns the instance of the class.
	 * 
	 * @return
	 */
	public static AtnLocationManager getInstance() {
		if (instance == null) {
			instance = new AtnLocationManager();
		}
		return instance;
	}

	/**
	 * Verify that Google Play services is available before making a mFsRequest.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(AtnApp.getAppContext());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {

			if (this.activity != null) {
				// Display an error dialog
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this.activity, 0);
				if (dialog != null) {
					ErrorDialogFragment errorFragment = new ErrorDialogFragment();
					errorFragment.setDialog(dialog);
					errorFragment.show(this.activity.getFragmentManager(),LocationUtils.APPTAG);
				}
			} else {
				AtnUtils.showToast("Google Play Service not available or need to be update");
			}
			return false;
		}
	}
	
	public void startUpdates(Activity activity) {
		this.activity = activity;

		// If the client is connected
		if (mGoogleApiClient.isConnected() && servicesConnected()) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
	                mGoogleApiClient,
	                REQUEST,
	                this); 
		}
	}

	public void stopUpdates() {
		// If the client is connected
		if (mGoogleApiClient.isConnected() && servicesConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		}
		this.activity = null;
	}

	// clean cached location data
	public void clearCachedLocation() {
		SharedPrefUtils.clearLastLocation(AtnApp.getAppContext());
	}
	    
	///disconnect client
	public void disconnect() {
		// After disconnect() is called, the client is considered "dead".
		mGoogleApiClient.disconnect();
        instance = null;
	}
	
	    
	/**
	 * Returns true if location manager is connected.
	 * 
	 * @return true if location manager client is connected, otherwise returns false.
	 */
	public boolean isConnected() {
		if (mGoogleApiClient != null) {
			return mGoogleApiClient.isConnected();
		}
		return false;
	}
	
	/**
	 * Returns true if the the specified old location is 100 meters far then the current location.
	 * @return
	 */
	public boolean isLocationChanged() {
		Location oldLocation = getLastLocation();
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			Location newLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			if (newLocation == null || oldLocation == null) {
				return false;
			}
			return (newLocation.distanceTo(oldLocation) >=LocationUtils.MINIMUM_DISPLACMENT_IN_METERS);
		}
		return false;
	}
	
	//always return current location
	public Location getCurrentLocation() {
			if (mGoogleApiClient != null) {
					if (mGoogleApiClient.isConnected() && LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {
						return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
					} else {
						return null;
					}
			}
		return null;
	}
	
	/**
	 * Returns the last location from preference. if not saved yet then it will
	 * save and return last known location from provider
	 * 
	 * @return Location
	 */
	public Location getLastLocation() {
		Location currentLocation = SharedPrefUtils.getLastLocation(AtnApp.getAppContext());
		if(currentLocation!=null) return currentLocation;
		
		currentLocation = getCurrentLocation();
		if(currentLocation!=null) {
			SharedPrefUtils.saveLocation(AtnApp.getAppContext(), currentLocation.getLatitude(), currentLocation.getLongitude());
		}
		return currentLocation;
	}
	
	
	/**
	 * Returns the instance of location manager.
	 * @return
	 */
	public LocationManager getLocationManager() {
		return mLocationManager;
	}
	
	/**
	 * Returns true if location services are enabled.
	 * 
	 * @return true if location services are enabled, otherwise returns false;
	 */
	public boolean isLocationServicesEnabled() {
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
				|| mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this.activity,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if(this.activity!=null) {
			startUpdates(this.activity);
		}
		
		if(shouldStartService&&!SynchService.IS_RUNNING) {
			AtnUtils.runSynchService(AtnApp.getAppContext(),SynchService.Command.RELOAD_VENUE);
		}
		shouldStartService = false;
	}

//	@Override
//	public void onDisconnected() {
//		AtnUtils.log("Location Client disconnected!");
//	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLocationChanged(Location location) {
		if(isLocationChanged()) {
			showLocationChangedDialog();
		}
	}
	
	/**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

    	if(this.activity!=null){
    		 // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                activity,
                LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {

                // Create a new DialogFragment in which to show the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();

                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);

                // Show the error dialog in the DialogFragment
                errorFragment.show(activity.getFragmentManager(), LocationUtils.APPTAG);
            }
    	}
    }
    
    private boolean isDialogVisible = false;
    private void showLocationChangedDialog() {
    	
    	if(this.activity!=null&&!isDialogVisible&&(this.activity instanceof AtnActivity)) {
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
    		TextView txtView = (TextView) this.activity.getLayoutInflater().inflate(R.layout.text_layout, null, false);
    		txtView.setText(R.string.location_changed_alert);
    		builder.setView(txtView);
    		builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   dialog.dismiss();
    		        	   Location currentLocation = getCurrentLocation();
    		        	   SharedPrefUtils.saveLocation(AtnApp.getAppContext(), currentLocation.getLatitude(), currentLocation.getLongitude());
    		        	   AtnUtils.runSynchService(activity,SynchService.Command.RELOAD_VENUE);
    		        	   isDialogVisible = false;
    		           }
    		       });
    		builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   dialog.dismiss();
    		        	   isDialogVisible = false;
    		           }
    		       });
    		builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					isDialogVisible = false;
				}
			});
    		// Create the AlertDialog
    		AlertDialog dialog = builder.create();
    		dialog.show();	
    		isDialogVisible   = true;
    	}
	}

    //auto start service when ever client connected
	public void setStartService(boolean shouldStart) {
		shouldStartService =shouldStart;
	}
}
