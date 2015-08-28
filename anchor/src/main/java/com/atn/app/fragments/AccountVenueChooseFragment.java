/**
 * @Copyright 2014 Coppermobile.
 * 
 * **/
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.utils.TypefaceUtils;
import com.atn.app.webservices.BusinessSubscribeWebservice;
import com.atn.app.webservices.BusinessSubscribeWebserviceListener;
import com.atn.app.webservices.WebserviceError;
import com.atn.app.webservices.WebserviceType.ServiceType;


/**
 * Creates fragment to show all the venues so that user can receive push
 * notifications from the selected venues.
 * 
 */
public class AccountVenueChooseFragment extends AtnBaseFragment implements OnItemClickListener {
	
	private ListView lstBusiness;
	private VenueAdapter adapter;
	private ProgressDialog progressDialog;
	private AtnRegisteredVenueData mRegBar = null;
	private BusinessSubscribeWebservice subscribeService = null;
	public static AccountVenueChooseFragment newInstance(AddFragmentListener chilFragmentListener) {
		AccountVenueChooseFragment fragment = new  AccountVenueChooseFragment();
		fragment.setChildFragmentListener(chilFragmentListener);
		return fragment;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setTitle(R.string.venue_i_choose);
		View view = inflater.inflate(R.layout.account_venue_choose_fragment, container, false);
		lstBusiness = (ListView)view.findViewById(R.id.lst_favorite_business);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayList<AtnOfferData> venueData = DbHandler.getInstance().getBulkFavoriteVenueDetails();
		adapter = new VenueAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, venueData);
		lstBusiness.setOnItemClickListener(this);
		lstBusiness.setAdapter(adapter);
	}
	
	
	private class VenueAdapter extends ArrayAdapter<AtnOfferData> {

		private Context mContext;
		public VenueAdapter(Context context, int resource,
				List<AtnOfferData> objects) {
			super(context, resource, objects);	
			this.mContext = context;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			CheckedTextView view = (CheckedTextView)convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (CheckedTextView)inflater.inflate(R.layout.category_list_item, parent, false);
				view.setBackgroundColor(Color.WHITE);
				LayoutParams param = view.getLayoutParams();
				param.height = mContext.getResources().getDimensionPixelSize(R.dimen.anchor_account_row_height);
				TypefaceUtils.applyTypeface(getActivity(), view, TypefaceUtils.ROBOTO_MEDIUM);
			}
			final AtnRegisteredVenueData atnVenue = (AtnRegisteredVenueData) getItem(position);
			view.setText(atnVenue.getBusinessName());
			view.setChecked(atnVenue.isSubscribed());
			return view;
		}	
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(subscribeService==null) {
			subscribeService = new BusinessSubscribeWebservice();
			subscribeService.setBusinessSubscribeWebserviceListener(subscribeServiceListener);
		}
		
		mRegBar = (AtnRegisteredVenueData) adapter.getItem(position);
		if (mRegBar.isSubscribed()) {
			showUnsubscribeDialog(mRegBar.getBusinessId());
		} else {
			progressDialog = ProgressDialog.show(getActivity(),
					getActivity().getString(R.string.please_wait),
					getActivity().getString(R.string.please_wait));
			subscribeService.subscribeBusiness(mRegBar.getBusinessId());
		}
	}
	
	
	/**
	 * Shows an alert dialog to ask user whether he want to unsubscribe to
	 * selected venue or not.
	 * 
	 * @param businessId
	 *            to unsubscribe.
	 */
	private void showUnsubscribeDialog(final String businessId) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setMessage(getActivity().getResources().getString(
				R.string.unsubscribe_atn_venue));

		alertDialog.setPositiveButton(
				getActivity().getResources().getString(R.string.dialog_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog = ProgressDialog.show(getActivity(),
								getActivity().getString(R.string.please_wait),
								getActivity().getString(R.string.please_wait));
						subscribeService.unsubscribeBusiness(businessId);
					}
				});
		alertDialog.setNegativeButton(
				getActivity().getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		alertDialog.show();
	}
	
	/**
	 * Listens for subscribe/unsubscribe web service response from server.
	 */
	private BusinessSubscribeWebserviceListener subscribeServiceListener = new BusinessSubscribeWebserviceListener() {
		@Override
		public void onSuccess(ServiceType serviceType, String businessId) {
			mRegBar.setSubscribed(!mRegBar.isSubscribed());
			DbHandler.getInstance().updateSubscriptionStatus(businessId,
					mRegBar.isSubscribed());
			adapter.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}

		@Override
		public void onFailed(ServiceType serviceType, int errorCode,
				String errorMessage) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			switch (errorCode) {
			case WebserviceError.INTERNET_ERROR:
				AtnApp.showMessageDialog(getActivity(), getActivity()
						.getString(R.string.no_internet_available), false);
				break;
			case WebserviceError.UNKNOWN_ERROR:
				AtnApp.showMessageDialog(getActivity(), getActivity()
						.getString(R.string.unknown_webservice_error), false);
				break;
			default:
				AtnApp.showMessageDialog(getActivity(), errorMessage, false);
				break;
			}
		}
	};
}
