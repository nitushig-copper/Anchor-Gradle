/**
 * @Copyright Coppermobile 2014. 
 * */
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.adapters.LoopOfferAdapter;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.listener.AddFragmentFromAdpterListener;
import com.atn.app.task.AtnRegisterBarTask;
import com.atn.app.utils.AtnUtils;
/**
 * Show nearby businesses 
 * **/
public class LoopAtnOfferFragment extends AtnBaseFragment implements AddFragmentFromAdpterListener,OnRefreshListener {	
	
	private LoopOfferAdapter adapter;
	private List<AtnOfferData> venueList;
	private ListView venueOfferList;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private AlertDialog dialog = null;
	private TextView mBlankTextView;
	
	/**
     * Factory method to generate a new instance
     * */
	public static LoopAtnOfferFragment newInstance() {
		LoopAtnOfferFragment fragment = new  LoopAtnOfferFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		venueList = new ArrayList<AtnOfferData>();
		adapter = new LoopOfferAdapter(getActivity(), venueList);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setActionBarAlpha(ACTION_BAR_OPEQUE);
		setTitle(R.string.tips_text);
		View view = inflater.inflate(R.layout.loop_atn_offers_fragment, container, false);
		
		mSwipeRefreshWidget = (SwipeRefreshLayout)view.findViewById(R.id.tips_swipe_refresh_widget);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2);
		mBlankTextView = (TextView)view.findViewById(R.id.not_tips_blank_text_view);
		mBlankTextView.setVisibility(View.INVISIBLE);
		venueOfferList = (ListView)view.findViewById(R.id.lst_atn_offer);
		
		adapter.setAddFragmentFromAdpterListener(this);
		venueOfferList.setAdapter(adapter);
		
		//register Bar load listener
		AtnRegisterBarTask.getInstance().registerListener(mBarHandler);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//load from db
		AtnRegisterBarTask.getInstance().refreshDataFromDb();		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		//unregister Bar load listener
		AtnRegisterBarTask.getInstance().unRegisterListener(mBarHandler);
		super.onDestroyView();
	}
  
   
	//open bar detail or tip detail
	@Override
	public void addFragmentToStack(Fragment newFragment) {
		
		if(newFragment instanceof TipsDialog) {
			((TipsDialog)newFragment).show(getFragmentManager(), TipsDialog.TIPS_DIALOG);	
		} else {
			addToBackStack((AtnBaseFragment)newFragment);
		}
	}
	
	//Message send by AtnRegisterBarTask
	private Handler	mBarHandler = new Handler(Looper.getMainLooper(), new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what==1&&msg.obj!=null){
				@SuppressWarnings("unchecked")
				List<AtnOfferData> regBars = (List<AtnOfferData>) msg.obj;
				if(regBars.size() > 0) {
					venueList.clear();
					venueList.addAll(getAtnOfferData(regBars));
					adapter.notifyDataSetChanged();
				}
			} else {
				AtnUtils.showToast(msg.obj);
			}
			checkLoadingStatus();
			return false;
		}
	});
	
	//prepare data set for rows. 
	private List<AtnOfferData> getAtnOfferData(List<AtnOfferData> regBars) {
		  
    	List<AtnOfferData> offerData = new ArrayList<AtnOfferData>();
    	for (AtnOfferData atnBar : regBars) {
    		AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData)atnBar;
    		offerData.add(atnVenueData);
   			offerData.addAll(atnVenueData.getBulkPromotion());	
		}
    	return offerData;
    }
	
	//check to show loader
	private void checkLoadingStatus() {
		if((AtnRegisterBarTask.getInstance().getStatus() == AsyncTask.Status.FINISHED)&&venueList.size()==0) {
			//showContactDialog();
			mBlankTextView.setVisibility(View.VISIBLE);
		} else {
			if(dialog!=null&&dialog.isShowing()) {
				dialog.dismiss();
			}
			mBlankTextView.setVisibility(View.INVISIBLE);
		}
		mSwipeRefreshWidget.setRefreshing(AtnRegisterBarTask.getInstance().getStatus()!=Status.FINISHED);
	}
	
	@Override
	public void onRefresh() {
		if(AtnRegisterBarTask.getInstance().getStatus()==Status.FINISHED) {
			AtnRegisterBarTask.getInstance().setLoadFromServer(true);
	    	AtnRegisterBarTask.getInstance().refreshDataFromDb();
		}
	}
}
