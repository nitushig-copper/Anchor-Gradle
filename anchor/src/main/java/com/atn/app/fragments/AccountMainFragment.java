/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.atn.app.R;
import com.atn.app.activities.SplashScreen;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.UiUtil;

/**
 * Shows All settings of application
 * */
public class AccountMainFragment extends AtnBaseFragment implements OnClickListener,OnCheckedChangeListener {

	private CheckBox mReceiveNotificationCheckBox;
	
	public static AccountMainFragment newInstance() {
		AccountMainFragment fragment = new  AccountMainFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setTitle(R.string.account_title);
		setActionBarAlpha(ACTION_BAR_OPEQUE);
		View view = inflater.inflate(R.layout.account_main_fragment, container, false);
		
		//my interest.
		view.findViewById(R.id.my_interest_text_view).setOnClickListener(this);

		mReceiveNotificationCheckBox = (CheckBox)view.findViewById(R.id.receive_notification_check_box);
		mReceiveNotificationCheckBox.setOnCheckedChangeListener(this);
		///push notification
		view.findViewById(R.id.receive_pushnotification_text_view).setOnClickListener(this);
		///contact Anchor
		view.findViewById(R.id.about_anchor_text_view).setOnClickListener(this);
		///contact anchor
		view.findViewById(R.id.contact_anchor_text_view).setOnClickListener(this);
		view.findViewById(R.id.term_and_condition_text_view).setOnClickListener(this);
		view.findViewById(R.id.account_detail_text_view).setOnClickListener(this);
		
		return view;
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
    	if(item.getTitle().equals("Logout")) {
    		showLogoutAlertDialog();
    		return true;
    	}
        return super.onOptionsItemSelected(item);
    }
	
    
    @Override
	public void onClick(View v) {
		
    	switch (v.getId()) {
		case R.id.my_interest_text_view:
			
			addToBackStack(ChooseInterestFragment.newInstance());
			break;
		case R.id.receive_pushnotification_text_view:
			addToBackStack(AccountPushNotificationFragment.newInstance());
			break;
		case R.id.about_anchor_text_view:
			UiUtil.showToast(getActivity(), "Content Needed");
			break;
		case R.id.contact_anchor_text_view:
			addToBackStack(AccountContactAtnFragment.newInstance());
			break;
		case R.id.term_and_condition_text_view:
			addToBackStack(TAndCFragment.newInstance());
			break;
		case R.id.account_detail_text_view:
			addToBackStack(ProfileFragment.newInstance(false));
			break;
		default:
			break;
		}
		
	}
    
	
	/**
	 * Shows an alert dialog to ask logged-in user whether he wants to logout or not. 
	 */
	private void showLogoutAlertDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(getActivity().getResources().getString(R.string.logout_title));
		alertDialog.setMessage(getActivity().getResources().getString(R.string.logout_alert_message));
		
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
	 * Logout the user by deleting user entry from database and clearing all the data pool. 
	 */
	private void logoutUser() {
		
		Atn.cleanDatabase(getActivity());
		
		DbHandler.getInstance().logoutUser(UserDataPool.getInstance().getUserDetail().getUserId());
		DbHandler.getInstance().clearDatabase();
		UserDataPool.getInstance().setUserDetail(null);
		//Clear Instagram and Facebook access token details from shared preferences.
		InstagramSession.resetAccessToken(getActivity());
		FacebookSession fbSession = new FacebookSession(getActivity());
		fbSession.resetAccessToken();	
		AtnUtils.logoutFacebook(fbSession);
		
		
		Intent loginIntent = new Intent(getActivity(), SplashScreen.class);
		startActivity(loginIntent);
		getActivity().finish();
	}
	
	@Override
	protected int getScreenName() {
		return R.string.screen_name_account;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mReceiveNotificationCheckBox.setChecked(isChecked);
	}
	
}
