/**
 * @Copyright Coppermobile 2014
 * **/
package com.coppermobile.gcm;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.atn.app.utils.AtnUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmHandler {

	public interface RegistrationGcm {
		public void gcmRegistered(boolean isSusscess, String deviceId);
	}

	public static final String GCM_MESSAGE_ACTION = "com.dynamex.GCM_MESSAGE";
	public static final String IS_GCM_MESSAGE = "IS_GCM_MESSAGE";
	public static final String GCM_NOTIFICATION_OBJECT = "GCM_NOTIFICATION_OBJECT";

	static final String TAG = "com.coppermobile.gcm.PushHandler";
	public static final String EXTRA_MESSAGE = "message";
	private static final String GCM_PREF = "coppermobile_pref";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static String GCM_SENDER_ID = "365009076643";//"120741351980";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	private static final String IS_STORED_ONSERVER = "is_stored_onserver";
	
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	String regid;
	Context context;
	private RegistrationGcm registrationDelegate = null;

	public void setRegistrationDelegate(RegistrationGcm registrationDelegate) {
		this.registrationDelegate = registrationDelegate;
	}

	public GcmHandler(Activity activity, RegistrationGcm registrationDelegate) {
		this.registrationDelegate = registrationDelegate;
		context = activity.getApplicationContext();
		if (checkPlayServices(activity)) {
			gcm = GoogleCloudMessaging.getInstance(activity);
			regid = getRegistrationId(context);

			if (regid.equals("")) {
				registerInBackground();
			} else {
				if (registrationDelegate != null) {
					registrationDelegate.gcmRegistered(true, regid);
				}
			}
		} else {
			if (registrationDelegate != null) {
				registrationDelegate.gcmRegistered(false,"Service not available");
			}
		}
	}

	public GcmHandler(Activity activity) {
		context = activity.getApplicationContext();
		if (checkPlayServices(activity)) {
			gcm = GoogleCloudMessaging.getInstance(activity);
			regid = getRegistrationId(context);

			if (regid.equals("")) {
				registerInBackground();
			}
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, Boolean>() {
			String msg = "";

			@Override
			protected Boolean doInBackground(Void... params) {
				Thread.currentThread()
				.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
						+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(GCM_SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean isSuccess) {
				AtnUtils.log("RegId::"+msg);
				if (isSuccess) {
					if (registrationDelegate != null) {
						registrationDelegate.gcmRegistered(true,getRegistrationId(context));
					}
				} else {
					if (registrationDelegate != null) {
						registrationDelegate.gcmRegistered(false, msg);
					}
				}
			}
		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public static void setRegIdStoreOnServer(Context context,boolean isStored) {
		final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(IS_STORED_ONSERVER, isStored);
		editor.commit();
	}
	
	public static boolean isStoredOnServer(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(GCM_PREF, Context.MODE_PRIVATE);
		return prefs.getBoolean(IS_STORED_ONSERVER, false);
	}
	
}
