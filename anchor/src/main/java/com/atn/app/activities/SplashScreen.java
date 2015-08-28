package com.atn.app.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.animation.AnimationDelete;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.httprequester.InstagramImageLoader;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.pool.UserDataPool;
import com.atn.app.process.usermanagement.UserManagement;
import com.atn.app.process.usermanagement.UserManagementListener;
import com.atn.app.service.SynchService;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.StoreGcmIdOnServer;
import com.atn.app.utils.UiUtil;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceType.ServiceType;
import com.coppermobile.gcm.GcmHandler;
import com.coppermobile.gcm.GcmHandler.RegistrationGcm;

/**
 * Creates a splash screen this is shown at application launch. Also it gets the
 * current location of device.
 */
public class SplashScreen extends FragmentActivity implements OnClickListener,OnEditorActionListener {

	//start activity for result flags
	private static final int PROFILE_CREATE = 11;
	private static final int CHOOSE_CATEGORIES = 12;
	
	//bundle contain this flag if login screen is launched from main screen
	public static final String IS_FROM_MAIN_SCREEN = "IS_FROM_MAIN_SCREEN";
	
	private Handler mHandler;// for splash screen delay.
	
	// //ui component.
	private RelativeLayout mContainer = null;
	private FrameLayout mBottomContainer = null;
	private ImageView mLogoImageView = null;
	private EditText mUserNameEditText = null;
	private EditText mPasswordNameEditText = null;
	private Button mLoginButton = null;
	private TextView mFogotPasswordTextView = null;
	private TextView mNewUserTextView = null;
	private Button mCreateAccountButton = null;
	private Button mStartBrowsingButton = null;
	
	//forgot password conatiner ui element
	private TextView mFogotPasswordTitleTextView = null;
	private EditText mEnterEmailEditText = null;
	private Button mDoneButton = null;
	private ImageButton mBackButton = null;
	private LinearLayout mForgotButtonContainer = null;
	

	// we start service as splash screen starts. For downloading foursquare and
	// instagram data. If user press back button from this screen the we have stop service
	private boolean shouldStopService = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Crashlytics.start(this);
		setContentView(R.layout.splash_screen);	
		//check for gcm msg reg id.
		new GcmHandler(this);
		///save screen width.
		SharedPrefUtils.saveScreenWidth(this, UiUtil.getScreenWidth(this));
		
		// if activity is launch first time then we need start service to
		// download data.
		if(!getIntent().getBooleanExtra(IS_FROM_MAIN_SCREEN, false)) {
			
			StoreGcmIdOnServer.setGcmIdStored(GcmHandler.isStoredOnServer(getApplicationContext()));
			SharedPrefUtils.setFoursquareVenueStatus(getApplicationContext(), false);
			AtnLocationManager.getInstance().shouldStartService = true;
			AtnUtils.runSynchService(getApplicationContext(), SynchService.Command.RELOAD_VENUE);
		}
		
		mHandler = new Handler();
		//ga session started
		AtnUtils.gASendEvent(this, "Session", "Session Started","ATN Session Started");

		mContainer = (RelativeLayout) findViewById(R.id.container);

		mLogoImageView = (ImageView) findViewById(R.id.logo_image);
		mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
		mUserNameEditText.setOnEditorActionListener(this);
		mPasswordNameEditText = (EditText) findViewById(R.id.password_edit_text);
		mPasswordNameEditText.setOnEditorActionListener(this);
		mLoginButton = (Button) findViewById(R.id.login_button);
		mFogotPasswordTextView = (TextView) findViewById(R.id.forgot_password_text_view);
		mNewUserTextView = (TextView) findViewById(R.id.new_user_text_view);
		mCreateAccountButton = (Button) findViewById(R.id.create_account_button);
		mStartBrowsingButton = (Button) findViewById(R.id.start_browsing_button);
		mBottomContainer = (FrameLayout) findViewById(R.id.bottom_container);
		
		//forgot password
		mFogotPasswordTitleTextView = (TextView) findViewById(R.id.forgot_password_title);
		mEnterEmailEditText = (EditText) findViewById(R.id.forgot_password_edit_text);
		mEnterEmailEditText.setOnEditorActionListener(this);
		
		mDoneButton = (Button) findViewById(R.id.done_button);;
		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mForgotButtonContainer = (LinearLayout)findViewById(R.id.forgot_password_container);
		//set listeners
		mLoginButton.setOnClickListener(this);
		mFogotPasswordTextView.setOnClickListener(this);
		mNewUserTextView.setOnClickListener(this);
		mCreateAccountButton.setOnClickListener(this);
		mStartBrowsingButton.setOnClickListener(this);
		mDoneButton.setOnClickListener(this);
		mBackButton.setOnClickListener(this);
		mFogotPasswordTextView.setOnClickListener(this);
		
		mLogoImageView.setEnabled(false);
		
	}

	// run after splash screen times up
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			launchHomeOrLoginScreen();
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		//animate once
		if(!mLogoImageView.isEnabled()){
			mHandler.removeCallbacks(mRunnable);
			mHandler.postDelayed(mRunnable, 1000);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mRunnable);
	}

	@Override
	protected void onDestroy() {

		if(shouldStopService&&!getIntent().getBooleanExtra(IS_FROM_MAIN_SCREEN, false)) {
			AtnLocationManager.getInstance().disconnect();
			InstagramImageLoader.loader.cancel();
			AtnUtils.stopSynchService(this);
		}

		super.onDestroy();
	}
	
	@SuppressLint("NewApi")
	private void launchHomeOrLoginScreen() {

		mLogoImageView.setEnabled(true);
		UserDetail loggedUser = DbHandler.getInstance().getLoggedInUser();
		if (loggedUser != null) {
			startActivityForResult(new Intent(this,ChooseInterest.class),CHOOSE_CATEGORIES);
		} else {

			// Get the background, which has been compiled to an
			// TransitionDrawable object.
			// animate background drawable
			TransitionDrawable transition = (TransitionDrawable) mContainer
					.getBackground();
			transition.startTransition(1500);
			
			int yDelta = (mUserNameEditText.getTop() - mLogoImageView.getHeight()) / 2 - mLogoImageView.getTop();
			TranslateAnimation tranlateAnimetion = new TranslateAnimation(0, 0,0, yDelta);

			tranlateAnimetion.setDuration(1500);
			tranlateAnimetion.setFillAfter(true);
			tranlateAnimetion.setFillEnabled(true);
			mLogoImageView.startAnimation(tranlateAnimetion);
			
			startAnimtionWithDelay(mUserNameEditText, 0);
			startAnimtionWithDelay(mPasswordNameEditText, 100);
			startAnimtionWithDelay(mLoginButton, 150);
			startAnimtionWithDelay(mFogotPasswordTextView, 200);
			startAnimtionWithDelay(mNewUserTextView, 250);
			startAnimtionWithDelay(mBottomContainer, 300);
		}
	}

	//start animating views
	private void startAnimtionWithDelay(View view, int delay) {
		view.setVisibility(View.VISIBLE);
		TranslateAnimation tranlateAnimetion = new TranslateAnimation(0, 0,
				mContainer.getHeight(), 0);
		
		tranlateAnimetion.setDuration(1500);
		tranlateAnimetion.setFillAfter(true);
		tranlateAnimetion.setFillEnabled(true);
		tranlateAnimetion.setStartOffset(delay);
		view.startAnimation(tranlateAnimetion);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.login_button:
			loginButtonClick();
			break;
		case R.id.forgot_password_text_view:
			mFogotPasswordTitleTextView.setClickable(false);
			forgotPasswordButtonClick();
			break;
		case R.id.create_account_button:
			createAccountButtonClick();
			break;
		case R.id.start_browsing_button: {
			if(SharedPrefUtils.isUserFirstTimeBrowsing(getApplicationContext())) {
				SharedPrefUtils.setUserFirstTimeBrowsing(getApplicationContext(), false);
				startActivityForResult(new Intent(this,ChooseInterest.class),CHOOSE_CATEGORIES);
			} else {
				startBrowsingButtonClick();	
			}
		}
			break;
		case R.id.done_button:
			doneButtonButtonClick();
			break;
		case R.id.back_button:
			backButtonClick();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		//show login screen on back press if user is on forget password screen
		if(mFogotPasswordTitleTextView.getVisibility()==View.VISIBLE){
			showFogotPasswordView(false);
		} else {
			super.onBackPressed();
		}
	}
	
	
	/**
	 * validate all fields and hit login api.
	 * */
	private void loginButtonClick() {

		if (UiUtil.showToastIfTrue(getApplicationContext(),
				R.string.empty_user_name,
				TextUtils.isEmpty(mUserNameEditText.getText()))) return;
		
		
		if (UiUtil.showToastIfTrue(getApplicationContext(),
				R.string.blank_space_in_user_name,
				mUserNameEditText.getText().toString().contains(" "))) return;
		
		
		if (UiUtil.showToastIfTrue(getApplicationContext(),
				R.string.empty_password,
				TextUtils.isEmpty(mPasswordNameEditText.getText()))) return;
		
		
		if (UiUtil.showToastIfTrue(getApplicationContext(),
				R.string.password_low_length,
				mPasswordNameEditText.getText().length()<6)) return;
		
		//make call for login.....
		doSignIn(mUserNameEditText.getText().toString(),mPasswordNameEditText.getText().toString());
	}

	/**
	 * Call login api.
	 */
	private void doSignIn(final String userName, final String password) {
		AnchorProgressDialog.show(this, R.string.loggin_title);
		new GcmHandler(this, new RegistrationGcm() {
			@Override
			public void gcmRegistered(boolean isSusscess, String deviceId) {
				UserManagement userManagement = new UserManagement();
				userManagement.setUserManagementListener(managementListener);
				if (isSusscess)
					userManagement.doSignIn(userName, password, deviceId);
				else
					userManagement.doSignIn(userName, password);
			}
		});
	}
	///login listener
	private UserManagementListener managementListener = new UserManagementListener() {
		@Override
		public void onSuccess(ServiceType serviceType, String response) {
			parseUserDetail(response);
			AnchorProgressDialog.conceal();
			//CategoriesFetchRequest.loadUserCategory(getApplicationContext());
			if(UserDataPool.getInstance().isUserLoggedIn()) {
				
			}
			startActivityForResult(new Intent(SplashScreen.this,ChooseInterest.class),CHOOSE_CATEGORIES);
		}
		@Override
		public void onError(ServiceType serviceType, int errorCode, String errorMessage) {
			switch (errorCode) {
			case WebserviceError.INTERNET_ERROR:
				AtnApp.showMessageDialog(SplashScreen.this, getResources().getString(R.string.no_internet_available), false);
				break;

			case WebserviceError.UNKNOWN_ERROR:
				AtnApp.showMessageDialog(SplashScreen.this, getResources().getString(R.string.unknown_webservice_error), false);
				break;

			// Show error message for invalid credentials.
			case 4:
				AtnApp.showMessageDialog(SplashScreen.this, errorMessage, false);
				break;
			}
			AnchorProgressDialog.conceal();
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

//			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.FB_ACCESS_TOKEN)) {
//				//loggedUser.setUserFbToken(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.FB_ACCESS_TOKEN));
//				//FacebookSession fbSession = new FacebookSession(SignIn.this);
//				//fbSession.storeAccessToken(loggedUser.getUserFbToken());
//			}

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.PICTURE_JSON_KEY))
				loggedUser.setImageUrl(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.PICTURE_JSON_KEY));
			
			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.INSTAGRAM_ACCESS_TOKEN)) {
				loggedUser.setUserInstagramToken(dataObject.getJSONObject(UserDetail.USER).getString(UserDetail.INSTAGRAM_ACCESS_TOKEN));
				InstagramSession.storeAccessToken(SplashScreen.this,loggedUser.getUserInstagramToken());
			}

			if (!dataObject.getJSONObject(UserDetail.USER).isNull(UserDetail.GENDER)) {
				loggedUser.setUserGender(dataObject.getJSONObject(UserDetail.USER).getInt(UserDetail.GENDER));
			}
			loggedUser.setUserPassword(mPasswordNameEditText.getText().toString());
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

	private void forgotPasswordButtonClick() {	
		showFogotPasswordView(true);
	}

	//show or hide forgot password views animation
	//isShow true if animation for showing forget password screen
	private void showFogotPasswordView(boolean isShow){
		
		moveView(mUserNameEditText,isShow);
		moveView(mPasswordNameEditText,isShow);
		moveView(mLoginButton,isShow);
		moveView(mFogotPasswordTextView,isShow);
		moveView(mFogotPasswordTitleTextView,!isShow);
		moveView(mEnterEmailEditText,!isShow);
		moveView(mForgotButtonContainer,!isShow);
	}
	
	
	/**
	 * Move view from right to left, 
	 * @param view to be animate
	 * @param isVisible true if view is currently visible. 
	 * */
	private void moveView(final View view,final boolean isVisible) {
	
		TranslateAnimation tranlateAnimetion = new TranslateAnimation(isVisible?0:mContainer.getWidth(),isVisible?-mContainer.getWidth():0, 0, 0);
		tranlateAnimetion.setDuration(600);
		if(!isVisible) tranlateAnimetion.setStartOffset(100);
		tranlateAnimetion.setAnimationListener(new AnimationDelete(){
			@Override
			public void onAnimationEnd(Animation animation) {
				if(isVisible){
					view.setVisibility(View.INVISIBLE);
				}
				view.setClickable(true);
			}
			@Override
			public void onAnimationStart(Animation animation) {
				if(!isVisible){
					view.setVisibility(View.VISIBLE);
				}
				view.setClickable(false);
			}
		});		
		view.startAnimation(tranlateAnimetion);
	}
	
	//goto create account screen
	private void createAccountButtonClick() {
		Intent browsingIntent = new Intent(SplashScreen.this,CreateProfileActivity.class);		
		startActivityForResult(browsingIntent, PROFILE_CREATE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK) {
			if(PROFILE_CREATE==requestCode){
				startActivityForResult(new Intent(this,ChooseInterest.class),CHOOSE_CATEGORIES);
			}else if(CHOOSE_CATEGORIES==requestCode) {
				startBrowsingButtonClick();
			}
		}
	}

	//open home screen
	private void startBrowsingButtonClick() {
		shouldStopService  = false;
		Intent browsingIntent = new Intent(SplashScreen.this, MainMenuActivity.class);
		startActivity(browsingIntent);
		finish();
	}
	
	//hit forgot password mFsRequest
	private void doneButtonButtonClick() {
		
		if (UiUtil.showToastIfTrue(getApplicationContext(),
				R.string.empty_user_name,
				TextUtils.isEmpty(mEnterEmailEditText.getText()))) return;
		
		AnchorProgressDialog.show(this, R.string.please_wait);
	
		UserManagement userManagement = new UserManagement();
		userManagement.setUserManagementListener(mUserManagementListener);
		userManagement.doPasswordRecovery(mEnterEmailEditText.getText().toString());
	}
	
	/**
	 * Listens for the user management web service notifications. The service
	 * will return a response message in onSuccess if it is executed
	 * successfully, otherwise returns an error message in onError.
	 */
	UserManagementListener mUserManagementListener = new UserManagementListener() {

		@Override
		public void onSuccess(ServiceType webserviceType, String response) {
			AnchorProgressDialog.conceal();
			mEnterEmailEditText.setText("");
			AtnApp.showMessage(SplashScreen.this,
					getResources().getString(R.string.recover_password_response));
			showFogotPasswordView(false);
		}

		@Override
		public void onError(ServiceType webserviceType, int errorCode,
				String errorMessage) {
			AnchorProgressDialog.conceal();
			switch (errorCode) {
			case WebserviceError.INTERNET_ERROR:
				AtnApp.showMessageDialog(SplashScreen.this, getResources()
						.getString(R.string.no_internet_available), false);
				break;

			case WebserviceError.UNKNOWN_ERROR:
				AtnApp.showMessageDialog(SplashScreen.this, getResources()
						.getString(R.string.unknown_webservice_error), false);
				break;
			// Show error message for invalid credentials.
			case 4:
				AtnApp.showMessageDialog(SplashScreen.this, errorMessage,
						false);
				break;
			}
			
		}
	};
	
	//show login screen again
	private void backButtonClick() {
		showFogotPasswordView(false);
	}

	//call appropriate function on keyboard done button press
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			AtnUtils.hideKeyboard(this);
			if(mFogotPasswordTitleTextView.getVisibility()==View.VISIBLE){
				doneButtonButtonClick();
			} else {
				loginButtonClick();
			}
			return true;
		}
		return false;
	}
}
