
/***
 * @Coppermobile India pvt. ltd. 2014
 * */
package com.atn.app.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atn.app.R;
import com.atn.app.datamodels.Comment;
import com.atn.app.datamodels.UserDetail;
import com.atn.app.datamodels.VenueModel;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.HttpUtility.ImageType;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;
import com.squareup.picasso.Picasso;

/**
 * Show venue comments
 * **/
public class CommentFragment extends AtnBaseFragment implements OnClickListener {

	//fragment is shown on venue detail screen or shown in full screen
	public static final String IS_SMALL_VIEW = "IS_SMALL_VIEW";
	public static final String IS_SHOUL_SEND_SINGLE = "IS_SHOUL_SEND_SINGLE";
	
	public static final String VENUE_ID = "venue_id"; 
	
	//comment close button listener
	public interface OnCommentCloseListener {
		public void onClose(boolean showBigView);
	}
	
	private OnCommentCloseListener mOnCloseListener = null;
	public void setOnCloseListener(OnCommentCloseListener onCloseListener) {
		this.mOnCloseListener = onCloseListener;
	}
	
	//fragment is shown on venue detail screen or shown in full screen
	private boolean isSmallView = false;
	private boolean shoulClose = false;
	private ImageButton mCloseButton; 
	private EditText mEditText;
	private ListView mCommentListView;
	private String venueId;
	private ImageButton mCommentButton;
	private CommentAdapter mCommentAdapter = null;
		
	private VenueModel mVenueModel;
	//comment load task from server
	private LoadCommentTask mLoadCommentTask;
	private ArrayList<Comment> mComments = new ArrayList<Comment>();
	private ProgressBar mProgressBar = null;
	
	public void setVenueModel(VenueModel venueModel) {
		this.mVenueModel = venueModel;
	}
	
	public static CommentFragment newInsatnce(OnCommentCloseListener onCloseListener) {
		CommentFragment fragment = new CommentFragment();
		fragment.setOnCloseListener(onCloseListener);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments()!=null) {
			 isSmallView = getArguments().getBoolean(IS_SMALL_VIEW, false);
			 shoulClose = getArguments().getBoolean(IS_SHOUL_SEND_SINGLE, false);
			 venueId = getArguments().getString(VENUE_ID);
		}
		
		mCommentAdapter = new CommentAdapter(getActivity(), 0, mComments);
		
		//if venue is not set then fetch from database
		if(this.mVenueModel==null) {
			this.mVenueModel = Atn.Venue.getVenue(venueId, getActivity());
		}
		startCommentLoadTask();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		RelativeLayout view = (RelativeLayout)inflater.inflate(R.layout.comment_layout, container, false);
		
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		
		mEditText = (EditText)view.findViewById(R.id.comment_edit_text_view);
		mCommentButton = (ImageButton)view.findViewById(R.id.comment_button);
		mCommentListView = (ListView)view.findViewById(R.id.comment_list_view);
		mCloseButton = (ImageButton)view.findViewById(R.id.close_comment_button);
		mCloseButton.setOnClickListener(this);
		mCommentButton.setOnClickListener(this);
		
		mCommentListView.setAdapter(mCommentAdapter);
		
		TextView textView = new  TextView(getActivity());
		textView.setTextColor(Color.BLACK);
		textView.setText("No Comment!");
		mCommentListView.setEmptyView(textView);
		
		if(isSmallView) {
			mEditText.setFocusable(false);
			mEditText.setOnClickListener(this);
		} //else {
			
		setCommentButtonSelectedState(false);

		//}
		
		if(!isSmallView) {
			view.setPadding(0, getResources().getDimensionPixelSize(android.R.dimen.app_icon_size), 0, 0);
			setTitle(R.string.comment_title);
			setActionBarAlpha(ACTION_BAR_OPEQUE);
			view.findViewById(R.id.title_text_view).setVisibility(View.GONE);
			mCloseButton.setVisibility(View.GONE);
		}
		
		mEditText.addTextChangedListener(mTextWatcher);
		
		return view;
	}

	// MOHAR : change Comment Button state according to textFeild Text
	private void setCommentButtonSelectedState(Boolean isSelected) {
		if(isSelected) {
			mCommentButton.setSelected(false);
			mCommentButton.setBackgroundResource(R.drawable.blue_bg);
			
		} else {
			mCommentButton.setSelected(true);
			mCommentButton.setBackgroundResource(R.drawable.blue_dissable_bg);
		}
	}
	
	TextWatcher mTextWatcher=new TextWatcher(){
	   
	    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	    public void onTextChanged(CharSequence s, int start, int before, int count){}
		@Override
		public void afterTextChanged(Editable s) {

			if(s.length() > 0)
				setCommentButtonSelectedState(true);
			else
				setCommentButtonSelectedState(false);
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadCommentFromServer();
		if(!isSmallView) {
			mEditText.post(new Runnable() {
				@Override
				public void run() {
					AtnUtils.showKeyboard(getActivity(),mEditText);
				}
			});
		}
	}
	
	
	@Override
	public void onPause() {
		if(!isSmallView) {
			AtnUtils.hideKeyboard(getActivity());	
		}
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		stopCommentLoadTask();
		super.onDestroyView();
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.comment_edit_text_view: {
				if(mOnCloseListener!=null) mOnCloseListener.onClose(true);
                			getFragmentManager().popBackStack();
			}
				break;
			case R.id.comment_button: {
				if(isSmallView) {
					if(mOnCloseListener!=null) mOnCloseListener.onClose(true);
        			         getFragmentManager().popBackStack();
				} else {
					commentButtonClick();
				}
			}
				break;
				
			case R.id.close_comment_button: {
				if(mOnCloseListener!=null) mOnCloseListener.onClose(false);
                			getFragmentManager().popBackStack();
			}
				break; 
		default:
			break;
		}
	}
	//load 
	private void loadCommentFromServer() {
		
		
		if(mComments.size()==0){
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
				HttpUtility.buildGetMethodWithAppParams().appendPath(ApiEndPoints.GET_COMMENTS),
				Method.GET, new AnchorHttpResponceListener() {
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
				//save in db
				if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {
					Atn.CommentTable.insertOrUpdate(getActivity(), jsonObject);
				}
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {
				if(isVisible()) {
					mProgressBar.setVisibility(View.GONE);
					startCommentLoadTask();
				}
			}
			@Override
			public void onError(Exception ex) {
				if(isVisible()) {
					mProgressBar.setVisibility(View.GONE);
					UiUtil.showToast(getActivity(), ex.getMessage());
				}
			}
		});
		anchorRequest.addText(JsonUtils.VenuePicUpload.FS_VENUE_ID, mVenueModel.getVenueId());
		anchorRequest.execute();
	}
	
	//new comment button click post comment on venue
	private void commentButtonClick() {
		
		if(!UserDataPool.getInstance().isUserLoggedIn()) {
			UiUtil.showToast(getActivity(), "Loggin For Comment");
			return;
		}
		
		if (!AtnUtils.isConnected(getActivity())) {
			UiUtil.showToast(getActivity(), "No internet");
			return;
		}
		
		if(TextUtils.isEmpty(mEditText.getText().toString())) return;
		
		
		if(shoulClose)
			mEditText.setEnabled(false);
		
		AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
				HttpUtility.buildBaseUri().appendPath(ApiEndPoints.POST_COMMENT),
				Method.POST, new AnchorHttpResponceListener() {
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
				if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS) {	
					if(JsonUtils.resultCode(jsonObject)==JsonUtils.ANCHOR_SUCCESS){
						try {
							JSONObject commentjObj = jsonObject.getJSONObject(JsonUtils.RESPONSE).getJSONObject(JsonUtils.DATA);
							
							ContentValues values = new ContentValues();
							values.put(Atn.CommentTable.COMMENT_ID, commentjObj.getInt(JsonUtils.CommentKey.ID));
							values.put(Atn.CommentTable.TEXT, commentjObj.getString(JsonUtils.CommentKey.COMMENT));
							values.put(Atn.CommentTable.COMMENT_DATE, AtnUtils.convertInMillisecond(commentjObj.getString(JsonUtils.CommentKey.CREATED)));
							values.put(Atn.CommentTable.FS_VENUE_ID, commentjObj.getString(JsonUtils.CommentKey.FS_VENUE_ID));
							
							UserDetail user = UserDataPool.getInstance().getUserDetail();
							values.put(Atn.CommentTable.USER_ID, user.getUserId());
							values.put(Atn.CommentTable.USER_NAME, user.getUserName());
							values.put(Atn.CommentTable.USER_PHOTO, user.getImageUrl());
							Atn.CommentTable.insertComment(getActivity(), values);
							
							
							if(shoulClose)
								popBackStack();
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {
				AtnUtils.log(jsonObject.toString());
			}
			@Override
			public void onError(Exception ex) {
			}
		});
		
		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		anchorRequest.addText(JsonUtils.CommentKey.TEXT,HttpUtility.encode(mEditText.getText().toString()));
		
		HttpUtility.addVenueParams(getActivity(), anchorRequest, mVenueModel);
        
		anchorRequest.execute();
		//add to list.
		Comment newComment = new Comment();
		newComment.setCommentId(0);
		newComment.setCommentText(mEditText.getText().toString());
		newComment.setUserIconUrl(UserDataPool.getInstance().getUserDetail().getImageUrl());
		newComment.setUserName(UserDataPool.getInstance().getUserDetail().getUserName());
		newComment.setUserId(Integer.valueOf(UserDataPool.getInstance().getUserDetail().getUserId()));
		
		mComments.add(newComment);
		mCommentAdapter.notifyDataSetChanged();
		mCommentListView.smoothScrollToPosition(mComments.size()-1);
		
		mEditText.setText("");
		
		mVenueModel.setCommentCount(mVenueModel.getCommentCount()+1);
		ContentValues values = new ContentValues();
		values.put(Atn.Venue.VENUE_ID, venueId);
		values.put(Atn.Venue.COMMENT_COUNT, mVenueModel.getCommentCount());
		
		if(Atn.Venue.update(values, getActivity())==0) {
			Atn.Venue.insertOrUpdateSearchedVenue(mVenueModel, getActivity());
		};
		
	}
	
	
	//start comment loading task 
	private void startCommentLoadTask() {
		stopCommentLoadTask();
		mLoadCommentTask = new LoadCommentTask();
		mLoadCommentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,venueId);
	}
	
	//stop comment loading task
	private void stopCommentLoadTask() {
		if(mLoadCommentTask!=null&&mLoadCommentTask.getStatus()!=AsyncTask.Status.FINISHED){
			mLoadCommentTask.cancel(true);
		}
		mLoadCommentTask = null;
	}
	
	/**
	 * Load comment task from db
	 * **/
	private class LoadCommentTask extends AsyncTask<String, Comment, ArrayList<Comment>>{
		@Override
		public ArrayList<Comment> doInBackground(String... params) {
			return Atn.CommentTable.getAllComment(getActivity(), params[0]);
		}
		@Override
		public void onPostExecute(ArrayList<Comment> result) {
			super.onPostExecute(result);
			if(result!=null) {
				mComments.clear();
				mComments.addAll(result);
				mCommentAdapter.notifyDataSetChanged();
				if(mComments.size()>0) {
					mProgressBar.setVisibility(View.GONE);	
				}
			}
		}
	}
	
	
	/*
	 * Comment cursor adapter
	 * ***/
	
	public class CommentAdapter extends ArrayAdapter<Comment> {

		public CommentAdapter(Context context, int resource, List<Comment> objects) {
			super(context, resource, objects);
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view  = convertView;
			CommentHolder holder;
			if(view==null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 view  = inflater.inflate(R.layout.comment_row_layout, parent, false);
			    holder = new  CommentHolder();
				holder.mUserPicImageView = (ImageView)view.findViewById(R.id.user_pic_image_view);
				holder.mUserNameTextView = (TextView)view.findViewById(R.id.user_name_text_view);
				holder.mCommentTextView = (TextView)view.findViewById(R.id.comment_text_view);
				view.setTag(holder);
			} else {
				holder = (CommentHolder) view.getTag();
			}
			Comment comment = getItem(position);
			//holder.mUserPicImageView
			holder.mUserNameTextView.setText(comment.getUserName());
			holder.mCommentTextView.setText(comment.getCommentText());
			
			//load picture
			if(!TextUtils.isEmpty(comment.getUserIconUrl())) {
				
				AtnUtils.log(HttpUtility.getUserImageUrl(comment.getUserIconUrl(), ImageType.S));
				Picasso.with(getContext())
				.load(HttpUtility.getUserImageUrl(comment.getUserIconUrl(), ImageType.S))
				.resizeDimen(R.dimen.tips_bar_image_size,
						R.dimen.tips_bar_image_size)
						.placeholder(R.drawable.user_img_holder)
				.into(holder.mUserPicImageView);
			} else {
				holder.mUserPicImageView.setImageResource(R.drawable.user_img_holder);
			}
			
			return view;
		}

		private class CommentHolder {
			ImageView mUserPicImageView;
			TextView mUserNameTextView;
			TextView mCommentTextView;
		} 
	}
}
