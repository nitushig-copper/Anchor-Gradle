/**
 * Copyright Copeprmobile 2014
 * **/
package com.atn.app.fragments;

import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.AtnActivity.FacebookLoginListener;
import com.atn.app.activities.AtnActivity.ImagePicker;
import com.atn.app.activities.MainMenuActivity;
import com.atn.app.activities.TermsAndCondition;
import com.atn.app.component.BlurImageView;
import com.atn.app.component.TrackingScrollView;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.datamodels.UserDetail.GenderType;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.instagram.InstagramDialog.InstagramLoginListener;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.instagram.InstagramSession.InstagramUserInfoListner;
import com.atn.app.listener.OnScrollChangedListener;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.pool.UserDataPool;
import com.atn.app.process.usermanagement.UserManagement;
import com.atn.app.process.usermanagement.UserManagementListener;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.UiUtil;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceType;
import com.atn.app.webservices.WebserviceType.ServiceType;
import com.coppermobile.gcm.GcmHandler;
import com.coppermobile.gcm.GcmHandler.RegistrationGcm;
import com.facebook.model.GraphUser;

import coppermobile.googlesearch.GeocodeResponse;
import coppermobile.googlesearch.GoogleSearchAsync;
/**
 * Create or update profile. We are using this fragment on 
 * two screen for creating user profile and updating user profile.
 * **/
public class ProfileFragment extends AtnBaseFragment  implements OnClickListener,ImagePicker ,FacebookLoginListener {
	
	//key to decide new or existing user
	public static final String IS_NEW_USER = "IS_NEW_USER";
	private boolean isNewUser = true;
	//1 for male,0 for female , 2 for nothing.
	private int mSelectedGender = 2;
	private BlurImageView mUserPicImageView;
	private EditText mUserNameTextEdit;
	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mConfirmPasswordEditText;
	private EditText mLoactionEditText;
	private EditText mGenderEditText;
	private CheckBox mInstgramCheckBox;
	private TrackingScrollView mScroller = null;
	//user profile pic height
	private float firstItemHeight;
	private GetAddressTask mAddressDecodeTask;
	private String profilePicPath  = null;
	
	
	////for updating profile
	private FacebookSession fbSession;
	private CheckBox mFacebookCheckBox;
	private UserDetail userDetail;
	
	/**
	 * factory method for creating new instance of this fragment
	 * @param isNewUser true comes from login screen
	 * @return new instance of ProfileFragment
	 * */ 
	public static ProfileFragment newInstance(boolean isNewUser) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_NEW_USER, isNewUser);
		ProfileFragment profileFragment = new ProfileFragment();
		profileFragment.setArguments(bundle);
		
		return profileFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			isNewUser = getArguments().getBoolean(IS_NEW_USER, true);
		}
		fbSession = new FacebookSession(getActivity());
		firstItemHeight = getResources().getDimensionPixelSize(R.dimen.user_pic_height);
		
		if(!isNewUser) {
			userDetail  = DbHandler.getInstance().getLoggedInUser();
			fbSession = new FacebookSession(getActivity());
		}
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setActionBarAlpha(ACTION_BAR_TRANSPARENT);
		// set title
		setTitle(R.string.account_detail_title);
		View view = inflater.inflate(R.layout.profile_fragment_layout,container, false);
		mUserPicImageView = (BlurImageView)view.findViewById(R.id.user_pic_image_view);
		mUserNameTextEdit = (EditText)view.findViewById(R.id.user_name_edit_text);
		mEmailEditText  = (EditText)view.findViewById(R.id.email_edit_text);
		mPasswordEditText  = (EditText)view.findViewById(R.id.password_edit_text);
		mConfirmPasswordEditText  =(EditText)view.findViewById(R.id.confirm_password_edit_text);
		mLoactionEditText  =(EditText)view.findViewById(R.id.location_edit_text);
		mGenderEditText = (EditText)view.findViewById(R.id.gender_edit_text);
		mGenderEditText.setOnClickListener(this);
		view.findViewById(R.id.space_view).setVisibility(View.VISIBLE);
		view.findViewById(R.id.space_view).setOnClickListener(this);
		
		
		mInstgramCheckBox = (CheckBox)view.findViewById(R.id.instagram_checkBox);
		mInstgramCheckBox.setChecked(!TextUtils.isEmpty(InstagramSession.getToken(getActivity())));
		mInstgramCheckBox.setOnClickListener(this);
		
		mUserNameTextEdit.addTextChangedListener(new MyTextWather(mUserNameTextEdit));
		mEmailEditText.addTextChangedListener(new MyTextWather(mEmailEditText));
		mGenderEditText.addTextChangedListener(new MyTextWather(mGenderEditText));
		
		if(isNewUser) {
			mPasswordEditText.addTextChangedListener(new MyTextWather(mPasswordEditText));
			mConfirmPasswordEditText.addTextChangedListener(new MyTextWather(mConfirmPasswordEditText));
			view.findViewById(R.id.see_terms_button).setOnClickListener(this);
		} else {
			mConfirmPasswordEditText.setVisibility(View.GONE);
			view.findViewById(R.id.see_terms_button).setVisibility(View.GONE);
			mPasswordEditText.setFocusable(false);
			mPasswordEditText.setOnClickListener(this);
			mPasswordEditText.setText(R.string.change_password_title);
			mPasswordEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			
			mFacebookCheckBox = (CheckBox)view.findViewById(R.id.facebook_checkBox);
			mFacebookCheckBox.setVisibility(View.VISIBLE);
			setFacebookStatus();
			//mFacebookCheckBox.setOnCheckedChangeListener(this);
			
			mFacebookCheckBox.setOnClickListener(this);
		}
		
		//scroll container
		mScroller = ((TrackingScrollView) view.findViewById(R.id.scroller_container));
		mScroller.setOnScrollChangedListener( new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ViewGroup source, int l, int t, int ol, int ot) {
            	handleScroll(source, t);
            }
        });
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//if new show blank fields
		if (isNewUser) loadAddress();
		else setDataInViews();
		
		super.onActivityCreated(savedInstanceState);
	}
	
	//load address by lat long
	private void loadAddress() {
		Location location = AtnLocationManager.getInstance().getCurrentLocation();
		if(location!=null) {
			mAddressDecodeTask = new GetAddressTask(getActivity().getApplicationContext());	
			mAddressDecodeTask.execute(location);
		}
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.single_item, menu);
		MenuItem item = menu.findItem(R.id.single_item);
		item.setIcon(0);
		item.setTitle(isNewUser?"+ Join":"Save");
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.single_item) {
			createOrUpdateProfile();	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//translate the user image along with scroll view to show parralax effect
	//and chnage the action bar alpha accordingly
	@SuppressLint("NewApi")
	private void handleScroll(ViewGroup source, int top) {
		final float alpha = Math.min(firstItemHeight, Math.max(0, top))/ firstItemHeight;
		mUserPicImageView.setTranslationY(-firstItemHeight / 3.0f * alpha);
		setActionBarAlpha((int) (255 * alpha));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gender_edit_text: {
			//show gender dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.gender_dialog_title)
		           .setItems(R.array.gender_array, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   mGenderEditText.setText(which==0?"Male":"Famale"); 
	            		   mSelectedGender = 1-which;
	            		  
		           }
		    }).setNegativeButton(R.string.cancel_button,null);
		    builder.create().show();
		}
			break;
		case R.id.space_view:{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.profile_photo_title)
		           .setItems(R.array.gallary_camera_remove_array, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   if(which==1) {
			            		invokeImagePicker(ImagePicker.CAMERA, ProfileFragment.this);
		            	   } else if(which==0){
		            		    invokeImagePicker(ImagePicker.GALLARY, ProfileFragment.this);
		            	   } else {
		            		   profilePicPath = null;
		            		   mUserPicImageView.setPath(BlurImageView.REMOVE_PATH);
		            		   if(userDetail!=null)
		            			   userDetail.setRemovePicture(true);
		            	   }
		           }
		    }).setNegativeButton(R.string.cancel_button, null);
		    builder.create().show();
		}
			break;
		case R.id.see_terms_button: {
			Intent termsAndConditionIntent = new Intent(getActivity(),
					TermsAndCondition.class);
			startActivity(termsAndConditionIntent);
		}
			break;
		case R.id.password_edit_text: {
			addToBackStack(AccountChangePasswordFragment.newInstance());
		}
			break;

		case R.id.facebook_checkBox: {
			if (TextUtils.isEmpty(fbSession.getAccessToken())) {
				((AtnActivity) getActivity()).postOnFacebookClick(this);
			} else {
				showFacebookLogoutDialog();
			}
			break;
		}
		
		case R.id.instagram_checkBox: {
			setInstagramStatus();
			if(TextUtils.isEmpty(InstagramSession.getToken(getActivity()))){
				InstagramSession.showInstagramDialog(getFragmentManager(),new InstagramLoginListener() {
					@Override
					public void onLoggedIn(boolean isSuccess, String message) {
						if(!isSuccess) {
							AtnUtils.showToast(message);
						}
						setInstagramStatus();
					}
				});
			} else {
				showInstagramLogoutDialog();
			}
			break;
		}
		default:
			break;
		}
	}
	

	/**
	 * Shows a dialog to login to Instagram.
	 */
	private void showInstagramLogoutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getResources().getString(R.string.logout_from_instagram))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				InstagramSession.resetAccessToken(getActivity());
				setInstagramStatus();
			}
		})
		.setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				setInstagramStatus();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Asynchronously converts the current location into the street address and
	 * sets this address in the location field of sign up form. If there are no
	 * or null location, then it will set the blank location.
	 * 
	 */
	private  class GetAddressTask extends AsyncTask<Location, String, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Location loc = params[0];
			try {
				List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (addresses != null && addresses.size() > 0) {
					Address address = addresses.get(0);
					String addressText = address.getLocality() + ", " + address.getAdminArea();
					return addressText;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				//fail geocoding use google api.
				GoogleSearchAsync searchRequest = new GoogleSearchAsync(null);
				searchRequest.setMaxResultCount(1);
				GeocodeResponse responce = searchRequest.searchByLatLng(loc.getLatitude()+","+loc.getLongitude()); 
				if(responce.isSuccess()&&responce.getResults().size()>0){
					return responce.getResults().get(0).getFormattedAddress();
				}
			} 
			 return "";
		}
		@Override
		protected void onPostExecute(String result) {
			mLoactionEditText.setText(result);
			super.onPostExecute(result);
		}
	}
	//on destroy cleanup
	@Override
	public void onDestroy() {
		if(mAddressDecodeTask!=null&&mAddressDecodeTask.getStatus()==Status.RUNNING) {
			mAddressDecodeTask.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	public void onPickedImage(boolean isSuccess, String errorMsg, String uri) {
		if(isSuccess) {
			profilePicPath = uri;
			mUserPicImageView.setPath(uri);
			if(!isNewUser){
				userDetail.setRemovePicture(false);
			}
		}
	}
	//remove error compound image after text change
	private class MyTextWather implements TextWatcher {
		private EditText mEditText;
		public MyTextWather(EditText editext){
			this.mEditText = editext;
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {	
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
	}
	
	///create profile
	private void createOrUpdateProfile() {
		
		if (validateFields()) {
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
			if (isNewUser) {
				new GcmHandler(getActivity(), new RegistrationGcm() {
					@Override
					public void gcmRegistered(boolean isSusscess,String deviceId) {
						UserManagement mUserManagement = new UserManagement();
						mUserManagement.setUserManagementListener(userManagementListener);
						if (TextUtils.isEmpty(InstagramSession.getToken(getActivity())))
							mUserManagement.doSignUp(mUserNameTextEdit.getText().toString(), 
									mEmailEditText.getText().toString(), 
									mPasswordEditText.getText().toString(), 
									mSelectedGender,
									mLoactionEditText.getText().toString(),deviceId, profilePicPath);
						else
							mUserManagement.doSignUp(mUserNameTextEdit
									.getText().toString(), mEmailEditText
									.getText().toString(), mPasswordEditText
									.getText().toString(), mSelectedGender,
									mLoactionEditText.getText().toString(),
									deviceId, InstagramSession.getToken(getActivity()),profilePicPath);
					}
				});
			} else {
				
				userDetail.setUserName(mUserNameTextEdit.getText().toString());
				userDetail.setUserEmail(mEmailEditText.getText().toString());
				userDetail.setUserLocation(mLoactionEditText.getText().toString());
				userDetail.setUserAddress(mLoactionEditText.getText().toString());
				userDetail.setUserInstagramToken(InstagramSession.getToken(getActivity()));
				userDetail.setUserFbToken(fbSession.getAccessToken());
				userDetail.setUserGender(mSelectedGender);
				userDetail.setImageUrl(profilePicPath);
				UserManagement mUserManagement = new UserManagement();
				mUserManagement.setUserManagementListener(userManagementListener);
				mUserManagement.doProfileUpdate(userDetail);
			}

		}
	}
	
	//validate all user fields
	private boolean validateFields() {
		
		if (UiUtil.showToastIfTrue(getActivity(), R.string.empty_user_name,
				TextUtils.isEmpty(mUserNameTextEdit.getText().toString()))){
			mUserNameTextEdit.setFocusable(true);
			mUserNameTextEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
			return false;
		}
		
		if (UiUtil.showToastIfTrue(getActivity(), R.string.blank_space_in_user_name,
				mUserNameTextEdit.getText().toString().contains(" "))){
			mUserNameTextEdit.setFocusable(true);
			mUserNameTextEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
			return false;
		}
			
		if (UiUtil.showToastIfTrue(getActivity(), R.string.email_format_invalid,
				!Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches())) {
			mEmailEditText.setFocusable(true);
			mEmailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
			return false;
		}
		
		if(isNewUser) {
			if (UiUtil.showToastIfTrue(getActivity(), R.string.password_low_length,
					mPasswordEditText.getText().toString().length()<6)) {
				mPasswordEditText.setFocusable(true);
				mPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
				return false;
			}
				
			if (UiUtil.showToastIfTrue(getActivity(), R.string.confirm_password_mismatch,
					!mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString()))) {
				mConfirmPasswordEditText.setFocusable(true);
				mConfirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
				return false;
			}
		}
		
		if(UiUtil.showToastIfTrue(getActivity(), R.string.choose_gender,
				mSelectedGender==2)) {
			mGenderEditText.setFocusable(true);
			mGenderEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_error, 0);
			return false;
		}
		
		return true;
	}
	
	
	private UserManagementListener userManagementListener = new UserManagementListener() {
		@Override
		public void onSuccess(ServiceType webserviceType, String response) {
			AnchorProgressDialog.conceal();
			AtnUtils.log(response);
			if(WebserviceType.ServiceType.REGISTER==webserviceType) {
				//CategoriesFetchRequest.loadUserCategory(getActivity());
				parseUserDetail(response);
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}else if(WebserviceType.ServiceType.UPDATE_USER_PROFILE==webserviceType) {
				parseUserDetail(response);
				AtnApp.showMessageDialog(getActivity(), getResources().getString(R.string.profile_updated_message), false);
				if(getActivity() instanceof MainMenuActivity) {
					((MainMenuActivity)getActivity()).profileUpdated(profilePicPath);	
				}
			}
		}

		@Override
		public void onError(ServiceType webserviceType, int errorCode,
				String errorMessage) {
			AnchorProgressDialog.conceal();
			if (errorCode == WebserviceError.INTERNET_ERROR) {
				AtnApp.showMessageDialog(getActivity(), errorMessage, false);
				return;
			}
			if(webserviceType==WebserviceType.ServiceType.REGISTER){
				AtnApp.showMessageDialog(getActivity(), errorMessage, false);
			}
		}
	};
	
	/**
	 * Parses the user login response from the server and stores the logged user
	 * details in user data pool.
	 * 
	 * @param response
	 *            login response from server.
	 */
	private void parseUserDetail(String response) {
		try {
			JSONObject dataObject = new JSONObject(response);

			UserDetail loggedUser = new UserDetail();

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.ID))
				loggedUser.setUserId(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.ID));

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.USER_NAME))
				loggedUser.setUserName(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.USER_NAME));

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.EMAIL))
				loggedUser.setUserEmail(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.EMAIL));

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.ADDRESS))
				loggedUser.setUserLocation(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.ADDRESS));
			
			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.PICTURE_JSON_KEY))
				loggedUser.setImageUrl(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.PICTURE_JSON_KEY));

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.FB_ACCESS_TOKEN)) {
				//loggedUser.setUserFbToken(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.FB_ACCESS_TOKEN));
				//FacebookSession fbSession = new FacebookSession(SignUp.this);
				//fbSession.storeAccessToken(loggedUser.getUserFbToken());
			}

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.INSTAGRAM_ACCESS_TOKEN)) {
				loggedUser.setUserInstagramToken(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.INSTAGRAM_ACCESS_TOKEN));
				InstagramSession.storeAccessToken(getActivity(),loggedUser.getUserInstagramToken());
			}

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.GENDER)) {
				loggedUser.setUserGender(dataObject.getJSONObject(UserDetail.USER).getInt(UserDetail.GENDER));
			}

			loggedUser.setUserPassword(mPasswordEditText.getText().toString());

			/**
			 * Add logged in user details in data pool.
			 */
			UserDataPool.getInstance().setUserDetail(loggedUser);

			/**
			 * Add logged in user details in database.
			 */
			DbHandler.getInstance().loginUser(loggedUser);

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}

	
	
	
	/***
	 * Set data in views
	 * ***/
	private void setDataInViews() {
		
		mUserNameTextEdit.setText(userDetail.getUserName());
		mEmailEditText.setText(userDetail.getUserEmail());
		mLoactionEditText.setText(userDetail.getUserLocation());
		if (userDetail.getUserGender() == GenderType.MALE) {
			mGenderEditText.setText(getResources().getString(R.string.male_title));
			mSelectedGender = 0;
		} else {
			mGenderEditText.setText(getResources().getString(R.string.female_title));
			mSelectedGender = 1;
		}
	}

	@Override
	public void onFacebookLogin(boolean isSuccess, String message) {
		setFacebookStatus();
	}

	@Override
	public void onUserInfoFetch(GraphUser user) {
		mFacebookCheckBox.setChecked(!TextUtils.isEmpty(fbSession.getAccessToken()));
		mFacebookCheckBox.setText(R.string.facebook_account_title);
		
		String test=fbSession.getName();
		
		if(mFacebookCheckBox.isChecked()&&!TextUtils.isEmpty(user.getName())) {
			mFacebookCheckBox.setText(UiUtil.getSocialTitle(getActivity(), R.string.facebook_account_title, user.getName()));
		}
	}
	
	/**
	 * Shows a dialog to login to Facebook.
	 */
	private void showFacebookLogoutDialog() {
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(getResources().getString(R.string.logout_from_facebook))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.dialog_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								fbSession.resetAccessToken();
								AtnUtils.logoutFacebook(fbSession);
								setFacebookStatus();
							}
						})
				.setNegativeButton(getResources().getString(R.string.dialog_no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								setFacebookStatus();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void setFacebookStatus() {
		
		mFacebookCheckBox.setChecked(!TextUtils.isEmpty(fbSession.getAccessToken()));
		mFacebookCheckBox.setText(R.string.facebook_account_title);
		
		String test=fbSession.getName();
		
		if(mFacebookCheckBox.isChecked()&&!TextUtils.isEmpty(fbSession.getAccessToken())) {
			mFacebookCheckBox.setText(UiUtil.getSocialTitle(getActivity(), R.string.facebook_account_title, fbSession.getName()));
		}
	}
	
	/*
	 * Set Check state of instagram checkbox
	 * **/
	private void setInstagramStatus() {

		mInstgramCheckBox.setChecked(!TextUtils.isEmpty(InstagramSession.getToken(getActivity())));
		if(mInstgramCheckBox.isChecked()){
			if(!TextUtils.isEmpty(InstagramSession.getName(getActivity()))){
				mInstgramCheckBox.setText(UiUtil.getSocialTitle(getActivity(), R.string.instagram_account_title, InstagramSession.getName(getActivity())));
			} else {
				mInstgramCheckBox.setText("Fetching...");
				InstagramSession.loadUserInfo(new InstagramUserInfoListner() {
					@Override
					public void onLoaduserInfo(boolean isSuccess, String message) {
						if(!isSuccess){
							AtnUtils.showToast(message);
						}else{
							mInstgramCheckBox.setText(UiUtil.getSocialTitle(getActivity(), R.string.instagram_account_title, message));	
						}
					}
				});
			}
		}
	}
}
