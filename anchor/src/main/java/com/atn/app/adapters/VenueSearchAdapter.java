/**
 * @Copyright CopperMobile 2014.
 * 
 * */
package com.atn.app.adapters;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.VenueModel;
import com.squareup.picasso.Picasso;

//venue search adapter
public class VenueSearchAdapter extends BaseAdapter {

		private Context mContext;
		private List<AtnOfferData> mVenueList = null;
		//private Location mVenueLocation = new Location("");
		public VenueSearchAdapter(Context context,List<AtnOfferData> venueList) {
			this.mContext = context;
			this.mVenueList = venueList;
		}

		@Override
		public int getCount() {
			return mVenueList.size();
		}

		@Override
		public Object getItem(int position) {
			return mVenueList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			VenueHolder venueHolder =null;
			if (view == null) {
				venueHolder =new  VenueHolder();
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.review_row_layout, parent,false);
				
				venueHolder.mVenuePicImageView = (ImageView)view.findViewById(R.id.user_pic_image_view);
				venueHolder.mVenueNameTextView = (TextView)view.findViewById(R.id.venue_name_text_view);
				venueHolder.mDistanceTextView = (TextView)view.findViewById(R.id.venue_distance_text_view);
				view.setTag(venueHolder);
				
			} else {
				venueHolder = (VenueHolder) view.getTag();
			}

			VenueModel venue= (VenueModel) mVenueList.get(position);
			
			if(!TextUtils.isEmpty(venue.getPhoto())){
				Picasso.with(mContext)
				.load(venue.getPhoto())
				.placeholder(R.drawable.empty_photo)
				.resizeDimen(R.dimen.tips_bar_image_size,
						R.dimen.tips_bar_image_size)
				.into(venueHolder.mVenuePicImageView);
			}else{
				venueHolder.mVenuePicImageView.setImageResource(R.drawable.empty_photo);
			}
			
			venueHolder.mVenueNameTextView.setText(venue.getVenueName());
			//calculate distance in mi
			String distance = String.format(mContext.getResources().getString(R.string.venue_distance),venue.getDistance());
			venueHolder.mDistanceTextView.setText(distance);
			
			return view;
		}

		
		private class VenueHolder {
			ImageView mVenuePicImageView;
			TextView mVenueNameTextView;
			TextView mDistanceTextView;
		}
	  }