/**
 * @CopyRight CopperMobile 2014.
 * */
package com.atn.app.adapters; 

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.fragments.OfferDetailFragment;
import com.atn.app.fragments.TipsDialog;
import com.atn.app.fragments.VenueDetailFragment;
import com.atn.app.listener.AddFragmentFromAdpterListener;
import com.atn.app.utils.AtnUtils;
import com.squareup.picasso.Picasso;

//Anchor bar and tips adapter
public class LoopOfferAdapter extends BaseAdapter {

	//anchor bar and tips data list
	private List<AtnOfferData> mAtnOfferData;
	private LayoutInflater inflater;
	private Context mContext;
	
	/**
	 * Listener for push and pop fragment.
	 * */
	private AddFragmentFromAdpterListener childFragmentListener;
	public void setAddFragmentFromAdpterListener(AddFragmentFromAdpterListener childFragmentListener) {
		this.childFragmentListener = childFragmentListener;
	}

	public LoopOfferAdapter(Context context, List<AtnOfferData> venueList) {
		mContext = context;
		mAtnOfferData = venueList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setBarList(List<AtnOfferData> mAtnOfferData){
		this.mAtnOfferData = mAtnOfferData;
		notifyDataSetChanged();
	}
	

	@Override
	public int getItemViewType(int position) {
		final AtnOfferData atnOfferData = getItem(position);
		if (atnOfferData.getDataType() == VenueType.ANCHOR) {
			return 0;
		} else if (atnOfferData.getDataType() == VenueType.TIPS) {
			return 1;
		} else {
			return 2;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getCount() {
		return mAtnOfferData.size();
	}
	
	@Override
	public AtnOfferData getItem(int i) {
		return mAtnOfferData.get(i);
	}
	
	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final AtnOfferData atnOfferData = getItem(position);
		PromotionDetail holder = null;
			if (view==null)  {
				holder = new PromotionDetail();
				if(atnOfferData.getDataType() == VenueType.ANCHOR) {
					view = inflater.inflate(R.layout.bar_row_layout, parent, false);
					holder.venueImage = (ImageView) view.findViewById(R.id.img_atn_venue_image);
					holder.venueDirection = (ImageView) view.findViewById(R.id.img_atn_venue_direction);
					holder.venueName = (TextView) view.findViewById(R.id.txt_atn_venue_name);
					view.setTag(holder);
				} else if(atnOfferData.getDataType() ==VenueType.TIPS) {
					view = inflater.inflate(R.layout.promotion_row_layout, parent, false);
					holder.promotionImage = (ImageView) view.findViewById(R.id.img_venue_promotion_image);
					holder.promotionName = (TextView) view.findViewById(R.id.txt_offer_name);
					holder.promotionDetail = (TextView) view.findViewById(R.id.txt_offer_detail);
					view.setTag(holder);
				} 
			} else {
				holder = (PromotionDetail) view.getTag();
			}
			
			if(atnOfferData.getDataType() == VenueType.ANCHOR) {
				final AtnRegisteredVenueData venueData =  (AtnRegisteredVenueData) atnOfferData;
				if (TextUtils.isEmpty(venueData.getBusinessImageUrl())) {
					holder.venueImage.setImageResource(R.drawable.empty_photo);
				} else {
					Picasso.with(mContext).load(venueData.getBusinessImageUrl())
							.placeholder(R.drawable.empty_photo)
							.into(holder.venueImage);
				}
				
				holder.venueName.setText(venueData.getBusinessName());
				holder.venueDirection.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AtnUtils.gotoDirection(mContext, venueData.getBusinessLat(), venueData.getBusinessLng());
					}
				});
				
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showVenueOfferFragment(venueData.getBusinessId());
					}
				});
		} else if (atnOfferData.getDataType() == VenueType.TIPS) {

			final AtnPromotion promotionData = (AtnPromotion) atnOfferData;
	
				if (TextUtils.isEmpty(promotionData.getPromotionLogoUrl())) {
					holder.promotionImage.setImageResource(R.drawable.empty_photo);
				} else {
					Picasso.with(mContext)
							.load(promotionData.getPromotionLogoUrl())
							.placeholder(R.drawable.empty_photo)
							.into(holder.promotionImage);
				}

			if (promotionData.getPromotionTitle() != null) {
				holder.promotionName.setText(promotionData.getPromotionTitle());
			}

			if (promotionData.getPromotionDetail() != null) {
				holder.promotionDetail.setText(promotionData
						.getPromotionDetail());
			}
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showOfferEventFragment(promotionData);
				}
			});
		}
		return view;
	}
	
	/**
	 * Show the offer/event details of ATN venue using specified venue id.
	 * 
	 * @param VenueId to get offer/event details
	 */
	private void showVenueOfferFragment(String venueId) {
		Bundle dataBundle = new Bundle();
		dataBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID, venueId);
		OfferDetailFragment offerDetailFragment = OfferDetailFragment.newInstance();
		offerDetailFragment.setArguments(dataBundle);
		
		if (this.childFragmentListener != null)
			this.childFragmentListener.addFragmentToStack(offerDetailFragment);
	}
	
	
	
	/**
	 * Show the promotion details of ATN promotion using specified ATN promotion id.
	 * 
	 * @param VenueId to get offer/event details
	 */
	private void showOfferEventFragment(AtnPromotion promotion) {
		
		Bundle dataBundle = new Bundle();
		dataBundle.putString(AtnPromotion.PROMOTION_ID, promotion.getPromotionId());
		TipsDialog tipsDialog = new TipsDialog();
		tipsDialog.setPromotion(promotion);
		tipsDialog.setArguments(dataBundle);
		
		
		if (this.childFragmentListener != null)
			this.childFragmentListener.addFragmentToStack(tipsDialog);
	}
	
	
	///Rows view holder
	private static class PromotionDetail {
		ImageView venueImage;
		ImageView venueDirection;
		ImageView promotionImage;
		TextView venueName;
		TextView promotionName;
		TextView promotionDetail;
	}

}
