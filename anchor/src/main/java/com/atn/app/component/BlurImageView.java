/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.component;

import java.io.File;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.ImageView;

import com.atn.app.AtnApp;
import com.atn.app.R;
import com.atn.app.pool.UserDataPool;
import com.atn.app.utils.AtnImageUtils;
import com.atn.app.utils.HttpUtility;
import com.atn.app.utils.HttpUtility.ImageType;
import com.atn.app.utils.SharedPrefUtils;
import com.atn.app.utils.UiUtil;
import com.squareup.picasso.Picasso;

/*
 * Custom image view that support two images user can set a image 
 * as background image and another small round image on top of it.
 * */
public class BlurImageView extends ImageView {

	//user round image position. bottom, top, right
	private int mRoundImagePosition;
	//user round default image, will be set from xml file.
	private int mDefaultSrc;
	//user round bitmap
	private Bitmap mRoundBitmap;
	//round image padding from all side
	private int mTopLeftPadding;
	//overlay color
	private int mOverlayColor = 0;
	//action bat height
   int actionBarHeight = 0;
	
   private ImageProcessTask mImageProcessTask = null;
   
	public BlurImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public BlurImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public BlurImageView(Context context) {
		super(context);
	}

	//init style and user image
	private void init(Context context, AttributeSet attrs) {
		actionBarHeight  = UiUtil.calculateActionBarSize(getContext());
		//load default and custom style.
		TypedArray a = context.getTheme().obtainStyledAttributes(
		        attrs,
		        R.styleable.BlurImageView,
		        0, 0);
		   try {
			   mRoundImagePosition = a.getInteger(R.styleable.BlurImageView_roundImagePosition, 0);
			   mDefaultSrc = a.getResourceId(R.styleable.BlurImageView_defaultSrc, 0);  
			   mTopLeftPadding = a.getDimensionPixelSize(R.styleable.BlurImageView_topLeftPadding, 0);
		   } finally {
		       a.recycle();
		   }
		   
		   if(mDefaultSrc!=0) {
			   mRoundBitmap = BitmapFactory.decodeResource(getResources(), mDefaultSrc);
		   }
		   
		   this.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					setPath(null);
				}
			});
	}
	
	public void setRoundImagePosition(int roundImagePosition) {
		this.mRoundImagePosition = roundImagePosition;
	}

	public void setDefaultSrc(int defaultSrc) {
		this.mDefaultSrc = defaultSrc;
	}

	/**
	 * @param mOverlayColor the mOverlayColor to set
	 */
	public void setOverlayColor(int overlayColor) {
		this.mOverlayColor = overlayColor;
	}


	//draw round image after all children drawn
	public void setTopLeftPadding(int roundImgPadding) {
		this.mTopLeftPadding = roundImgPadding;
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mOverlayColor!=0){
			canvas.drawColor(mOverlayColor);
		}
		
		//
		if(mRoundBitmap!=null) {
			int top = 0;
			int left = 0;
			if(mRoundImagePosition==1) {//bottom
				top = actionBarHeight+ (getHeight()-actionBarHeight-mRoundBitmap.getHeight())/2+mTopLeftPadding;
			    left = (getWidth()-mRoundBitmap.getWidth())/2;
			} else if(mRoundImagePosition==2) {//left
				top = (getHeight()-mRoundBitmap.getHeight())/2+mTopLeftPadding;//form top padding
			    left = mTopLeftPadding;//for left padding
			} else {	//top default
				top =  mTopLeftPadding;
			    left = (getWidth()-mRoundBitmap.getWidth())/2;
			}
			canvas.drawBitmap(mRoundBitmap, left, top, null);	
		}
	}

	@Override
    protected void onDetachedFromWindow() {
		if(mImageProcessTask!=null&&mImageProcessTask.getStatus()!=AsyncTask.Status.FINISHED) {
			mImageProcessTask.cancel(true);
		}
        super.onDetachedFromWindow();
    }
	/**
	 * @param mPath the mPath to set
	 */
	public void setPath(String path) {
		
		if(mImageProcessTask!=null&&mImageProcessTask.getStatus()!=AsyncTask.Status.FINISHED){
			mImageProcessTask.cancel(true);
		}
		mImageProcessTask = new ImageProcessTask(path);
		mImageProcessTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	
	
	
	@Override
	@ExportedProperty(category = "drawing")
	public boolean isOpaque() {
		return true;
	}
	
	public static final String REMOVE_PATH = "remove";
	/**
	 * blurred image creator task and download image and blur
	 * If user is logged in it will  download user image
	 * */
	private class ImageProcessTask extends AsyncTask<Void, Bitmap, Bitmap> {

		private String mPath = null;
		public ImageProcessTask(String path) { 
			this.mPath = path;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			int hw = getResources().getDimensionPixelSize(R.dimen.round_image_size);
			int bigImgH = getResources().getDimensionPixelSize(R.dimen.user_pic_height);
			Bitmap mBitmap = null;
			try {
				if(!TextUtils.isEmpty(mPath)) {
					if(!mPath.equals(REMOVE_PATH)){
						mBitmap = Picasso.with(getContext()).load(new File(mPath))
								.resize(SharedPrefUtils.getScreenWith(getContext()), bigImgH).get();
					}
				} else if (UserDataPool.getInstance().isUserLoggedIn()
							&& !TextUtils.isEmpty(UserDataPool.getInstance()
									.getUserDetail().getImageUrl())) {
						mBitmap = Picasso.with(AtnApp.getAppContext()).load(HttpUtility
									.getUserImageUrl(UserDataPool.getInstance()
									.getUserDetail().getImageUrl(), ImageType.L))
									.resize(SharedPrefUtils.getScreenWith(getContext()), bigImgH).get();
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//
			if(mBitmap!=null) {
				//set round small image
				publishProgress(AtnImageUtils.getRoundBitmap(Bitmap.createScaledBitmap(mBitmap, hw, hw, true)));
				
				return AtnImageUtils.blurBitmap(mBitmap, AtnApp.getAppContext());
			} else {
				//load default.
				publishProgress(BitmapFactory.decodeResource(getResources(), R.drawable.user_img_holder));
				return BitmapFactory.decodeResource(getResources(), R.drawable.user_img_bg);
			}
			
		}
		@Override
		protected void onProgressUpdate(Bitmap... values) {
			///set round image		
			super.onProgressUpdate(values);
			mRoundBitmap = values[0];
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
		   super.onPostExecute(result);
		   if(!isCancelled()) {
			   setImageBitmap(result);
		   }
		}
	}
}
