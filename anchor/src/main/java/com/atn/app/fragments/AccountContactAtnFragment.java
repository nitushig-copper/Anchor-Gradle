/**
 * @Copyright CopeprMobile 2014.
 * */
package com.atn.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
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
import com.atn.app.utils.AtnUtils;
import com.atn.app.webservices.ContactAtnWebService;
import com.atn.app.webservices.WebserviceListener;
import com.atn.app.webservices.WebserviceType.ServiceType;

/**
 * Contact to Anchor screen user can send message to or feed back to anchor
 * */
public class AccountContactAtnFragment extends AtnBaseFragment {

	private EditText txtFullName, txtEmail, txtMessage;
	private ProgressDialog progressDialog;
	
	public static AccountContactAtnFragment newInstance() {
		AccountContactAtnFragment fragment = new  AccountContactAtnFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		setTitle(R.string.contact_title);
		View view = inflater.inflate(R.layout.account_contact_fragment, container, false);
		txtFullName = (EditText) view.findViewById(R.id.txt_full_name);
		txtEmail = (EditText) view.findViewById(R.id.txt_email);
		txtMessage = (EditText) view.findViewById(R.id.txt_message);
		return view;
	}
	
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.single_item, menu);
		MenuItem item = menu.findItem(R.id.single_item);
		item.setIcon(0);
		item.setTitle(R.string.send_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch (item.getItemId()) {
		case R.id.single_item: {
			
			if (isFieldsValidated()) {
				progressDialog = ProgressDialog.show(getActivity(),
						getResources().getString(R.string.please_wait),
						getResources().getString(R.string.please_wait));

				UserDetail userDetail = new UserDetail();
				userDetail.setUserId(UserDataPool.getInstance().getUserDetail().getUserId());
				userDetail.setUserFullName(txtFullName.getText().toString().trim());
				userDetail.setUserEmail(txtEmail.getText().toString().trim());
				userDetail.setUserMessage(txtMessage.getText().toString().trim());

				ContactAtnWebService contactService = new ContactAtnWebService();
				contactService.setWebserviceListener(responseListener);
				contactService.setUserData(userDetail);
				contactService.sendDetails();
			}
		}
			return true;
		default:
			break;
		}
    	
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		AtnUtils.hideKeyboard(getActivity());
	}
	
	/**
	 * Returns true if all the required fields are validated, otherwise returns false. 
	 * @return
	 */
	private boolean isFieldsValidated() {
		if (txtFullName.getText().length() == 0) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.empty_user_name), false);
			return false;
		}

		if (txtEmail.getText().length() == 0) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.empty_email), false);
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(
				txtEmail.getText().toString()).matches()) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.email_format_invalid),
					false);
			return false;
		}

		if (txtMessage.getText().length() == 0) {
			AtnApp.showMessageDialog(getActivity(),
					getResources().getString(R.string.message_cant_empty),
					false);
			return false;
		}

		return true;
	}
	
	
	/**
	 * Shows a dialog that message has been sent to ATN.
	 */
	private void showMessageSentDialog() {
		AlertDialog.Builder errorAlertDialog = new AlertDialog.Builder(getActivity());
		errorAlertDialog.setMessage(getResources().getString(R.string.contact_atn_response));
		errorAlertDialog.setPositiveButton(getActivity().getResources().getString(R.string.dialog_button_dismiss),
				new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//popBackStack();
			}
		});
		errorAlertDialog.show();
	}
	
	
	/**
	 * Listens for contact ATN web service response.
	 */
	private WebserviceListener responseListener = new WebserviceListener() {

		@Override
		public void onSetUrlError(ServiceType serviceType, Exception ex) {
			progressDialog.dismiss();
			if (serviceType == ServiceType.CONTACT_ATN) {
				AtnApp.showMessageDialog(getActivity(), ex.getMessage(), false);
			}
		}

		@Override
		public void onServiceResult(ServiceType serviceType, String result) {
			progressDialog.dismiss();
			showMessageSentDialog();
		}

		@Override
		public void onServiceError(ServiceType serviceType, int errorCode,
				String errorMessage) {
			progressDialog.dismiss();

			if (serviceType == ServiceType.CONTACT_ATN) {
				AtnApp.showMessageDialog(getActivity(), errorMessage, false);
			}
		}

		@Override
		public void onNoInternet(ServiceType serviceType, Exception ex) {
			progressDialog.dismiss();

			if (serviceType == ServiceType.CONTACT_ATN) {
				AtnApp.showMessageDialog(getActivity(), getResources()
						.getString(R.string.no_internet_available), false);
			}
		}
	};
	
	@Override
	protected int getScreenName() {
		return R.string.screen_name_Contact;
	};
}
