/**
 * @Copyright coppermobile pvt. ltd. 2014
 * **/
package com.atn.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.utils.SharedPrefUtils;
/**
 * Show help screen on first time login user.
 * **/
@Deprecated
public class HelpDialog extends DialogFragment implements OnPageChangeListener ,OnClickListener{

	public static final String HELP_DIALOG = "HELP_DIALOG";
	private static final String RIGHT_MARGIN = "RIGHT_MARGIN";
	
	private DialogClickEventListener mButtonListener;
	
	private ViewPager mPager;	
	private HelpPagerAdapter mPagerAdapter = null;
	private ImageView mFirstIndicatorImage;
	private ImageView mSecondIndicatorImage;
	private Button mAddPhotoButton;
	private Button mAddReviewButton;
	
	private int mBottomMargin = 0;
	
	public static HelpDialog newInstance(int margin,DialogClickEventListener mButtonListener) {
		Bundle bunlde = new Bundle();
		bunlde.putInt(RIGHT_MARGIN, margin);
		HelpDialog fragment = new HelpDialog();
		fragment.mButtonListener = mButtonListener;
		fragment.setArguments(bunlde);	
		return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0,R.style.help_dialog_style);
        mPagerAdapter = new HelpPagerAdapter(getChildFragmentManager());
        mBottomMargin = getArguments().getInt(RIGHT_MARGIN, 0);
        SharedPrefUtils.setUserFirstTimeLoginStatus(getActivity(), false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout)inflater.inflate(R.layout.help_layout, container, false);
		
		View bottomContainer = view.findViewById(R.id.bottom_button_container);
		LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)bottomContainer.getLayoutParams();
		param.setMargins(0, 0, mBottomMargin, 0);
		bottomContainer.setLayoutParams(param);
		
		mFirstIndicatorImage = (ImageView)view.findViewById(R.id.first_image);
		mSecondIndicatorImage = (ImageView)view.findViewById(R.id.second_image);
		mAddPhotoButton = (Button)view.findViewById(R.id.help_add_photos_button);
		mAddReviewButton = (Button)view.findViewById(R.id.help_add_reviews_button);
		view.findViewById(R.id.help_close_hint_button).setOnClickListener(this);
		mAddPhotoButton.setVisibility(View.INVISIBLE);
		mFirstIndicatorImage.setSelected(true);
		mAddPhotoButton.setOnClickListener(this);
		mAddReviewButton.setOnClickListener(this);
		
		mPager = (ViewPager)view.findViewById(R.id.help_view_pager);
	    mPager.setAdapter(mPagerAdapter);
	    mPager.setOffscreenPageLimit(2);
	    mPager.setOnPageChangeListener(this);
		
		return view;
	}
	
	
	private class ContentFragment extends Fragment {
		public static final String POSITION = "position";
		private int mPosition = 0;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			mPosition = getArguments().getInt(POSITION, 0);
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			LinearLayout view = (LinearLayout)inflater.inflate(R.layout.help_content_layout, container, false);
			
			ImageView mImageView = (ImageView)view.findViewById(R.id.help_big_image_view);
			TextView mHelpNameTextView = (TextView)view.findViewById(R.id.hint_name_text_view);
			TextView mHelpDescriptionTextView = (TextView)view.findViewById(R.id.hint_description_text_view);
			
			mImageView.setImageResource(mPosition==0?R.drawable.icn_menu_addreview_hint_big:R.drawable.icn_menu_addphoto_hint_big);
			mHelpNameTextView.setText(mPosition==0?R.string.add_review_text:R.string.add_photo_text);
			
			if(mPosition==0) {
				mHelpDescriptionTextView.setText(R.string.help_text_add_review_part_1);
			} else {
				mHelpDescriptionTextView.setText(R.string.help_text_add_photo);
			}
			return view;
		}
		
	}
	
	 private class HelpPagerAdapter extends FragmentStatePagerAdapter {
	        public HelpPagerAdapter(FragmentManager fm) {
	            super(fm); 
	        }
			@Override
			public int getCount() {
				return 2;
			}
	        @Override
	        public Fragment getItem(int position) {
	        	Bundle bunlde = new Bundle();
				bunlde.putInt(ContentFragment.POSITION, position);
				ContentFragment fragment = new ContentFragment();
				fragment.setArguments(bunlde);
				return fragment;
	        }
	    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels){
	}

	@Override
	public void onPageSelected(int position) {
		mFirstIndicatorImage.setSelected(position==0);
		mSecondIndicatorImage.setSelected(position==1);
		
		if(position==0){
			mAddPhotoButton.setVisibility(View.INVISIBLE);
			mAddReviewButton.setVisibility(View.VISIBLE);
		}else{
			mAddPhotoButton.setVisibility(View.VISIBLE);
			mAddReviewButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if(mButtonListener!=null){
			mButtonListener.onClick(v.getId());
		}
		
//		switch (v.getId()) {
//	
//		case R.id.help_add_photos_button:
//			if(mButtonListener!=null){
//				mButtonListener.onClick(R.id.help_add_photos_button);
//			}
//			break;
//		case R.id.help_add_reviews_button:
//
//			if(mButtonListener!=null){
//				mButtonListener.onClick(R.id.help_add_reviews_button);
//			}
//			
//			break;
//		case R.id.help_close_hint_button:
//			if(mButtonListener!=null){
//				mButtonListener.onClick(R.id.help_add_reviews_button);
//			}
//			break;
//		default:
//			break;
//		}
		
	}

	
	
}




