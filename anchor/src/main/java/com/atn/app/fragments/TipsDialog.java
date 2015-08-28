/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.activities.AtnActivity;
import com.atn.app.activities.AtnActivity.FacebookLoginListener;
import com.atn.app.database.handler.DbHandler;
import com.atn.app.datamodels.AtnPromotion;
import com.atn.app.datamodels.PushNotificationData;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.httprequester.AnchorHttpRequest;
import com.atn.app.httprequester.AnchorHttpRequest.AnchorHttpResponceListener;
import com.atn.app.httprequester.AnchorHttpRequest.Method;
import com.atn.app.httprequester.ApiEndPoints;
import com.atn.app.httprequester.AtnBarRequest;
import com.atn.app.pool.UserDataPool;
import com.atn.app.provider.Atn;
import com.atn.app.utils.AnchorProgressDialog;
import com.atn.app.utils.AtnTask;
import com.atn.app.utils.AtnUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.JsonUtils;
import com.atn.app.utils.UiUtil;
import com.atn.app.webservices.AccessTokenUpdateWebservice;
import com.atn.app.webservices.AccessTokenUpdateWebserviceListener;
import com.facebook.model.GraphUser;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.squareup.picasso.Picasso;


/*
 * Show tips
 * **/
public class TipsDialog extends DialogFragment implements OnClickListener, FacebookLoginListener{

	
	public OfferDetailFragment mDetailFragment = null;
	//fragment key
	public static final String TIPS_DIALOG = "TIPS_DIALOG";
	//tip object
	private AtnPromotion mPromotion = null;
	private PushNotificationData mGcmMessage = null;
	private ImageView mPromotionImageView;
	private TextView mTitleTextView;
	private TextView mDetailTextView;
	private TextView mStartTimeTextView;
	private TextView mEndTimeTextView;
	private Button mCloseButton;
	private Button mShareButton;
	
	private FacebookSession fbSession;
	private ProgressBar mProgressBar = null;
	
	private LoadTipsTask mLoadTipTask;
	
	/**
	 * @param mPromotion the mPromotion to set
	 */
	public void setPromotion(AtnPromotion promotion) {
		this.mPromotion = promotion;
	}
	
	public void setGcmMessage(PushNotificationData gcmMessage){
		this.mGcmMessage = gcmMessage;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(0, R.style.tips_dialog);
		fbSession = new FacebookSession(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.tips_dialog_layout, container, false);
		mPromotionImageView = (ImageView)view.findViewById(R.id.tip_image);
		
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar); 
		mTitleTextView = (TextView)view.findViewById(R.id.tips_title_text_view);
		mDetailTextView = (TextView)view.findViewById(R.id.tips_detail_text_view);
		mStartTimeTextView = (TextView)view.findViewById(R.id.start_time_text_view);
		mEndTimeTextView = (TextView)view.findViewById(R.id.end_time_text_view);
		
		mShareButton = (Button)view.findViewById(R.id.tips_share_button);
		mCloseButton = (Button)view.findViewById(R.id.close_button);
		mShareButton.setOnClickListener(this);
		mCloseButton.setOnClickListener(this);
		
		return view;
	}

	
	@Override
	public void onActivityCreated(Bundle arg0) {
		//if its open by due to gcm message then load tips from server
		if(this.mGcmMessage!=null) {
			mLoadTipTask = new LoadTipsTask();
			mLoadTipTask.execute(this.mGcmMessage.getPromotionId(),this.mGcmMessage.getId());
		} else {
			setDataInViews();
		}
		super.onActivityCreated(arg0);
	}
	
	//set data in views
	private void setDataInViews() {

		if (mPromotion != null) {
			Picasso.with(getActivity()).load(mPromotion.getPromotionImageSmallUrl()).placeholder(R.drawable.empty_photo).into(mPromotionImageView);
			mTitleTextView.setText(mPromotion.getPromotionTitle());
			mDetailTextView.setText(mPromotion.getPromotionDetail());
			
			AtnUtils.log(mPromotion.getStartDate());
			mStartTimeTextView.setText(AtnUtils.getTweleveHourTimeWith2DigtYear(mPromotion.getStartDate()));
			mEndTimeTextView.setText(AtnUtils.getTweleveHourTimeWith2DigtYear(mPromotion.getEndDate()));
			
			updateTipsVisitStatus();
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.close_button:
			dismiss();
			break;
		case R.id.tips_share_button:
			showFacebookShareDialog();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		 cancelTask();
		super.onDestroy();
	}
	//cancel task if runnings
	private void cancelTask() {
		if(mLoadTipTask!=null&&mLoadTipTask.getStatus()!=AtnTask.Status.FINISHED){
			mLoadTipTask.cancel(true);
		}
	}
	 /**
     * Shows a dialog to ask user whether he wants to share this venue on his/her Facebook wall.
     */
    private void showFacebookShareDialog() {
    	
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.share_title);
        alertDialog.setMessage(R.string.share_on_facebook);
        alertDialog.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
				if (TextUtils.isEmpty(fbSession.getAccessToken())) {
					((AtnActivity) getActivity()).postOnFacebookClick(TipsDialog.this);
					return;
				}
				
				if(mPromotion!=null) {
					new ShareTask().execute(mPromotion.getPromotionId());	
				} else {
					AtnUtils.showToast("Please Wait while Tip is loading");
				}
            }
        });
     
        alertDialog.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
	
	@Override
	public void onFacebookLogin(boolean isSuccess, String message) {
		if (isSuccess) {
			// show progress
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
			UserDataPool.getInstance().getUserDetail()
					.setUserFbToken(fbSession.getAccessToken());
			AccessTokenUpdateWebservice tokenUpdateService = new AccessTokenUpdateWebservice(
					UserDataPool.getInstance().getUserDetail().getUserId());
			tokenUpdateService
					.setUpdateAccessTokenWebserviceListener(mAccessTokenUpdateWebserviceListener);
			tokenUpdateService.updateFacebookToken(fbSession.getAccessToken(),
					null);
		}
	}

	@Override
	public void onUserInfoFetch(GraphUser user) {
	}
	
	
	private  class ShareTask extends AtnTask<String, Void, String> {
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			AnchorProgressDialog.show(getActivity(), R.string.please_wait);
		}
		
		@Override
		public String doInBackground(String... params) {
			AtnBarRequest newShareRquest = new AtnBarRequest(AtnApp.getAppContext());
			if(!newShareRquest.sharePromotion(params[0])) {
				return newShareRquest.getError();
			}
			return null;
		}
		
		@Override
		public void onPostExecute(String result) {
			super.onPostExecute(result);
			AnchorProgressDialog.conceal();
			if(result!=null) {
				AtnUtils.showToast(result);
			} else {
				 AtnApp.showMessageDialog(getActivity(), getActivity().getString(R.string.venue_shared_on_wall), false);
			}
		}
    }    
	 /**
     * Listen for the Facebook access token update web service notifications. If
     * access token is updated then share the business on Facebook wall.
     */
    private AccessTokenUpdateWebserviceListener mAccessTokenUpdateWebserviceListener = new AccessTokenUpdateWebserviceListener() {
        @Override
        public void onSuccess(String message) {
        	AnchorProgressDialog.conceal();
            UserDataPool.getInstance().getUserDetail().setUserFbToken(fbSession.getAccessToken());
            new ShareTask().execute(mPromotion.getPromotionId());
        }
 
        @Override
        public void onFailed(int errorCode, String errorMessage) {
        	AnchorProgressDialog.conceal();
            AtnApp.showMessageDialog(getActivity(), errorMessage, false);
        }
    };
    
    //load tips from server task and save into db
    private class LoadTipsTask extends AtnTask<String, Void, AtnPromotion>{
    	 
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }
 
        @Override
        public AtnPromotion doInBackground(String... params) {
            AtnPromotion promotionData = null;
            String promotionId = params[0];
             promotionData = DbHandler.getInstance().getPromotionDetail(promotionId);
             if(promotionData==null) {
                 if(params.length>1) { 
                    AtnBarRequest atnBarRequest = new  AtnBarRequest(AtnApp.getAppContext());
                    atnBarRequest.fetchBarById(params[1]);
                    promotionData = DbHandler.getInstance().getPromotionDetail(promotionId);
                 }
             }
            return promotionData;
        }
 
        @Override
        public void onPostExecute(AtnPromotion result) {
        	mProgressBar.setVisibility(View.GONE);
            if(result!=null){
            	mPromotion = result;
                if(!isCancelled()) {
                	setDataInViews(); 
                	if(mDetailFragment!=null) {
                		mDetailFragment.reloadData();
                	}
                }
            } else {
                AtnUtils.showToast("Promotion Not Found");
            }
            super.onPostExecute(result);
        }
    }
    
    
    private void updateTipsVisitStatus ()
    {
  
    	AnchorHttpRequest anchorRequest = new AnchorHttpRequest(getActivity(), 
				HttpUtility.buildGetMethodWithAppParams().appendPath(ApiEndPoints.TIP_VISIT),
				Method.GET, new AnchorHttpResponceListener() {
			@Override
			public void onSuccessInBackground(JSONObject jsonObject) {
			}
			@Override
			public void onSuccess(JSONObject jsonObject) {

			}
			@Override
			public void onError(Exception ex) {
			}
		});

		anchorRequest.addText(JsonUtils.VenuePicUpload.USER_ID,UserDataPool.getInstance().getUserDetail().getUserId());
		anchorRequest.addText(AtnPromotion.VISIT_PROMOTION_ID,mPromotion.getPromotionId());
		anchorRequest.execute();

    	
    }
}
