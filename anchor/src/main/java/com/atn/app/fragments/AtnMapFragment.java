/**
 * @Copyright CoppperMobile 2014.
 * */
package com.atn.app.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.MapCustomPinData;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.instagram.InstagramDialog.InstagramLoginListener;
import com.atn.app.instagram.InstagramSession;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.provider.Atn;
import com.atn.app.service.SynchService;
import com.atn.app.task.AtnRegisterBarTask;
import com.atn.app.utils.AtnImageUtils;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.UiUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

/**
 * Show all venue on map
 * */
public class AtnMapFragment extends AtnBaseFragment {
	
	//set opacity of action bar 
	private static final int ACTION_BAR_ALPHA = ACTION_BAR_OPEQUE;
	//used for accessing map fragment from stack
	private static String MAP_FRAGMENT_TAG = "ATN_MAP_FRAGMENT_TAG"; 
	private GoogleMap googleMap = null;	
	private Location currentLocation;
	
	//marker vs map 
	private Map<Marker, AtnOfferData> markersVsVenues = new HashMap<Marker, AtnOfferData>();
	//BitmapDescriptor for default Anchor bar
	private BitmapDescriptor mDefaultAtnBarDesc = null;
	//BitmapDescriptor for default non Anchor bar(i.e for foursquare bar)
	private BitmapDescriptor mDefaultNonAtnBarDesc = null;
	
	//venue marker those logo need be fetch from server or from cache
	List<Marker> listOfMarkers = new ArrayList<Marker>();
	
	List<Marker> atnBarMarkers = new ArrayList<Marker>();
	//marker logo download task it download venue logo images one by one
	LoadMarkerAsynch loadTask = null;
	
	//if fragment in pause state it will stop
	//loader task and in on resume we set it true and start loader again 
	boolean  shouldLoadLogo = false;
	
	//if map zoomed once then we will not zoom it again
	boolean hasOnceZoomIN = false;
	//hold resource reference  
	Resources resourse = null;
	
	//set venue image and create bitmap from that
	private ImageView mNonAtnBarView = null;
	private View mAtnBarView = null;
	
	
	
	/**
	 * Custom Loader replacement
	 * */
	private LoadVenueTask mLoadTask = null;
    private Uri mUri = Atn.Venue.CONTENT_URI;
    private String mSortOrder = Atn.Venue.DEFAULT_SORT_ORDER;
	private static final int MAP_DATA_RELOAD_TIME = 5000;
    
    
	public static AtnMapFragment newInstance() {
		AtnMapFragment atnMapFragment = new  AtnMapFragment();
		return atnMapFragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAtnBarView =  View.inflate(getActivity(), R.layout.atn_bar_item, null);
		mNonAtnBarView = (ImageView)View.inflate(getActivity(), R.layout.non_atn_map_pin, null);
		
		hasOnceZoomIN = false;
		
		//create synchronized we are accessing data from different thread
		listOfMarkers = Collections.synchronizedList(listOfMarkers);
		markersVsVenues = Collections.synchronizedMap(markersVsVenues);
		atnBarMarkers = Collections.synchronizedList(atnBarMarkers);
		
		//setup map fragment....
		FragmentManager childFm = getChildFragmentManager();
		SupportMapFragment supMap = SupportMapFragment.newInstance();
		FragmentTransaction ft = childFm.beginTransaction();
		ft.replace(R.id.map_framelayout, supMap, MAP_FRAGMENT_TAG);
		ft.addToBackStack(null);
		ft.commit();
		 
		//register receiver for service response
		IntentFilter filter = new IntentFilter();
	    filter.addAction(SynchService.ACTION_SERVICE);
	    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setActionBarAlpha(ACTION_BAR_ALPHA);
		resourse = getResources();
		View viewRoot = inflater.inflate(R.layout.map_fragment, container,false);
		ImageButton btnCurrentLocation = (ImageButton) viewRoot.findViewById(R.id.img_current_location);
		btnCurrentLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locateCurrentLocation();
			}
		});
		viewRoot.findViewById(R.id.map_filter_container).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showFilterDialog();
			}
		});
		registerListeners();
		
		return viewRoot;
	}
	
	//show filter screen
	private void showFilterDialog() {
		
		ChooseInterestFragment myInterest = (ChooseInterestFragment) ChooseInterestFragment.newInstance();
		myInterest.setDoneClickEventListener(new DialogClickEventListener() {
			@Override
			public void onClick(int resId) {
				AtnRegisterBarTask.getInstance().refreshDataFromDb();
				loadData();
			}
		});
		addToBackStack(myInterest);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		shouldLoadLogo = true;
		//invoke loader for remaining venue
		loadMarkerImage();
	}
	
	
	@Override
	public void onPause() {
		shouldLoadLogo = false;
		super.onPause();
	}
	
	//for business load task
	private void registerListeners() {
		AtnRegisterBarTask.getInstance().registerListener(mBarHandler);
	}
	
	private void unRegisterListeners(){
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		AtnRegisterBarTask.getInstance().unRegisterListener(mBarHandler);
		cancelTask();
	}
	
	@Override
	public void onDestroyView() {
		unRegisterListeners();
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		setTitle(R.string.map_title);
		getView().post(new Runnable() {
			@Override
			public void run() {
				initializeGoogleMap();
				checkLoadingStatus();
				if (googleMap != null&&!hasOnceZoomIN) {
					hasOnceZoomIN = true;
					locateCurrentLocation();
				}
				AtnRegisterBarTask.getInstance().refreshDataFromDb();
				getActivity().getContentResolver().registerContentObserver(mUri, true,mObserver);
			    loadData();
			}
		});
		
		if (!AtnLocationManager.getInstance().isLocationServicesEnabled()) {
			AtnApp.showTurnOnLocationServicesDialog(getActivity());
		} 
		super.onActivityCreated(savedInstanceState);	
	}

	////menu items
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.map_menu, menu);
		MenuItem item = menu.findItem(R.id.refresh_menu_item);
        if ((AtnRegisterBarTask.getInstance().getStatus()==Status.RUNNING||
				AtnRegisterBarTask.getInstance().getStatus()==Status.PENDING)||SynchService.IS_RUNNING) {
				MenuItemCompat.setActionView(item, R.layout.progress_bar);
		}else{
			MenuItemCompat.setActionView(item, null);
		}
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch (item.getItemId()) {
		case R.id.refresh_menu_item:
			onRefreshButtonClick();
			return true;	
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
    }
	
	//refresh venues
	private void onRefreshButtonClick() {
		if(!SynchService.IS_RUNNING) {
			
			if(SharedPrefUtils.fSVenueLoadStatus(getActivity())) {
        		if(TextUtils.isEmpty(InstagramSession.getToken(getActivity()))) {
        			showInstagramLoginDialog();
        		} else {
        			//refresh media images only
        		 AtnRegisterBarTask.getInstance().setLoadFromServer(true);
        		 AtnRegisterBarTask.getInstance().refreshDataFromDb();
   				 AtnUtils.runSynchService(getActivity(), SynchService.Command.REFRESH_MEDIA);
        		}
        	} else {
        		//Reload venues
        		 AtnRegisterBarTask.getInstance().setLoadFromServer(true);
       		     AtnRegisterBarTask.getInstance().refreshDataFromDb();
  				 AtnUtils.runSynchService(getActivity(), SynchService.Command.RELOAD_VENUE);
        	}
		}
		checkLoadingStatus();
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
                            	onRefreshButtonClick();
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
	
	/**
	 *  set progress bar
	 * */
	private void checkLoadingStatus() {
		invalidateOptionMenu();
	}
	

	private void initializeGoogleMap() {
		
		FragmentManager childFm = getChildFragmentManager();
		Fragment fragment = childFm.findFragmentByTag(MAP_FRAGMENT_TAG);
		if(fragment!=null){
			googleMap =((SupportMapFragment)fragment).getMap();
		}

		if (googleMap == null) {
			AtnApp.showMessageDialog(getActivity(),getResources().getString(R.string.unable_to_load_map),false);
		} else {
			
			final View view = getView();
			if (view != null) {
				View currentLoc = view.findViewById(R.id.img_current_location);
				currentLoc.bringToFront();
				view.findViewById(R.id.map_filter_container).bringToFront();
				googleMap.setPadding(currentLoc.getWidth()+10, UiUtil.calculateActionBarSize(getActivity()), 0, getResources().getDimensionPixelSize(R.dimen.map_filter_button_height)+10);
			}
			
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(final Marker marker) {
					
					//check dustance is greater then zero or not if distance is not greater then zero then camera will not move
					if(AtnUtils.willCameraMove(googleMap.getCameraPosition().target, marker.getPosition())){
						openBarBubble(marker);
					} else {
						googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), new CancelableCallback() {
							@Override
							public void onFinish() {
								openBarBubble(marker);
							}
							@Override
							public void onCancel() {}
						});
					}
					return true;
				}
			});
		}
	}
	
   /**
    * Open bubble view for marker
    * @param marker clicked marker.
    * 
    * */
	private void openBarBubble(final Marker marker){
		
		AtnOfferData atnBar = markersVsVenues.get(marker);
		Bundle mBundle = new Bundle();
		String imgUrl = null;
		if (atnBar.getDataType() == VenueType.ANCHOR) {
			AtnRegisteredVenueData atnVenue = (AtnRegisteredVenueData) markersVsVenues.get(marker);
			mBundle.putString(MapCustomPinData.VENUE_NAME,atnVenue.getBusinessName());
			mBundle.putString(MapCustomPinData.VENUE_ID,atnVenue.getBusinessId());
			
			mBundle.putString(MapOverlayFragment.BAR_ADDRESS,atnVenue.getBusinessCity());
			mBundle.putBoolean(AtnRegisteredVenueData.IS_REGISTERED_VENUE,true);
			if (TextUtils.isEmpty(atnVenue.getBusinessFoursquareVenueId())) {
				mBundle.putString(VenueDetailFragment.ANCHOR_BAR_ID,atnVenue.getBusinessId());
			} else {
				mBundle.putString(MapCustomPinData.VENUE_ID,atnVenue.getBusinessFoursquareVenueId());
			}
			imgUrl = atnVenue.getBusinessImageUrl();
		} else {
			VenueModel fsVenue = (VenueModel) markersVsVenues.get(marker);
			mBundle.putString(MapOverlayFragment.BAR_ADDRESS,fsVenue.getAddress());
			mBundle.putString(MapCustomPinData.VENUE_ID,fsVenue.getVenueId());
			mBundle.putString(MapCustomPinData.VENUE_NAME,fsVenue.getVenueName());
			//if has one media image then show media image otherwise show venue photo
			if(fsVenue.getInstagramMedia().size()>0) {
				IgMedia media = fsVenue.getInstagramMedia().get(0);
				imgUrl = media.getThumbnailUrl();
			} else {
				imgUrl = AtnUtils.createIconUrlFrmFsVenue(fsVenue.getPhoto(),
						getResources().getDimensionPixelSize(R.dimen.non_atn_bar_image_size));
			}
		}
		if(!TextUtils.isEmpty(imgUrl)) {
			mBundle.putString(MapOverlayFragment.IMAGE_URL, imgUrl);
		}
		MapOverlayFragment mapOve = MapOverlayFragment.newInstance(mBundle);
		mapOve.setCancelable(false);
		mapOve.setChildFragmentListener(getChildFragmentListener());
		if (mapOve != null) {
			mapOve.show(getFragmentManager(),MapOverlayFragment.MAP_OVERLAY_DIALOG);
		}
	}
	
	
	/**
	 * Shows the current location.
	 */
	private void locateCurrentLocation() {

		if (AtnLocationManager.getInstance().isLocationServicesEnabled()) {
			currentLocation = AtnLocationManager.getInstance().getLastLocation();
			if (currentLocation != null) {
				LatLng defaultLocation = new LatLng(
						currentLocation.getLatitude(),
						currentLocation.getLongitude());
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));
			}
		} else {
			AtnApp.showTurnOnLocationServicesDialog(getActivity());
		}
	}
    
	///Receiver for synch service 
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(SynchService.ACTION_SERVICE)) {
                 if(intent.getIntExtra(SynchService.STATUS, 0)==SynchService.FAIL) {
             	   AtnUtils.showToast(intent.getStringExtra(SynchService.MESSAGE));
                }
             }
             checkLoadingStatus();
         }
     };

     
     //load atn bars only called when anchor businesses are loaded
	private Handler	mBarHandler = new Handler(Looper.getMainLooper(),new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			//check for change in bar, if changed then removed and add again.
			if(msg.what==1) {
				@SuppressWarnings("unchecked")
				List<AtnOfferData> list = (List<AtnOfferData>) msg.obj;				
				List<Marker> removeMarkers = new ArrayList<Marker>();
				for (Marker marker : atnBarMarkers) {
					
					AtnOfferData onMap = markersVsVenues.get(marker);
					if(list.contains(onMap)) {
						final int loc = list.indexOf(onMap);
						AtnRegisteredVenueData newData = (AtnRegisteredVenueData)list.get(loc);
							if (TextUtils.isEmpty(newData.getBusinessModified())
									|| !(newData.getBusinessModified().equalsIgnoreCase(((AtnRegisteredVenueData) onMap).getBusinessModified()))) {
								removeMarkers.add(marker);
								markersVsVenues.remove(marker);
								tryToRemoveMarker(marker);
							} else {
								list.remove(loc);
							}
					} else {
						removeMarkers.add(marker);
						markersVsVenues.remove(marker);
						tryToRemoveMarker(marker);
					}
				}
				atnBarMarkers.removeAll(removeMarkers);
				addOnMapMarker(list);
			} else {
				if(msg.obj!=null) {
					AtnUtils.showToast(msg.obj.toString());
				}
			}
			checkLoadingStatus();
			return false;
		}
	});
	
	
	/***
     * Loader replacement stuff
     * **/
    private boolean isDataChanged = true;
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
    private Handler mHandler = new Handler(Looper.getMainLooper(), new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			loadData();
			return true;
		}
	});
    
    private void refreshDataIfNeeded() {
    	if((!mHandler.hasMessages(mWhat)) && isDataChanged) {
    		mHandler.sendEmptyMessageDelayed(mWhat, MAP_DATA_RELOAD_TIME);
		}
    }
   
    private void loadData() {
    	cancelTask();
    	isDataChanged = false;
    	mLoadTask = new LoadVenueTask(getActivity());
    	mLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    private void cancelTask() {
    	mHandler.removeMessages(mWhat);
    	if(mLoadTask!=null && mLoadTask.getStatus()!=AsyncTask.Status.FINISHED) {
    		mLoadTask.cancel(true);
    	}
    }
    
    private class LoadVenueTask extends AsyncTask<Integer, Void, List<AtnOfferData>>{
    	public Context mContext = null;
		public LoadVenueTask(Context context){
			mContext = context;
		}
		
		@Override
		protected  List<AtnOfferData> doInBackground(Integer... params) {
			if(mContext==null)return null;
			
			String mSelection = null;
			String[] mSelectionArgs = null;
			String[] mProjection = null;
			//if filter query return null then dont apply filter on category and only loaded non followed venues.
			String filterQuery = AtnUtils.getFilterQueryString(mContext);
			if(TextUtils.isEmpty(filterQuery)) {
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
			
			Location currntLoc = AtnLocationManager.getInstance().getLastLocation();
			Location venueLocation = new Location("");
			Cursor cursor = mContext.getContentResolver().query(mUri,mProjection, mSelection,
					mSelectionArgs, mSortOrder);
			List<AtnOfferData> fSVenue = new ArrayList<AtnOfferData>();
			if (cursor != null&&cursor.getCount()>0) {
				cursor.moveToFirst();
				if (cursor.getCount() > 0) {
					do {
						if(isCancelled()) return null;
						
						venueLocation.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LAT))));
						venueLocation.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(Atn.Venue.LNG))));
						//if venue location great then 5 mi return.
						if((currntLoc==null)||!((currntLoc.distanceTo(venueLocation)<=FsHttpRequest.RADIUS_VALUE))) {
							continue;
						}
						
						VenueModel fsObjModel = new VenueModel(cursor);
						Cursor mediaCursor = mContext.getContentResolver()
								      .query(Atn.InstagramMedia.CONTENT_URI, null,
										Atn.InstagramMedia.FOUR_SQUARE_ID + " = ? ",
										new String[] { fsObjModel.getVenueId() },
										Atn.InstagramMedia.DEFAULT_SORT_ORDER);

						if (mediaCursor != null && mediaCursor.getCount() > 0) {
							mediaCursor.moveToFirst();
							IgMedia igMe = new IgMedia(mediaCursor);
							fsObjModel.addInstagramMedia(igMe);
						}
						if (mediaCursor != null) {
							mediaCursor.close();
						}
						fSVenue.add(fsObjModel);
					} while (cursor.moveToNext());
				}
			}
			if(cursor!=null){cursor.close();}
			return fSVenue;
		}
		@Override
		protected void onPostExecute(List<AtnOfferData> newVenues) {
			super.onPostExecute(newVenues);
			if(!isCancelled() && newVenues!=null) {
					AtnUtils.log("Count:"+newVenues.size());
					//venue marker need to be removed from map
					List<Marker> needToBeRemoved = new ArrayList<Marker>();
					Iterator<Map.Entry<Marker, AtnOfferData>> entries = markersVsVenues.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Marker, AtnOfferData> entry = (Map.Entry<Marker, AtnOfferData>) entries.next();
						Marker key = (Marker)entry.getKey();
						AtnOfferData value = (AtnOfferData)entry.getValue();
						if(value.getDataType()==VenueType.FOURSQUARE) {
							//venue is already added on map
							if(newVenues.contains(value)) {
								newVenues.remove(value);
							} else {
								//venue is not in venue result set but its on map so we need to removed it.
								needToBeRemoved.add(key);
							}
						}
					}
					//removed marker from map
					for (Marker marker : needToBeRemoved) {
						markersVsVenues.remove(marker);
						tryToRemoveMarker(marker);
					}
					//add new venue on map
					addOnMapMarker(newVenues);
			}
		}
    }
	
	
	
	//add New venue on map
	private void addOnMapMarker(List<AtnOfferData> arg1) {
		
		//create once only
		if(mDefaultNonAtnBarDesc==null) {
			mDefaultNonAtnBarDesc = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
							R.drawable.map_image_holder_new));
		}

		//create once only
		if(mDefaultAtnBarDesc==null) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_map);
			ImageView barImage =(ImageView)mAtnBarView.findViewById(R.id.img_atn_bar_image);
			barImage.setImageBitmap(bitmap);
			mDefaultAtnBarDesc  = BitmapDescriptorFactory.fromBitmap(AtnUtils.getAtnBarMapPin(mAtnBarView));
		}
		
		for (AtnOfferData atnBar : arg1) {
			Marker marker = null;
			if(atnBar.getDataType() == VenueType.ANCHOR) {
				AtnRegisteredVenueData regVenueData = (AtnRegisteredVenueData)atnBar;
				marker = googleMap.addMarker(new MarkerOptions().position(regVenueData.getLatLng()).icon(mDefaultAtnBarDesc));
				atnBarMarkers.add(marker);
			} else {
				VenueModel venueModel = (VenueModel)atnBar;
				marker = googleMap.addMarker(new MarkerOptions().position(venueModel.getLatLng()).icon(mDefaultNonAtnBarDesc));
			}
			marker.setAnchor(0.7f, 0.36f);
			listOfMarkers.add(marker);
			markersVsVenues.put(marker, atnBar);
		}
		loadMarkerImage();
	}
	
	//invoke Venue Logo task if its not started yet
	private void loadMarkerImage() {
		
		if((loadTask==null||loadTask.getStatus()==Status.FINISHED)&&shouldLoadLogo) {
			//start task if we have any venue logo to load
			if(listOfMarkers.size()>0) {
				loadTask = new LoadMarkerAsynch(listOfMarkers.remove(0));
				loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}
	
/**
 * Task For Loading Venue Logo and create BitmapDescriptor for appropriate venue type in background
 * */
private class LoadMarkerAsynch extends AsyncTask<Void, Void, BitmapDescriptor> {

	Marker mMarker = null;
	//check should remove marker or add back to loader list to reload image
	private boolean isRemoved = false;
	private LoadMarkerAsynch(Marker marker) {
		mMarker = marker;
	}
	
	@Override
	protected BitmapDescriptor doInBackground(Void... params) {
		//image url for venue logo
		String imgUrl = null;
		
		AtnOfferData atnBar = markersVsVenues.get(mMarker);	
		if(atnBar == null) {
			return null;
		}
		
		if(atnBar.getDataType() == VenueType.ANCHOR) {
			AtnRegisteredVenueData atnRegister = (AtnRegisteredVenueData)atnBar;
			imgUrl = atnRegister.getBusinessImageUrl();
		} else {
			//First Media image if venue has one
			VenueModel fsVenue = (VenueModel)atnBar;
			if(fsVenue.getInstagramMedia().size()>0) {
				IgMedia media = fsVenue.getInstagramMedia().get(0);
				imgUrl = media.getThumbnailUrl();
			} else if(!TextUtils.isEmpty(fsVenue.getPhoto())) {
				imgUrl = AtnUtils.createIconUrlFrmFsVenue(fsVenue.getPhoto(), 
						 resourse.getDimensionPixelSize(R.dimen.non_atn_bar_image_size));
			} else {
				isRemoved = true;
			}
		}
		//load image.
		if(!TextUtils.isEmpty(imgUrl)) {
			Bitmap bitmap = null;
			try {
				bitmap = Picasso.with(AtnApp.getAppContext()).load(imgUrl).get();
			} catch (IOException e) {
				e.printStackTrace();
				AtnUtils.log("FAIL "+imgUrl);
			}
			//create map pin for venue
			if(atnBar.getDataType() == VenueType.ANCHOR&&bitmap!=null) {
				Activity activity = getActivity();
				if(activity!=null) {
					int atnPic = resourse.getDimensionPixelSize(R.dimen.atn_bar_image_size);
					ImageView barImage = (ImageView)mAtnBarView.findViewById(R.id.img_atn_bar_image);
					barImage.setImageBitmap(AtnImageUtils.getMapRoundBitmap(bitmap, atnPic/2));
					return BitmapDescriptorFactory.fromBitmap(AtnUtils.getAtnBarMapPin(mAtnBarView));
				} else {
					isRemoved = false;
					return null;
				}
			}else if(atnBar.getDataType() == VenueType.FOURSQUARE && bitmap!=null) {
				mNonAtnBarView.setImageBitmap(bitmap);
				return BitmapDescriptorFactory.fromBitmap(AtnUtils.getNonAtnBarMapPin(mNonAtnBarView));
			}
	    	
		} else {
			isRemoved = true;
		}
		return null;
	}

	@Override
	protected void onPostExecute(BitmapDescriptor result) {
		super.onPostExecute(result);
		if(result!=null) {
			if(shouldLoadLogo) {
				try {
					mMarker.setIcon(result);
				} catch (Exception e) {
					e.printStackTrace();
					AtnUtils.log("Error in set marker");
				}
			}
		} else {
			if(!isRemoved) {
				listOfMarkers.add(mMarker);
			}
		}
		loadTask = null;
		//start loader task again for next venue marker
		loadMarkerImage();
	 }
  }

	private void tryToRemoveMarker(Marker marker) {
		try {
			marker.remove();
	    } catch (IllegalArgumentException e) {
	     e.printStackTrace();   
	    }
	}

	protected int getScreenName() {
		return R.string.screen_name_map;
	};

}
