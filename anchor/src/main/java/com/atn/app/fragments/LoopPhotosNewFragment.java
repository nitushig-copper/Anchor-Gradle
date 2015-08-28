/**
 * Copyright (C) 2014 CopperMobile Pvt. Ltd. 
 * 
 * */
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.atn.app.R;
import com.atn.app.adapters.PhotoAdapter;
import com.atn.app.datamodels.AnchorCategory;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.instagram.InstagramDialog.InstagramLoginListener;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.provider.Atn;
import com.atn.app.service.SynchService;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.TypefaceUtils;
import com.atn.app.utils.UiUtil;


/**
 * Happening now screen show near by venue and instagram media
 * */
public class LoopPhotosNewFragment extends AtnBaseFragment implements OnRefreshListener {

	private static final int LOAD_MORE_VENUE_LIMIT = 10;

	private static final int MOST_RECENT_TAG = 1020;
	private static final int NEAR_BY_TAG = 1021;	

	private SwipeRefreshLayout mSwipeRefreshWidget;
	private PhotoAdapter mAdapter;

	private TextView txtLoadingPlaceholder;
	//venue load limit
	private int venueLoadLimit = LOAD_MORE_VENUE_LIMIT;
	//list view footer for load more venues
	private Button loadMoreButton;
	private Button mostRecentBtn;
	private Button nearByBtn;
	private boolean shouldUpdateCount = true;
	private Boolean isNeedToshowAlrt;
	private int mListScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	public  LoopPhotosNewFragment() {} 

	private AsyncTask<Integer, Void, List<VenueModel>> mLoadTask = null;
	private Uri mUri = Atn.Venue.CONTENT_URI;

	private String mSortOrder = Atn.Venue.DEFAULT_SORT_ORDER;

	private Boolean isNearBySelected=false;
	
	private View mostRecentView;
	private View nearByView;

	List<VenueModel> mVenueList ;//= new ArrayList<VenueModel>();

	//factory method for create new instance
	public static AtnBaseFragment newInstance() {
		LoopPhotosNewFragment loop = new LoopPhotosNewFragment();
		return loop;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mAdapter = new PhotoAdapter(getActivity().getApplicationContext(),
				UiUtil.getLoopPhotoSize(getActivity()));
	}   

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		isNeedToshowAlrt=true;
		shouldUpdateCount = true;
		setActionBarAlpha(ACTION_BAR_OPEQUE);
		setTitle(R.string.happaning_now_text_screen);

		View view = inflater.inflate(R.layout.loop_photo_fragments, container, false);

		mSwipeRefreshWidget = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_widget);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2);
		txtLoadingPlaceholder = (TextView)view.findViewById(R.id.insta_image_loading_title);
		ListView mVenueListView = (ListView)view.findViewById(R.id.content);
		loadMoreButton  = (Button)inflater.inflate(R.layout.load_more_view, mVenueListView, false);
		mVenueListView.addFooterView(loadMoreButton);
		loadMoreButton.setVisibility(View.INVISIBLE);
		mVenueListView.setAdapter(mAdapter);
		loadMoreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AtnUtils.log("click");
				loadMoreButton.setEnabled(false);
				//if(venueLoadLimit<=mAdapter.getCount()) {
				venueLoadLimit = mAdapter.getCount()+10;
				venueLoadLimit = venueLoadLimit<LOAD_MORE_VENUE_LIMIT?LOAD_MORE_VENUE_LIMIT:venueLoadLimit;
				loadMoreButton.setText("Loading...");
				loadData();
				//}
			}
		});

		mVenueListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mListScrollState = scrollState;
				//AtnUtils.log("IDLE:"+(mListScrollState==OnScrollListener.SCROLL_STATE_IDLE));
				refreshDataIfNeeded();
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});


		View headerView = inflater.inflate(R.layout.happening_now_header_view, mVenueListView, false);
		mVenueListView.addHeaderView(headerView, null, false);

		mostRecentView = (View) headerView.findViewById(R.id.happening_now_most_recent_bottom_view);
		nearByView = (View) headerView.findViewById(R.id.happening_now_near_by_bottom_view);
//		mostRecentRl.setOnClickListener(headerClickListner);
//		nearByRl.setOnClickListener(headerClickListner);

		mostRecentBtn = (Button)headerView.findViewById(R.id.happening_now_most_recent_btn);
		nearByBtn = (Button)headerView.findViewById(R.id.happening_now_near_by_btn);

		//synch service  Broadcast message like start stop or fail to load venues
		IntentFilter filter = new IntentFilter();
		filter.addAction(SynchService.ACTION_SERVICE);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
		getActivity().getContentResolver().registerContentObserver(mUri, true,mObserver);
		AtnUtils.hideKeyboard(getActivity());
		
		mostRecentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mostRecentClicked();
				
			}
		});
		
		nearByBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nearByClicked();
			}
		});

		isNearBySelected = SharedPrefUtils.getSelectedState(getActivity());
		
		
		if(isNearBySelected)
			nearByClicked();
		else
			mostRecentClicked();
		
//		loadData();
		return view;
	}

	@Override
	public void onDestroyView() {
		isNeedToshowAlrt=false;
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		shouldUpdateCount = false;
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
		cancelTask();
		super.onDestroyView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	
	@Override
	public void onRefresh() {
		if (!SynchService.IS_RUNNING) {

			if(SharedPrefUtils.fSVenueLoadStatus(getActivity())) {
				if(TextUtils.isEmpty(InstagramSession.getToken(getActivity()))) {
					showInstagramLoginDialog();
					mSwipeRefreshWidget.setRefreshing(false);
				} else {
					//refresh media images only
					AtnUtils.runSynchService(getActivity(), SynchService.Command.REFRESH_MEDIA);
				}
			} else {
				//Reload venues
				AtnUtils.runSynchService(getActivity(), SynchService.Command.RELOAD_VENUE);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.single_item, menu);
		MenuItem item = menu.findItem(R.id.single_item);
		item.setTitle("Filter Venues");
		item.setIcon(R.drawable.icn_filter);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.single_item:
			//open choose interest screen
			addToBackStack(ChooseInterestFragment.newInstance());
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//listen for synch service events.
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SynchService.ACTION_SERVICE)) {

				if(intent.getIntExtra(SynchService.STATUS, 0)==SynchService.SUCCESS) {
					//
				}else if(intent.getIntExtra(SynchService.STATUS, 0)==SynchService.FAIL){
					AtnUtils.showToast(intent.getStringExtra(SynchService.MESSAGE));
				}

				if (mAdapter.getCount() > 0) {
					txtLoadingPlaceholder.setVisibility(View.GONE);
				} else {
					txtLoadingPlaceholder.setVisibility(View.VISIBLE);
					txtLoadingPlaceholder.setText(SynchService.IS_RUNNING ? R.string.insta_image_loading_message
							: R.string.no_venue_found_msg);
				}
				setLoadMoreStatus();
			}
		}
	};

	//set loading status on text view and show loader if its refreshing data.
	private void setLoadMoreStatus() {

		loadMoreButton.setVisibility(mAdapter.getCount()==0?View.INVISIBLE:View.VISIBLE);
		//        if(!SynchService.IS_RUNNING && venueLoadLimit>mAdapter.getCount()) {
		//            loadMoreButton.setText("No More Venues!");
		////        } else if(venueLoadLimit>mAdapter.getCount()&&SynchService.IS_RUNNING) {
		////            loadMoreButton.setText("Loading...");
		//        } else {
		//            loadMoreButton.setText("Tap To Load More");
		//        }
		mSwipeRefreshWidget.setRefreshing(SynchService.IS_RUNNING);
	}



	/***
	 * Loader replacement stuff
	 * **/
	//once all data is loaded the we will not reload data after 5 second.
	private boolean isDataChanged = true;
	/*
	 * Look For data change in databse and reload after 5 second if screen is in idle state
	 * **/
	private ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
		@Override
		public void onChange(boolean selfChange) {
			//AtnUtils.log("content changed");
			isDataChanged = true;
			refreshDataIfNeeded();

		}
	};


	private int mWhat = 101;
	//fire handle after 5 second to reload data.
	private Handler mHandler = new Handler(Looper.getMainLooper(), new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			//check if already task is running the ignore this relaod.
			if(mLoadTask == null || mLoadTask.getStatus()==AsyncTask.Status.FINISHED) {
				loadData();
			}
			return true;
		}
	});

	private void refreshDataIfNeeded() {
		// Schedule reload data if ListView is in idle state for 5 second and
		// user is not scrolling image horizontlly and we have not schedule any
		// meesage yet and data is changed. 
		if(mListScrollState==OnScrollListener.SCROLL_STATE_IDLE
				&& mAdapter.shouldReplaceData 
				&& (!mHandler.hasMessages(mWhat)) 
				&& isDataChanged) {
			mHandler.sendEmptyMessageDelayed(mWhat, 5000);
		}

		//remove all message if listview in not in idle state.
		if(mListScrollState!=OnScrollListener.SCROLL_STATE_IDLE) {
			mHandler.removeMessages(mWhat);
		}
	}

	private void loadData() {
		cancelTask();
		isDataChanged = false;
		mLoadTask = new LoadVenueTask(getActivity());
		mLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Integer.valueOf(venueLoadLimit));
	}

	//cancel task and remove all schedule messages
	private void cancelTask() {
		mHandler.removeMessages(mWhat);
		if(mLoadTask!=null && mLoadTask.getStatus()!=AsyncTask.Status.FINISHED) {
			mLoadTask.cancel(true);
		}
	}

	/**
	 * Load venue from data base asynch task
	 * **/
	private class LoadVenueTask extends AsyncTask<Integer, Void, List<VenueModel>>{
		public Context mContext = null;
		public LoadVenueTask(Context context) {
			mContext = context;
		}

		@Override
		protected List<VenueModel> doInBackground(Integer... params) {

			String mSelection = null;
			String[] mSelectionArgs = null;
			String[] mProjection = new String[]{String.valueOf(params[0])};
			String[] subcat = new String[]{};
			String[] mainCat = new String[]{};
			//create filter query IN Clause
			String filterQuery = AtnUtils.getFilterQueryString(mContext);
			if(!TextUtils.isEmpty(filterQuery)) {
				String[] query = filterQuery.split(",");
				mainCat = new String[query.length];
				StringBuffer qqsnMarks = new StringBuffer();
				qqsnMarks.append("(");
				for (int i = 0; i < query.length; i++) {
					mainCat[i] = query[i];
					qqsnMarks.append("?,");
				}
				qqsnMarks.deleteCharAt(qqsnMarks.length()-1);
				qqsnMarks.append(")");
				//				mSelection = Atn.Venue.CATEGORY +" IN "+qqsnMarks.toString() +" AND  "+Atn.Venue.SUB_CATEGORY+" = '4bf58dd8d48988d1d0941735'";
				mSelection = Atn.Venue.CATEGORY +" IN "+qqsnMarks.toString() ;
				mSelectionArgs = mainCat;
			} 

			/*
			 * ArrayList<AnchorCategory> getSearchCatgoryList
			 * */
			String searchText = SharedPrefUtils.getSearchText(getActivity());
			if(!TextUtils.isEmpty(searchText) && searchText.length() >0) {
				ArrayList<AnchorCategory> subCateList=Atn.Category.getSearchCatgoryList(mContext, searchText);
				subcat = new String[subCateList.size()];
				StringBuffer qqsnMarks = new StringBuffer();
				qqsnMarks.append("(");

				if(subCateList.size() >0 ) {
					for (int i = 0; i < subCateList.size(); i++) {
						AnchorCategory catObj = subCateList.get(i);
						subcat[i] = catObj.foursquareId;
						qqsnMarks.append("?,");
					}
					qqsnMarks.deleteCharAt(qqsnMarks.length()-1);
				}
				qqsnMarks.append(")");
				if(mSelection == null)
					mSelection = Atn.Venue.SUB_CATEGORY+" IN "+qqsnMarks.toString();
				else
					mSelection = mSelection +" AND "+Atn.Venue.SUB_CATEGORY+" IN "+qqsnMarks.toString();

			}
			mSelectionArgs = combine(mainCat, subcat);


			/*
			 * ArrayList<AnchorCategory> getSearchCatgoryList
			 * */

			Cursor cursor = mContext.getContentResolver().query(mUri,mProjection, mSelection, 
					mSelectionArgs, mSortOrder);
			List<VenueModel> fSVenue = new ArrayList<VenueModel>();
			Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
			Location meLoc = new Location("");
			if (cursor != null&&cursor.getCount()>0) {
				cursor.moveToFirst();
				do {
					if(isCancelled()) return null;
					meLoc.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT))));
					meLoc.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG))));

					if(currntLoc==null||(!(currntLoc.distanceTo(meLoc)<=FsHttpRequest.RADIUS_VALUE))) {
						continue;
					}

					VenueModel fsObjModel = new VenueModel(cursor);
					if (!TextUtils.isEmpty(fsObjModel.getVenueId())) {
						Cursor mediaCursor = mContext.getContentResolver().query(Atn.InstagramMedia.CONTENT_URI,null,Atn.InstagramMedia.FOUR_SQUARE_ID
								+ " = ? ",
								new String[] { fsObjModel.getVenueId() },Atn.InstagramMedia.DEFAULT_SORT_ORDER);

						if (mediaCursor != null && mediaCursor.getCount() > 0) {
							mediaCursor.moveToFirst();
							do {
								if(isCancelled()) return null;
								IgMedia igMe = new IgMedia(mediaCursor);
								fsObjModel.addInstagramMedia(igMe);
							} while (mediaCursor.moveToNext());
						}
						if(mediaCursor!=null){mediaCursor.close();}	
					} 
					fSVenue.add(fsObjModel);
				} while (cursor.moveToNext());
			}
			if(cursor!=null){cursor.close();}

			return fSVenue;
		}
		@Override
		protected void onPostExecute(List<VenueModel> result) {
			super.onPostExecute(result);
			loadMoreButton.setEnabled(true);

			if(mListScrollState==OnScrollListener.SCROLL_STATE_IDLE
					&& mAdapter.shouldReplaceData 
					&& !isCancelled() 
					&& result!=null) {

				if(shouldUpdateCount && mAdapter.getCount()<result.size()) {
					venueLoadLimit = result.size(); 
					venueLoadLimit = venueLoadLimit<LOAD_MORE_VENUE_LIMIT?LOAD_MORE_VENUE_LIMIT:venueLoadLimit;
				}    	

				mVenueList = result;
				//				mVenueList.addAll(result);
				reloadListData();

				//				mAdapter.setVenueData(result);
				mAdapter.setChildFragmentListener(getChildFragmentListener());
				if(result.size()>0) {
					txtLoadingPlaceholder.setVisibility(View.GONE);
				} else {
					txtLoadingPlaceholder.setVisibility(View.VISIBLE);
					txtLoadingPlaceholder.setText(SynchService.IS_RUNNING ? R.string.insta_image_loading_message
							: R.string.no_venue_found_msg);
				}
				setLoadMoreStatus();
			}
			loadMoreButton.setText("Tap To Load More");
		}
	}


	public static String[] combine(String[] a, String[] b){
		int length = a.length + b.length;
		String[] result = new String[length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}


	//ask user for login on instagram so that he can fetch more images.
	public void showInstagramLoginDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		TextView txtView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.text_layout, null, false);
		txtView.setText(R.string.instgram_login_message);
		builder.setView(txtView);
		builder.setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				InstagramSession.showInstagramDialog(getActivity().getSupportFragmentManager(), new InstagramLoginListener() {
					@Override
					public void onLoggedIn(boolean isSuccess, String message) {
						if(isSuccess) {
							onRefresh();
						} else {
							AtnUtils.showToast(message);
						}
					}
				});
			}
		});
		builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();  
	}

	OnClickListener headerClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.happening_now_header_most_recent_view_rl:
			{
				mostRecentClicked();
			}
			break;
			case R.id.happening_now_header_near_by_view_rl:
			{
				nearByClicked();
			}
			break;


			default:
				break;
			}
			Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();

		}
	};

	private void mostRecentClicked() {
		isNearBySelected = false;
		loadData();

		nearByBtn.setSelected(false);
		TypefaceUtils.applyTypeface(getActivity().getApplicationContext(),
				nearByBtn, TypefaceUtils.ROBOTO_CONDENCED);

		mostRecentBtn.setSelected(true);
		TypefaceUtils.applyTypeface(getActivity().getApplicationContext(),
				mostRecentBtn, TypefaceUtils.ROBOTO_CONDENCED_BOLD_ITALIC);

		nearByView.setBackgroundColor(getResources().getColor(R.color.text_light_grey));
		mostRecentView.setBackgroundColor(getResources().getColor(R.color.light_green_text_color));
		
		SharedPrefUtils.setSelectedState(getActivity(),isNearBySelected);
	}

	private void nearByClicked() {
		isNearBySelected = true;
		loadData();

		nearByBtn.setSelected(true);
		TypefaceUtils.applyTypeface(getActivity().getApplicationContext(),
				nearByBtn, TypefaceUtils.ROBOTO_CONDENCED_BOLD_ITALIC);

		mostRecentBtn.setSelected(false);
		TypefaceUtils.applyTypeface(getActivity().getApplicationContext(),
				mostRecentBtn, TypefaceUtils.ROBOTO_CONDENCED);
		
		mostRecentView.setBackgroundColor(getResources().getColor(R.color.text_light_grey));
		nearByView.setBackgroundColor(getResources().getColor(R.color.light_green_text_color));

		SharedPrefUtils.setSelectedState(getActivity(),isNearBySelected);

	}



	private void reloadListData() {
		if(isNearBySelected) {
			//sort list base on Distance.
			Collections.sort(mVenueList, new VenueModel.DistanceComparator());
		}
		else {
			//sort list base on Time .
			Collections.sort(mVenueList, new VenueModel.DateComparator());
		}
		mAdapter.setVenueData(mVenueList);

		String searchText = SharedPrefUtils.getSearchText(getActivity());
		if(!TextUtils.isEmpty(searchText) && searchText.length() >0 && mVenueList.size() <= 0 && isNeedToshowAlrt) {
			showAlertForNotSearchFoundClick();
		}

	}


	private void showAlertForNotSearchFoundClick() {

		AlertDialog.Builder loginAlertDialog = new AlertDialog.Builder(getActivity());
		loginAlertDialog.setTitle(R.string.app_name);
		loginAlertDialog.setMessage(R.string.happening_now_no_venue_found);
		loginAlertDialog.setPositiveButton(R.string.btn_okay,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				onReopenIntrestButtonClick();
			}
		});
		loginAlertDialog.show();
	}

	//favorite or unfavorite bar 
	private void onReopenIntrestButtonClick() {
		addToBackStack(ChooseInterestFragment.newInstance());

	}

}
