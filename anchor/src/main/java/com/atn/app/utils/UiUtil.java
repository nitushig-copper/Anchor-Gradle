/**
 * Copyright CopperMobile
 * 
 * */

package com.atn.app.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.app.ActionBarActivity;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.atn.app.R;
import com.atn.app.datamodels.ReviewTag;
import com.atn.app.provider.Atn;

/***
 * Utility class for 
 * */
public class UiUtil {

	/**
	 * show toast message
	 * 
	 * @param context
	 *            application context
	 * @param msg
	 *            toast message.
	 * */
	public static void showToast(Context context, String msg) {
		if(context==null) return ;
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * show toast message
	 * 
	 * @param context
	 *            application context
	 * @param msg
	 *            toast message resourse id.
	 * */
	public static void showToast(Context context, int regId) {
		if(regId!=0)
			showToast(context, context.getResources().getString(regId));
	}

	public static boolean showToastIfTrue(Context context, int regId,
			boolean condition) {
		if (condition) {
			showToast(context, regId);
		}
		return condition;
	}

	private static final int[] RES_IDS_ACTION_BAR_SIZE = { R.attr.actionBarSize };
	/** Calculates the Action Bar height in pixels. */
	public static int calculateActionBarSize(Context context) {
		if (context == null) {
			return 0;
		}

		Resources.Theme curTheme = context.getTheme();
		if (curTheme == null) {
			return 0;
		}

		TypedArray att = curTheme
				.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
		if (att == null) {
			return 0;
		}

		float size = att.getDimension(0, 0);
		att.recycle();
		return (int) size;
	}

	public static int setColorAlpha(int color, float alpha) {
		int alpha_int = Math.min(Math.max((int) (alpha * 255.0f), 0), 255);
		return Color.argb(alpha_int, Color.red(color), Color.green(color),
				Color.blue(color));
	}

	public static int scaleColor(int color, float factor, boolean scaleAlpha) {
		return Color.argb(
				scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color
						.alpha(color), Math.round(Color.red(color) * factor),
				Math.round(Color.green(color) * factor), Math.round(Color
						.blue(color) * factor));
	}

	public static boolean hasActionBar(ActionBarActivity activity) {
		return activity.getSupportActionBar() != null;
	}
	
	//cell height
	public static int mCellHeight = 0;
	/*
	 * All Screen big photos size will be 40 % of total height of screen.
	 * calculate once if not calculated.
	 * 
	 * ***/
	public static int getLoopPhotoSize(Activity context) {
		if(mCellHeight==0){
			final DisplayMetrics displayMetrics = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        mCellHeight = (displayMetrics.heightPixels*40/100);
		}
		return mCellHeight;
	}

	/**
	 * Here apply color filter on imageview 
	 * */
	public static void makeImageViewBlackAndWhite(ImageView iv) {
		float brightness = -10f;// (float)(200 - 255);
		float[] colorMatrix = { 0.33f, 0.33f, 0.33f, 0, brightness, // red
				0.33f, 0.33f, 0.33f, 0, brightness, // green
				0.33f, 0.33f, 0.33f, 0, brightness, // blue
				0, 0, 0, 1, 0 // alpha
		};
		ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		iv.setColorFilter(colorFilter);
	}
	
	//return screen width in dpi only for foursqare photo url
	public static int getScreenWidth(Activity context) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.densityDpi;
	}
	
	public static int getPixelScreenWidth(Activity context) {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}
	
	/**
	 * Appned Social name and user name with two different font
	 * **/
	public static Spannable getSocialTitle(Context context,int titleResId,String name) {

		if(context==null) return null;
		
		//Typeface titleFont = TypefaceUtils.getTypeface(context, TypefaceUtils.ROBOTO_MEDIUM);   
        String title = context.getResources().getString(titleResId);
		SpannableStringBuilder span = new SpannableStringBuilder(title+" "+name);

		span.setSpan (new ForegroundColorSpan(Color.BLACK),0,title.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		span.setSpan (new ForegroundColorSpan(Color.GRAY), title.length()+1, span.length() ,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		
		span.setSpan (new StyleSpan(android.graphics.Typeface.BOLD),0,title.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		span.setSpan (new StyleSpan(android.graphics.Typeface.ITALIC), title.length()+1, span.length() ,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		return span;
	}

	
	//show alert dialog with title and message
	/*
	 * @params activity activity on which dialog will appear
	 * @oarams title 
	 * **/
	public static void showAlertDialog(Activity activity, int title,
			int message) {
		showAlertDialog(activity, title == 0 ? null : activity.getResources()
				.getString(title), activity.getResources().getString(message));
	}
	//show alert dialog with title and message
	public static void showAlertDialog(Activity activity,
			String title, String message) {
		AlertDialog.Builder errorAlertDialog = new AlertDialog.Builder(activity);
		
		if(!TextUtils.isEmpty(title)) 
			errorAlertDialog.setTitle(title);
		else
			errorAlertDialog.setTitle(R.string.app_name);
		
		errorAlertDialog.setMessage(message);
		errorAlertDialog.setCancelable(true);
		errorAlertDialog.setPositiveButton(activity.getResources().getString(R.string.dialog_button_dismiss),null);
		errorAlertDialog.show();
	}

	// /show dialog with text
	public static void showAlertDialog(Activity activity,
			String message) {
		showAlertDialog(activity,null,message);
	}

	public static void showAlertDialog(Activity activity, int message) {
		showAlertDialog(activity,0,message);
	}
	
	public static void showAlertDialog(Activity activity,
			Exception ex) {
		
		String message = ex.getLocalizedMessage();
		if(TextUtils.isEmpty(message))
			message = ex.getLocalizedMessage();
		
		if(TextUtils.isEmpty(message))
			message = "Unknow error!";
		
		showAlertDialog(activity,null,message);
	}
	
	
	//return review text
	public static String getReviewCountText(int count){
		if (count <= 1) {
			return count+" REVIEW";
		} else {
			return count+" REVIEWS";
		}
	}
	
	/**
	 * Evaluate rating if rating between x.0 to x.05 then rating will be x.5
	 * and if rating between x.6 to x.9 then rating will be x+1;
	 * **/
	public static float calculateRating(float rating) {
		
		if(rating==0) {
			return 0;
		}
		
		DecimalFormat df = new DecimalFormat("#.#");
		rating = Float.valueOf(df.format(rating));
		float precision = (float) (rating-Math.floor(rating));
		
		if(precision==0.0f) {
			return rating;
		}
		
		if(precision<=0.5) {
			rating = (float) (Math.floor(rating)+0.5f);
		} else {
			rating = (float) (Math.floor(rating)+1.0f);
		}
		return rating;
	}
	
	/***
	 * Return Top Two Tags comma seperated
	 * @param venueId foursquare venue id
	 * @param context app context
	 * */
	public static String getTopTwoTags(Context context,String venueId) {
		StringBuilder builder = new StringBuilder();
		ArrayList<ReviewTag> list = Atn.ReviewTable.getVenueTwoReviewTag(context, venueId);
		for (ReviewTag reviewTag : list) {
			builder.append(reviewTag.getName());
			builder.append(", ");
		}
		if(builder.length()>0) {
			//1 for space
			builder.deleteCharAt(builder.length()-2);
		}
		return builder.toString().trim();
	}
	
}
