/****
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.activities.SplashScreen;
import com.atn.app.constants.ATNConstants;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.webservices.BusinessSubscribeWebservice;
import com.atn.app.webservices.BusinessSubscribeWebserviceListener;
import com.atn.app.webservices.WebserviceType.ServiceType;

/**
 * User can enable or disable GCM Notification on this screen
 * **/
public class AccountPushNotificationFragment extends AtnBaseFragment {

	//save in shared Preference if all venue selected.
	private static final String pref_name = "PUSH_NOTIFICATION_PREFERENCES";
	private static final String pref_name_value = "PUSH_NOTIFICATION_PREFERENCES_VALUE";
	
	//
	private CheckBox chkPushNotification;
	private BusinessSubscribeWebservice subscribeService;
	private ProgressDialog progressDialog;
	private boolean isSubscribed;
	private SharedPreferences sharedPref;
	
	//pass any argument here if needed
	public static AccountPushNotificationFragment newInstance() {
		AccountPushNotificationFragment fragment = new  AccountPushNotificationFragment();
		return fragment;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		setTitle(R.string.push_notification_text);
		
		View view = inflater.inflate(R.layout.account_push_notification_fragment, container, false);
		sharedPref = getActivity().getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
		subscribeService = new BusinessSubscribeWebservice();
		subscribeService.setBusinessSubscribeWebserviceListener(listener);
		
		chkPushNotification = (CheckBox) view.findViewById(R.id.push_notification_check_box);
		chkPushNotification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkSubscriptionStatus();
			}
		});
		
		TextView txtVenueChoose = (TextView) view.findViewById(R.id.txt_venue_i_choose);
		txtVenueChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addToBackStack(AccountVenueChooseFragment.newInstance(getChildFragmentListener()));
			}
		});
		return view;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();

		if (sharedPref.getBoolean(pref_name_value, false)) {
			chkPushNotification.setChecked(true);
			isSubscribed = true;
		} else {
			chkPushNotification.setChecked(false);
			isSubscribed = false;
		}
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		MenuItem item = menu.add("Logout");
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		item.setIcon(R.drawable.icn_actionbar_logout);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	if(item.getTitle().equals("Logout")){
    		showLogoutAlertDialog();
    		return true;
    	}
        return super.onOptionsItemSelected(item);
    }
	
	
	/**
	 * If subscription is already enabled then show alert dialog to disable it, otherwise enable subscription.
	 */
	private void checkSubscriptionStatus() {
		if (isSubscribed) {
			showUnsubscribeDialog();
		} else {
			progressDialog = ProgressDialog.show(getActivity(), getResources()
					.getString(R.string.please_wait),
					getResources().getString(R.string.please_wait));
			subscribeService.subscribeAllBusiness();
		}
	}
	
	/**
	 * Shows an alert dialog to unsubscribe to all favorite ATN businesses.
	 */
	private void showUnsubscribeDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setMessage(getActivity().getResources().getString(
				R.string.unsubsribe_all_atn_venue));

		alertDialog.setPositiveButton(
				getActivity().getResources().getString(R.string.dialog_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog = ProgressDialog.show(getActivity(),
								getResources().getString(R.string.please_wait),
								getResources().getString(R.string.please_wait));
						subscribeService.unsubscribeAllBusiness();
					}
				});

		alertDialog.setNegativeButton(
				getActivity().getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						chkPushNotification.setChecked(true);
					}
				});

		alertDialog.show();
	}

	/**
	 * Shows an alert dialog to ask logged-in user whether he wants to logout or
	 * not.
	 */
	private void showLogoutAlertDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(getActivity().getResources().getString(
				R.string.logout_title));
		alertDialog.setMessage(getActivity().getResources().getString(
				R.string.logout_alert_message));

		alertDialog.setPositiveButton(
				getActivity().getResources().getString(R.string.dialog_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						logoutUser();
					}
				});

		alertDialog.setNegativeButton(
				getActivity().getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING
					}
				});

		alertDialog.show();
	}

	/**
	 * Logout the user by deleting user entry from database and clearing all the
	 * data pool.
	 */
	private void logoutUser() {
		Atn.cleanDatabase(getActivity());
		DbHandler.getInstance().logoutUser(
				UserDataPool.getInstance().getUserDetail().getUserId());
		DbHandler.getInstance().clearDatabase();
		UserDataPool.getInstance().setUserDetail(null);

		// Clear Instagram and Facebook access token details from shared
		// preferences.
		InstagramSession.resetAccessToken(getActivity());

		FacebookSession fbSession = new FacebookSession(getActivity());
		fbSession.resetAccessToken();
		AtnUtils.logoutFacebook(fbSession);

		Bundle dataBundle = new Bundle();
		dataBundle.putBoolean(ATNConstants.DESTROY_SPLASH, true);
		Intent loginIntent = new Intent(getActivity(), SplashScreen.class);
		loginIntent.putExtra(ATNConstants.MAIN_BUNDLE, dataBundle);

		startActivity(loginIntent);
		getActivity().finish();
	}

	/**
	 * Listens for subscribe all/un-subscribe all atn venue notifications for
	 * push notification.
	 */
	private BusinessSubscribeWebserviceListener listener = new BusinessSubscribeWebserviceListener() {
		@Override
		public void onSuccess(ServiceType serviceType, String businessId) {
			progressDialog.dismiss();

			switch (serviceType) {
			case BUSINESS_SUBSCRIBE_ALL:
				chkPushNotification.setChecked(true);
				isSubscribed = true;
				sharedPref.edit().putBoolean(pref_name_value, true).commit();

				/**
				 * Update subscription status for all favorited ATN businesses.
				 */
				DbHandler.getInstance().updateSubscriptionStatusForAllBusiness(
						true);

				break;

			case BUSINESS_UNSUBSCRIBE_ALL:
				chkPushNotification.setChecked(false);
				isSubscribed = false;
				sharedPref.edit().putBoolean(pref_name_value, false).commit();

				/**
				 * Update subscription status for all favorited ATN businesses.
				 */
				DbHandler.getInstance().updateSubscriptionStatusForAllBusiness(
						false);

				break;
			default:
				break;
			}
		}

		@Override
		public void onFailed(ServiceType serviceType, int errorCode,
				String errorMessage) {
			isSubscribed = false;
			progressDialog.dismiss();
			chkPushNotification.setChecked(false);
			sharedPref.edit().putBoolean(pref_name_value, false).commit();
		}
	};

}
