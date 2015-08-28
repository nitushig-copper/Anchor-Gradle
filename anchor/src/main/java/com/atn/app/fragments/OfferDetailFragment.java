/**
 * Copyright Copeprmobile 2014
 * **/
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.PaletteAsyncListener;
import android.support.v7.graphics.PaletteItem;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.AtnActivity.FacebookLoginListener;
import com.atn.app.adapters.TipsAdapter;
import com.atn.app.component.TrackingListView;
import com.atn.app.constants.ATNConstants;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.listener.OnScrollChangedListener;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnImageUtils;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;
import com.atn.app.webservices.AccessTokenUpdateWebservice;
import com.atn.app.webservices.AccessTokenUpdateWebserviceListener;
import com.atn.app.webservices.AtnVenueShareWebservice;
import com.atn.app.webservices.AtnVenueShareWebserviceListener;
import com.facebook.model.GraphUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
/**
 * Screen shows Anchor bar detail.
 * */
public class OfferDetailFragment extends AtnBaseFragment implements
		FacebookLoginListener, OnItemClickListener {

	//venue address
	private TextView mVenueAddressTextView; 
	//venue contact number if any
	private TextView mVenuePhoneTextView;
	//venue photo
	private ImageView mVenueImageView;
	//open map image view
	private ImageView mGoToMapImageView;
	
	//promotion list view.
	private TrackingListView mVenueOfferListView;
	private TipsAdapter mPromotionsAdapter;
	//promotions
	private ArrayList<AtnPromotion> mPromotions;
	private AtnRegisteredVenueData mAnchorBar;
	///facebook session
	private FacebookSession fbSession;
	
   ///display bar detail address phone number etc.
	private FrameLayout mBarDetailContainer;
	///view display background of venue detail and fill gap between action bar and detail 
	private View mScalableView = null;
	
	private int mActionBarHeight = 0;
	private int mFirstItemHeight = 0;
	private boolean isGapFilled = false;
	
	public static OfferDetailFragment newInstance() {
		OfferDetailFragment offerDetailFragment = new OfferDetailFragment();
		return offerDetailFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionBarHeight = UiUtil.calculateActionBarSize(getActivity());
		mFirstItemHeight = UiUtil.getLoopPhotoSize(getActivity());
		mPromotions = new ArrayList<AtnPromotion>();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setActionBarAlpha(ACTION_BAR_TRANSPARENT);
		View view = inflater.inflate(R.layout.bar_details_fragment, container,false);
		mBarDetailContainer = (FrameLayout)view.findViewById(R.id.bar_detail_container);
		mScalableView = view.findViewById(R.id.blank_view);
		mScalableView.setBackgroundColor(Color.GRAY);
		fbSession = new FacebookSession(getActivity());
		mVenuePhoneTextView = (TextView) view.findViewById(R.id.txt_venue_phone);
		mVenuePhoneTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AtnUtils.doCall(getActivity(), mAnchorBar.getBusinessPhone());
			}
		});
		mVenueAddressTextView = (TextView) view.findViewById(R.id.txt_venue_address);
		mVenueImageView = (ImageView) view.findViewById(R.id.img_venue_image);
		
		//calculate height for venue image view this will be same as loop photo cell.  
		LayoutParams params = mVenueImageView.getLayoutParams();
		params.height = mFirstItemHeight;
		mVenueImageView.setLayoutParams(params);
		
		///set height of space view same as venue image view.
		View containerSpace = view.findViewById(R.id.conatiner_space);
		params = containerSpace.getLayoutParams();
		params.height = mFirstItemHeight;
		containerSpace.setLayoutParams(params);
		
		////
		mGoToMapImageView = (ImageView) view.findViewById(R.id.img_goto_direction);
		mGoToMapImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AtnUtils.gotoDirection(getActivity(),
						mAnchorBar.getBusinessLat(),
						mAnchorBar.getBusinessLng());
			}
		});
		mVenueOfferListView = (TrackingListView) view.findViewById(R.id.lst_venue_offer_details);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((AtnActivity) getActivity()).registerFacebookLoginListener(this);

		Bundle dataBundle = this.getArguments();
		if (dataBundle.containsKey(VenueDetailFragment.ANCHOR_BAR_ID)) {
			String businessId = dataBundle.getString(VenueDetailFragment.ANCHOR_BAR_ID);
			mAnchorBar = DbHandler.getInstance().getAtnBusinessDetail(businessId);
			
			if(mAnchorBar==null){
				popBackStack();
				return;
			}
			
			setTitle(mAnchorBar.getBusinessName());
			mVenueAddressTextView.setText(mAnchorBar.getBusinessStreet() + ","+ mAnchorBar.getBusinessCity());

			if (!TextUtils.isEmpty(mAnchorBar.getBusinessPhone())) {
				mVenuePhoneTextView.setText(mAnchorBar.getBusinessPhone());
			} else {
				mVenuePhoneTextView.setVisibility(View.INVISIBLE);
			}

			mBarDetailContainer.post(new Runnable() {	
				@Override
				public void run() {
					setUpViews();
				}
			});
			
			//load image and get prominent color after that
			Picasso.with(getActivity()).load(mAnchorBar.getBusinessImageUrl()).into(mVenueImageView, new Callback(){
				@Override
				public void onError() {}
				@Override
				public void onSuccess() {
					Bitmap bitmap = AtnImageUtils.drawableToBitmap(mVenueImageView.getDrawable());
					if(bitmap!=null){
						Palette.generateAsync(bitmap, paletteAsynchlistener);
					}
				}
			});
		}
		
		updateBussinessVisitStatus();
	}

	///palette generate listener
	private PaletteAsyncListener paletteAsynchlistener = new PaletteAsyncListener() {
		@Override
		public void onGenerated(Palette palette) {
			List<PaletteItem> list = palette.getPallete();
			if(list!=null&&list.size()>0) {
				mScalableView.setBackgroundColor(list.get(0).getRgb());
			}
		}
	};
	
	
	public void reloadData() {
		Bundle dataBundle = this.getArguments();
		if (dataBundle.containsKey(VenueDetailFragment.ANCHOR_BAR_ID)) {
			String businessId = dataBundle.getString(VenueDetailFragment.ANCHOR_BAR_ID);
			AtnRegisteredVenueData mAnchorBar = DbHandler.getInstance().getAtnBusinessDetail(businessId);
			if(mAnchorBar!=null && mAnchorBar.getBulkPromotion()!=null && mAnchorBar.getBulkPromotion().size()>0){
				mPromotions.clear();
				mPromotions.addAll(mAnchorBar.getBulkPromotion());
				if (mPromotionsAdapter != null) {
					mPromotionsAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	//set up view
	private void setUpViews() {
		mPromotions.clear();
		mPromotions.addAll(mAnchorBar.getBulkPromotion()); 
		
		Space space = new Space(getActivity());
		if (mPromotionsAdapter == null) {
			mPromotionsAdapter = new TipsAdapter(getActivity(), mPromotions);
		}
		LayoutParams param = new ListView.LayoutParams(LayoutParams.MATCH_PARENT,mBarDetailContainer.getHeight());
		space.setLayoutParams(param);
		mVenueOfferListView.addHeaderView(space);
		mVenueOfferListView.setAdapter(mPromotionsAdapter);
		mVenueOfferListView.setOnItemClickListener(OfferDetailFragment.this);
		mPromotionsAdapter.notifyDataSetChanged();
		invalidateOptionMenu();
		
		LayoutParams params2 = mScalableView.getLayoutParams();
		params2.height = mBarDetailContainer.getHeight()-UiUtil.getLoopPhotoSize(getActivity());
		mScalableView.setLayoutParams(params2);
		
		mVenueOfferListView.setOnScrollChangedListener(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged(ViewGroup source, int l, int t, int oldl,
					int oldt) {
				handleScroll(source,mVenueOfferListView.getScroll());
			}
		});
	}
	
     
	//handle scrolling of List to change action bar color and show 
	//Scale animation to fill the gap between view and header view and action bar
	@SuppressLint("NewApi")
	private void handleScroll(ViewGroup source, int top) {
		int maxScroll = mFirstItemHeight - mActionBarHeight;
		int actuallScroll = Math.min(maxScroll, top);
		mVenueImageView.setTranslationY(-actuallScroll / 3.0f);
		mBarDetailContainer.setTranslationY(-actuallScroll);
		if (actuallScroll == maxScroll && !isGapFilled) {
			isGapFilled = true;
			mScalableView.animate().scaleY(3.5f).setDuration(250).start();
		} else if (top < maxScroll && isGapFilled) {
			isGapFilled = false;
			mScalableView.animate().scaleY(1.0f).setDuration(250).start();
		}
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		if (mAnchorBar != null) {
			inflater.inflate(R.menu.bar_detail_menu, menu);
			menu.removeItem(R.id.atn_bar_detail);
			MenuItem favMenuItem = menu.findItem(R.id.fav_menu_item);
			favMenuItem.setIcon(mAnchorBar.isFavorited() ? R.drawable.icn_actionbar_star_active
							: R.drawable.icn_actionbar_star);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.fav_menu_item:
		{
			if(mAnchorBar.isFavorited())
				showAlertForFavoriteButtonClick();
			else
				onFavoriteButtonClick();
		}
			return true;
		case R.id.share_menu_item:
			if (!UserDataPool.getInstance().isUserLoggedIn()) {
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

	//favorite or unfavorite bar 
	private void onFavoriteButtonClick() {

		/**
		 * If user is not logged-in, show login dialog before posting comment.
		 */
		if (!UserDataPool.getInstance().isUserLoggedIn()) {
			AtnApp.showLoginScreen(getActivity());
			return;
		}
		
		AnchorProgressDialog.show(getActivity(), R.string.please_wait);
		Uri.Builder uriBuilder = HttpUtility.buildBaseUri().appendPath(ApiEndPoints.BUSINESS)
				.appendPath(mAnchorBar.isFavorited() ? 
						ApiEndPoints.ANCHOR_BAR_UNFAVORITE:ApiEndPoints.ANCHOR_BAR_FAVORITE);
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), uriBuilder, Method.GET,
				new AnchorHttpResponceListener() {
					@Override
					public void onSuccessInBackground(JSONObject jsonObject) {}
					@Override
					public void onSuccess(JSONObject jsonObject) {
						AnchorProgressDialog.conceal();
						if (JsonUtils.resultCode(jsonObject) == JsonUtils.ANCHOR_SUCCESS) {
							mAnchorBar.setFavorited(!mAnchorBar.isFavorited());
							DbHandler.getInstance().updateBusinessFavoriteStatus(mAnchorBar.getBusinessId(), true);
							invalidateOptionMenu();
							if(mAnchorBar.isFavorited())
								UiUtil.showAlertDialog(getActivity(), mAnchorBar.getBusinessName()+" has been added to your travel log.");
							
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
		anchorRequest.addText(JsonUtils.VenuePicUpload.BUSINESS_ID,mAnchorBar.getBusinessId());
		anchorRequest.execute();
	}

	@Override
	public void onDestroyView() {
		((AtnActivity) getActivity()).unRegisterFacebookLoginListener();
		super.onDestroyView();
	}


	/**
	 * Shows a dialog to ask user whether he wants to share this venue on
	 * his/her Facebook wall.
	 */
	private void showFacebookShareDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.share_title);
		alertDialog.setMessage(R.string.share_on_facebook);
		alertDialog.setPositiveButton(R.string.dialog_yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (TextUtils.isEmpty(fbSession.getAccessToken())) {
							showFacebookLoginDialog();
							return;
						}

						AnchorProgressDialog.show(getActivity(), R.string.please_wait);
						AtnVenueShareWebservice shareService = new AtnVenueShareWebservice(
								UserDataPool.getInstance().getUserDetail()
										.getUserId(), mAnchorBar
										.getBusinessId());
						shareService
								.setAtnVenueShareWebServiceListener(mAtnVenueShareWebserviceListener);
						shareService.shareVenue();
					}
				});

		alertDialog.setNegativeButton(R.string.dialog_no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING
					}
				});

		alertDialog.show();
	}

	/**
	 * Shows a dialog to login to Facebook.
	 */
	private void showFacebookLoginDialog() {
		((AtnActivity) getActivity()).postOnFacebookClick(this);
	}

	/**
	 * Listens for web service response when share icon is clicked.
	 */
	private AtnVenueShareWebserviceListener mAtnVenueShareWebserviceListener = new AtnVenueShareWebserviceListener() {
		@Override
		public void onSuccess(String message) {
			
			AnchorProgressDialog.conceal();
			
			if (message != null
					&& message.contains(ATNConstants.OAUTH_EXCEPTION)) {
				AtnApp.showMessageDialog(getActivity(), message, false);
			} else {
				AtnApp.showMessageDialog(getActivity(), getActivity()
						.getString(R.string.venue_shared_on_wall), false);
			}
		}

		@Override
		public void onFailed(int errorCode, String errorMessage) {
			AnchorProgressDialog.conceal();
		}
	};
	
	/**
	 * Listen for the Facebook access token update web service notifications. If
	 * access token is updated then share the business on Facebook wall.
	 */
	private AccessTokenUpdateWebserviceListener mAccessTokenUpdateWebserviceListener = new AccessTokenUpdateWebserviceListener() {

		@Override
		public void onSuccess(String message) {
			UserDataPool.getInstance().getUserDetail()
					.setUserFbToken(fbSession.getAccessToken());
			AtnVenueShareWebservice shareService = new AtnVenueShareWebservice(
					UserDataPool.getInstance().getUserDetail().getUserId(),
					mAnchorBar.getBusinessId());
			shareService
					.setAtnVenueShareWebServiceListener(mAtnVenueShareWebserviceListener);
			shareService.shareVenue();
		}

		@Override
		public void onFailed(int errorCode, String errorMessage) {
			AnchorProgressDialog.conceal();
			AtnApp.showMessageDialog(getActivity(), errorMessage, false);
		}
	};

	@Override
	public void onFacebookLogin(boolean isSuccess, String message) {
		if (isSuccess) {
			UserDataPool.getInstance().getUserDetail().setUserFbToken(fbSession.getAccessToken());
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
			AccessTokenUpdateWebservice tokenUpdateService = new AccessTokenUpdateWebservice(
					UserDataPool.getInstance().getUserDetail().getUserId());
			tokenUpdateService.setUpdateAccessTokenWebserviceListener(mAccessTokenUpdateWebserviceListener);
			tokenUpdateService.updateFacebookToken(fbSession.getAccessToken(),null);
		}
	}

	@Override
	public void onUserInfoFetch(GraphUser user) {
		// TODO Auto-generated method stub
	}

	/**
	 * Show the promotion details of ATN promotion using specified ATN promotion
	 * id.
	 * @param VenueId
	 *            to get offer/event details
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AtnPromotion mPromotion = (AtnPromotion) mVenueOfferListView.getItemAtPosition(position);
		if(mPromotion!=null) {
			Bundle dataBundle = new Bundle();
			dataBundle.putString(AtnPromotion.PROMOTION_ID,mPromotion.getPromotionId());
			TipsDialog tipsDialog = new TipsDialog();
			tipsDialog.setPromotion(mPromotion);
			tipsDialog.setArguments(dataBundle);
			tipsDialog.show(getFragmentManager(),TipsDialog.TIPS_DIALOG);
		}
	}

	@Override
	protected int getScreenName() {
		return R.string.screen_name_bar_detail;
	}
	
	private void updateBussinessVisitStatus () {
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
				HttpUtility.buildGetMethodWithAppParams().appendPath(ApiEndPoints.VENUE_VISIT),
				Method.GET, new AnchorHttpResponceListener() {
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {
			}
			@Override
			public void onError(Exception ex) {
				
			}
		});

		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		anchorRequest.addText(AtnRegisteredVenueData.VISIT_BUSSINESS_ID,mAnchorBar.getBusinessId());
		
        
		anchorRequest.execute();
	}

}
