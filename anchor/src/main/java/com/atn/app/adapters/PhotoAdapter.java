/**
 * Copyright (C) 2014 CopperMobile Pvt. Ltd. 
 * 
 * */
package com.atn.app.adapters;
  
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.fragments.VenueDetailFragment;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.squareup.picasso.Picasso;
  
/**
 * use for display instagram media image in loop View.
 * 
 * */
public class PhotoAdapter extends BaseAdapter {
  
    private List<VenueModel> mVenuesList = null;
    private Context mContext = null;
    private AddFragmentListener mChildFragmentListener = null;
    //40% of screen
    private final int mCellHeight;
    //check if user's finger on view then will not replace data
    public  boolean shouldReplaceData = true; 
    //Media image scrolled
    private SparseIntArray mScrolledPosition = new SparseIntArray();
    
    //holds invisible venue names only use when we replace data to maintain venue name visibility
    private Set<Integer> mInvisibleLabelsItem = new HashSet<Integer>();
    
    //true if notifydataChanged is called by user or new data 
    private boolean isNotifyDataSetChanged = false;
    //fire after data change
    private Handler myHandler;
     
    public void setChildFragmentListener(AddFragmentListener childFragmentListener) {
        this.mChildFragmentListener = childFragmentListener;
    }
  
    /**
     * Constructor for PhotoAdapter
     * @param context application context
     * @param height Row height (Will be 40% of screen)
     * */
    public PhotoAdapter(Context context,int height) {
        this.mContext = context; 
        mCellHeight = height;
        
        
        myHandler = new Handler(context.getMainLooper(), new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				 isNotifyDataSetChanged = false;
				return true;
			}
		});
    }
  
    //replace all data with this data
    //@param venueDataList venue list data
    public void setVenueData(List<VenueModel> venueDataList) {
        if(shouldReplaceData) {
        	if(this.mVenuesList==null) {
        		this.mVenuesList = venueDataList;	
        	} else {
        		this.mVenuesList .clear();
        		this.mVenuesList.addAll(venueDataList);
        	}
        	isNotifyDataSetChanged = true;
        	notifyDataSetChanged();
            myHandler.sendMessage(Message.obtain());
        }
    }
  
    @Override
    public int getCount() {
        if (mVenuesList != null)
            return mVenuesList.size();
        return 0;
    }
  
    @Override
    public Object getItem(int position) {
        if (mVenuesList != null)
            mVenuesList.get(position);
        return null;
    }
  
    @Override
    public long getItemId(int position) {
        return mVenuesList.get(position).getId();
    }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View view = convertView;
        final PhotoHanlder holder;
        if (view == null) {
            holder = new PhotoHanlder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.photo_row_layout, parent, false);
             
            LayoutParams layoutParam = view.getLayoutParams();
            layoutParam.height = mCellHeight;
            view.setLayoutParams(layoutParam);
             
            holder.mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
            holder.mVenueNameTxtView = (TextView) view.findViewById(R.id.venue_name_text_view);
            holder.venueCatImageView = (ImageView) view.findViewById(R.id.photo_venue_cat_iv);
            holder.venueDealImageView = (ImageView) view.findViewById(R.id.photo_venue_deal_iv);
            holder.mPagerAdapter = new MediaPagerAdapter();
            holder.mViewPager.setAdapter(holder.mPagerAdapter);
            holder.mOnPageChangeListner = new OnPageChangeListner();
            holder.mViewPager.setOnPageChangeListener(holder.mOnPageChangeListner);
             
            view.setTag(holder);
        } else {
            holder = (PhotoHanlder) view.getTag();
        }
  
        holder.mPosition = position;
        final VenueModel venue = this.mVenuesList.get(holder.mPosition);
        
		// is called due to notify data change and mInvisibleLabelsItem contain
		// venue id then hide venue name text
        if(isNotifyDataSetChanged&&mInvisibleLabelsItem.contains(venue.getId())) {
        	holder.mVenueNameTxtView.setVisibility(View.INVISIBLE);	
        } else {
        	holder.mVenueNameTxtView.setVisibility(View.VISIBLE);
        	mInvisibleLabelsItem.remove(Integer.valueOf(venue.getId()));
        }
        
        //initially set null so that Page Listener don't not change it visibility
        holder.mViewPager.setOnPageChangeListener(null);
        holder.mOnPageChangeListner.setVenueNameTxtView(null);
        holder.mOnPageChangeListner.setVenue(venue);
        holder.mPagerAdapter.setVenue(venue);
        //set previously visited position if any. 
        holder.mViewPager.setCurrentItem(mScrolledPosition.get(venue.getId()), false);
        holder.mVenueNameTxtView.setText(venue.getVenueName());
        holder.mOnPageChangeListner.setVenueNameTxtView(holder.mVenueNameTxtView);
        holder.mViewPager.setOnPageChangeListener(holder.mOnPageChangeListner);
   
        // MOHAR: show cat on venue image in happening screen
        holder.venueCatImageView.setVisibility(View.GONE);
        String filterQuery = AtnUtils.getFilterQueryString(mContext);
       boolean isAllSelected= SharedPrefUtils.isAllCategoriesSelected(mContext);
		if(!TextUtils.isEmpty(filterQuery) || isAllSelected) {
			String[] query=new String[0];
			if(!TextUtils.isEmpty(filterQuery))
				query = filterQuery.split(",");
			
			if(query.length > 1 || isAllSelected)
			{
				 holder.venueCatImageView.setVisibility(View.VISIBLE);
				// show cat icon if user selected more than one
				
				 holder.venueCatImageView.setImageResource(getResourceIDBaseONCatID(venue.getVenueCategoryId()));
			}
		}
		
		// MOHAR: show Deals if user have Any Deal 
		holder.venueDealImageView.setVisibility(View.GONE);
		if(venue.getAtnBarType() == VenueModel.ATN_BAR_FAV ||venue.getAtnBarType() == VenueModel.ATN_BAR)
		{
			holder.venueDealImageView.setVisibility(View.VISIBLE);
		}
        

		
        
        return view;
    }
  
    private int getResourceIDBaseONCatID(int catID)
    {
    	switch (catID) {
		case 63: // Cuisine
			return R.drawable.icn_interest_restaurant;
		case 314: // Outdoors
			return R.drawable.icn_interest_livemusic;
		case 1: // Arts 
			return R.drawable.icn_interest_concert;
		case 291:  // night life
			return R.drawable.icn_interest_nightlife;
		case 391:  // travel 
			return R.drawable.icn_interest_travel;
		default:
			return R.drawable.icn_interest_concert;

		
		}
    	
    }
    
    //View holder for reuse things
    private static class PhotoHanlder {
        private int mPosition;
        private ViewPager mViewPager;
        private OnPageChangeListner mOnPageChangeListner;
        private TextView mVenueNameTxtView;
        private MediaPagerAdapter mPagerAdapter;
        private ImageView venueCatImageView;
        private ImageView venueDealImageView;
    }
  
    //View pager Listener , we are setting Visible and Invisible on venue name text view.
    private class OnPageChangeListner extends ViewPager.SimpleOnPageChangeListener {
  
        private VenueModel mVenue;
        private TextView mVenueNameTxtView = null;
        
        public void setVenueNameTxtView(TextView venueNameTxtView) {
            this.mVenueNameTxtView = venueNameTxtView;
        }
  
        public void setVenue(VenueModel venue) {
            this.mVenue = venue; 
        }
  
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            shouldReplaceData = state==ViewPager.SCROLL_STATE_IDLE;
        }
  
        @Override
        public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
  
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            
            mScrolledPosition.put(mVenue.getId(), position);
            
            //hide if index is not zero. 
            if(mVenueNameTxtView != null) {
            	if(position==0) {
            		mVenueNameTxtView.setVisibility(View.VISIBLE);
            		mInvisibleLabelsItem.remove(mVenue.getId());
            	} else {
            		mInvisibleLabelsItem.add(mVenue.getId());
            		mVenueNameTxtView.setVisibility(View.INVISIBLE);
            	}
            }
        }
    }
  
    ///View pager Adapter.
    private class MediaPagerAdapter extends PagerAdapter {
         
        private VenueModel mVenue;
        public void setVenue(VenueModel venue) {
            this.mVenue = venue;
            notifyDataSetChanged();
        }
  
        // return none if position is changed.
        @Override
        public int getItemPosition(Object object) {
            Object obj = ((View) object).getTag();
            if ((obj instanceof IgMedia) && mVenue.getInstagramMedia().contains(object)) {
                return POSITION_UNCHANGED;
            }
            if((obj instanceof VenueModel)&&mVenue==obj) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
  
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
  
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.empty_photo);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            if(mVenue.getInstagramMedia().size()>position) {
                final IgMedia igMedia = mVenue.getInstagramMedia().get(position);
                loadImage(imageView, igMedia);
            } else {
                loadImage(imageView, mVenue);
            }
            container.addView(imageView);
            imageView.setClickable(true);
            imageView.setOnClickListener(onPhotoClickListner);
  
            return imageView;
        }
  
        /**
         * Load image for media and set it in view.
         * @imageView image view reference.
         * @igMedia IgMedia Object.
         * */
        private void loadImage(ImageView imageView, IgMedia igMedia) {
            imageView.setTag(igMedia);
  
            if(!TextUtils.isEmpty(igMedia.getImageUrl())) {
            	 Picasso.with(mContext).load(igMedia.getImageUrl())
                 .placeholder(R.drawable.empty_photo).into(imageView);
            }else{
            	imageView.setImageResource(R.drawable.empty_photo);
            }
           
        }
         
        /**
         * Load image for Venue and set it in view.
         * @imageView image view reference.
         * @igMedia venue Object.
         * */
        private void loadImage(ImageView imageView, VenueModel venue) {
           imageView.setTag(venue); 
           if(!TextUtils.isEmpty(venue.getPhoto())) {
        	   Picasso.with(mContext).load(venue.getPhoto())
               .placeholder(R.drawable.empty_photo).into(imageView);   
           }else{
           	imageView.setImageResource(R.drawable.empty_photo);
           }
        }
  
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
  
        @Override
        public int getCount() {
            if (mVenue != null) {
                final int count = mVenue.getInstagramMedia().size();
                return count>0?count:1;
            }
            return 0;
        }
  
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
  
    ///open bar detail screen, on click on particular media image
    private OnClickListener onPhotoClickListner = new OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
        	//calculate screen position for animation
            int[] rootLocation = new int[2];
            v.getLocationOnScreen(rootLocation);
            Object object = v.getTag();
            String url = null;
            String venueID = null;
            Bundle mBundle = null;
            if ((object instanceof IgMedia)) {
                 mBundle = new Bundle();
                url = ((IgMedia) v.getTag()).getImageUrl();
                venueID = ((IgMedia) v.getTag()).getFourSquareId();
                mBundle.putString(VenueDetailFragment.SELECTED_MEDIA_ID,  ((IgMedia) v.getTag()).getImageId());
            } else if ((object instanceof VenueModel)) {
                mBundle = new Bundle();
                url = ((VenueModel)v.getTag()).getPhoto();
                venueID = ((VenueModel)v.getTag()).getVenueId();
            }
            if(!TextUtils.isEmpty(venueID)) {
                mBundle.putString(VenueDetailFragment.FS_VENUE_ID, venueID);
                mBundle.putString(VenueDetailFragment.URL, url);
                mBundle.putInt(VenueDetailFragment.Y_POSITION, rootLocation[1]);
                VenueDetailFragment vm = new VenueDetailFragment();
                vm.setArguments(mBundle);
                if (mChildFragmentListener != null) {
                    mChildFragmentListener.addToBackStack(vm);
                }
            } else {
            	//rare
            	AtnUtils.showToast("Venue is not valid");
            }
        }
    };
}
