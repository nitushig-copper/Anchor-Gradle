/**
 * @Copyright Coppermobile 2014. 
 * */
package com.atn.app.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atn.app.R;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.listener.DialogClickEventListener;

/**
 * Full Screen image we show here big image of venues
 * */
public class FullScreenImageDialogFragment extends DialogFragment {

	public static final String FULL_SCREEN_IMAGE_FRAGMENT = "FULL_SCREEN_IMAGE_FRAGMENT";
	private List<IgMedia> mMediaList = null;
	private ImagePagerAdapter mPagerAdapter;
	private VenueModel mVenueModel;
	private int mPosition = 0;
	
	// here we are using this as page change listener for notifying venuedetail screen
	private DialogClickEventListener mDialogEventListener = null;
	
	/**
	 * Create Big image pager dialog
	 * @param venueModel FourSqaure venue model
	 * @param selected position show on first time
	 * **/
	public static FullScreenImageDialogFragment newInstance(VenueModel venueModel, int position, DialogClickEventListener dialogEventListener) {
		FullScreenImageDialogFragment dialog = new FullScreenImageDialogFragment();
		dialog.mDialogEventListener = dialogEventListener;
		dialog.mVenueModel = venueModel;
		dialog.mMediaList = venueModel.getInstagramMedia();
		dialog.mPosition = position;
		return dialog;
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0,R.style.help_dialog_style);
        mPagerAdapter = new ImagePagerAdapter(getChildFragmentManager());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout view = (FrameLayout)inflater.inflate(R.layout.full_screen_image, container, false);
		ViewPager pager = (ViewPager)view.findViewById(R.id.view_pager);
		pager.setAdapter(mPagerAdapter);
		ImageButton btn = (ImageButton)view.findViewById(R.id.close_button);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		pager.setCurrentItem(mPosition, false);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(mDialogEventListener!=null) {
					mDialogEventListener.onClick(position);
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		
		// MOHAR: dismiss full image View when click outside of Image 
		view.setOnTouchListener(new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {

	                if(event.getAction() == MotionEvent.ACTION_DOWN)
	    				dismiss();
	                return true;
	            }
	    });
		
		return view;
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
			if (mMediaList != null&&mMediaList.size()>0) {
				return mMediaList.size();
			}
			//for venue photo
			return 1;
		}
		
        @Override
        public Fragment getItem(int position) {
        	if (mMediaList != null&&mMediaList.size()>0) {
        		IgMedia igMedia = mMediaList.get(position);
    			return FullImageFragment.newInstance(igMedia.getImageUrl());
			} else {
				return FullImageFragment.newInstance(mVenueModel.getPhoto());
			}
        }
    }
}
