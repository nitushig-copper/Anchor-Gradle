/**
 * @Copyright Copeprmobile 2014
 * **/
package com.atn.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.AtnPromotion;
import com.squareup.picasso.Picasso;

/**
 * Tips adapter
 * */
public class TipsAdapter extends BaseAdapter {

	// tips data list
	private ArrayList<AtnPromotion> mTipsPromotion;
	// Inflater for inflating view
	private LayoutInflater mInflater;
	// application context
	private Context mContext = null;

	/**
	 * TipsAdapter Constructor
	 * 
	 * @param context
	 *            Application
	 * @param atnPromotion
	 *            Tips data
	 * */
	public TipsAdapter(Context context, ArrayList<AtnPromotion> atnPromotion) {
		mTipsPromotion = atnPromotion;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mTipsPromotion == null)
			return 0;
		else
			return mTipsPromotion.size();
	}

	@Override
	public AtnPromotion getItem(int position) {
		return mTipsPromotion.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		TipsHolder holder = null;

		if (view == null) {
			holder = new TipsHolder();

			view = mInflater.inflate(R.layout.promotion_row_layout, parent,
					false);
			holder.mTipImage = (ImageView) view
					.findViewById(R.id.img_venue_promotion_image);
			holder.mTipName = (TextView) view.findViewById(R.id.txt_offer_name);
			holder.mTipDetail = (TextView) view
					.findViewById(R.id.txt_offer_detail);
			view.setTag(holder);
		} else {
			holder = (TipsHolder) view.getTag();
		}

		final AtnPromotion atnPromotion = getItem(position);

		if (TextUtils.isEmpty(atnPromotion.getPromotionLogoUrl())) {
			holder.mTipImage.setImageResource(R.drawable.empty_photo);
		} else {
			Picasso.with(mContext).load(atnPromotion.getPromotionLogoUrl())
					.placeholder(R.drawable.empty_photo).into(holder.mTipImage);
		}

		holder.mTipName.setText(atnPromotion.getPromotionTitle());
		holder.mTipDetail.setText(atnPromotion.getPromotionDetail());

		return view;
	}

	/**
	 * Holder for tips row
	 * */
	private class TipsHolder {
		ImageView mTipImage;
		TextView mTipName;
		TextView mTipDetail;
	}
}
