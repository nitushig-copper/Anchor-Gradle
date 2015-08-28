/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.fragments.VenueDetailFragment;
import com.atn.app.listener.AddFragmentFromAdpterListener;
import com.atn.app.utils.AtnUtils;
import com.squareup.picasso.Picasso;

/**
 * Adapter for following screen
 * */
public class FollowingAdapter extends  ArrayAdapter<AtnOfferData> {

	//row types
	public interface RowType {
		int TOTAL_ROWS = 4;
		int ANCHOR_BAR_WITH_FS_VENUE = 0;
		int ANCHOR_BAR = 1;
		int TIPS = 2;
		int FS_VENUE = 3;
	}
	
	private Context mContext;
	
	//holds selected venue for unfollow
	private ArrayList<String> mSelectedVenueId = new ArrayList<String>();
	/**
	 * Listener for push and pop fragment
	 * */
	private AddFragmentFromAdpterListener childFragmentListener;

	public FollowingAdapter(Context context, ArrayList<AtnOfferData> venueList) {
		super(context, 0, venueList);
		mContext = context;
	}

	//set listener for add fragment to backstack
	public void setAddFragmentFromAdpterListener(
			AddFragmentFromAdpterListener fragmentListener) {
		this.childFragmentListener = fragmentListener;
	}
	
	/**
	 * Toggle selection of given venue id or anchor bar id
	 * */
	public void toggleVenueSelection(String venueId) {
		
		if(mSelectedVenueId.contains(venueId)) {
			mSelectedVenueId.remove(venueId);
		} else {
			mSelectedVenueId.add(venueId);
		}
		notifyDataSetChanged();
	}
	
	//return venue selected for unfollow
	public ArrayList<String> getSelectionList() {
		return mSelectedVenueId;
	}
	
	//clean all selection 
	public void cleanSelectionMode() {
		mSelectedVenueId.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		final AtnOfferData atnOfferData = getItem(position);
		if (atnOfferData.getDataType() == VenueType.ANCHOR) {
			return (((AtnRegisteredVenueData) atnOfferData).getFsVenueModel() != null) 
					? RowType.ANCHOR_BAR_WITH_FS_VENUE
					: RowType.ANCHOR_BAR;
		} else if (atnOfferData.getDataType() == VenueType.TIPS) {
			return RowType.TIPS;
		} else if (atnOfferData.getDataType() == VenueType.FOURSQUARE) {
			return RowType.FS_VENUE;
		}
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return RowType.TOTAL_ROWS;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		View view = convertView;
		
		final AtnOfferData offerData = getItem(position);
		final FollowingHolder holder;
		final int viewType = getItemViewType(position);

		if(view == null) {
			holder = new FollowingHolder();
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(viewType==RowType.ANCHOR_BAR_WITH_FS_VENUE) {
				
				view = inflater.inflate(R.layout.bar_with_images_row_layout, viewGroup, false);
				holder.venueName = (TextView)view.findViewById(R.id.txt_atn_venue_name);
				holder.venueImage = (ImageView)view.findViewById(R.id.img_atn_venue_image);
				
				holder.venueImage.setVisibility(View.VISIBLE);
				holder.venueDirection = (ImageView)view.findViewById(R.id.img_atn_venue_direction);
				
				holder.instaImg0 = (ImageView)view.findViewById(R.id.image1);
				holder.instaImg1 = (ImageView)view.findViewById(R.id.image2);
				holder.instaImg2 = (ImageView)view.findViewById(R.id.image3);
				holder.instaImg3 = (ImageView)view.findViewById(R.id.image4);
				holder.instaImg4 = (ImageView)view.findViewById(R.id.image5);
				view.setTag(holder);
				
			} else if(viewType==RowType.ANCHOR_BAR) {
				
				view = inflater.inflate(R.layout.bar_row_layout, viewGroup, false);
				holder.venueName = (TextView)view.findViewById(R.id.txt_atn_venue_name);
				holder.venueImage = (ImageView)view.findViewById(R.id.img_atn_venue_image);
				holder.venueDirection = (ImageView)view.findViewById(R.id.img_atn_venue_direction);
				view.setTag(holder);
				
			} else if(viewType==RowType.FS_VENUE) {
				
				view = inflater.inflate(R.layout.bar_with_images_row_layout, viewGroup, false);
				holder.venueName = (TextView)view.findViewById(R.id.txt_atn_venue_name);
				//RelativeLayout.LayoutParams param = (LayoutParams) holder.venueName.getLayoutParams();
				//param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				//holder.venueName.setPadding(0, 0, 0, 0);
				holder.venueImage = (ImageView)view.findViewById(R.id.img_atn_venue_image);
				//holder.venueImageOverlay.setVisibility(View.GONE);
				//holder.venueImage.setVisibility(View.GONE);
				holder.venueDirection = (ImageView)view.findViewById(R.id.img_atn_venue_direction);
				holder.instaImg0 = (ImageView)view.findViewById(R.id.image1);
				holder.instaImg1 = (ImageView)view.findViewById(R.id.image2);
				holder.instaImg2 = (ImageView)view.findViewById(R.id.image3);
				holder.instaImg3 = (ImageView)view.findViewById(R.id.image4);
				holder.instaImg4 = (ImageView)view.findViewById(R.id.image5);
				
				view.setTag(holder);
				
			} else if(viewType==RowType.TIPS) {
				
				view = inflater.inflate(R.layout.promotion_row_layout, viewGroup, false);
				holder.promotionImage = (ImageView)view.findViewById(R.id.img_venue_promotion_image);
				holder.promotionName = (TextView)view.findViewById(R.id.txt_offer_name);
				holder.promotionDetail = (TextView)view.findViewById(R.id.txt_offer_detail);
				view.setTag(holder);
			} 
			holder.contentContainer = view.findViewById(R.id.content_overlay);
			
		} else {
			holder = (FollowingHolder) view.getTag();
		}
		
		if(viewType == RowType.ANCHOR_BAR_WITH_FS_VENUE || viewType == RowType.FS_VENUE) {
			setbarDataInView(holder,offerData,view);
		} else if(viewType == RowType.ANCHOR_BAR) {
			final AtnRegisteredVenueData venueData = (AtnRegisteredVenueData) offerData;
			if(TextUtils.isEmpty(venueData.getBusinessImageUrl())){
				holder.venueImage.setImageResource(R.drawable.empty_photo);
			} else {
				Picasso.with(mContext).load(venueData.getBusinessImageUrl())
				.placeholder(R.drawable.empty_photo)
				.resizeDimen(R.dimen.tips_bar_image_size, R.dimen.tips_bar_image_size)
				.into(holder.venueImage);
			}
			
			holder.venueName.setText(venueData.getBusinessName());
			holder.venueDirection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AtnUtils.gotoDirection(mContext, venueData.getBusinessLat(), venueData.getBusinessLng());
				}
			});
		} else if(viewType == RowType.TIPS) {
			final AtnPromotion promotionData = (AtnPromotion) offerData;
			Picasso.with(mContext).load(promotionData.getPromotionLogoUrl())
			.placeholder(R.drawable.empty_photo)
			.into(holder.promotionImage);
			holder.promotionName.setText(promotionData.getPromotionTitle());
			holder.promotionDetail.setText(promotionData.getPromotionDetail());
		}
		return view;
	}


	//set anchor or non anchor bat data in views.
	private void setbarDataInView(FollowingHolder holder,AtnOfferData offerData,View view) {
		
		if(offerData.getDataType()==VenueType.ANCHOR) {			
			final AtnRegisteredVenueData venueData = (AtnRegisteredVenueData) offerData;
			holder.venueName.setText(venueData.getBusinessName());
			
			if(TextUtils.isEmpty(venueData.getBusinessImageUrl())) {
				holder.venueImage.setImageResource(R.drawable.empty_photo);
			} else {
				Picasso.with(mContext).load(venueData.getBusinessImageUrl())
				.placeholder(R.drawable.empty_photo)
				.resizeDimen(R.dimen.tips_bar_image_size, R.dimen.tips_bar_image_size)
				.into(holder.venueImage);
			}
			
			holder.venueDirection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AtnUtils.gotoDirection(mContext,
							venueData.getBusinessLat(),
							venueData.getBusinessLng());
				}
			});
			
			holder.contentContainer.setSelected(mSelectedVenueId.contains(venueData.getBusinessId()));
			if(venueData.getFsVenueModel()!=null) {
				setThumbImages(venueData.getFsVenueModel(),holder,venueData.getBusinessId(),true);
			}
		} else {
					
			final VenueModel  venueModel  = (VenueModel)offerData;			
			if(TextUtils.isEmpty(venueModel.getPhoto())) {
				holder.venueImage.setImageResource(R.drawable.empty_photo);
			} else {
				Picasso.with(mContext).load(venueModel.getPhoto())
				.placeholder(R.drawable.empty_photo)
				.resizeDimen(R.dimen.tips_bar_image_size, R.dimen.tips_bar_image_size)
				.into(holder.venueImage);
			}
			holder.venueName.setText(venueModel.getVenueName());
			holder.venueDirection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AtnUtils.gotoDirection(mContext, venueModel.getLat(), venueModel.getLng());
				}
			});
			holder.contentContainer.setSelected(mSelectedVenueId.contains(venueModel.getVenueId()));
			setThumbImages(venueModel,holder,venueModel.getVenueId(),false);
		}
	}
	
	/**
	 *load Grid images for venue
	 * @param venueModel venue object
	 * @param holder View holder
	 * 
	 * */
	private void setThumbImages(VenueModel venueModel, FollowingHolder holder,
			String venueId, boolean isRegisterVenue) {
		
		holder.instaImg0.setVisibility(View.INVISIBLE);
		holder.instaImg1.setVisibility(View.INVISIBLE);
		holder.instaImg2.setVisibility(View.INVISIBLE);
		holder.instaImg3.setVisibility(View.INVISIBLE);
		holder.instaImg4.setVisibility(View.INVISIBLE);
		
		//iterate only for times.
		int counter = 0;
		for (IgMedia igMedia : venueModel.getInstagramMedia()) {
			
			if(counter==0) {
				holder.instaImg0.setVisibility(View.VISIBLE);
				holder.instaImg0.setTag(R.id.VENUE_ID, venueId);
				holder.instaImg0.setTag(R.id.IS_REGISTERED_VENUE, Boolean.valueOf(isRegisterVenue));
				holder.instaImg0.setTag(R.id.MEDIA_ID, igMedia.getImageId());
				holder.instaImg0.setOnClickListener(onThumbClick);
			
				Picasso.with(mContext).load(igMedia.getThumbnailUrl())
				.placeholder(R.drawable.empty_photo)
				.into(holder.instaImg0);
				
			} else if(counter==1) {
				holder.instaImg1.setOnClickListener(onThumbClick);
				holder.instaImg1.setVisibility(View.VISIBLE);
				holder.instaImg1.setTag(R.id.VENUE_ID, venueId);
				holder.instaImg1.setTag(R.id.IS_REGISTERED_VENUE, Boolean.valueOf(isRegisterVenue));
				holder.instaImg1.setTag(R.id.MEDIA_ID, igMedia.getImageId());
				Picasso.with(mContext).load(igMedia.getThumbnailUrl())
				.placeholder(R.drawable.empty_photo)
				.into(holder.instaImg1);
			} else if(counter==2) {
				holder.instaImg2.setOnClickListener(onThumbClick);
				holder.instaImg2.setVisibility(View.VISIBLE);
				holder.instaImg2.setTag(R.id.VENUE_ID, venueId);
				holder.instaImg2.setTag(R.id.IS_REGISTERED_VENUE, Boolean.valueOf(isRegisterVenue));
				holder.instaImg2.setTag(R.id.MEDIA_ID, igMedia.getImageId());
				Picasso.with(mContext).load(igMedia.getThumbnailUrl())
				.placeholder(R.drawable.empty_photo)
				.into(holder.instaImg2);
			} else if(counter==3) {
				holder.instaImg3.setOnClickListener(onThumbClick);
				holder.instaImg3.setVisibility(View.VISIBLE);
				holder.instaImg3.setTag(R.id.VENUE_ID, venueId);
				holder.instaImg3.setTag(R.id.IS_REGISTERED_VENUE, Boolean.valueOf(isRegisterVenue));
				holder.instaImg3.setTag(R.id.MEDIA_ID, igMedia.getImageId());
				Picasso.with(mContext).load(igMedia.getThumbnailUrl())
				.placeholder(R.drawable.empty_photo)
				.into(holder.instaImg3);
			} else if(counter==4) {
				holder.instaImg4.setOnClickListener(onThumbClick);
				holder.instaImg4.setVisibility(View.VISIBLE);
				holder.instaImg4.setTag(R.id.VENUE_ID, venueId);
				holder.instaImg4.setTag(R.id.IS_REGISTERED_VENUE, Boolean.valueOf(isRegisterVenue));
				holder.instaImg4.setTag(R.id.MEDIA_ID, igMedia.getImageId());
				Picasso.with(mContext).load(igMedia.getThumbnailUrl())
				.placeholder(R.drawable.empty_photo)
				.into(holder.instaImg4);
			}
			if(counter==4) break;
			counter++;
		}
	}
	
	//on grid image thumb click open venue detail screen
	private OnClickListener onThumbClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			    Bundle atnBundle = new Bundle();
				atnBundle.putString(VenueDetailFragment.FS_VENUE_ID,  String.valueOf(v.getTag(R.id.VENUE_ID)));
				atnBundle.putString(VenueDetailFragment.SELECTED_MEDIA_ID, String.valueOf(v.getTag(R.id.MEDIA_ID)));
				atnBundle.putBoolean(AtnRegisteredVenueData.IS_REGISTERED_VENUE, Boolean.valueOf(v.getTag(R.id.IS_REGISTERED_VENUE).toString()));
				VenueDetailFragment venueDetailFragment = new VenueDetailFragment();
				venueDetailFragment.setArguments(atnBundle);
				if (childFragmentListener != null)
					childFragmentListener.addFragmentToStack(venueDetailFragment);
			 
		}
	};
	
	//holder for Following rows view
	private class FollowingHolder {
		View contentContainer;
		ImageView venueImage;
		ImageView venueDirection;
		TextView venueName;
		ImageView promotionImage;
		
		TextView promotionName;
		TextView promotionDetail;
		
		ImageView instaImg0;
		ImageView instaImg1;
		ImageView instaImg2;
		ImageView instaImg3;
		ImageView instaImg4;
	}
}
