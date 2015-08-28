/**
 * @Copyright CopperMobile india Pvt Ltd 2014
 * 
 * */
package com.atn.app.activities;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity.ImagePicker;
import com.atn.app.component.BlurImageView;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.PushNotificationData;
import com.atn.app.fragments.AccountMainFragment;
import com.atn.app.fragments.AtnBaseFragment;
import com.atn.app.fragments.AtnMapFragment;
import com.atn.app.fragments.ChooseInterestFragment;
import com.atn.app.fragments.FollowingFragment;
import com.atn.app.fragments.LoopAtnOfferFragment;
import com.atn.app.fragments.LoopPhotosNewFragment;
import com.atn.app.fragments.OfferDetailFragment;
import com.atn.app.fragments.TipsDialog;
import com.atn.app.fragments.VenueDetailFragment;
import com.atn.app.fragments.VenuesListFragment;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.httprequester.InstagramImageLoader;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.TypefaceUtils;

/*
 * Main Menu screen. This is the home screen of the app with one left side menu. menu contain all atn main screen access.
 * **/
public class MainMenuActivity extends AtnActivity implements
		 AddFragmentListener,OnBackStackChangedListener,ImagePicker {
	
			//delay in fragment replace for smooth menu slide. 
			public static final int DELAY_REPLACE_FRAGMENT = 300;
			//show exit toast once before Exiting screen
			public static final int EXIT_TOAST_DISPLAY_TIME = 1000;
	
			//
	        private DrawerLayout mDrawerLayout;
			private  ActionBar mActionBar;
			private ActionBarDrawerToggle mDrawerToggle;
			
			//menu items
			private BlurImageView mUserImageView;
			private Button mSearchButton;
			private Button mExproleButton;
			private Button mHappeningNowButton;
			private Button mFollowingButton;
			private Button mTipsButton;
			private Button mAccountButton;
			private Button mMyInterestButton;
			private Button mAddPhotoButton;
			private Button mAddReviewButton;
			
			//currently selected menu button
			private Button mSelectedButton;
			//used for show toast once on first time back press.
			private boolean mShouldExitApp = false;
			
			///helper hanlder for sending message with some delay
			private Handler mHandler = null;
			
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.menu_layout_activity);
				setUpFacebookLogin(savedInstanceState);
				getSupportFragmentManager().addOnBackStackChangedListener(this);

				mActionBar = getSupportActionBar();
				mActionBar.setDisplayHomeAsUpEnabled(true);
				
				mHandler = new Handler(getMainLooper());
				
				mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
				mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			    mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.the_loop_title));
				mDrawerLayout.setDrawerListener(new AnchorDrawerListener());
			    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
			    		R.string.drawer_open, R.string.drawer_close);
			    
			    mDrawerToggle.setDrawerIndicatorEnabled(true);
			     
			    mUserImageView  = (BlurImageView)findViewById(R.id.user_pic_image_view);
				mSearchButton = (Button)findViewById(R.id.search_button);
				mExproleButton = (Button)findViewById(R.id.expore_button);
				mHappeningNowButton= (Button)findViewById(R.id.happening_now_button);
				mFollowingButton= (Button)findViewById(R.id.following_button);
				mTipsButton= (Button)findViewById(R.id.tips_button);
				mAccountButton= (Button)findViewById(R.id.account_button);
				
				mMyInterestButton = (Button)findViewById(R.id.my_inetrest_button);
				mAddPhotoButton= (Button)findViewById(R.id.add_photos_button);
				mAddReviewButton= (Button)findViewById(R.id.add_reviews_button);
				
			    //set click listener
				
				mUserImageView.setOnClickListener(onOtherViewClickListener);
				mSearchButton.setOnClickListener(onOtherViewClickListener);
				mAddPhotoButton.setOnClickListener(onOtherViewClickListener);
				mAddReviewButton.setOnClickListener(onOtherViewClickListener);
				mExproleButton.setOnClickListener(onMenuItemClickListener);
				mHappeningNowButton.setOnClickListener(onMenuItemClickListener);
				mFollowingButton.setOnClickListener(onMenuItemClickListener);
				mTipsButton.setOnClickListener(onMenuItemClickListener);
				mAccountButton.setOnClickListener(onMenuItemClickListener);
				mMyInterestButton.setOnClickListener(onMenuItemClickListener);
				
				//show the loop first time.
				mDrawerLayout.post(new Runnable() {
					@Override
					public void run() {
						mDrawerToggle.syncState();
						//if user login first time then show help screen
						if(UserDataPool.getInstance().isUserLoggedIn() && 
								SharedPrefUtils.isUserLoginFirstTime(getApplicationContext())) {
							mDrawerLayout.openDrawer(GravityCompat.START);
							SharedPrefUtils.setUserFirstTimeLoginStatus(getApplicationContext(), false);
//							LinearLayout linearLayout = (LinearLayout) findViewById(R.id.add_review_button_container);
//							if(linearLayout!=null) {
//								HelpDialog mDialog = HelpDialog.newInstance(UiUtil
//										.getPixelScreenWidth(MainMenuActivity.this)
//										- linearLayout.getWidth(),helpDialogListener);
//								
//							    // mDialog.show(getSupportFragmentManager(), HelpDialog.HELP_DIALOG);
//								///Use for commitAllowingStateLoss.
//								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//								ft.add(mDialog,  HelpDialog.HELP_DIALOG);
//								ft.commitAllowingStateLoss();
//								mSelectedButton = mHappeningNowButton;
//							     //unselected first time.
//							     mHappeningNowButton.setSelected(false);
//									TypefaceUtils.applyTypeface(getApplicationContext(),
//											mHappeningNowButton, TypefaceUtils.ROBOTO_CONDENCED);
//							}
				        }
					}
				});
				
				//check if activity started by GCm notification or not.
				if(getIntent().hasExtra(PushNotificationData.PUSH_DATA)) {
					handleGcmNotification();
				} else {
					mHappeningNowButton.performClick();	
				}
			}
			
			//menu item click listener
			OnClickListener onMenuItemClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDrawerLayout.closeDrawers();
					//if same menu item click then pop to root fragment
					if(mSelectedButton==v) {
						//pop to root fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						 if (fragmentManager.getBackStackEntryCount() >= 1) {
							 fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(),
				                     FragmentManager.POP_BACK_STACK_INCLUSIVE);
				         }
						 return;
					}  
					
					AtnBaseFragment fragment = null;
					switch (v.getId()) {
						case R.id.expore_button: {
							fragment = AtnMapFragment.newInstance();
						}
							break;
						case R.id.happening_now_button: {
							fragment =  LoopPhotosNewFragment.newInstance();
						}
							break;
						case R.id.following_button: {
							if (!UserDataPool.getInstance().isUserLoggedIn()) {
								AtnApp.showLoginScreen(MainMenuActivity.this);
								return;
							}
							fragment = FollowingFragment.newInstance();
						}
							break;
						case R.id.tips_button: {
							fragment = LoopAtnOfferFragment.newInstance();
						}
							break;
						case R.id.account_button: {
							if (!UserDataPool.getInstance().isUserLoggedIn()) {
								AtnApp.showLoginScreen(MainMenuActivity.this);
								return;
							}
							fragment = AccountMainFragment.newInstance();
						}
						break;
						case R.id.my_inetrest_button: {
							//hold selected menu item when user choose filter then we have move to on same fragments
							final View mView = mSelectedButton;
							fragment = ChooseInterestFragment.newInstance();
							((ChooseInterestFragment)fragment).setDoneClickEventListener(new DialogClickEventListener() {
								@Override
								public void onClick(int resId) {
									if(mView!=null){
										mView.performClick();	
									}
								}
							});
						}
							break;
					default:
						break;
					}
					
					if (fragment != null) {
						replaceFragmentFromMenu(fragment);
						selectedMenuItem(v);
					}
				}
			};
			
			//select this view and unselect previous view if any.
			private void selectedMenuItem(View selectedView) {
				
				if (mSelectedButton != null) {
					mSelectedButton.setSelected(false);
					TypefaceUtils.applyTypeface(getApplicationContext(),
							mSelectedButton, TypefaceUtils.ROBOTO_CONDENCED);
				}
				// replace
				mSelectedButton = (Button) selectedView;
				mSelectedButton.setSelected(true);
				TypefaceUtils.applyTypeface(getApplicationContext(),
						mSelectedButton,
						TypefaceUtils.ROBOTO_CONDENCED_BOLD_ITALIC);
				
			}
			
			
			/*
			 * pop all fragment and replace one only 
			 * **/
			private void replaceFragmentFromMenu(final AtnBaseFragment fragment) {
				
				fragment.setChildFragmentListener(this);
				 mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						FragmentManager fragmentManager = getSupportFragmentManager();
						//remove all back stack 
						 if (fragmentManager.getBackStackEntryCount() > 0) {
							 fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(),
				                     FragmentManager.POP_BACK_STACK_INCLUSIVE);
				         }
						 
						fragmentManager.beginTransaction().replace(R.id.main_framelayout, fragment).commitAllowingStateLoss();
						mActionBar.setDisplayShowHomeEnabled(true);
					}
				}, DELAY_REPLACE_FRAGMENT);
			}
			
			/**
			 * Other view click listener.
			 * */
			OnClickListener onOtherViewClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.add_photos_button:
						if (!UserDataPool.getInstance().isUserLoggedIn()) {
							AtnApp.showLoginScreen(MainMenuActivity.this);
							return;
						}
						openVenuesList(VenuesListFragment.ADD_PHOTO);
						break;
					case R.id.add_reviews_button:
						if (!UserDataPool.getInstance().isUserLoggedIn()) {
							AtnApp.showLoginScreen(MainMenuActivity.this);
							return;
						}
						openVenuesList(VenuesListFragment.ADD_REVIEW);
						break;
					case R.id.user_pic_image_view:
						if (!UserDataPool.getInstance().isUserLoggedIn()) {
							AtnApp.showLoginScreen(MainMenuActivity.this);
							return;
						}
						showAddPicDialog();
						break;
						
					case R.id.search_button:
						openVenuesList(VenuesListFragment.SEARCH);
						break;
					default:
						break;
					}
				}
			};

			//open pre-populated venue list
			private void openVenuesList(final int requestType) {
				onBackOrHomePress();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						addToBackStack(VenuesListFragment.newInstance(requestType));
					}
				}, DELAY_REPLACE_FRAGMENT);
			}
			
			
			//show add or remove picture dialog 
			private void showAddPicDialog() {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setTitle(R.string.profile_photo_title)
			           .setItems(R.array.gallary_camera_remove_array, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			            	   if(which==1) {
				            		invokeImagePicker(ImagePicker.CAMERA, MainMenuActivity.this);
			            	   } else if(which==0) {
			            		    invokeImagePicker(ImagePicker.GALLARY, MainMenuActivity.this);
			            	   }else {
			            		   removeUserPic();
			            	   }
			           }
			    }).setNegativeButton(R.string.cancel_button, null);
			    builder.create().show();
			}
				
			// Add the fragment to the activity, pushing this transaction
		    // on to the back stack.
			@Override
			public void addToBackStack(AtnBaseFragment newFragment) {
				
				newFragment.setChildFragmentListener(this);
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();        
		       // ft.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment, 
		        //R.anim.fade_in_fragment, R.anim.fade_out_fragment);
		        //ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
		        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		        ft.replace(R.id.main_framelayout, newFragment);
		        ft.addToBackStack(null);
		        ft.commit();
			}

			/**
			 * called from fragments to popout fragment
			 * @param fragment to pop
			 * */
			@Override
			public boolean popBackStack(AtnBaseFragment fragment) {
				
				if(!onBackOrHomePress()) {
		    		FragmentManager fm = getSupportFragmentManager();
		    		if(fm.getBackStackEntryCount()>0) {
		    			fm.popBackStack();
		    			return true;
		    		}
		    	}
				return false;
			}
			
			@Override
			protected void onPostCreate(Bundle savedInstanceState) {
				super.onPostCreate(savedInstanceState);
				// Sync the toggle state after onRestoreInstanceState has occurred.
				mDrawerToggle.syncState();
			}

		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        /*
		         * The action bar home/up action should open or close the drawer.
		         * mDrawerToggle will take care of this.
		         */
		    	if((item.getItemId()==android.R.id.home)&&!onBackOrHomePress()) {
		    		FragmentManager fm = getSupportFragmentManager();
		    		if(fm.getBackStackEntryCount()>0) {
		    			fm.popBackStack();
		    			return true;
		    		}
		    	}
		  
		        if (mDrawerToggle.onOptionsItemSelected(item)) {
		            return true;
		        }
		        return super.onOptionsItemSelected(item);
		    }

		    @Override
		    public void onBackPressed() {
				if (!onBackOrHomePress()) {
					FragmentManager fm = getSupportFragmentManager();
					if(fm.getBackStackEntryCount()==0) {
						if(mShouldExitApp) {
							super.onBackPressed();
						} else {
							Toast.makeText(this, "Press again to exit!", EXIT_TOAST_DISPLAY_TIME).show();
							mShouldExitApp = true;
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									mShouldExitApp = false;
								}
							}, EXIT_TOAST_DISPLAY_TIME);
						}
					} else {
						super.onBackPressed();
					}
				}
		    }
		    
		    //check for drawer if drawer open then closed it on back press or home press
		    private boolean onBackOrHomePress() {
				if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
					mDrawerLayout.closeDrawer(GravityCompat.START);
					return true;
				}
				return false;
		    }
		    
		    @Override
		    public void onConfigurationChanged(Configuration newConfig) {
		        super.onConfigurationChanged(newConfig);
		        mDrawerToggle.onConfigurationChanged(newConfig);
		    }
			
		    /**
		     * Drawer listener
		     * */
			 private class AnchorDrawerListener implements DrawerLayout.DrawerListener {
			        @Override
			        public void onDrawerOpened(View drawerView) {
			            mDrawerToggle.onDrawerOpened(drawerView);
			        }

			        @Override
			        public void onDrawerClosed(View drawerView) {
			            mDrawerToggle.onDrawerClosed(drawerView);  
			        }

			        @Override
			        public void onDrawerSlide(View drawerView, float slideOffset) {
			            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
			            //change action bar background color on menu slide
			            getActionBarDrawable().setAlpha(Math.max(alpha, (int)(255*slideOffset)));
			        }
			        
			        @Override
			        public void onDrawerStateChanged(int newState) {
			            mDrawerToggle.onDrawerStateChanged(newState);
			        }
			    }

			 //back stack change listener
			@Override
			public void onBackStackChanged() {
				FragmentManager fm = getSupportFragmentManager();
				if(fm.getBackStackEntryCount()==0) {
					mActionBar.setDisplayShowHomeEnabled(true);
					mActionBar.setIcon(R.drawable.icn_actionbar_applogo);
					mDrawerToggle.setDrawerIndicatorEnabled(true);
				} else {
					mActionBar.setDisplayShowHomeEnabled(false);
					mDrawerToggle.setDrawerIndicatorEnabled(false);
				}
			}
			
			@Override
			protected void onDestroy() {
				
				//clean up on exit
				AtnLocationManager.getInstance().disconnect();
				InstagramImageLoader.loader.cancel();
				AtnUtils.stopSynchService(this);
				if(!UserDataPool.getInstance().isUserLoggedIn()) {
					InstagramSession.resetAccessToken(this);
				}
			
				super.onDestroy();
			}

			@Override
			public void onPickedImage(boolean isSuccess, String errorMsg, String path) {
				if (isSuccess) {
					updateUserPicture(path);
				} else {
					AtnUtils.showToast(errorMsg);
				}
			}

			/**
			 * Update user profile pic
			 * @param userImagePath image path
			 * */
			private void updateUserPicture(final String userImagePath) {
				AnchorProgressDialog.show(this, R.string.please_wait);
				AnchorHttpRequest anchorRequest = new AnchorHttpRequest(this, HttpUtility.buildBaseUri().appendPath("addProfilePic"), Method.POST, new AnchorHttpResponceListener() {
					@Override
					public void onSuccessInBackground(JSONObject jsonObject) {
					}
					
					@Override
					public void onSuccess(JSONObject jsonObject) {
						AnchorProgressDialog.conceal();
						if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS){
							try {
								UserDataPool.getInstance()
								.getUserDetail().setImageUrl(jsonObject.getJSONObject(JsonUtils.RESPONSE)
										.getJSONObject(JsonUtils.DATA).getString("picture"));
								DbHandler.getInstance().loginUser(UserDataPool.getInstance().getUserDetail());
								mUserImageView.setPath(userImagePath);
							} catch (Exception e) {
								AtnUtils.log(e.getMessage());
							}
						}
					}
					
					@Override
					public void onError(Exception ex) {
						AnchorProgressDialog.conceal();
						AtnUtils.showToast(ex.getLocalizedMessage());
					}
				});
				
				anchorRequest.addText(JsonUtils.UserKey.USER_ID, UserDataPool.getInstance()
						.getUserDetail().getUserId());
				anchorRequest.addFile(JsonUtils.UserKey.PROFILE_PIC, userImagePath);
				anchorRequest.execute();
			}
			
			//remove user picture
			private void removeUserPic() {

				if(TextUtils.isEmpty(UserDataPool
						.getInstance().getUserDetail().getImageUrl())) {
					return;
				}
				
				AnchorProgressDialog.show(this, R.string.please_wait);
				AnchorHttpRequest anchorRequest = new AnchorHttpRequest(this, HttpUtility.buildBaseUri()
						.appendPath(ApiEndPoints.REMOVE_USER_PIC), Method.GET, new AnchorHttpResponceListener() {
					@Override
					public void onSuccessInBackground(JSONObject jsonObject) {
					}
					@Override
					public void onSuccess(JSONObject jsonObject) {
						AnchorProgressDialog.conceal();
						if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS){
							try {
								UserDataPool.getInstance()
								.getUserDetail().setImageUrl("");
								DbHandler.getInstance().loginUser(UserDataPool.getInstance().getUserDetail());
								mUserImageView.setPath(BlurImageView.REMOVE_PATH);
							} catch (Exception e) {
								AtnUtils.log(e.getMessage());
							}
						}
					}
					
					@Override
					public void onError(Exception ex) {
						AnchorProgressDialog.conceal();
						AtnUtils.showToast(ex.getLocalizedMessage());
					}
				});
				anchorRequest.addText(JsonUtils.UserKey.USER_ID, UserDataPool.getInstance()
						.getUserDetail().getUserId());
				anchorRequest.execute();
			}
	
			///help dialog listener
	DialogClickEventListener helpDialogListener = new DialogClickEventListener() {
		@Override
		public void onClick(int resId) {
			
			selectedMenuItem(mHappeningNowButton);
				
			switch (resId) {
			case R.id.help_add_photos_button:
				openVenuesList(VenuesListFragment.ADD_PHOTO);
				break;
			case R.id.help_add_reviews_button:
				openVenuesList(VenuesListFragment.ADD_REVIEW);
				break;
			default:
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						onBackOrHomePress();
					}
				}, 300);
				break;
			}
		}
	};
	
	//call it when profile updated.
	public void profileUpdated(String path) {
		mUserImageView.setPath(path);
	}
	
	//show tips screen directly
	private void handleGcmNotification() {
		AtnLocationManager.getInstance().shouldStartService = true;
		final PushNotificationData pushData = (PushNotificationData) getIntent().getSerializableExtra(PushNotificationData.PUSH_DATA);
		
		mTipsButton.performClick();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Bundle dataBundle = new Bundle();
				dataBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID, pushData.getId());
				OfferDetailFragment offerDetailFragment = OfferDetailFragment.newInstance();
				offerDetailFragment.setArguments(dataBundle);
				addToBackStack(offerDetailFragment);
				
				//
				dataBundle = new Bundle();
				TipsDialog tipsDialog = new TipsDialog();
				tipsDialog.mDetailFragment = offerDetailFragment;
				tipsDialog.setGcmMessage(pushData);
				tipsDialog.setArguments(dataBundle);
				tipsDialog.show(getSupportFragmentManager(),  TipsDialog.TIPS_DIALOG);
			}
		}, 300);
	}
	

}
