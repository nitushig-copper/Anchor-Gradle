/**
 * @Copyright coppermobile 2014.
 * */
package com.atn.app.activities;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarActivity;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.crop.Crop;
import com.atn.app.facebook.FacebookHandler;
import com.atn.app.facebook.FacebookHandler.FacebookSessionListener;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.UiUtil;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * Base activity class for application. Here we define common functionality. 
 */
public class AtnActivity extends  ActionBarActivity implements
		FacebookSessionListener {
	//action background drawable
	Drawable mActionBarDrawable = null;
	//alpha of action bar bg
	protected int alpha = 0;

	//image picker interface
	public interface ImagePicker {
		public int GALLARY = 1;
		public int CAMERA = 2;

		public void onPickedImage(boolean isSuccess, String errorMsg,
				String path);
	}

	//faceboook login interface
	public interface FacebookLoginListener {
		public void onFacebookLogin(boolean isSuccess, String message);

		public void onUserInfoFetch(GraphUser user);
	}

	//facebook login listener.
	private FacebookLoginListener fbLoginListener;
	private FacebookHandler fbHandler;
	private LoginButton postOnFBBtn;
	//set listener for image capture
	private ImagePicker mImagePickerListener;
	//picked image url;
	private String pickedImageUri;

	/**
	 * Initialize location services.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (UiUtil.hasActionBar(this)) {
			final ActionBar bar = getSupportActionBar();
			mActionBarDrawable = getResources().getDrawable(R.drawable.ab_solid_anchor);
			bar.setBackgroundDrawable(mActionBarDrawable);
		}
	}

	//set alpha of action bar background drawable .
	public void setActionBarAlpha(int alpha) {
		if (mActionBarDrawable != null) {
			this.alpha = alpha;
			mActionBarDrawable.setAlpha(alpha);
		}
	}

	public Drawable getActionBarDrawable() {
		return mActionBarDrawable;
	}

	//set activity title basically this function is called from fragments.
	public void setTitle(String title) {
		if(UiUtil.hasActionBar(this)){
			final ActionBar bar = getSupportActionBar();
			bar.setTitle(title);
		}
	}

	/**
	 * set activity title basically this function is called from fragments.
	 * @param resId string resource id
	 * */
	public void setTitle(int resId) {
		setTitle(getResources().getString(resId));
	}
	/**
	 * Set up facebook login button and permissions
	 * */
	protected void setUpFacebookLogin(Bundle savedInstanceState) {
		prepareFacebookHandler(savedInstanceState);
		postOnFBBtn = new LoginButton(this);
		postOnFBBtn.setReadPermissions(Arrays.asList("email", "user_hometown",
				"user_about_me", "user_birthday", "user_location",
				"friends_location", "user_photos", "friends_photos",
				"friends_about_me"));
		postOnFBBtn.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
	}

	//create facebook Handler object if not created yet
	private void prepareFacebookHandler(Bundle savedInstanceState) {
		if (fbHandler == null) {
			fbHandler = new FacebookHandler(this);
			fbHandler.setFacebookSessionListener(this);
			fbHandler.onCreate(savedInstanceState);
		}
	}
	
	//set facebook login handler. called from Fragment.
	public void postOnFacebookClick(FacebookLoginListener facebookClickListner) {
		this.fbLoginListener = facebookClickListner;
		if ((Session.getActiveSession() == null || Session.getActiveSession()
				.getState() != SessionState.OPENED)) {
			dofacebookLogin();
		} else {
			if (this.fbLoginListener != null) {
				this.fbLoginListener.onFacebookLogin(true, Session
						.getActiveSession().getAccessToken());
			}
		}
	}

	//called for facebook login
	public void dofacebookLogin() {
		fbHandler.onResume();
		postOnFBBtn.performClick();
	}

	@Override
	protected void onResume() {
		if (fbHandler != null)
			fbHandler.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (fbHandler != null)
			fbHandler.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (fbHandler != null)
			fbHandler.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fbHandler != null)
			fbHandler.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		/**
		 * If location services are disabled, shows an alert dialog to enable
		 * location services.
		 */
		if (!AtnLocationManager.getInstance().isLocationServicesEnabled()) {
			AtnApp.showTurnOnLocationServicesDialog(this);
		}
		AtnLocationManager.getInstance().startUpdates(this);
	}

	@Override
	protected void onStop() {
		AtnLocationManager.getInstance().stopUpdates();
		super.onStop();
	}


	/**
	 * Starts activity for getting image from camera/gallery
	 * 
	 * @param intent
	 * @param requestCode
	 */
	public void getImage(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	/**
	 * Invoke image picker either form camera or galle ry and call listener
	 * 
	 * @param sourceType
	 *            Camera or gallery
	 * @param pickerListener
	 *            listener.
	 * */

	public void invokeImagePicker(int sourceType, ImagePicker pickerListener) {
		mImagePickerListener = pickerListener;
		pickedImageUri = null;
		switch (sourceType) {
		case ImagePicker.CAMERA:
			File file = AtnUtils.getTempImageCaptureUri();
			pickedImageUri = file.getAbsolutePath();
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(cameraIntent, ImagePicker.CAMERA);

			break;
		case ImagePicker.GALLARY:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			startActivityForResult(
					Intent.createChooser(intent,getResources().getString(R.string.choose_image_using)),
					ImagePicker.GALLARY);
			break;
		default:
			AtnUtils.showToast("Invalid Source");
			break;
		}
	}

	/**
	 * Starts activity to login to Facebook.
	 * 
	 * @param intent
	 * @param requestCode
	 */
	public void doFacebookLogin(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// call fb handler
		if (fbHandler != null)
			fbHandler.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ImagePicker.GALLARY:
				Uri uri = data.getData();
				File file = AtnUtils.getTempImageCaptureUri();
				pickedImageUri = file.getAbsolutePath();
				startActivityForResult(new Crop(uri).output(Uri.fromFile(file))
						.asSquare().setSourceType(ImagePicker.GALLARY)
						.getIntent(this), Crop.REQUEST_CROP);
				break;
			case ImagePicker.CAMERA:
				if (pickedImageUri != null) {
					startActivityForResult(new Crop(Uri.fromFile(new File(pickedImageUri)))
									.output(Uri.fromFile(new File(pickedImageUri))).asSquare()
									.setSourceType(ImagePicker.CAMERA)
									.getIntent(this), Crop.REQUEST_CROP);
				}
				break;
			case Crop.REQUEST_CROP:
				if (mImagePickerListener != null)
					mImagePickerListener.onPickedImage(true, "success",
							pickedImageUri);
				break;
			default:
//				AtnUtils.showToast("UnKnow Request Code");
				break;
			}
		} else if (resultCode == Crop.RESULT_ERROR) {
			Throwable throwable = (Throwable) data.getSerializableExtra(Crop.Extra.ERROR);
			AtnUtils.logE(throwable);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//set listener for image picker
	public void registerImageSharingListener(ImagePicker listener) {
		mImagePickerListener = listener;
	}
	//remove  image picker listener
	public void unRegisterImageSharingListener() {
		mImagePickerListener = null;
	}

	//set FacebookLoginListener for facebook login
	public void registerFacebookLoginListener(FacebookLoginListener listener) {
		fbLoginListener = listener;
	}
	
	//remove  FacebookLoginListener 
	public void unRegisterFacebookLoginListener() {
		fbLoginListener = null;
	}

	//maintain facebook session. called when session changed or user login to facebook or post on facebook
	@Override
	public void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			final FacebookSession fbSession = new FacebookSession(AtnActivity.this);
			fbSession.storeAccessToken(session.getAccessToken());
			if (fbLoginListener != null) {
				fbLoginListener.onFacebookLogin(true, Session
						.getActiveSession().getAccessToken());
			}
			Request request = Request.newMeRequest(session,
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(final GraphUser user,
								Response response) {
							if (user != null) {
								fbSession.storeAccessToken(fbSession.getAccessToken(),
										user.getId(), user.getName(),
										user.getUsername());
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (fbLoginListener != null) {
											fbLoginListener.onUserInfoFetch(user);
										}
									}
								});
							}
						}
					});
			request.executeAsync();
		} else {
			if (fbLoginListener != null) {
				fbLoginListener.onFacebookLogin(false, "Logging fail");
			}
		}
	}
	

}
