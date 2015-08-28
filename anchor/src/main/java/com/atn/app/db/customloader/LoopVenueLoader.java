/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.db.customloader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;


/**
 * Custom loader for loading data in background from database
 * */
public class LoopVenueLoader extends AsyncTaskLoader<List<VenueModel>>{
	final LoadContentObserver mObserver;
	private class LoadContentObserver extends ContentObserver {
		public LoadContentObserver() {
			super(new Handler());
		}
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
		@Override
		public void onChange(boolean selfChange) {
			 onContentChanged();
		}
	}

	public enum FsQueryType {
		Venue, VenueAndMedia, VenueAndOneMedia
	}

	Uri mUri = Atn.Venue.CONTENT_URI;
	String[] mProjection = null;
	String mSelection = null;
	String[] mSelectionArgs = null;
	String mSortOrder = Atn.Venue.DEFAULT_SORT_ORDER;
	List<VenueModel> mFsVenue;

	public LoopVenueLoader(Context context) {
		super(context);
		mObserver = new LoadContentObserver();
		//create filter query IN Clouse
		String filterQuery = AtnUtils.getFilterQueryString(context);
		if(!TextUtils.isEmpty(filterQuery)) {
			String[] query = filterQuery.split(",");
			mSelectionArgs = new String[query.length];
			
			StringBuffer qqsnMarks = new StringBuffer();
			qqsnMarks.append("(");
			for (int i = 0; i < query.length; i++) {
				mSelectionArgs[i] = query[i];
				qqsnMarks.append("?,");
			}
			
			qqsnMarks.deleteCharAt(qqsnMarks.length()-1);
			qqsnMarks.append(")");
			
			mSelection = Atn.Venue.CATEGORY +" IN "+qqsnMarks.toString();
		} 
	}
	
	public LoopVenueLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		this(context);
		mUri = uri;
		mProjection = projection;
		mSelection = selection;
		mSelectionArgs = selectionArgs;
		mSortOrder = sortOrder;
	}

	public LoopVenueLoader(FragmentActivity activity, int venueLoadLimit) {
		this(activity);
		this.mProjection = new String[]{String.valueOf(venueLoadLimit)};
	}

	@Override
	public List<VenueModel> loadInBackground() {
		
		Cursor cursor = getContext().getContentResolver().query(mUri,mProjection, mSelection, 
				mSelectionArgs, mSortOrder);
		List<VenueModel> fSVenue = new ArrayList<VenueModel>();
		Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
		Location meLoc = new Location("");
		if (cursor != null&&cursor.getCount()>0) {
			cursor.moveToFirst();
				do {
					meLoc.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT))));
					meLoc.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG))));
					
					if(currntLoc==null||(!(currntLoc.distanceTo(meLoc)<=FsHttpRequest.RADIUS_VALUE))) {
						continue;
					}
					
					VenueModel fsObjModel = new VenueModel(cursor);
					
					if (!TextUtils.isEmpty(fsObjModel.getVenueId())) {
						Cursor mediaCursor = getContext().getContentResolver().query(Atn.InstagramMedia.CONTENT_URI,null,Atn.InstagramMedia.FOUR_SQUARE_ID
												+ " = ? ",
										new String[] { fsObjModel.getVenueId() },Atn.InstagramMedia.DEFAULT_SORT_ORDER);
						
						if (mediaCursor != null && mediaCursor.getCount() > 0) {
							mediaCursor.moveToFirst();
							do {
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

	/* Runs on the UI thread */
	@Override
	public void deliverResult(List<VenueModel> igMedia) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
//			 don't need the result.
			if (igMedia != null) {
				onReleaseResources(igMedia);
			}
		}
		List<VenueModel> oldIgMedia = igMedia;
		mFsVenue = igMedia;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(igMedia);
		}

		// release older.
		if (oldIgMedia != null) {
			onReleaseResources(oldIgMedia);
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<VenueModel> igMedia) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mFsVenue != null) {
			deliverResult(mFsVenue);
		}
		getContext().getContentResolver().registerContentObserver(mUri, true,mObserver);
		if (takeContentChanged() || mFsVenue == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(List<VenueModel> mIgMedia) {
		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(mIgMedia);
	}

	/**
	 * Handles a mFsRequest to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();
		// Ensure the loader is stopped
		onStopLoading();
		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mFsVenue != null) {
			onReleaseResources(mFsVenue);
			//mFsVenue = null;
		}
		if (mObserver != null) {
			getContext().getContentResolver().unregisterContentObserver(mObserver);
		}
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public String[] getProjection() {
		return mProjection;
	}

	public void setProjection(String[] projection) {
		mProjection = projection;
	}

	public String getSelection() {
		return mSelection;
	}

	public void setSelection(String selection) {
		mSelection = selection;
	}

	public String[] getSelectionArgs() {
		return mSelectionArgs;
	}

	public void setSelectionArgs(String[] selectionArgs) {
		mSelectionArgs = selectionArgs;
	}

	public String getSortOrder() {
		return mSortOrder;
	}

	public void setSortOrder(String sortOrder) {
		mSortOrder = sortOrder;
	}
}
