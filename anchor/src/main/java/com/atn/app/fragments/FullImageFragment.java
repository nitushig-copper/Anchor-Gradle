/***
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.atn.app.R;
import com.google.android.gms.plus.model.people.Person.Cover.Layout;
import com.squareup.picasso.Picasso;

//show full image from venue detail screen
public class FullImageFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private ImageView mImageView;
   
	/**
     * Factory method to generate a new instance of the fragment given an image url.
     *
     * @param imageUrl The image url to load 
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FullImageFragment newInstance(String imageUrl) {
      
    	final FullImageFragment f = new FullImageFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);
        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public FullImageFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link ImageDetailFragment#newInstance()} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null) {
        	mImageUrl = bundle.getString(IMAGE_DATA_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());  
        return mImageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	       if(!TextUtils.isEmpty(mImageUrl)) {
	    	   Picasso.with(getActivity()).load(mImageUrl)
	   		.placeholder(R.drawable.empty_photo).into(mImageView);
	       } else {
	    	 mImageView.setImageResource(R.drawable.empty_photo);  
	       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            mImageView.setImageDrawable(null);
        }
    }
}
