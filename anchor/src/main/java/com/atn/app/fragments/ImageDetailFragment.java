/*
 * Copyright (C) 2104 Anchor project
 */

package com.atn.app.fragments;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.utils.AtnUtils;
import com.squareup.picasso.Picasso;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements OnClickListener{
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String LIKE_COUNT = "like_count_data";
    private static final String DATE = "date_text";
    private static final String HASH_TAG_TEXT = "hash_tag_data";
    private static final String INDEX = "index";
    
    private String mImageUrl;
    private int likeCount = 0;
    private String dateText;
    private String hashTagText = "";
    
    private ImageView mImageView;
    private TextView mLikeCount;
    private TextView mdateTxView;
    private TextView mHashTagTxView;
    private int mPosition;
    private DialogClickEventListener dialogClickEventListener;
    
   
    /**
	 * @return the dialogClickEventListener
	 */
	public DialogClickEventListener getDialogClickEventListener() {
		return dialogClickEventListener;
	}

	/**
	 * @param dialogClickEventListener the dialogClickEventListener to set
	 */
	public void setDialogClickEventListener(
			DialogClickEventListener dialogClickEventListener) {
		this.dialogClickEventListener = dialogClickEventListener;
	}

	/**
     * Factory method to generate a new instance of the fragment given an image number.
     * @param imageUrl The image url to load
     * @param likeCount Media image like count
     * @param date media image added date
     * @param Media image hash tag
     * @param position in media array
     * @param media image view click listener
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static ImageDetailFragment newInstance(String imageUrl,
    		     									int likeCount,
    		     									String date,
    		     									String hashTag,
    		     									int position,
    		     									DialogClickEventListener dialogClickEventListener) {
      
    	final ImageDetailFragment f = new ImageDetailFragment();
    	f.setDialogClickEventListener(dialogClickEventListener);
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putInt(LIKE_COUNT, likeCount);
        args.putString(DATE, date);
        if(!TextUtils.isEmpty(hashTag)) {
        	args.putString(HASH_TAG_TEXT, hashTag);
        }
        args.putInt(INDEX, position);
        f.setArguments(args);
        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link ImageDetailFragment#newInstance()} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null&&dialogClickEventListener!=null) {
        	mImageUrl = bundle.getString(IMAGE_DATA_EXTRA);
        	likeCount = bundle.getInt(LIKE_COUNT, 0);
        	dateText = AtnUtils.convertDateToDisplayFormat(new Date(Long.valueOf(bundle.getString(DATE))*1000L));
        	if(bundle.containsKey(HASH_TAG_TEXT)) {
        		hashTagText = bundle.getString(HASH_TAG_TEXT);
        	}
        }
        mPosition = bundle.getInt(INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mImageView.setOnClickListener(this);
        mLikeCount = (TextView) v.findViewById(R.id.like_count_txt_view);
        mdateTxView = (TextView) v.findViewById(R.id.date_text_view);;
        mHashTagTxView = (TextView) v.findViewById(R.id.hash_tag_text_view);;
        
        if(dialogClickEventListener==null) {
        	mLikeCount.setVisibility(View.INVISIBLE);
        	mdateTxView.setVisibility(View.INVISIBLE);
        	mHashTagTxView.setVisibility(View.INVISIBLE);
        }
        
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

	        if(dialogClickEventListener!=null) {
	        	 mLikeCount.setText(likeCount+" likes");
	             mdateTxView.setText(dateText);
	             mHashTagTxView.setText(hashTagText);
	             mHashTagTxView.setSelected(true);
	        }
	        
	       if(!TextUtils.isEmpty(mImageUrl)) {
	    	   Picasso.with(getActivity()).load(mImageUrl)
	   		.placeholder(R.drawable.empty_photo).into(mImageView);
	       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            mImageView.setImageDrawable(null);
        }
    }

	@Override
	public void onClick(View v) {
		if(dialogClickEventListener!=null) {
			dialogClickEventListener.onClick(mPosition);
		}
	}
}
