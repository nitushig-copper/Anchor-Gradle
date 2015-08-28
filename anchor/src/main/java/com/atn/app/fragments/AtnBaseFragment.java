/**
 * @Copyright CoppperMobile 2014.
 * */
package com.atn.app.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.AtnActivity.ImagePicker;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.StoreGcmIdOnServer;

/**
 * AtnBaseFragment fragment, every fragment is sub-class of this fragment
 * 
 * */
public  class AtnBaseFragment extends Fragment {

	
	public static final int ACTION_BAR_OPEQUE = 255;
	public static final int ACTION_BAR_TRANSPARENT = 0;
	
	//add child fragment listener for push new fragment
	private AddFragmentListener childFragmentListener = null;
	
	public AddFragmentListener getChildFragmentListener() {
		return childFragmentListener;
	}

	public void setChildFragmentListener(AddFragmentListener childFragmentListener) {
		this.childFragmentListener = childFragmentListener;
	}

	///add new screen to back stack
	protected void addToBackStack(AtnBaseFragment fragment) {
		if (this.childFragmentListener != null)
			childFragmentListener.addToBackStack(fragment);
	}
	
	
	public AtnBaseFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		AtnUtils.gASendView(getActivity(), getScreenName());
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//check gcm for GCm Id stored on server or if not stored then call api.
		if(UserDataPool.getInstance().isUserLoggedIn()&&!StoreGcmIdOnServer.isGcmIdStored()) {
			StoreGcmIdOnServer.getInstance().storeGcmIdOnServer(getActivity());
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * Set Action Bar title from fragment
	 * @param title 
	 * 			Title for screen
	 * */
	protected void setTitle(String title) {
		if(getActivity() instanceof AtnActivity)
			((AtnActivity)getActivity()).setTitle(title);
	}
	
	/**
	 * Set Action Bar title from fragment
	 * @param title 
	 * 			string resource,Title for screen
	 * */
	protected void setTitle(int title) {
		if(getActivity() instanceof AtnActivity)
			((AtnActivity)getActivity()).setTitle(title);
	}
	
	/**
	 * will Call activity invalidateOptionMenu();
	 * */
	protected void invalidateOptionMenu() {
		 ActivityCompat.invalidateOptionsMenu(this.getActivity());
	}
	
	/**
	 * Set action bar background alpha
	 * @param alpha
	 * */
	protected void setActionBarAlpha(int alpha) {
		if(getActivity() instanceof AtnActivity)
			((AtnActivity)getActivity()).setActionBarAlpha(alpha);
	}
	
	protected Drawable getActioBarBg() {
		return ((AtnActivity)getActivity()).getActionBarDrawable();
	}
	
	/**
	 * Invoke image picker for specific source (i.e camera, Gallery )
	 * @param sourceType 
	 * 			(i.e camera, Gallery )
	 * @param pickerListener 
	 * 				Picker Listener
	 * */
	protected void invokeImagePicker(int sourceType,ImagePicker pickerListener) {
		if(getActivity() instanceof AtnActivity) {
			((AtnActivity)getActivity()).invokeImagePicker(sourceType, pickerListener);
		}
	}
	
	/**
	 * Pop current fragment from stack 
	 * @param return true if fragment is pop out
	 * */
	protected boolean popBackStack() {
		if (this.childFragmentListener != null)
			return childFragmentListener.popBackStack(this);
		
			return false;
	}
		
	/**
	 * Google analytics.
	 * */
	public  final void  gASendEvent(String eventCatagory,String eventAction,String eventLabel) {
		AtnUtils.gASendEvent(getActivity(), eventCatagory, eventAction, eventLabel);
	}

	/**
	 * Override this method and return screen name <b>(string resource)</b> for google analytics
	 * 
	 * */
	protected int getScreenName() {
		return 0;
	}

}
