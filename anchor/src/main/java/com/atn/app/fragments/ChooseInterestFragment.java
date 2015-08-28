/**
 * @Copyright Coppermobile 
 * **/
package com.atn.app.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.MainMenuActivity;
import com.atn.app.datamodels.AnchorCategory;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.provider.Atn;
import com.atn.app.service.SynchService;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.UiUtil;

/**
 * user can choose interest on basis of that we will filter venue on happening now and map screens
 * */
public class ChooseInterestFragment extends AtnBaseFragment implements OnItemClickListener{
	
	//top level category or user interest
	private ArrayList<AnchorCategory> mCategories;
	private InterestAdapter mAdapter1,mAdapter2;
	private ProgressBar mProgressBar;
	private DialogClickEventListener listener;
	private EditText searchTextET;
	
	public void setDoneClickEventListener(DialogClickEventListener listener){
		this.listener = listener;	
	}
	
	public static AtnBaseFragment newInstance() {
		return new  ChooseInterestFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		//set title if fragment is replace from main menu
		if(getActivity() instanceof AtnActivity) {
			setActionBarAlpha(ACTION_BAR_TRANSPARENT);
			setTitle(R.string.my_interest_text);
		}
		
		View view = inflater.inflate(R.layout.choose_interest_layout, container, false);
		///fetch main category
		mCategories = Atn.Category.populateCategories(getActivity(), true);
		mAdapter1 = new InterestAdapter(getActivity().getApplicationContext(),true);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		GridView  mGridView1 = (GridView) view.findViewById(R.id.category_grid_first);
		mGridView1.setOnItemClickListener(this);
		mGridView1.setAdapter(mAdapter1);
		
		mAdapter2 = new InterestAdapter(getActivity().getApplicationContext(),false);

		GridView  mGridView2 = (GridView) view.findViewById(R.id.category_grid_second);
		mGridView2.setOnItemClickListener(this);
		mGridView2.setAdapter(mAdapter2);
		
		//if there is no category then and synch service is not running then we start service for download category
		if(mCategories.size()==0) {
			if(!SynchService.IS_RUNNING) {
				AtnUtils.runSynchService(getActivity(), SynchService.Command.RELOAD_VENUE);	
			}
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		String searchText = SharedPrefUtils.getSearchText(getActivity());
		searchTextET=(EditText) view.findViewById(R.id.choose_category_search_et);
		searchTextET.setText(searchText);
		LinearLayout doneLayout= (LinearLayout) view.findViewById(R.id.choose_category_done_btn);
		doneLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doneBtnClickEvent();
			}
		});

		
		RelativeLayout clearBtn= (RelativeLayout)view.findViewById(R.id.choose_category_clear_btn);
		clearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearBtnAction();
			}
		});
		
		
		searchTextET.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                	
                    doneBtnClickEvent();

                    handled = true;
                }
                return handled;
            }
        });
		
		prepareCatgories();
		return view;
	}
	

	@Override
	public void onResume() {
		IntentFilter filter = new IntentFilter();
	    filter.addAction(SynchService.ACTION_SERVICE);
	    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
		super.onPause();
	}
	
	private void clearBtnAction() {
		
		SharedPrefUtils.saveSearchText(getActivity(), "");
		searchTextET.setText("");
		
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchTextET.getWindowToken(), 0);
	}
	
	//Receiver for category load
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SynchService.ACTION_SERVICE)) {
				mProgressBar.setVisibility(View.GONE);
				if (intent.getIntExtra(SynchService.STATUS, 0) == SynchService.CATEGORY_LAODED) {
					mCategories = Atn.Category.populateCategories(getActivity(), true);
					prepareCatgories();
					mAdapter1.notifyDataSetChanged();
					mAdapter2.notifyDataSetChanged();					
				} else if (intent.getIntExtra(SynchService.STATUS, 0) == SynchService.FAIL&&mCategories.size()==0) {
					AtnUtils.showToast(intent.getStringExtra(SynchService.MESSAGE));
				}
			}
		}
    };
    
    //add done button (category object for Done Button)
    private void prepareCatgories() {
    	if(mCategories.size()>0) {
//    		AnchorCategory mCategory = new AnchorCategory();
//    		mCategory.name = "Done";
//    		mCategory.categoryType = AnchorCategory.CategoryType.DONE_BUTTON;
//    		mCategories.add(mCategory);
    	}
    	
//    	// Enable first Category if nothing is selected 
//    	if(mCategories.size() > 1 && countSelectedCategory()==0) {
//    		AnchorCategory category = mCategories.get(0);
//    		category.status=1-category.status;
//    	}
    	
    }
    
    /**
     * Adapter for Categories
     * */
    private class InterestAdapter extends BaseAdapter {

    	Boolean isForFirstRow;
    	private Context mContext;
    	public InterestAdapter(Context mContext,Boolean firstRow){
    		this.mContext = mContext;
    		this.isForFirstRow = firstRow;
    	}
		@Override
		public int getCount() {
			if(mCategories!=null){
				if(this.isForFirstRow)
					return mCategories.size() >= 3 ? 3 : mCategories.size();
				else
					return mCategories.size() >= 5 ? 2 : mCategories.size()-3;
							
//				return mCategories.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mCategories!=null){
				return mCategories.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			
			int position = 0 ;
			if(isForFirstRow)
				position = index;
			else
				position = index + 3;
			
			View view  = convertView;
			InterestHolder holder = null;
			if (view == null) {
				holder = new InterestHolder();
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (LinearLayout)inflater.inflate(R.layout.choose_interest_cell, parent, false);
				holder.mInterestImageView = (ImageView)view.findViewById(R.id.interest_button);
				holder.mInterestNameTextView = (TextView)view.findViewById(R.id.interest_name_text_view);
				view.setTag(holder);
			} else {
				holder = (InterestHolder) view.getTag();
			}
			
			holder.position=position;
			AnchorCategory category = mCategories.get(position);
			//chec
			if(category.categoryType==AnchorCategory.CategoryType.DONE_BUTTON) {
				int count = countSelectedCategory();
				holder.mInterestImageView.setBackgroundResource(0);
				holder.mInterestImageView.setImageResource(count==0?
						R.drawable.icn_interest_done_inactive:R.drawable.icn_interest_done);
				holder.mInterestImageView.setEnabled(count>0);
			} else {
				holder.mInterestImageView.setBackgroundResource(R.drawable.interest_button_selector);
				holder.mInterestImageView.setImageResource(getImageId(category.getCategoryId()));
				holder.mInterestImageView.setSelected(category.status>0);
				
				if (category.status > 0) {
					holder.mInterestImageView.setAlpha(1.0f);
				} else {
					holder.mInterestImageView.setAlpha(0.7f);
				}
			}
			holder.mInterestNameTextView .setText(category.name);

			return view;
		}
		
		private class InterestHolder {
			TextView mInterestNameTextView;
			ImageView mInterestImageView;
			int position;
		}
    }

    //return category logo res id
	private int getImageId(int id) {

		switch (id) {
		case 1:
			return R.drawable.icn_interest_concert;
		case 63:
			return R.drawable.icn_interest_restaurant;
		case 291:
			return R.drawable.icn_interest_nightlife;
		case 314:
			return R.drawable.icn_interest_livemusic;
		case 391:
			return R.drawable.icn_interest_travel;
		default:
			return R.drawable.icn_interest_concert;
		}
    }
    

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int index,
			long id) {
		
		InterestAdapter.InterestHolder holder = null;
		holder = (InterestAdapter.InterestHolder) view.getTag();
		
		int position  = holder.position;
		
		AnchorCategory category = mCategories.get(position);
		if(category.categoryType!=AnchorCategory.CategoryType.DONE_BUTTON){
			category.status=1-category.status;
			mAdapter1.notifyDataSetChanged();
			mAdapter2.notifyDataSetChanged();	
		} else {
			if(countSelectedCategory()>0) {
				//remove done button and update category in database
				//mCategories.remove(mCategories.size()-1);
				SharedPrefUtils.saveFilterQuery(getActivity(), mCategories);
				Atn.Category.updateStatus(getActivity(), mCategories);
				
				//if fragment fush from main mainMenu Screen then pop it out.
				if(getActivity() instanceof MainMenuActivity) {
					popBackStack();
				} else {
					getActivity().setResult(Activity.RESULT_OK);
					getActivity().finish();
				}
				
				//call listener
				if(this.listener!=null) {
					this.listener.onClick(AnchorCategory.CategoryType.DONE_BUTTON);
				}
			}
		}
	}
	
	private void doneBtnClickEvent() {
		
		AtnUtils.hideKeyboard(getActivity());

		SharedPrefUtils.saveSearchText(getActivity(), searchTextET.getText().toString());
				
		if(countSelectedCategory()>0) {
			//remove done button and update category in database
			//mCategories.remove(mCategories.size()-1);
			SharedPrefUtils.saveFilterQuery(getActivity(), mCategories);
			Atn.Category.updateStatus(getActivity(), mCategories);
			
			//if fragment fush from main mainMenu Screen then pop it out.
			if(getActivity() instanceof MainMenuActivity) {
				popBackStack();
			} else {
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}
			
			//call listener
			if(this.listener!=null) {
				this.listener.onClick(AnchorCategory.CategoryType.DONE_BUTTON);
			}
		}
		else
			UiUtil.showToast(getActivity(), R.string.category_select_msg);

	}
	
    //return number of selected category count
	private int countSelectedCategory() {
		int count = 0;
		for (AnchorCategory category : mCategories) {
			if(category.categoryType!=AnchorCategory.CategoryType.DONE_BUTTON&&category.status==1) {
				count++;
			}
		}
		return count;
	}
	
//	//call appropriate function on keyboard done button press
//		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//		
//			if (actionId == EditorInfo.IME_ACTION_DONE) {
//				AtnUtils.hideKeyboard(getActivity());
//				doneBtnClickEvent();
//				return true;
//			}
//			return false;
//		}
//	
		
}
