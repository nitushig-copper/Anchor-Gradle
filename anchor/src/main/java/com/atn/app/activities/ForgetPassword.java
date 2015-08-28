package com.atn.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.process.usermanagement.UserManagement;
import com.atn.app.process.usermanagement.UserManagementListener;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceType.ServiceType;

/**
 * Used to recover the forget password. This creates a web service and sends the
 * user password to the specified user's email.
 */
public class ForgetPassword extends ActionBarActivity {

	EditText txtUserName;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_password);
		 final ActionBar bar = getSupportActionBar();
		 bar.setDisplayHomeAsUpEnabled(true);
		 bar.setDisplayShowHomeEnabled(false);
		txtUserName = (EditText) findViewById(R.id.txt_user_forgot_password);
	}

	/**
	 * Returns whether userName is in valid format or not.
	 * 
	 * @param userName
	 *            to check.
	 * @return true if userName is valid, otherwise returns false.
	 */
	private boolean isUserNameValidated(String userName) {
		if (userName.length() == 0) {
			AtnApp.showMessageDialog(ForgetPassword.this, getResources().getString(R.string.empty_user_name), false);
			return false;
		}

		return true;
	}

	/**
	 * Sends the password details on the specified userName.
	 */
	private void sendPasswordDetailsOnEmail(String userName) {
		progressDialog = ProgressDialog.show(ForgetPassword.this, 
				getResources().getString(R.string.please_wait), getResources().getString(R.string.please_wait));

		UserManagement userManagement = new UserManagement();
		userManagement.setUserManagementListener(mUserManagementListener);
		userManagement.doPasswordRecovery(userName);
	}

	/**
	 * Listens for the user management web service notifications. The service
	 * will return a response message in onSuccess if it is executed
	 * successfully, otherwise returns an error message in onError.
	 */
	UserManagementListener mUserManagementListener = new UserManagementListener() {

		@Override
		public void onSuccess(ServiceType webserviceType, String response) {
			AtnApp.showMessage(ForgetPassword.this,
					getResources().getString(R.string.recover_password_response));
			progressDialog.dismiss();
			ForgetPassword.this.finish();
		}

		@Override
		public void onError(ServiceType webserviceType, int errorCode,
				String errorMessage) {
			switch (errorCode) {
			case WebserviceError.INTERNET_ERROR:
				AtnApp.showMessageDialog(ForgetPassword.this, getResources()
						.getString(R.string.no_internet_available), false);
				break;

			case WebserviceError.UNKNOWN_ERROR:
				AtnApp.showMessageDialog(ForgetPassword.this, getResources()
						.getString(R.string.unknown_webservice_error), false);
				break;

			// Show error message for invalid credentials.
			case 4:
				AtnApp.showMessageDialog(ForgetPassword.this, errorMessage,
						false);
				break;
			}

			progressDialog.dismiss();
		}
	};
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.signup, menu);
		MenuItem item = menu.findItem(R.id.sign_up);
		item.setTitle(R.string.send_title);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.sign_up) {
			
			String userName = txtUserName.getText().toString();
			if (isUserNameValidated(userName))
				sendPasswordDetailsOnEmail(userName);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
