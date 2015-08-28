package com.atn.app;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.atn.app.activities.SplashScreen;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Creates application level context this can be used anywhere throughout the
 * App if required. This is mainly used in those codes where we don't have
 * access to the activity's context, i.e. used in ApnUtils to check Internet
 * connectivity.
 * 
 */
public class AtnApp extends Application {
	private static Context context;
	private static boolean isDialogVisible = false;

	public void onCreate() {
		super.onCreate();
		
		AtnApp.context = getApplicationContext();
	}

	/**
	 * Returns the context of the base application.
	 * 
	 * @return context of the app.
	 */
	public static Context getAppContext() {
		return AtnApp.context;
	}

	/**
	 * Shows message on toast using specified message when any field of form is
	 * not validated.
	 * 
	 * @param message
	 *            to show.
	 */
	public static void showMessage(Activity activity, final String message) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(AtnApp.getAppContext(), message, Toast.LENGTH_LONG).show();
			}
		});

	}

	/**
	 * Shows an alert dialog to notify user when some serious error occurred,
	 * like: No Internet access, invalid URL etc.
	 * 
	 * @param errorMessage
	 *            to show.
	 */
	public static void showMessageDialog(final Activity activity, final String errorMessage, final boolean dismissActivity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				AlertDialog.Builder errorAlertDialog = new AlertDialog.Builder(activity);
				errorAlertDialog.setMessage(errorMessage);
				errorAlertDialog.setPositiveButton(getAppContext().getResources().getString(R.string.dialog_button_dismiss),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (dismissActivity)
									activity.finish();
							}
						});

				errorAlertDialog.show();
			}
		});
	}

	/**
	 * Shows an alert dialog to notify user when some serious error occurred,
	 * like: No Internet access, invalid URL etc.
	 * 
	 * @param errorMessage
	 *            to show.
	 */
	public static void showErrorDialog(final String errorMessage) {
		AlertDialog.Builder errorAlertDialog = new AlertDialog.Builder(context);
		errorAlertDialog.setMessage(errorMessage);
		errorAlertDialog.setPositiveButton(getAppContext().getResources().getString(R.string.dialog_button_dismiss),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Dismiss alert dialog
					}
				});

		errorAlertDialog.show();
	}

	/**
	 * Shows an alert dialog when user is not logged-in.
	 */
	public static void showLoginScreen(final Activity activityContext) {
		
		AlertDialog.Builder loginAlertDialog = new AlertDialog.Builder(activityContext);
		loginAlertDialog.setTitle(R.string.app_name);
		loginAlertDialog.setMessage(R.string.message_text);
		loginAlertDialog.setPositiveButton(R.string.login_button_text,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent browsingIntent = new Intent(activityContext, SplashScreen.class);
						browsingIntent.putExtra("IS_FROM_MAIN_SCREEN", true);
						activityContext.startActivity(browsingIntent);
					}
				});
		loginAlertDialog.setNegativeButton(R.string.not_now_Buttun_text, new  OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		loginAlertDialog.show();
	}

	/**
	 * Shows a dialog to turn on location services if it is turned off.
	 */
	public static void showTurnOnLocationServicesDialog(final Activity activity) {
		synchronized (activity) {
			if (!isDialogVisible) {
				isDialogVisible = true;

				final AlertDialog.Builder enableLocationDialog = new AlertDialog.Builder(activity);
				enableLocationDialog.setTitle(activity.getResources().getString(R.string.turn_on_location_services_title));
				enableLocationDialog.setMessage(activity.getResources().getString(R.string.turn_on_location_services_message));
				enableLocationDialog.setCancelable(false);

				/**
				 * Opens location settings to enable location services.
				 */
				enableLocationDialog.setPositiveButton(activity.getResources()
						.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								activity.startActivity(intent);
								isDialogVisible = false;
							}
						});

				/**
				 * Dismiss location settings alert dialog.
				 */
				enableLocationDialog.setNegativeButton(activity.getResources().getString(R.string.dialog_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Dismiss the dialog
								isDialogVisible = false;
							}

						});

				enableLocationDialog.show();
			}
		}
	}
	
	
	
	/**
	 * Google analytics v4 impl
	 * */
	// The following line should be changed to include the correct property id.
    //private static final String PROPERTY_ID = "UA-XXXXX-Y";using config file
    //public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
   public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
         // Set the log level to verbose.
            //analytics.getLogger().setLogLevel(LogLevel.VERBOSE);
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
	
}