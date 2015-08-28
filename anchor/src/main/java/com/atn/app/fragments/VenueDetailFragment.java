/**
 * Copyright Copeprmobile 2014
 * **/
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.SplashScreen;
import com.atn.app.activities.AtnActivity.FacebookLoginListener;
import com.atn.app.activities.OpenFourSquareUrlActivity;
import com.atn.app.constants.ATNConstants;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.ReviewTag;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.fragments.CommentFragment.OnCommentCloseListener;
import com.atn.app.fragments.ReviewDialogFragment.ReviewDialogListener;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.listener.AnimationAdapter;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;
import com.atn.app.webservices.AccessTokenUpdateWebservice;
import com.atn.app.webservices.AccessTokenUpdateWebserviceListener;
import com.atn.app.webservices.AtnVenueShareWebservice;
import com.atn.app.webservices.AtnVenueShareWebserviceListener;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

/***
 * Fourquare venue detail screen
 * */
public class VenueDetailFragment extends AtnBaseFragment implements OnClickListener,FacebookLoginListener {

	//small view comment fragment key
	private static final String COMMENT_FRAGMENT = "COMMENT_FRAGMENT";
	//have key called from venue Search Screen
	public static final String IS_FROM_SEARCH = "IS_FROM_SEARHC";
	//venue id key
	public static final String FS_VENUE_ID = "FS_VENUE_ID";
	
	public static final String ANCHOR_BAR_ID = "ANCHOR_BAR_ID";
	//if user coming from loop photos section then tapped media id. so that we will show same media here.
	public static final String SELECTED_MEDIA_ID = "SELECTED_MEDIA_ID";
	//image on wich user tapped
	public static final String URL = "IMAGE_URL";
	///screen position for moving image from that point to top.
	public static final String Y_POSITION = "Y_POSITION";
	
	//venue object may Foursquare or anchor venue object have to check on runtime
	private AtnOfferData mBarObject = null;
	//click image url thats was clicked on Happening now screen
	private String mImageUrl = null;
	//venue image position on happ
	private int mYPosition  = 0;
	private ViewPager mPager;	
	private List<IgMedia> mMediaList  = null;
	private ImageView mStaticMapImageView;
	private ImagePagerAdapter mPagerAdapter = null;
	
	//its true if image animation is running
	boolean isAnimationRunning = true;
	//we are loading data in asynch task if its done then true
	boolean isDataLoading = true;
	//image view for moving or show animation
	private ImageView mImageView;
	//add review scalable view 
	private View mBlueView;
	
	//tags
	private TextView mFirstTag;
	private TextView mSecondTag;
	
	private Button mAddReviewButton = null;
	private Button mYesButton = null;
	private Button mNoButton = null;
	private TextView mReviewCountTextView = null;
	private RatingBar mRatingBar;
	
	private FacebookSession mFbSession;
	
	///comment button
	private TextView mCommentCountTextView = null;
	private View mCommentView = null;
	
	private ProgressBar mProgressBar = null;
	private ImageTask loadVenueTask = null;
	public void setVenueData(AtnOfferData venueModel){
		mBarObject = venueModel;
	}
	
	private ReviewDialogFragment   mReviewTagDialog=null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments()!=null) {
			mImageUrl = getArguments().getString(URL);
			//Calculate top y position for image translating
			mYPosition = getArguments().getInt(Y_POSITION);
			final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                    new int[] { android.R.attr.actionBarSize });
			mYPosition = mYPosition - (int) styledAttributes.getDimension(0, 0);
			styledAttributes.recycle();
		}
		setHasOptionsMenu(true);
		mPagerAdapter = new ImagePagerAdapter(getChildFragmentManager());
		mFbSession = new FacebookSession(getActivity());
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mImageView = null;
		
		FrameLayout view = (FrameLayout)inflater.inflate(R.layout.venue_detail_layout, container, false);
		view.findViewById(R.id.open_map_button).setOnClickListener(this);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		mCommentCountTextView = (TextView)view.findViewById(R.id.comment_count_text_view);
		mBlueView  = view.findViewById(R.id.blue_view);
		mBlueView.setVisibility(View.INVISIBLE);
		mAddReviewButton = (Button)view.findViewById(R.id.add_review_button_on_bar);
		mYesButton = (Button)view.findViewById(R.id.yes_button);
		mNoButton = (Button)view.findViewById(R.id.no_button);
		
	    mFirstTag = (TextView)view.findViewById(R.id.first_tag);
		mSecondTag = (TextView)view.findViewById(R.id.second_tag);
		mReviewCountTextView = (TextView)view.findViewById(R.id.review_count_text_view);
		mRatingBar = (RatingBar)view.findViewById(R.id.rating_bar);
		
		mAddReviewButton.setOnClickListener(this);
		mYesButton.setOnClickListener(this);
		mNoButton.setOnClickListener(this);
		mCommentCountTextView.setOnClickListener(this);
		
		view.findViewById(R.id.open_four_web_view_button).setOnClickListener(this);
		mStaticMapImageView = (ImageView)view.findViewById(R.id.google_static_map);
		

		mPager = (ViewPager)view.findViewById(R.id.view_pager);
	    mPager.setAdapter(mPagerAdapter);
	    mPager.setOffscreenPageLimit(2);
	     
	    LayoutParams layoutParam = mPager.getLayoutParams();
		layoutParam.height = UiUtil.getLoopPhotoSize(getActivity());
	    mPager.setLayoutParams(layoutParam);
		
	    mCommentView = view.findViewById(R.id.comment_container);
	    FrameLayout.LayoutParams commentParams  = (FrameLayout.LayoutParams) mCommentView.getLayoutParams();
	    commentParams.setMargins(0, UiUtil.getLoopPhotoSize(getActivity()), 0, 0);
	    mCommentView.setLayoutParams(commentParams);
	    
	    //set button margin
	    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mCommentCountTextView.getLayoutParams();
		params.setMargins(10, UiUtil.getLoopPhotoSize(getActivity())-getResources().getDimensionPixelSize(R.dimen.comment_button_height), 0, 0);
		mCommentCountTextView.setLayoutParams(params);
		
		//if fragemnt argument have Y position then it will show the animation and 
		//isAnimationRunning is true. we set it false when animation is done.
		if(getArguments()!=null&&getArguments().containsKey(Y_POSITION)&&isAnimationRunning) {
			addImageView(view);
			view.post(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					//set image initially at y position from where we start the animation
					mImageView.animate().setDuration(0).translationY(mYPosition).withEndAction(new Runnable() {
						@Override
						public void run() {
							//now translate the View to 0 y axis
							ObjectAnimator anim = ObjectAnimator.ofInt(getActioBarBg(), "alpha", 255,0);
							anim.setDuration(Math.abs(mYPosition));
							anim.start();
							mImageView.animate().translationY(0)
							.setDuration(Math.abs(mYPosition)).withEndAction(new Runnable() {
								@Override
								public void run() {
									onAnimationEnd();
								}
							});
						}
					});
				}
			});
		} else {
			isAnimationRunning = false;
			onAnimationEnd();
		}
		//load venue detail
		loadVenueTask = new ImageTask();
		loadVenueTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	 return view;
	}
	
	//add venue image for animation and if 
	//venue does not have media images
	private void addImageView(FrameLayout view) {
		
		mImageView = new  ImageView(getActivity());
		mImageView.setScaleType(ScaleType.CENTER_CROP);
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
												UiUtil.getLoopPhotoSize(getActivity()));
		mImageView.setLayoutParams(param);
		view.addView(mImageView);
		
		if(TextUtils.isEmpty(mImageUrl)&&mBarObject!=null) {
			if (mBarObject.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData) mBarObject;
				mImageUrl = atnVenueData.getBusinessImageUrl();
			} else {
				VenueModel venue = (VenueModel) mBarObject;
				mImageUrl = venue.getPhoto();
			}
		}
		
		if(!TextUtils.isEmpty(mImageUrl)) {
			Picasso.with(AtnApp.getAppContext()).load(mImageUrl)
			.placeholder(R.drawable.empty_photo).into(mImageView);
		} else {
			mImageView.setImageResource(R.drawable.empty_photo);
		}
		mCommentCountTextView.bringToFront();
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openFullScreenDialog(0);
			}
		});
		
	}
	
	//called when animation is done
	private void onAnimationEnd() {
		isAnimationRunning = false;
		setActionBarAlpha(ACTION_BAR_TRANSPARENT);
		//set data in view if data is not loading 
		//by asynch task and fragment is visible to user
		if(!isDataLoading&&isVisible()) {
			setDataInViews();
		}
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		//show animation when animation is finished
    	if(!isAnimationRunning && mBarObject!=null) {
    		 	inflater.inflate(R.menu.bar_detail_menu, menu);
    		 	MenuItem favMenuItem = menu.findItem(R.id.fav_menu_item);
    		 	 boolean isFav = false;
    		 	if(mBarObject.getDataType()==VenueType.FOURSQUARE) {
    		 		 menu.removeItem(R.id.atn_bar_detail);
    		 		 menu.removeItem(R.id.share_menu_item);
    		 		 isFav = (((VenueModel)mBarObject).getAtnBarType()==VenueModel.NON_ATN_BAR_FAV);
    		 	} else {
    		 		isFav = ((AtnRegisteredVenueData)mBarObject).isFavorited();
    		 	}   
    		 	favMenuItem.setIcon(isFav?R.drawable.icn_actionbar_star_active:R.drawable.icn_actionbar_star);
    	}
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch (item.getItemId()) {
		case R.id.atn_bar_detail:
			Bundle atnBundle = new Bundle();
			atnBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID, 
					           ((AtnRegisteredVenueData)mBarObject).getBusinessId());
			OfferDetailFragment barFragment = OfferDetailFragment.newInstance();
			barFragment.setArguments(atnBundle);
			addToBackStack(barFragment);
			return true;
		case R.id.fav_menu_item:
		{
				if(getIsFavorits())
					showAlertForFavoriteButtonClick();
				else
					onFavoriteButtonClick();
		}
			 
			return true;
		case R.id.share_menu_item:
			if(!UserDataPool.getInstance().isUserLoggedIn()) {
				AtnApp.showLoginScreen(getActivity());
			} else {
				showFacebookShareDialog();
			}
			return true;
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
    }
	
    private boolean  getIsFavorits() {
		
    	if (mBarObject.getDataType() == VenueType.ANCHOR) {
    		return ((AtnRegisteredVenueData) mBarObject).isFavorited();
		} else {
			VenueModel mVenueModel = (VenueModel)mBarObject;
			return mVenueModel.getAtnBarType() == VenueModel.NON_ATN_BAR_FAV ?  true	: false;
			
		}
   	}
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(loadVenueTask!=null&&loadVenueTask.getStatus()!=Status.FINISHED){
    		loadVenueTask.cancel(true);
    	}
    }
    
	/**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm); 
        }
        
		@Override
		public int getCount() {
			if (mMediaList != null) {
				return mMediaList.size();
			}
			return 0;
		}
		
        @Override
        public Fragment getItem(int position) {
        	IgMedia igMedia = mMediaList.get(position);
			return ImageDetailFragment.newInstance(igMedia.getImageUrl(),
					igMedia.getLikesCount(), igMedia.getCreatedDate(),
					igMedia.getHashTag(),position,dialogClickListener);
        }
    }

    private DialogClickEventListener dialogClickListener = new DialogClickEventListener() {
		@Override
		public void onClick(int position) {
			openFullScreenDialog(position);
		}
	};
	//method for open full screen image dialog
	private void openFullScreenDialog(int position) {
		VenueModel venueModel = null;
		if(mBarObject.getDataType() == VenueType.ANCHOR) {
			venueModel = ((AtnRegisteredVenueData)mBarObject).getFsVenueModel();
		} else {
			venueModel = (VenueModel)mBarObject;
		}
		//show full screen dialog
		FullScreenImageDialogFragment mDialog = FullScreenImageDialogFragment.newInstance(venueModel, 
				position, new DialogClickEventListener() {
			@Override
			public void onClick(int position) {
				mPager.setCurrentItem(position, true);
			}
		});
	     mDialog.show(getFragmentManager(), FullScreenImageDialogFragment.FULL_SCREEN_IMAGE_FRAGMENT);
	}
	
	
    //
	// load venue from data base or if come from search and venue is not exist in local db then  
	//Hit api to fetch media image
	//
    class ImageTask extends AsyncTask<Void, AtnOfferData, AtnOfferData> {
		private FsHttpRequest fsRequest = null;
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			if(getArguments().containsKey(IS_FROM_SEARCH)){
				fsRequest = new FsHttpRequest(getActivity());
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public AtnOfferData doInBackground(Void... params) {

			Bundle bundle = getArguments();
			AtnOfferData barObjet = null;
			if(bundle.containsKey(FS_VENUE_ID)) {
				String venueId = bundle.getString(FS_VENUE_ID);
				barObjet = DbHandler.getInstance().getAtnBusinessDetail(venueId);
				//if still null then load from 
				if(barObjet==null) {
					barObjet = Atn.Venue.getVenue(venueId,AtnApp.getAppContext());	
				}
			} else if(bundle.containsKey(ANCHOR_BAR_ID)) {
				String venueId = bundle.getString(ANCHOR_BAR_ID);
				barObjet = DbHandler.getInstance().getAtnBusinessDetail(venueId);
			}
			
			
			if(barObjet==null&&bundle.containsKey(IS_FROM_SEARCH)&&mBarObject!=null) {
				barObjet = mBarObject;
				loadTags(barObjet);	
				//publish result so that data is filled in views
				publishProgress(barObjet);
				fsRequest.fetchVenueInfo(((VenueModel)mBarObject));
				
				if(fsRequest.getIgMediaRequester().fetchInstgramIdForSearchedVenue(((VenueModel)mBarObject))) {
					fsRequest.getIgMediaRequester().fetchInstgramMediaSearch(((VenueModel)mBarObject));
				}	
			} else {
				loadTags(barObjet);	
			}
			
			return barObjet;
		}
		//load tags from db
		private void loadTags(AtnOfferData barObj) {
			if (barObj.getDataType() == VenueType.FOURSQUARE&&((VenueModel) barObj).getReviewCount()>0) {
				((VenueModel) barObj).setReviews(Atn.ReviewTable.getVenueTwoReviewTag(
						AtnApp.getAppContext(),
						((VenueModel) barObj)));
			} else if(barObj.getDataType() == VenueType.ANCHOR&&((AtnRegisteredVenueData)barObj).getFsVenueModel().getReviewCount()>0){
				VenueModel venue = ((AtnRegisteredVenueData)barObj).getFsVenueModel();
				if(venue!=null) {
					venue.setReviews(Atn.ReviewTable.getVenueTwoReviewTag(AtnApp.getAppContext(),venue));
				}
			}
		}
		
		@Override
		public void onCancelled() {
			if (fsRequest != null)
				fsRequest.setCanceled(true);
			super.onCancelled();
		}

		@Override
		public void onProgressUpdate(AtnOfferData... values) {
			super.onProgressUpdate(values);
			if(isVisible()&&values.length>0) {
				mBarObject = values[0];
			    setDataInViews();
			    AtnUtils.showToast("Please Wait....");
			}
		}
		
		@Override
		public void onPostExecute(AtnOfferData result) {
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.INVISIBLE);
			if(isVisible()) {
				if (result != null) {
					mBarObject = result;
					isDataLoading = false;
					setDataInViews();
				} 
			}
		}
	}
    
    /**
     * Set data in their corresponding views
     * 
     * */
	public void setDataInViews() {
		
		if(!isVisible()&&getActivity()!=null)
			return;
	
		if(!isAnimationRunning) {
			mPager.setVisibility(View.VISIBLE);
		}
	 
		if(mBarObject.getDataType()==VenueType.ANCHOR) {
			 AtnRegisteredVenueData atnVenueData =(AtnRegisteredVenueData) mBarObject;
			 setTitle(atnVenueData.getBusinessName());
			 
			if(atnVenueData.getFsVenueModel()!=null) {
				///set tags.
				setTags(atnVenueData.getFsVenueModel().getReviews());
				mMediaList = atnVenueData.getFsVenueModel().getInstagramMedia();
				mPagerAdapter.notifyDataSetChanged();
				mCommentCountTextView.setText(atnVenueData.getFsVenueModel().getCommentCount()+"");
				setReviewAndRating(atnVenueData.getFsVenueModel());
			}
			
			loadGoogleStaticMap(atnVenueData.getLatLng());
		} else {
			VenueModel venueModel = (VenueModel)mBarObject;
			setTitle(venueModel.getVenueName());
			
			mMediaList = venueModel.getInstagramMedia();
			mPagerAdapter.notifyDataSetChanged();
			setTags(venueModel.getReviews());
			mCommentCountTextView.setText(venueModel.getCommentCount()+"");
			
			if(venueModel.getLat()!=null) {
				loadGoogleStaticMap(venueModel.getLatLng());
			} else {
				loadGoogleStaticMap(venueModel.getiGlatLng());
			}
			setReviewAndRating(venueModel);
		}
		
		// decide show or remove venue image if venue has media image 
		//then we will remove venue image view otherwise add single image view to show venue image
		if(mMediaList!=null&&mMediaList.size()>0&&mImageView!=null&&!isAnimationRunning) {
			mImageView.setVisibility(View.GONE);
			mImageView.setImageBitmap(null);
			FrameLayout fram = (FrameLayout)getView().findViewById(R.id.bar_detail_top_container);
			fram.removeView(mImageView);
			mImageView= null;
		}else if(mImageView==null&&(mMediaList==null||mMediaList.size()==0)&&!isAnimationRunning) {
			FrameLayout view = (FrameLayout)getView().findViewById(R.id.bar_detail_top_container);
			addImageView(view);
		}
		
		//identify clicked venue if position found then show it on first time
		final Bundle bundle = getArguments();
		int selectedIndex = 0;
		if(bundle!=null) {
			String mediaId = bundle.getString(SELECTED_MEDIA_ID);
			if(!TextUtils.isEmpty(mediaId)&&mMediaList!=null) {
				for (IgMedia media : mMediaList) {
					if(media.getImageId().equals(mediaId)) {
						selectedIndex = mMediaList.indexOf(media);
						break;
					}
				}
				if (selectedIndex == -1)
					selectedIndex = 0;
			}
		}
		mPager.setCurrentItem(selectedIndex,false);
		invalidateOptionMenu();
		
		updateBussinessVisitStatus();
	}
    
	//download static map and apply B&W
	private void loadGoogleStaticMap(LatLng latLng) {
		String urlStr=HttpUtility.getStaticMapUrl(latLng, mStaticMapImageView.getWidth(),
				mStaticMapImageView.getHeight());
		Picasso.with(getActivity()).load(HttpUtility.getStaticMapUrl(latLng, mStaticMapImageView.getWidth(),
				mStaticMapImageView.getHeight()))
		.placeholder(R.drawable.empty_photo).into(mStaticMapImageView);
		UiUtil.makeImageViewBlackAndWhite(mStaticMapImageView);
	}
	
    //set tags 
	private void setTags(ArrayList<ReviewTag> reviewList) {
		mFirstTag.setVisibility(View.GONE);
		mSecondTag.setVisibility(View.GONE);
		if(reviewList!=null) {
			if(reviewList.size()>1) {
				mFirstTag.setVisibility(View.VISIBLE);
				mFirstTag.setText(reviewList.get(0).getName());
				mSecondTag.setVisibility(View.VISIBLE);
				mSecondTag.setText(reviewList.get(1).getName());
			} else if(reviewList.size()>0) {
				mFirstTag.setVisibility(View.VISIBLE);
				mFirstTag.setText(reviewList.get(0).getName());
			}
		} 
	}
	
	/**
	 * 
	 * **/
	private void openGoogleApp() {
		
		if (mBarObject.getDataType() == VenueType.ANCHOR) {
			AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)mBarObject;
			AtnUtils.gotoDirection(getActivity(),atnVenueData.getBusinessLat(),atnVenueData.getBusinessLng());
		} else {
			VenueModel atnVenueData = (VenueModel)mBarObject;
			AtnUtils.gotoDirection(getActivity(),atnVenueData.getLat(),atnVenueData.getLng());
		}
	}
	
	@Override
	public void onClick(View v) {
		onClick(v.getId());
	}

	//helper method for calling view click programaticaly
	private void onClick(int viewId){
		switch (viewId) {
		case R.id.add_review_button_on_bar: {
			openReviewSection();
		}
			break;
		case R.id.yes_button:{

			
			if (mBarObject.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData) mBarObject;
				mReviewTagDialog  = ReviewDialogFragment.newInstance(this,atnVenueData.getFsVenueModel());
				mReviewTagDialog.setVenue(atnVenueData.getFsVenueModel());
			} else {
				mReviewTagDialog  = ReviewDialogFragment.newInstance(this,((VenueModel)mBarObject));
				mReviewTagDialog.setVenue((VenueModel)mBarObject);
				
			}
			
			mReviewTagDialog.setClickEventListener(new ReviewDialogListener() {
				@Override
				public void onClick(int resId) {
					closeReviewSection();
				}

				@Override
				public void selectedReview(ArrayList<ReviewTag> tags) {
					addReviews(tags);
				}
			});
			
			FragmentManager childFm = getChildFragmentManager();
			addToBackStack(mReviewTagDialog);

//			FragmentTransaction ft = childFm.beginTransaction();
//			ft.setCustomAnimations(R.anim.comment_up, R.anim.comment_down,R.anim.comment_up, R.anim.comment_down);
//			ft.replace(R.id.comment_container, mReviewTagDialog, COMMENT_FRAGMENT);
//			ft.addToBackStack(null);
//			ft.commit();
			
	       // mReviewTagDialog.show(getFragmentManager(), ReviewDialogFragment.REVIEW_SCREEN);
			break;
		}
		case R.id.no_button: {
			closeReviewSection();
			addReviews(null);
			break;
		}
		case R.id.open_map_button:{
			openGoogleApp();
			break;
		}
		case R.id.comment_count_text_view:{
			commentButtonClick();
			break;
		}
		case R.id.open_four_web_view_button:{
			VenueModel atnVenueData;
			if (mBarObject.getDataType() == VenueType.ANCHOR) {
				atnVenueData = ((AtnRegisteredVenueData)mBarObject).getFsVenueModel();
			} else {
				 atnVenueData = (VenueModel)mBarObject;
			}
			if(atnVenueData!=null) {
				Intent intent = new Intent(getActivity(),OpenFourSquareUrlActivity.class);
				intent.putExtra(OpenFourSquareUrlActivity.FOUR_SQUARE_ID, atnVenueData.getVenueId());
				if(!TextUtils.isEmpty(atnVenueData.getCanonicalURL()))
					intent.putExtra(OpenFourSquareUrlActivity.CANONICAL_URL, atnVenueData.getCanonicalURL());
				startActivity(intent);
			}
			break;
		}
		default:
			break;
		}
	}
	
	//open review section
	private void openReviewSection() {
		
		mAddReviewButton.setEnabled(false);
		mAddReviewButton.setText(R.string.do_u_like_this_place);
		mBlueView.setVisibility(View.VISIBLE);
		mBlueView.animate()
         .scaleY(mStaticMapImageView.getHeight())
         .setInterpolator(new AccelerateInterpolator(2f))
         .setDuration(300)
         .setListener(new AnimatorListenerAdapter() {
        	 @Override
        	public void onAnimationEnd(Animator animation) {
        		 //start animation of showing yes and no button
        		 	mYesButton.setVisibility(View.VISIBLE);
					mNoButton.setVisibility(View.VISIBLE);
					Animation scaleAnimYes = AnimationUtils.loadAnimation(getActivity(),R.anim.map_bubble_animation);
					Animation scaleAnimNo = AnimationUtils.loadAnimation(getActivity(),R.anim.map_bubble_animation);
					scaleAnimNo.setStartOffset(100);
					mYesButton.startAnimation(scaleAnimYes);
					mNoButton.startAnimation(scaleAnimNo);
        	 }
		})
         .start();
	}
	
	//close review section
	private void closeReviewSection() {
		
		mYesButton.setVisibility(View.GONE);
		mNoButton.setVisibility(View.GONE);
		mBlueView.animate()
        .scaleY(1.0f)
        .setInterpolator(new AccelerateInterpolator(2f))
        .setDuration(300)
        .setListener(new AnimationAdapter() {
       	 public void onAnimationEnd(Animator animation) {
       		mBlueView.setVisibility(View.INVISIBLE);
			mAddReviewButton.setEnabled(true);
			mAddReviewButton.setText(R.string.add_review_button);
       	 };
        })
        .start();
	}

	/**
	 * Shows a dialog to ask user whether he wants to share this venue on his/her Facebook wall.
	 */
	private void showFacebookShareDialog() {
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.share_title);
		alertDialog.setMessage(R.string.share_on_facebook);
		alertDialog.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/**
				 * If user has no Facebook access token then show Facebook login dialog.
				 */
				if(TextUtils.isEmpty(mFbSession.getAccessToken())){
					showFacebookLoginDialog();
				} else {
					shareOnFacebook();
				}
			}
		});
		
		alertDialog.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	
	/**
	 * Shows a dialog to login to Facebook.
	 */
	private void showFacebookLoginDialog(){
		((AtnActivity) getActivity()).postOnFacebookClick(this);
	}
	
	private void shareOnFacebook() {
		AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)mBarObject;
		AnchorProgressDialog.show(getActivity(), R.string.please_wait);
		AtnVenueShareWebservice shareService = new AtnVenueShareWebservice(UserDataPool.getInstance().getUserDetail().getUserId(), atnVenueData.getBusinessId());
		shareService.setAtnVenueShareWebServiceListener(mAtnVenueShareWebserviceListener);
		shareService.shareVenue();
	}
	
	/**
	 * Listens for web service response when share icon is clicked.
	 */
	private AtnVenueShareWebserviceListener mAtnVenueShareWebserviceListener = new AtnVenueShareWebserviceListener() {
		
		@Override
		public void onSuccess(String message) {
			AnchorProgressDialog.conceal();
			
			if(message != null && message.contains(ATNConstants.OAUTH_EXCEPTION)) {
				AtnUtils.logE("ERROR " + message);
				AtnApp.showMessageDialog(getActivity(), message, false);
			} else {
				AtnApp.showMessageDialog(getActivity(), getActivity().getString(R.string.venue_shared_on_wall), false);
			}
		}
		
		@Override
		public void onFailed(int errorCode, String errorMessage){
			AnchorProgressDialog.conceal();
		}
	};
	 
	@Override
	public void onFacebookLogin(boolean isSuccess, String message) {
		
		if(isSuccess) {
			
			UserDataPool.getInstance().getUserDetail().setUserFbToken(mFbSession.getAccessToken());
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
			AccessTokenUpdateWebservice tokenUpdateService = new AccessTokenUpdateWebservice(UserDataPool.getInstance().getUserDetail().getUserId());
			tokenUpdateService.setUpdateAccessTokenWebserviceListener(mAccessTokenUpdateWebserviceListener);
			tokenUpdateService.updateFacebookToken(mFbSession.getAccessToken(),null);
			
		} else {
			//AtnUtils.showToast(message);
		}
	}

	@Override
	public void onUserInfoFetch(GraphUser user) {
	}

	/**
	 * Listen for the Facebook access token update web service notifications. If access token is updated then
	 * share the business on Facebook wall.
	 */
	private AccessTokenUpdateWebserviceListener mAccessTokenUpdateWebserviceListener = new AccessTokenUpdateWebserviceListener() {
		
		@Override
		public void onSuccess(String message) {
			AnchorProgressDialog.conceal();
			shareOnFacebook();
		}
		
		@Override
		public void onFailed(int errorCode, String errorMessage){
			AnchorProgressDialog.conceal();
			AtnApp.showMessageDialog(getActivity(), errorMessage, false);
		}
	};
	
	private void showAlertForFavoriteButtonClick() {
		
		AlertDialog.Builder loginAlertDialog = new AlertDialog.Builder(getActivity());
		loginAlertDialog.setTitle(R.string.app_name);
		loginAlertDialog.setMessage(R.string.favorite__text);
		loginAlertDialog.setPositiveButton(R.string.favorite_remove_text,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						onFavoriteButtonClick();
					}
				});
		loginAlertDialog.setNegativeButton(R.string.favorite_cancel_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
			
		});
		loginAlertDialog.show();
	}
	
	
	
	//favorite bar of unfavorite bar
	private void onFavoriteButtonClick() {
		
		if (!UserDataPool.getInstance().isUserLoggedIn()) {
			AtnApp.showLoginScreen(getActivity());
			return;
		}
	 
		AnchorProgressDialog.show(getActivity(), R.string.please_wait);
		
		if (mBarObject.getDataType() == VenueType.ANCHOR) {
			
			final AtnRegisteredVenueData atnVenueData =(AtnRegisteredVenueData) mBarObject;
			Uri.Builder uriBuilder = HttpUtility.buildBaseUri().appendPath(ApiEndPoints.BUSINESS)
					.appendPath(atnVenueData.isFavorited()?ApiEndPoints.ANCHOR_BAR_UNFAVORITE:ApiEndPoints.ANCHOR_BAR_FAVORITE);
			
			AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), uriBuilder, Method.GET,
					new AnchorHttpResponceListener() {
						@Override
						public void onSuccessInBackground(JSONObject jsonObject) {}
						@Override
						public void onSuccess(JSONObject jsonObject) {
							AnchorProgressDialog.conceal();
							if (JsonUtils.resultCode(jsonObject) == JsonUtils.ANCHOR_SUCCESS) {
								atnVenueData.setFavorited(!atnVenueData.isFavorited());
								DbHandler.getInstance().updateBusinessFavoriteStatus(atnVenueData.getBusinessId(), atnVenueData.isFavorited());
								updateNonAtnBar(atnVenueData.isFavorited());
								invalidateOptionMenu();
																
							} else {
								UiUtil.showAlertDialog(getActivity(), JsonUtils.getErrorMessage(jsonObject));
							}
						}
						@Override
						public void onError(Exception ex) {
							AnchorProgressDialog.conceal();
							UiUtil.showAlertDialog(getActivity(), ex);
						}
					});
			
			anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
			anchorRequest.addText(JsonUtils.VenuePicUpload.BUSINESS_ID,atnVenueData.getBusinessId());
			anchorRequest.execute();
			
		} else {

			final VenueModel mVenueModel = (VenueModel)mBarObject;
			Uri.Builder uriBuilder = HttpUtility
					.buildBaseUri()
					.appendPath(mVenueModel.getAtnBarType() == VenueModel.NON_ATN_BAR_FAV ? 
							         ApiEndPoints.FOURSQUARE_BAR_UNFAVORITE
									: ApiEndPoints.FOURSQUARE_BAR_FAVORITE);
			
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), uriBuilder, Method.POST,
				new AnchorHttpResponceListener() {
					@Override
					public void onSuccessInBackground(JSONObject jsonObject) {}
					@Override
					public void onSuccess(JSONObject jsonObject) {
						
						AtnUtils.log(jsonObject.toString());
						AnchorProgressDialog.conceal();
						if (JsonUtils.resultCode(jsonObject) == JsonUtils.ANCHOR_SUCCESS) {
							if(mVenueModel.getAtnBarType() == VenueModel.NON_ATN_BAR_FAV) {
								mVenueModel.setAtnBarType(VenueModel.NON_ATN_BAR);
								updateNonAtnBar(false);
							} else {
								 
								try {
									String barId = jsonObject.getJSONObject(JsonUtils.RESPONSE).getString(JsonUtils.VenuePicUpload.FS_ANCHOR_VENUE_ID);
									mVenueModel.setAtnBarId(barId);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								mVenueModel.setAtnBarType(VenueModel.NON_ATN_BAR_FAV);
								if(getArguments().getBoolean(IS_FROM_SEARCH, false)) {
									Atn.Venue.insertOrUpdateSearchedVenue(mVenueModel, AtnApp.getAppContext());
								} else {
									updateNonAtnBar(true);
								}
							}
							invalidateOptionMenu();
						} else {
							UiUtil.showAlertDialog(getActivity(), JsonUtils.getErrorMessage(jsonObject));
						}
					}
					@Override
					public void onError(Exception ex) {
						AnchorProgressDialog.conceal();
						UiUtil.showAlertDialog(getActivity(), ex);
					}
				});
		
		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		
		if(mVenueModel.getAtnBarType() == VenueModel.NON_ATN_BAR_FAV) {
			
			anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
			anchorRequest.addText(JsonUtils.VenuePicUpload.FS_ANCHOR_VENUE_ID,mVenueModel.getAtnBarId());
			
		} else {
			
			HttpUtility.addVenueParams(getActivity(), anchorRequest, mVenueModel);
		}
		  anchorRequest.execute();	
		}
	}

 	//update
	private void updateNonAtnBar(boolean isAdded) {

		VenueModel venueModel = null;
		if (mBarObject.getDataType() == VenueType.ANCHOR) {
			venueModel = ((AtnRegisteredVenueData) mBarObject).getFsVenueModel();
			if (venueModel != null) {
				//venueModel.setAtnBarId(((AtnRegisteredVenueData) mBarObject).getBusinessId());
				venueModel.setAtnBarType(((AtnRegisteredVenueData) mBarObject)
						.isFavorited() ? VenueModel.ATN_BAR_FAV
						: VenueModel.ATN_BAR);
			}
		} else {
			venueModel = (VenueModel) mBarObject;
		}
		
		if (venueModel != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Atn.Venue.VENUE_ID, venueModel.getVenueId());
			contentValues.put(Atn.Venue.FOLLOWED,venueModel.getAtnBarType());
			
			if(!TextUtils.isEmpty(venueModel.getAtnBarId()))
			    contentValues.put(Atn.Venue.ATN_BAR_ID, venueModel.getAtnBarId());
			
			Atn.Venue.update(contentValues, AtnApp.getAppContext());
		}
		
		if(isAdded)
			UiUtil.showAlertDialog(getActivity(), venueModel.getVenueName()+" has been added to your travel log.");

		
	}
	
	//show comment or hide if visible.
	private void commentButtonClick() {

		if (mCommentView.isSelected()) {
			mCommentView.setClickable(false);
			FragmentManager childFm = getChildFragmentManager();
			childFm.popBackStack();
		} else {
			mCommentView.setClickable(true);
			// setup map fragment....
			FragmentManager childFm = getChildFragmentManager();
			CommentFragment commentFrag = CommentFragment.newInsatnce(onCommentCloseListnere);
			//addd.....
			Bundle bundle = new Bundle();
			bundle.putBoolean(CommentFragment.IS_SMALL_VIEW, true);
			
			if (mBarObject.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)mBarObject;
				bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getFsVenueModel().getVenueId());
			} else {
				VenueModel atnVenueData = (VenueModel)mBarObject;
				bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getVenueId());
				if(getArguments().containsKey(IS_FROM_SEARCH)) {
					commentFrag.setVenueModel(atnVenueData);
				}
			}
			
			commentFrag.setArguments(bundle);
			
			FragmentTransaction ft = childFm.beginTransaction();
			ft.setCustomAnimations(R.anim.comment_up, R.anim.comment_down,R.anim.comment_up, R.anim.comment_down);
			ft.replace(R.id.comment_container, commentFrag, COMMENT_FRAGMENT);
			ft.addToBackStack(null);
			ft.commit();
		}
		mCommentView.setSelected(!mCommentView.isSelected());
	}
	
	OnCommentCloseListener onCommentCloseListnere = new OnCommentCloseListener() {
		@Override
		public void onClose(boolean showBigView) {
			mCommentView.setClickable(false);
			mCommentView.setSelected(!mCommentView.isSelected());
			if(showBigView) {
				CommentFragment commentFragment = CommentFragment.newInsatnce(onCommentCloseListnere);
				Bundle bundle = new Bundle();
				if (mBarObject.getDataType() == VenueType.ANCHOR) {
					AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)mBarObject;
					bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getFsVenueModel().getVenueId());
				} else {
					VenueModel atnVenueData = (VenueModel)mBarObject;
					bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getVenueId());

					// Added for the Crash on Add Comments click, atnVenueData model was null;
					if(getArguments().containsKey(IS_FROM_SEARCH)) {
						commentFragment.setVenueModel(atnVenueData);
					}
				}
				commentFragment.setArguments(bundle);
				addToBackStack(commentFragment);
			}
		}
	};
	
	//add review on server
	private void addReviews(final ArrayList<ReviewTag> list) {
		
		if(!UserDataPool.getInstance().isUserLoggedIn()) {
			AtnApp.showLoginScreen(getActivity());
			return;
		}
		
		
		AnchorProgressDialog.show(getActivity(), R.string.please_wait);
		StringBuilder tagIds = new StringBuilder();
		if(list!=null) {
			for (ReviewTag reviewTag : list) {
				tagIds.append(String.valueOf(reviewTag.getTagId()));
				tagIds.append(",");
			}
		}
		
		if(tagIds.length()>0) {
			tagIds.deleteCharAt(tagIds.length()-1);
		}
		
		final VenueModel venue;
		final Context context = getActivity().getApplicationContext();
		if (mBarObject.getDataType() == VenueType.ANCHOR) {
			venue = ((AtnRegisteredVenueData)mBarObject).getFsVenueModel();
		} else {
			venue = (VenueModel)mBarObject;
		}
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(context, 
				HttpUtility.buildBaseUri().appendPath(ApiEndPoints.POST_VENUE_REVIEW), 
				Method.POST, new AnchorHttpResponceListener() {
			
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
				AtnUtils.log(jsonObject.toString());
				if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {
					try {
						JSONObject responce = jsonObject.getJSONObject(JsonUtils.RESPONSE);
						if(responce!=null) {
							if(!responce.isNull(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT)){
								venue.setReviewCount(responce.getInt(JsonUtils.FsVenueAnchorKeys.REVIEW_COUNT));
							}
							if(!responce.isNull(JsonUtils.FsVenueAnchorKeys.RATING)){
								venue.setRating((float)responce.getDouble(JsonUtils.FsVenueAnchorKeys.RATING));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(getArguments().containsKey(IS_FROM_SEARCH)) {
						Atn.Venue.insertOrUpdateSearchedVenue(venue, context);
					} else {
						//update venue detail in db.
						ContentValues values = new ContentValues();
						values.put(Atn.Venue.VENUE_ID, venue.getVenueId());
						values.put(Atn.Venue.REVEIW_COUNT, venue.getReviewCount());
						values.put(Atn.Venue.RATING, venue.getRating());
						Atn.Venue.update(values, getActivity());	
					}
					Atn.ReviewTable.insertOrUpdateReview(getActivity(), list);
				}
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {
				AnchorProgressDialog.conceal();
				if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {
					venue.setReviews(Atn.ReviewTable.getVenueTwoReviewTag(getActivity(), venue));
					setTags(venue.getReviews());
					//update ui.
					setReviewAndRating(venue);
				} else {
					UiUtil.showAlertDialog(getActivity(), JsonUtils.getErrorMessage(jsonObject));
				}
			}
			@Override
			public void onError(Exception ex) {
				AnchorProgressDialog.conceal();
				UiUtil.showAlertDialog(getActivity(), ex);
			}
		});
		
		//add user id.
		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		
		HttpUtility.addVenueParams(getActivity(), anchorRequest, venue);
        
        if(tagIds.length()>0)
        	anchorRequest.addText(JsonUtils.VenuePicUpload.TAGS,tagIds.toString());
        
		anchorRequest.execute();
	}
	//set data in revwiew and rating 
	private void setReviewAndRating(VenueModel venue) {
		mReviewCountTextView.setText(UiUtil.getReviewCountText(venue.getReviewCount()));
		mRatingBar.setRating(UiUtil.calculateRating(venue.getRating()));
	}

	
	// MOHAR : Add Comment Button Action 	
		public void addCommentButtonClick()
		{
			CommentFragment commentFragment = new CommentFragment();
			commentFragment.setOnCloseListener(onCommentCloseListnere);
			Bundle bundle = new Bundle();
			if (mBarObject.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)mBarObject;
				bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getFsVenueModel().getVenueId());
			} else {
				VenueModel atnVenueData = (VenueModel)mBarObject;
				bundle.putString(CommentFragment.VENUE_ID, atnVenueData.getVenueId());
			}
			
			bundle.putBoolean(CommentFragment.IS_SMALL_VIEW, false);
			commentFragment.setArguments(bundle);
			addToBackStack(commentFragment);

		}
	
	private void updateBussinessVisitStatus () {
		if (mBarObject.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData) mBarObject;
				
			AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
					HttpUtility.buildGetMethodWithAppParams().appendPath(ApiEndPoints.VENUE_VISIT),
					Method.GET, new AnchorHttpResponceListener() {
				@Override
				public void onSuccessInBackground(JSONObject jsonObject) {}
				@Override
				public void onSuccess(JSONObject jsonObject) {
				}
				@Override
				public void onError(Exception ex) {}
			});

			if(!UserDataPool.getInstance().getUserDetail().getUserId().isEmpty() && UserDataPool.getInstance().getUserDetail().getUserId()!= null)
			{
				anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
				anchorRequest.addText(AtnRegisteredVenueData.VISIT_BUSSINESS_ID,atnVenueData.getBusinessId());
				anchorRequest.execute();
			}
			else
			{
				Log.e("User Not Available ", "User is Unavailable");
			}


			

			}
		}
	
}
