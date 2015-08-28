/***
 * @Copyright Coppermobile pvt ldt.
 * */
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.adapters.FollowingAdapter;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.MapCustomPinData;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.listener.AddFragmentFromAdpterListener;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.task.AtnIgMediaHadler;
import com.atn.app.task.AtnIgMediaHadler.VenueLoadListener;
import com.atn.app.task.FollowingBarTask;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;
import com.atn.app.utils.JsonUtils.VenuePicUpload;
import com.squareup.picasso.Picasso;

/**
 * Shows followed venues
 * */
public class FollowingFragment extends AtnBaseFragment implements AddFragmentFromAdpterListener,OnRefreshListener,OnItemClickListener {
	
	private FollowingAdapter mAdapter;
	private ArrayList<AtnOfferData> mFollowedVenueList;
	private ListView mListView;
	private TextView blankTextView = null;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	
	private ActionMode mActionMode;
	
	public static FollowingFragment newInstance() {
		FollowingFragment fragment = new  FollowingFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mFollowedVenueList = new ArrayList<AtnOfferData>();
        mAdapter = new FollowingAdapter(getActivity(), mFollowedVenueList);
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setTitle(R.string.following_title);
		setActionBarAlpha(ACTION_BAR_OPEQUE);
		
		View view = inflater.inflate(R.layout.following_fragment, container, false);
		mSwipeRefreshWidget = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_widget);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2);
		
		blankTextView = (TextView) view.findViewById(R.id.following_blank_text_view);
		blankTextView.setVisibility(View.INVISIBLE);
		mListView = (ListView) view.findViewById(R.id.following_listview);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setMultiChoiceModeListener(new ModeCallback());
		mAdapter.setAddFragmentFromAdpterListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		//register FollowingBarTask listener
		FollowingBarTask.getInstance().registerListener(mBarHandler);
		//register Venue Images load listener
		AtnIgMediaHadler.getInstance().registerListener(venueLoadList);

		return view;
	}

	//called when one venue images loaded from instagram
	VenueLoadListener venueLoadList = new VenueLoadListener() {
		@Override
		public void onVenueLoad(VenueModel fsVenue) {
			FollowingBarTask.getInstance().refreshDataFromDb();
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FollowingBarTask.getInstance().refreshDataFromDb();
		AtnIgMediaHadler.getInstance().load();
	}

	@Override
	public void onPause() {
		mAdapter.cleanSelectionMode();
		finishActionMode();
		super.onPause();
	}
	
	/**
	 * List view action mode listener
	 * **/
	 private class ModeCallback implements ListView.MultiChoiceModeListener {

	        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        	mActionMode = mode;
	            MenuInflater inflater = getActivity().getMenuInflater();
	            inflater.inflate(R.menu.single_item, menu);
	            MenuItem item = menu.findItem(R.id.single_item);
	            item.setIcon(R.drawable.icn_actionbar_delete);
	            mode.setTitle("Delete");
	            return true;
	        }

	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	            return true;
	        }

	        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	            switch (item.getItemId()) {
	            case R.id.single_item:
	               unFavoriteBars(mAdapter.getSelectionList());
	                break;
	            default:
	                break;
	            }
	            return true;
	        }

	        public void onDestroyActionMode(ActionMode mode) {
	        	mAdapter.cleanSelectionMode();
	        }

	        public void onItemCheckedStateChanged(ActionMode mode,
	                int position, long id, boolean checked) {
	        	AtnOfferData offerData = mAdapter.getItem(position);
	        	if(offerData.getDataType()==VenueType.ANCHOR) {			
	    			final AtnRegisteredVenueData venueData = (AtnRegisteredVenueData) offerData;
	    				mAdapter.toggleVenueSelection(venueData.getBusinessId());
	    			} else {
	    				mAdapter.toggleVenueSelection(((VenueModel)offerData).getVenueId());
	    			}
	        	mAdapter.notifyDataSetChanged();
	            setTitle(mode);
	        }
	        private void setTitle(ActionMode mode) {
	            final int checkedCount = mAdapter.getSelectionList().size();
	            mode.setTitle(checkedCount+" Selected");
	        }
	    }
	
    //helper function for closing action mode
	 private void finishActionMode() {
		 if(mActionMode!=null) {
			 mActionMode.finish();
		 }
		 mActionMode = null;
	 }
	 
	 //http request for unfavorite bars.
	 private void unFavoriteBars(final ArrayList<String> selectedIds) {
		final ArrayList<AtnOfferData>  unFavList = new ArrayList<AtnOfferData>();
		
		//comma separated id of anchor bar
		StringBuilder anchorBarsId = new StringBuilder();
		//comma separated id of foursquare bar
		StringBuilder fsVenueIds = new StringBuilder();
		
		for (AtnOfferData venue : mFollowedVenueList) {
			if (venue.getDataType() == VenueType.ANCHOR
					&& selectedIds.contains(((AtnRegisteredVenueData) venue).getBusinessId())) {
				unFavList.add(venue);
				anchorBarsId.append(((AtnRegisteredVenueData) venue).getBusinessId()+",");
			} else if (venue.getDataType() == VenueType.FOURSQUARE
					&& selectedIds.contains(((VenueModel) venue).getVenueId())) {
				unFavList.add(venue);
				fsVenueIds.append(((VenueModel) venue).getAtnBarId()+",");
			}
		}
		 
		 AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
				 HttpUtility.buildBaseUri().appendPath(ApiEndPoints.REMOVE_FAVORITE), 
				 Method.POST, new AnchorHttpResponceListener() {
				@Override
				public void onSuccessInBackground(JSONObject jsonObject) {
					try {
						//update into database.
						if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {
							for (AtnOfferData atnOfferData : unFavList) {
									if (atnOfferData.getDataType() == VenueType.ANCHOR) {
										DbHandler.getInstance().updateBusinessFavoriteStatus(((AtnRegisteredVenueData) atnOfferData).getBusinessId(), false);
									} else {
										ContentValues contentValues = new ContentValues();
										contentValues.put(Atn.Venue.VENUE_ID, ((VenueModel) atnOfferData).getVenueId());
										contentValues.put(Atn.Venue.FOLLOWED, 0);
										Atn.Venue.update(contentValues, AtnApp.getAppContext());
									}
							}
							FollowingBarTask.getInstance().refreshDataFromDb();	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onSuccess(JSONObject jsonObject) {
					AtnUtils.log(jsonObject.toString());
					AnchorProgressDialog.conceal();
					if(JsonUtils.resultCode(jsonObject)!=JsonUtils.ANCHOR_SUCCESS) {
						UiUtil.showToast(getActivity(), JsonUtils.getErrorMessage(jsonObject));
					}
					mAdapter.cleanSelectionMode();
			        finishActionMode();
				}
				
				@Override
				public void onError(Exception ex) {
					UiUtil.showToast(getActivity(), ""+ex.getLocalizedMessage());
					AnchorProgressDialog.conceal();
				}
			});
			
			anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID, 
					UserDataPool.getInstance().getUserDetail().getUserId());
			
			if(anchorBarsId.length()>0)
				anchorRequest.addText(VenuePicUpload.BUSINESS_ID,anchorBarsId.toString());
			
			if(fsVenueIds.length()>0)
				anchorRequest.addText(VenuePicUpload.FS_ANCHOR_VENUE_ID,fsVenueIds.toString());
			
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
        	anchorRequest.execute();
	 }
	 
	 
	 //open venue detail screen
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle dataBundle = new Bundle();
		AtnOfferData offerData = mAdapter.getItem(position);
		if (offerData.getDataType() == VenueType.ANCHOR) {
			final AtnRegisteredVenueData venueData = (AtnRegisteredVenueData) offerData;
			dataBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID,venueData.getBusinessId());
			if(venueData.getFsVenueModel()!=null) {
				dataBundle.putString(VenueDetailFragment.FS_VENUE_ID,  venueData.getFsVenueModel().getVenueId());
			}
			dataBundle.putString(MapCustomPinData.VENUE_NAME, venueData.getBusinessName());
			dataBundle.putBoolean(AtnRegisteredVenueData.IS_REGISTERED_VENUE, true);
		} else {
			dataBundle.putString(VenueDetailFragment.FS_VENUE_ID,  ((VenueModel) offerData).getVenueId());
			dataBundle.putString(MapCustomPinData.VENUE_NAME, ((VenueModel) offerData).getVenueName());
		}
		
		VenueDetailFragment venueDetailFragment = new VenueDetailFragment();
		venueDetailFragment.setArguments(dataBundle);
		addToBackStack(venueDetailFragment);
	}
    
    
	@Override
	public void onDestroyView() {
		//unRegisterListener on Destroy view
		FollowingBarTask.getInstance().unRegisterListener(mBarHandler);
		AtnIgMediaHadler.getInstance().unRegisterListener(venueLoadList);
		super.onDestroyView();
	}
	
	//venue load listener
	private Handler	mBarHandler = new Handler(Looper.getMainLooper(), new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what==1){
				@SuppressWarnings("unchecked")
				List<AtnOfferData> regBars = (List<AtnOfferData>) msg.obj;
					mFollowedVenueList.clear();
					mFollowedVenueList.addAll(regBars);
					mAdapter.notifyDataSetChanged();
			} else {
				AtnUtils.showToast(msg.obj);
			}
			setLoader();
			return false;
		}
	});
	
	//loader status
	private void setLoader() {
		if(FollowingBarTask.getInstance().getStatus()==Status.FINISHED) {
			mSwipeRefreshWidget.setRefreshing(false);
			if(mFollowedVenueList.size()==0) {
				blankTextView.setVisibility(View.VISIBLE);
			} else {
				blankTextView.setVisibility(View.INVISIBLE);
			}
		} else {
			mSwipeRefreshWidget.setRefreshing(true);
		}
	}
	
	@Override
	protected int getScreenName() {
		return R.string.screen_name_user_favorite;
	}

	//called from adapter
	@Override
	public void addFragmentToStack(Fragment newFragment) {
		if(newFragment instanceof TipsDialog) {
			((TipsDialog)newFragment).show(getFragmentManager(), TipsDialog.TIPS_DIALOG);	
		} else {
			addToBackStack((AtnBaseFragment)newFragment);
		}
	}

	@Override
	public void onRefresh() {
		//don't call if already refreshing.
		if(FollowingBarTask.getInstance().getStatus()==Status.FINISHED) {
			FollowingBarTask.getInstance().setLoadFromServer(true);
			FollowingBarTask.getInstance().loadDataFromServer();
			//setLoader();
		}
	}

}
