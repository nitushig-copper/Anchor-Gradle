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
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;

/**
 * Custom loader for loading data in background from database
 * */
public class FsVanueLoader extends AsyncTaskLoader<List<AtnOfferData>> {

	//content Observer
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

	//type of data loaded,Venue , Venue with one  image or Venue with all media image. 
	public enum FsQueryType {
		Venue, VenueAndMedia, VenueAndOneMedia
	}

	Uri mUri = Atn.Venue.CONTENT_URI;
	String[] mProjection = null;
	String mSelection = null;
	String[] mSelectionArgs = null;
	String mSortOrder = Atn.Venue.DEFAULT_SORT_ORDER;
	List<AtnOfferData> mFsVenue;
	FsQueryType mFsQueryType = FsQueryType.VenueAndMedia;

	public FsVanueLoader(Context context) {
		super(context);
		mObserver = new LoadContentObserver();
	}

	public FsVanueLoader(FsQueryType fSQuery, Context context) {
		this(context);
		mFsQueryType = fSQuery;
		AtnUtils.log("Loader start");
		
		//if filter query return null then dont apply filter on category and only loaded non followed venues.
		String filterQuery = AtnUtils.getFilterQueryString(context);
		if(TextUtils.isEmpty(filterQuery)||FsQueryType.Venue==mFsQueryType) {
			mSelection = Atn.Venue.FOLLOWED +" < ? AND "+Atn.Venue.LAT +" IS NOT NULL ";
			mSelectionArgs = new String[]{"2"};
		} else {
			//apply IN Clouse 
			String[] query = filterQuery.split(",");
			mSelectionArgs = new String[1+query.length];
			mSelectionArgs[0] = "2";
			StringBuffer qqsnMarks = new StringBuffer();
			qqsnMarks.append("(");
			for (int i = 0; i < query.length; i++) {
				mSelectionArgs[i+1] = query[i];
				qqsnMarks.append("?,");
			}
			qqsnMarks.deleteCharAt(qqsnMarks.length()-1);
			qqsnMarks.append(")");
			mSelection = Atn.Venue.FOLLOWED + " < ? AND " + Atn.Venue.LAT
					+ " IS NOT NULL AND " + Atn.Venue.CATEGORY + " IN "
					+ qqsnMarks.toString();
		}
	}

	public FsVanueLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		this(context);
		mUri = uri;
		mProjection = projection;
		mSelection = selection;
		mSelectionArgs = selectionArgs;
		mSortOrder = sortOrder;
	}

	@Override
	public List<AtnOfferData> loadInBackground() {
		
		Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
		Location venueLocation = new Location("");
		Cursor cursor = getContext().getContentResolver().query(mUri,mProjection, mSelection, mSelectionArgs, mSortOrder);
		List<AtnOfferData> fSVenue = new ArrayList<AtnOfferData>();
		if (cursor != null&&cursor.getCount()>0) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					
					venueLocation.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT))));
					venueLocation.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG))));
					//if venue location great then 5 mi return.
					if((currntLoc==null)||!((currntLoc.distanceTo(venueLocation)<=FsHttpRequest.RADIUS_VALUE))) {
						continue;
					}
					
					VenueModel fsObjModel = new VenueModel(cursor);
					if (mFsQueryType != FsQueryType.Venue) {
						Cursor mediaCursor = getContext().getContentResolver().query(Atn.InstagramMedia.CONTENT_URI,null,Atn.InstagramMedia.FOUR_SQUARE_ID
												+ " = ? ",
										new String[] { fsObjModel.getVenueId() },
										Atn.InstagramMedia.DEFAULT_SORT_ORDER);
						
						if (mediaCursor != null && mediaCursor.getCount() > 0) {
							mediaCursor.moveToFirst();
							IgMedia igMe = new IgMedia(mediaCursor);
							fsObjModel.addInstagramMedia(igMe);
						}
						if(mediaCursor!=null){mediaCursor.close();}	
						//set tags
						//fsObjModel.setReviews(Atn.ReviewTable.getVenueTwoReviewTag(getContext(), fsObjModel));
					} else {
						//calculate distance
						fsObjModel.getDistance();
					}
					fSVenue.add(fsObjModel);
				} while (cursor.moveToNext());
			}
		}
		if(cursor!=null){cursor.close();}
		return fSVenue;
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(List<AtnOfferData> igMedia) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
//			 don't need the result.
			if (igMedia != null) {
				onReleaseResources(igMedia);
			}
		}
		List<AtnOfferData> oldIgMedia = igMedia;
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
	protected void onReleaseResources(List<AtnOfferData> igMedia) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * <p>
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
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
	public void onCanceled(List<AtnOfferData> mIgMedia) {
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
			mFsVenue = null;
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
