/**
 * Copyright Copeprmobile 2014
 * **/
package com.atn.app.fragments;

/**
 * Shows map bubble view.
 * **/
import java.io.IOException;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.MapCustomPinData;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.utils.AtnImageUtils;
import com.atn.app.utils.AtnTask;
import com.atn.app.utils.UiUtil;
import com.squareup.picasso.Picasso;

/**
 * Show Bubble view on map screen
 * */
public class MapOverlayFragment extends DialogFragment {

	//fragment tag
	public static final String MAP_OVERLAY_DIALOG = "MAP_OVERLAY_DIALOG";
	//fragment argument key for venue address
	public static final String BAR_ADDRESS = "BAR_ADDRESS";
	////fragment argument key for venue image url
	public static final String IMAGE_URL = "IMAGE_URL";
	
	private AddFragmentListener childFragmentListener = null;
	
	//for pushing next fragment
	public void setChildFragmentListener(AddFragmentListener childFragmentListener) {
		this.childFragmentListener = childFragmentListener;
	}

    //anchor bubble view
	private View mAtnView;
	private Bitmap mBitmap;
	private String mBarName;
	private String mBarAddress;
	private String mImageUrl;
	///true if its anchor business
	private boolean isAtnBar;
	
	private ImageButton mDismissButton;
	private ImageButton mNextButton;
	
	private RelativeLayout mWhiteBubbleViewContainer;
	
	//factory method for creating new instance
	public static MapOverlayFragment newInstance(Bundle bunlde) {
		MapOverlayFragment f = new MapOverlayFragment();
		f.setArguments(bunlde);
        return f;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = super.onCreateDialog(savedInstanceState);
	    dialog.getWindow().getAttributes().windowAnimations = R.style.map_overlay;
        return dialog;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0,R.style.map_overlay);
        
	    if(getArguments()!=null) {
	    	mBarName = getArguments().getString(MapCustomPinData.VENUE_NAME, "");
	    	mBarAddress = getArguments().getString(BAR_ADDRESS, "");
	    	mImageUrl = getArguments().getString(IMAGE_URL, "");
	    	isAtnBar = false;//getArguments().getBoolean(AtnRegisteredVenueData.IS_REGISTERED_VENUE, false);
	    	mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_image_holder_new);
	    	if(!TextUtils.isEmpty(mImageUrl)) {
	    		new  LoadTask().execute();	
	    	}
	    	//load top two tags
	    	mBarAddress = UiUtil.getTopTwoTags(getActivity(), getArguments().getString(MapCustomPinData.VENUE_ID));
	    }
    }
	
	//load venue image task.
	public class LoadTask extends AtnTask<Void, Void, Bitmap> {

		@Override
		public Bitmap doInBackground(Void... params) {
			Bitmap bitmap = null;
			try {
				bitmap = Picasso.with(AtnApp.getAppContext()).load(mImageUrl).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}
		
		@Override
		public void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(isVisible()&&result!=null&&mAtnView!=null) {
				mBitmap = result;
				if(isAtnBar) {
					int atnPic = getResources().getDimensionPixelSize(R.dimen.atn_bar_image_size);
					ImageView barImage =(ImageView)mAtnView.findViewById(R.id.img_atn_bar_image);
					barImage.setImageBitmap(AtnImageUtils.getMapRoundBitmap(mBitmap, atnPic/2));
				} else {

					((ImageView)mAtnView).setImageBitmap(AtnImageUtils.getMapRoundBitmap(mBitmap, 80));
					
					//((ImageView)mAtnView).setImageBitmap(mBitmap);
				}
			}
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout view = (FrameLayout)inflater.inflate(R.layout.map_over_lay, container, false);
		
		TextView mBarNameTextView = (TextView)view.findViewById(R.id.bar_name_text_view);
		TextView mBarAddressNameTextView = (TextView)view.findViewById(R.id.bar_address_text_view);
		mBarNameTextView.setText(mBarName);
		mBarAddressNameTextView.setText(mBarAddress);
		mWhiteBubbleViewContainer = (RelativeLayout)view.findViewById(R.id.white_bubble_container);
		mDismissButton = (ImageButton)view.findViewById(R.id.venue_popup_dismiss_button);
		mNextButton = (ImageButton)view.findViewById(R.id.venue_popup_open_button);
		mDismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();	
			}
		});
		
		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				openNextScreen();
			}
		});
		
//		mAtnView = view.findViewById(isAtnBar?R.id.atn_bar_view:R.id.non_atn_view);
		mAtnView = view.findViewById(R.id.non_atn_view);
		if(mBitmap!=null) {
			if(isAtnBar) {
				int atnPic = getResources().getDimensionPixelSize(R.dimen.atn_bar_image_size);
				ImageView barImage =(ImageView)mAtnView.findViewById(R.id.img_atn_bar_image);
				barImage.setImageBitmap(AtnImageUtils.getMapRoundBitmap(mBitmap, atnPic/2));
			} else {
				((ImageView)mAtnView).setImageBitmap(AtnImageUtils.getMapRoundBitmap(mBitmap, 80));
			}
		}
		mAtnView.setVisibility(View.VISIBLE);
		mWhiteBubbleViewContainer.setVisibility(View.INVISIBLE);
		
		//start animation after all 
		ViewTreeObserver observer = mWhiteBubbleViewContainer.getViewTreeObserver();
		   observer.addOnGlobalLayoutListener (new OnGlobalLayoutListener () {
		    @SuppressWarnings("deprecation")
			@Override
		     public void onGlobalLayout() {
		    	mWhiteBubbleViewContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		        mAtnView.setVisibility(View.VISIBLE);
				mWhiteBubbleViewContainer.setVisibility(View.VISIBLE);
				mDismissButton.setVisibility(View.VISIBLE);
				mNextButton.setVisibility(View.VISIBLE);
				
				Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(),R.anim.map_bubble_animation);
				mWhiteBubbleViewContainer.startAnimation(scaleAnim);
				Animation fadeIn = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);
				Animation fadeIn2 = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);
				mDismissButton.startAnimation(fadeIn);
				mNextButton.startAnimation(fadeIn2);
		      }
		    });
		return view;
	}

	
	/*
	 * On Click On Bubble Next button open venue detail screen
	 * And  Pass venue info to fragment  
	 * **/
	protected void openNextScreen() {
		
		Bundle bunlde = getArguments();
		
		if(childFragmentListener!=null&&bunlde!=null) {
			if (isAtnBar) {
						
				if(!bunlde.containsKey(VenueDetailFragment.ANCHOR_BAR_ID)) {
					Bundle atnBundle = new Bundle();
					atnBundle.putString(VenueDetailFragment.FS_VENUE_ID,  bunlde.getString(MapCustomPinData.VENUE_ID));
					atnBundle.putString(MapCustomPinData.VENUE_NAME, mBarName);
					atnBundle.putBoolean(AtnRegisteredVenueData.IS_REGISTERED_VENUE, true);
					VenueDetailFragment venueDetailFragment = new VenueDetailFragment();
					venueDetailFragment.setArguments(atnBundle);
					childFragmentListener.addToBackStack(venueDetailFragment);
				} else {
					Bundle atnBundle = new Bundle();
					atnBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID, bunlde.getString(VenueDetailFragment.ANCHOR_BAR_ID));
					OfferDetailFragment barFragment = OfferDetailFragment.newInstance();
					barFragment.setArguments(atnBundle);
					childFragmentListener.addToBackStack(barFragment);
				}			
			} else {
				
				Bundle nonATNBundleData = new Bundle();
				nonATNBundleData.putString(VenueDetailFragment.FS_VENUE_ID,  bunlde.getString(MapCustomPinData.VENUE_ID));
				nonATNBundleData.putString(MapCustomPinData.VENUE_NAME, mBarName);
				VenueDetailFragment venueDetailFragment = new VenueDetailFragment();
				venueDetailFragment.setArguments(nonATNBundleData);
				childFragmentListener.addToBackStack(venueDetailFragment);
			}
		}
	}

}
