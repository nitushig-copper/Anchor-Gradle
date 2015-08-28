/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.component.MyButton;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnOfferData;
import com.atn.app.datamodels.AtnRegisteredVenueData;
import com.atn.app.datamodels.IgMedia;
import com.atn.app.datamodels.ReviewTag;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.datamodels.AtnOfferData.VenueType;
import com.atn.app.fragments.CommentFragment.OnCommentCloseListener;
import com.atn.app.listener.AddFragmentListener;
import com.atn.app.listener.DialogClickEventListener;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.UiUtil;



/**
 * Show review of a venue and user can submit review on this screen
 * */
public class ReviewDialogFragment extends AtnBaseFragment implements OnClickListener {

	///
	private ReviewDialogListener mClickEventListener;
	
	
	AtnBaseFragment  atnBaseFragment;
	
	MyButton doneButton;
	
	/*Called after user has selected the tags
	 * **/
	public interface ReviewDialogListener extends DialogClickEventListener {
		/**
		 * @param tags selected tags
		 * */
		public void selectedReview(ArrayList<ReviewTag> tags);
	}
	
	//number review show to user
	public static final int MAX_REVIEW  = 5;
	//tag for fragment transaction
	public static final String REVIEW_SCREEN = "review_screen";
	//public static final String VENUE_ID = "venue_id";
	private List<ReviewTag> tagsList = null;
	private VenueModel mVenue;
//	private TagsAdapter tagAdapter = new TagsAdapter();
	private List<IgMedia> mMediaList = null;
	private ViewPager review_tag_pager;

	private int currentPageIndex=0;
	
	public boolean isShowingPopover;
	private TextView mCommentCountTextView = null;

	
	ArrayList<ReviewTag> selectedReview = new ArrayList<ReviewTag>();
 	
	public ReviewDialogFragment(AtnBaseFragment basefragment) {
		// TODO Auto-generated constructor stub
		atnBaseFragment=basefragment;
	}

	public static ReviewDialogFragment newInstance(AtnBaseFragment baseFragment,VenueModel venue) {
		Bundle bundle = new Bundle();
		ReviewDialogFragment fragment = new ReviewDialogFragment(baseFragment);
		//fragment.setVenue(venue);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * @param mClickEventListener the mClickEventListener to set
	 */
	public void setClickEventListener(ReviewDialogListener mClickEventListener) {
		this.mClickEventListener = mClickEventListener;
	}

	/**
	 * @param mVenue the mVenue to set
	 */
	public void setVenue(VenueModel venue) {
		this.mVenue = venue;
		mMediaList = venue.getInstagramMedia();

	}

//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		final Dialog dialog = super.onCreateDialog(savedInstanceState);
//		dialog.getWindow().getAttributes().windowAnimations = R.style.map_overlay;
//		isShowingPopover=true;
//		return dialog;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setStyle(0, R.style.map_overlay);
		tagsList = Atn.ReviewTable.getReviewTag(getActivity(), mVenue);
//		if(tagsList!=null) {
//			//add cancel button
//			ReviewTag tag = new ReviewTag();
//			tag.setViewType(ReviewTag.ViewType.CANCEL);
//			tag.setName("Cancel");
//			tagsList.add(tagsList.size()>0?tagsList.size()-1:0, tag);
//			
//			//add done button
//			tag = new ReviewTag();
//			tag.setViewType(ReviewTag.ViewType.DONE);
//			tag.setName("Done");
//			tagsList.add(tag);
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.review_fragment_layout,
				container, false);
		
		review_tag_pager=(ViewPager)view.findViewById(R.id.review_tag_pager);
//		 mPagerAdapter = new TagPagerAdapter();
		ReviewTagPagerAdapetr  mPagerAdapter = new ReviewTagPagerAdapetr(getChildFragmentManager());

		review_tag_pager.setAdapter(mPagerAdapter);

		view.findViewById(R.id.tag_cancel_button).setOnClickListener(this);
		mCommentCountTextView=(TextView)view.findViewById(R.id.tag_add_comment_button);
		mCommentCountTextView.setOnClickListener(this);
		doneButton = (MyButton)view.findViewById(R.id.tag_done_button);
		doneButton.setOnClickListener(this);
		setTitle("");
		setActionBarAlpha(0);
		
		setcommentCount();
		return view;
	}

	
	private void setcommentCount()
	{
		
		AtnOfferData barObjet = null;
		if(mVenue.getVenueId() != null) {
			barObjet = DbHandler.getInstance().getAtnBusinessDetail(mVenue.getVenueId() );
			//if still null then load from 
			if(barObjet==null) {
				barObjet = Atn.Venue.getVenue(mVenue.getVenueId() ,AtnApp.getAppContext());	
			}
		} else 
			barObjet = DbHandler.getInstance().getAtnBusinessDetail(mVenue.getVenueId() );
		
		
		if (barObjet.getDataType() == VenueType.ANCHOR) {
			AtnRegisteredVenueData atnVenueData = (AtnRegisteredVenueData) barObjet;
			mCommentCountTextView.setText(atnVenueData.getFsVenueModel().getCommentCount()+"");
		} else {
			mCommentCountTextView.setText(((VenueModel)barObjet).getCommentCount()+"");
			
		}
	
	
	
	
	
		
	}
	
//	MOHAR: set done button enable/dissable base on tag selection 
	private void changeDoneBtnStats() {
		
		if(selectedReview.size()>0)
			doneButton.setEnabled(true);
		else
			doneButton.setEnabled(false);
			
		
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tag_cancel_button:
			cancelButtonClick();
			break;
		case R.id.tag_add_comment_button:
			addCommentButtonClick();
			break;
		case R.id.tag_done_button:
			doneButtonClick();
			
	break;

		default:
			break;
		}
		
	}
	
	// MOHAR : Done Button Action 
	private void doneButtonClick()
	{
		if(!AtnUtils.isConnected(getActivity())){
			UiUtil.showToast(getActivity(), R.string.no_internet_available);
			return;
		}
		
		//if user select greater the zero then dismiss and called listener
		if(selectedReview.size()>0) {
			popBackStack();
			if(mClickEventListener!=null) {
				mClickEventListener.onClick(ReviewTag.ViewType.DONE);
				mClickEventListener.selectedReview(selectedReview);
			}
		}
	}
	
	// MOHAR : Cancel Button Action 	
	private void cancelButtonClick()
	{
		popBackStack();
		if(mClickEventListener!=null) {
			mClickEventListener.onClick(ReviewTag.ViewType.DONE);
		}
	}
	
	// MOHAR : Add Comment Button Action 	
	private void addCommentButtonClick()
	{
//		((VenueDetailFragment)atnBaseFragment).addCommentButtonClick();
		
		CommentFragment commentFragment = new CommentFragment();
		commentFragment.setOnCloseListener(null);
		Bundle bundle = new Bundle();
		bundle.putString(CommentFragment.VENUE_ID, mVenue.getVenueId());
		bundle.putBoolean(CommentFragment.IS_SMALL_VIEW, false);
		bundle.putBoolean(CommentFragment.IS_SHOUL_SEND_SINGLE, true);
		commentFragment.setArguments(bundle);
		addToBackStack(commentFragment);
		
	}
	
	public class TagsAdapter extends BaseAdapter {
		int currentIndex;
		public TagsAdapter(int index) {
			currentIndex=index;
		}
		
		public final int getCount() {
			
			if(tagsList.size() > (currentIndex+1)*9)
				return 9;
			else
				return tagsList.size()-(currentIndex*9);
			
//			
//			if(tagsList!=null){
//				return tagsList.size();
//			}
//			return 0;
		}
		public final Object getItem(int position) {
			if(tagsList!=null){
				return tagsList.get(currentIndex*9+position);
			}
			return null;
		}
		public final long getItemId(int position) {
			return position;
		}
		
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			TagHolder holder;
			if (convertView == null) {
				holder =new  TagHolder();
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.tag_layout, parent, false);
				holder.mTagCountButton = (Button)view.findViewById(R.id.tag_count_button);
				holder.mTagName = (TextView)view.findViewById(R.id.tag_name_text_view);
				view.setTag(holder);
			} 
			holder = (TagHolder) view.getTag();
			
			final int tagInddex=currentIndex*9+position;
			
			ReviewTag tag = tagsList.get(tagInddex);
			if (tag.getViewType()==ReviewTag.ViewType.DONE) {
				holder.mTagCountButton.setText("");
				holder.mTagCountButton.setBackgroundResource(R.drawable.icn_review_done);
				holder.mTagName.setText(tag.getName());
				holder.mTagName.setTextColor(getResources().getColor(R.color.tag_unselected));
			} else if (tag.getViewType()==ReviewTag.ViewType.CANCEL) {
				holder.mTagCountButton.setBackgroundResource(R.drawable.icn_review_cancel);
				holder.mTagCountButton.setText("");
				holder.mTagName.setText(tag.getName());
				holder.mTagName.setTextColor(getResources().getColor(R.color.tag_unselected));
			} else {
				holder.mTagName.setTextColor(Color.WHITE);
				holder.mTagCountButton.setText(String.valueOf(tag.getReviewCount()));
				holder.mTagName.setText(tag.getName());
				holder.mTagCountButton.setSelected(selectedReview.contains(tag));
			}
			
			holder.mTagCountButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClicked(tagInddex);
					notifyDataSetChanged();
					changeDoneBtnStats();
				}
			});
			return view;
		}
		
		public class TagHolder {
			private Button mTagCountButton;
			private TextView mTagName;
		}
	}
	
	//called when tag clicked
	//@param pisition of the tag 
	public void onItemClicked(int position) {
		ReviewTag tag = tagsList.get(position);
		
//		MOHAR: Remove check condition for DONE/CANCEL 
		//if item type done  
//		if (tag.getViewType()==ReviewTag.ViewType.DONE) {
//			
//			if(!AtnUtils.isConnected(getActivity())){
//				UiUtil.showToast(getActivity(), R.string.no_internet_available);
//				return;
//			}
//			
//			//if user select greater the zero then dismiss and called listener
//			if(selectedReview.size()>0) {
//				//dismiss();
//				if(mClickEventListener!=null) {
//					mClickEventListener.onClick(ReviewTag.ViewType.DONE);
//					mClickEventListener.selectedReview(selectedReview);
//				}
//			}
//		} else if (tag.getViewType()==ReviewTag.ViewType.CANCEL) {
//			//dismiss();
//			if(mClickEventListener!=null) {
//				mClickEventListener.onClick(ReviewTag.ViewType.DONE);
//			}
//		} else {
			// MOHAR: remove Tag max Limit 
			if(UserDataPool.getInstance().isUserLoggedIn()) {
				if(selectedReview.contains(tag)) {
					tag.setReviewCount(tag.getReviewCount()-1);
						selectedReview.remove(tag);
				} else  if(selectedReview.size() < MAX_REVIEW) {
					{
						tag.setReviewCount(tag.getReviewCount()+1);
						selectedReview.add(tag);
					}
				} else {
					UiUtil.showToast(getActivity(), R.string.max_review_limit_reached);
				}
				//adapter.notifyDataSetChanged();
			} else {
				AtnApp.showLoginScreen(getActivity());	
			}
//		}
	}
	
	
	 private class ReviewTagPagerAdapetr extends FragmentStatePagerAdapter {
	        public ReviewTagPagerAdapetr(FragmentManager fm) {
	            super(fm); 
	        }
	        
			@Override
			public int getCount() {
				if (tagsList != null) {
					if(tagsList.size()%9==0)
						return tagsList.size()/9; 
					return tagsList.size()/9+1;
				}
				//for default
				return 1;
			}
			
	        @Override
	        public Fragment getItem(int position) {
	        	currentPageIndex=position;
		    	final ReviewTagPageFragment f = new ReviewTagPageFragment(position);

	    			return f;
	        }
	    }
	
	 
	 public class ReviewTagPageFragment extends Fragment {

		 int currentIndex;
		    /**
		     * Empty constructor as per the Fragment documentation
		     */
		    public ReviewTagPageFragment(int index) {
		    	currentIndex=index;
		    }

		    /**
		     * Populate image using a url from extras, use the convenience factory method
		     * {@link ImageDetailFragment#newInstance()} to create this fragment.
		     */
		    @Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);		        
		    }

		    @Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container,
		            Bundle savedInstanceState) {
				View view = inflater.inflate(R.layout.review_tag_page, container, false);
				 GridView grid = (GridView) view.findViewById(R.id.review_tag_page_gridview);
		    	 TagsAdapter tagAdapter = new TagsAdapter(currentIndex);
				grid.setAdapter(tagAdapter);
				return view;

		    }

		    @Override
		    public void onActivityCreated(Bundle savedInstanceState) {
		        super.onActivityCreated(savedInstanceState);
			       
		    }

		    @Override
		    public void onDestroy() {
		        super.onDestroy();
		       
		    }
		}

	
	 
}
