/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.pool.UserDataPool;
import com.atn.app.process.usermanagement.UserManagement;
import com.atn.app.process.usermanagement.UserManagementListener;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceType.ServiceType;

//Change password screen
public class AccountChangePasswordFragment extends AtnBaseFragment {

	private EditText txtOldPassword, txtNewPassword, txtConfirmPassword;
	private ProgressDialog progressDialog;

	public static AccountChangePasswordFragment newInstance() {
		AccountChangePasswordFragment accountFragment = new AccountChangePasswordFragment();
		return accountFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setActionBarAlpha(ACTION_BAR_OPEQUE);
		setTitle(R.string.change_password_title);
		View view = inflater.inflate(R.layout.account_change_password,container, false);
		txtOldPassword = (EditText) view.findViewById(R.id.txt_current_password);
		txtNewPassword = (EditText) view.findViewById(R.id.txt_new_password);
		txtConfirmPassword = (EditText) view.findViewById(R.id.txt_confirm_password);

		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.signup, menu);
		MenuItem item = menu.findItem(R.id.sign_up);
		item.setTitle(R.string.save_title);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.sign_up) {
			if (isFormValidated()) {
				progressDialog = ProgressDialog.show(getActivity(),
						getResources().getString(R.string.please_wait),
						getResources().getString(R.string.please_wait));

				//hit change password webservice
				UserDetail loggedUser = UserDataPool.getInstance().getUserDetail();
				UserManagement userManagement = new UserManagement();
				userManagement.setUserManagementListener(mUserManagementListener);
				userManagement.changePassword(loggedUser, txtOldPassword
						.getText().toString(), txtNewPassword.getText()
						.toString());
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Returns true if form is validated.
	 * 
	 * @return true if form is validated, otherwise returns false.
	 */
	private boolean isFormValidated() {
		if (txtOldPassword.getText().length() == 0) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.empty_old_password),
					false);
			txtOldPassword.requestFocus();
			return false;
		} else if (txtNewPassword.getText().length() == 0) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.empty_new_password),
					false);
			txtNewPassword.requestFocus();
			return false;
		} else if (txtNewPassword.getText().length() < 6) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.password_low_length),
					false);
			txtNewPassword.requestFocus();
			return false;
		} else if (txtConfirmPassword.getText().length() == 0
				|| !txtConfirmPassword.getText().toString()
						.equalsIgnoreCase(txtNewPassword.getText().toString())) {
			AtnApp.showMessageDialog(getActivity(),
					getResources()
							.getString(R.string.confirm_password_mismatch),
					false);
			txtConfirmPassword.requestFocus();
			return false;
		}

		return true;
	}

	/**
	 * Listens for the change password response from the server.
	 */
	private UserManagementListener mUserManagementListener = new UserManagementListener() {

		@Override
		public void onSuccess(ServiceType serviceType, String response) {
			progressDialog.dismiss();
			if (serviceType == ServiceType.CHANGE_PASSWORD) {
				AtnApp.showMessageDialog(getActivity(), getResources()
						.getString(R.string.password_changed_message), false);
			}
		}

		@Override
		public void onError(ServiceType serviceType, int errorCode,
				String errorMessage) {
			progressDialog.dismiss();

			if (errorCode == WebserviceError.INTERNET_ERROR) {
				AtnApp.showMessageDialog(
						getActivity(),
						getActivity().getResources().getString(
								R.string.no_internet_available), false);
			} else if (errorCode == WebserviceError.UNKNOWN_ERROR) {
				AtnApp.showMessageDialog(getActivity(),
						getActivity().getResources().getString(
								R.string.no_internet_available), false);
			} else {
				AtnApp.showMessageDialog(getActivity(), errorMessage, false);
			}
		}
	};

}
