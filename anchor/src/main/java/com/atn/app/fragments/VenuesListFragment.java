/***
 * @Copyright Coppermobile pvt. ltd. 2014
 * */
package com.atn.app.fragments;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.atn.app.R;
import com.atn.app.activities.AtnActivity.ImagePicker;
import com.atn.app.adapters.VenueSearchAdapter;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.db.customloader.FsVanueLoader;
import com.atn.app.db.customloader.FsVanueLoader.FsQueryType;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.HttpUtility.ImageType;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;

/****
 * 
 * Shows near by venue list for search, add review and add photos. 
 * */
public class VenuesListFragment extends AtnBaseFragment implements
		OnItemClickListener, LoaderManager.LoaderCallbacks<List<AtnOfferData>>, OnActionExpandListener ,ImagePicker {

	private static int LOADER = 10;
	//to identify request search, add review and add photos.
	public static final String REQUEST_TYPE = "REQUEST_TYPE";

	public static final int ADD_PHOTO = 1001;
	public static final int ADD_REVIEW = 1002;
	public static final int SEARCH = 1003;
	private int mRequestType = 0;
	
	private VenueSearchAdapter mVenueAdpater = null;
	private List<AtnOfferData> venueList = null;
	private ProgressBar mProgressBar = null;

	private ActionBar mActionBar = null;
	private MenuItem mMenuItem = null;
	
	private String profilePicPath  = null;
	private VenueModel mSelectedVenueModel;
	private String mQuery = null;
	
	private SearchTask mSearchTask = null;
	private boolean shouldResetQuery = true;
	
	private ArrayList<VenueModel> mSearchedVenues = new  ArrayList<VenueModel>();
	
	//return new instance of this fragment with request type
	public static VenuesListFragment newInstance(int requstType) {
		Bundle bundle = new Bundle();
		bundle.putInt(REQUEST_TYPE, requstType);
		VenuesListFragment fragment = new VenuesListFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		venueList = new ArrayList<AtnOfferData>();
		mVenueAdpater = new VenueSearchAdapter(getActivity(), venueList);

		if (getArguments() != null)
			mRequestType = getArguments().getInt(REQUEST_TYPE, 0);

		mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		mActionBar.setIcon(R.drawable.transparent);
		getLoaderManager().initLoader(LOADER, null, this).forceLoad();
		
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Initialize this fragment.
		setTitle(R.string.choose_location);
		setActionBarAlpha(ACTION_BAR_OPEQUE);

		View view = inflater.inflate(R.layout.venue_review_layout, container,false);
		ListView listView = (ListView) view.findViewById(R.id.search_list_view);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		mProgressBar.setVisibility(View.VISIBLE);
		listView.setAdapter(mVenueAdpater);
		listView.setOnItemClickListener(this);
		shouldResetQuery = true;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().restartLoader(LOADER, null, VenuesListFragment.this).forceLoad();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_single_item, menu);
		mMenuItem = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(mMenuItem, this);
		setUpSearchView(mMenuItem);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//set up search view
	private void setUpSearchView(MenuItem menuItem) {

		AtnUtils.log("mQuery"+mQuery);
		SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		
		
		if (mSearchView != null) {
		        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String arg0) {
					mQuery =  arg0;
					getLoaderManager().restartLoader(LOADER, null, VenuesListFragment.this).forceLoad();
					performSearch();
					return false;
				}
				@Override
				public boolean onQueryTextChange(String arg0) {
					//mQuery = arg0;
					return false;
				}
			});
		        
			if (mRequestType == SEARCH) {
				MenuItemCompat.expandActionView(menuItem);
			}
			mSearchView.setQuery(mQuery, false);
		}
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem arg0) {
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem arg0) {
		if(shouldResetQuery) {
			mQuery = null;
		}
		getLoaderManager().restartLoader(LOADER, null, VenuesListFragment.this);
		return true;
	}

	@Override
	public void onDestroyView() {
		MenuItemCompat.collapseActionView(mMenuItem);
		super.onDestroyView();
	}
	
	

	//take appropriate action
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		switch (mRequestType) {
		case ADD_PHOTO:
			mSelectedVenueModel = (VenueModel) mVenueAdpater.getItem(position);
			showAddPicDialog(); 
			break;
		case ADD_REVIEW:
			showVenue(position);
			break;
		case SEARCH:
			showVenue(position);
			break;
		default:
			UiUtil.showToast(getActivity(), "Unknow Request");
			break;
		}
	}

	//open venue detail screen for review 
	private void showVenue(int position) {
		shouldResetQuery = false;
		Bundle mBundle = new Bundle();
		mBundle.putString(VenueDetailFragment.FS_VENUE_ID, ((VenueModel) 
				mVenueAdpater.getItem(position)).getVenueId());
		mBundle.putBoolean(VenueDetailFragment.IS_FROM_SEARCH, true);

		VenueDetailFragment fragment = new VenueDetailFragment();
		fragment.setVenueData((AtnOfferData) mVenueAdpater.getItem(position));
		fragment.setArguments(mBundle);
		addToBackStack(fragment);
	}
	
	@Override
	public Loader<List<AtnOfferData>> onCreateLoader(int arg0, Bundle arg1) {
	
		FsVanueLoader venueLoader =	new FsVanueLoader(FsQueryType.Venue, getActivity());
	    if(!TextUtils.isEmpty(mQuery)) {
	    	venueLoader.setSelection(Atn.Venue.VENUE_NAME + " LIKE ? ");
	    	venueLoader.setSelectionArgs( new String[] {"%"+mQuery+"%"});
	    } else {
	    	venueLoader.setSelection(null);
	    	venueLoader.setSelectionArgs(null);
	    }
		return venueLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<AtnOfferData>> arg0,
			List<AtnOfferData> arg1) {
		venueList.clear();
		venueList.addAll(arg1);
		mSearchedVenues.removeAll(venueList);
    	venueList.addAll(mSearchedVenues);
    	
    	//sort list.
    	Collections.sort(venueList, new VenueModel.DistanceComparator());
		mVenueAdpater.notifyDataSetChanged();
		if(mSearchTask==null||mSearchTask.getStatus()==AsyncTask.Status.FINISHED) {
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<AtnOfferData>> arg0) {}
	
	//show add or remove picture dialog 
	private void showAddPicDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.profile_photo_title)
	           .setItems(R.array.gallary_camera_array, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   if(which==1) {
	            		   invokeImagePicker(ImagePicker.CAMERA, VenuesListFragment.this);
	            	   } else if(which==0) {
	            		   invokeImagePicker(ImagePicker.GALLARY, VenuesListFragment.this);
	            	   }
	           }
	    }).setNegativeButton(R.string.cancel_button, null);
	    builder.create().show();
	}
	
	//picture captured and post on venue
	@Override
	public void onPickedImage(boolean isSuccess, String errorMsg, String path) {
		if(isSuccess) {
			profilePicPath = path;
			AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
					HttpUtility.buildBaseUri().appendPath(ApiEndPoints.ADD_PICTURE_ONVENUE), 
					Method.POST, new AnchorHttpResponceListener() {
				@Override
				public void onSuccessInBackground(JSONObject jsonObject) {
					try {
						if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {
							JSONObject	mediaObj = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONObject(JsonUtils.DATA);
							String pictureName = mediaObj.getString(JsonUtils.AnchorMediaKeys.PICTURE);
							ContentValues values = new  ContentValues();
							values.put(Atn.InstagramMedia.MEDIA_ID, mediaObj.getString(JsonUtils.AnchorMediaKeys.MEDIA_ID));
							values.put(Atn.InstagramMedia.FOUR_SQUARE_ID, mediaObj.getString(JsonUtils.AnchorMediaKeys.FS_VENUE_ID));
							values.put(Atn.InstagramMedia.MEDIA_URL, HttpUtility.getVenueMediaUrl(pictureName, ImageType.S));
							values.put(Atn.InstagramMedia.MEDIA_THUMB_URL, HttpUtility.getVenueMediaUrl(pictureName, ImageType.S));
							values.put(Atn.InstagramMedia.INSTAGRAM_ID_REF, mSelectedVenueModel.getInstagramLocationId());
							values.put(Atn.InstagramMedia.MEDIA_CREATED_DATE,mediaObj.getLong(JsonUtils.AnchorMediaKeys.UNIX_TIME));
							Atn.InstagramMedia.insert(values, getActivity());
							
							//update 
							values = new ContentValues();
							values.put(Atn.Venue.VENUE_ID, mediaObj.getString(JsonUtils.AnchorMediaKeys.FS_VENUE_ID));
							values.put(Atn.Venue.LATEST_MEDIA_DATE, mediaObj.getLong(JsonUtils.AnchorMediaKeys.UNIX_TIME));
							Atn.Venue.update(values, getActivity());
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
				}
				
				@Override
				public void onError(Exception ex) {
					UiUtil.showToast(getActivity(), ""+ex.getMessage());
					AnchorProgressDialog.conceal();
				}
			});
			
			anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
			HttpUtility.addVenueParams(getActivity(), anchorRequest, mSelectedVenueModel);
			
	        if(!TextUtils.isEmpty(profilePicPath)) {
	        	anchorRequest.addFile(JsonUtils.VenuePicUpload.MEDIA_PIC_TO_UPLOAD,profilePicPath);
	        	AnchorProgressDialog.show(getActivity(), R.string.please_wait);
	        	anchorRequest.execute();
	        }
		} else {
			UiUtil.showToast(getActivity(), errorMsg);	
		}
	}
	
	private void performSearch() {
		if(mSearchTask!=null&&mSearchTask.getStatus()!=AsyncTask.Status.FINISHED) {
			mSearchTask.cancel(true);
		}
		mSearchTask = new SearchTask();
		mSearchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mQuery);
	}
	
	////search venue within 20 miles 
	private class SearchTask extends AsyncTask<String, Void, ArrayList<VenueModel>> {
		 
        private FsHttpRequest fsVenueSearchRequest;
        private String errorString = null;
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mSearchedVenues.clear();
            mVenueAdpater.notifyDataSetChanged();
            fsVenueSearchRequest = new FsHttpRequest(getActivity());
        }
 
        @Override
        public ArrayList<VenueModel> doInBackground(String... params) {
             
            Location location = AtnLocationManager.getInstance().getLastLocation();
            if(location!=null) {
                if(AtnUtils.isConnectedToInternet()){
                    ArrayList<VenueModel> list = fsVenueSearchRequest.searchVenueByName(location,params[0]);;
                    if(!fsVenueSearchRequest.getResponse().isSuccess) {
                        errorString = fsVenueSearchRequest.getResponse().errorMessage;
                    }
                    return list;
                } else {
                    errorString  ="No Internet!";
                }
            } else {
                errorString  ="location not found";
            }
            return null;
        }
         
        @Override
        public void onCancelled() {
            super.onCancelled();
            fsVenueSearchRequest.setCanceled(true);
        }
         
        @Override
        public void onPostExecute(ArrayList<VenueModel> result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            if(result!=null) {
            	mSearchedVenues.addAll(result);
            	mSearchedVenues.removeAll(venueList);
            	venueList.addAll(mSearchedVenues);
            	//sort list.
            	Collections.sort(venueList, new VenueModel.DistanceComparator());
            	mVenueAdpater.notifyDataSetChanged();
            } else {
                AtnUtils.showToast(errorString);
            }
        }
    }

}
