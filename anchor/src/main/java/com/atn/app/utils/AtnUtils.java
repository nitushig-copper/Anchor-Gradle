package com.atn.app.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atn.app.AtnApp;
import com.atn.app.BuildConfig;
import com.atn.app.R;
import com.atn.app.activities.SplashScreen;
import com.atn.app.constants.ATNConstants;
import com.atn.app.facebook.FacebookSession;
import com.atn.app.httprequester.InstagramImageLoader;
import com.atn.app.location.AtnLocationManager;
import com.atn.app.service.SynchService;
import com.facebook.Session;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;


/**
 * A utility for Atn to handle all the utility tasks like getting rounded image, calculating difference between
 * time, opening native map and phone application for venue directions and make venue call, creating rotation
 * animation for the refresh button etc.
 *  
 *
 */
public class AtnUtils {

	private static final long IG_MEDIA_PHOTOS_RANGE =  2*24*60*60*1000; 
	
	private static final String MAP_PACKAGE_NAME = "com.google.android.apps.maps";
	private static final String MAP_DIRECTION_AVTIVITY_NAME = "com.google.android.maps.MapsActivity";
	private static final String MAP_DIRECTION_URI = "http://maps.google.com/maps?f=d&daddr=";
	private static final String VENUE_CALL = "tel:";
	
	//http image fetcher caches name
	public static final String IMAGE_FETCHER_CACHE = "big_image";
	public static final String IMAGE_FETCHER_USER_CACHE = "user_cache";
	public static final String IMAGE_FETCHER_ICON = "icon_image";
	public static final String FORUM_IMAGE_FETCHER = "forum_image";
	public static final String FORUM_LOGO_FETCHER = "forum_logo_image";
	public static final String PROMOTION_IMG_CACHE = "promotion_img";
	private final static float DP = 40;
	private static RotateAnimation rotateAnim;
	

	
	/**
	 * Used to specify the current device type.
	 *
	 */
	public enum DeviceType
	{
		LDPI,
		MDPI,
		HDPI,
		XHDPI,
		XXHDPI
	}
	
	
	// server side date format
	private static final String SERVER_SIDE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat serverSideDateFormator = null;

	static {
		serverSideDateFormator = new SimpleDateFormat(SERVER_SIDE_DATE_FORMAT);
	}

	/**
	 * Returns true of the device is online.
	 * 
	 * @return true if device is connected to Internet, otherwise returns false.
	 */
	public static boolean isConnectedToInternet() 
	{
	    ConnectivityManager connectivityManager  = (ConnectivityManager) AtnApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float getImagePixel(Context context)
	{
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = DP * (metrics.densityDpi / 160f);
	    return px;
	}
	
	
	
	/**
	 * Returns the converted image pixel using specified image size.
	 *  
	 * @param context app context.
	 * @param size to convert into pixels.
	 * @return image pixel in float.
	 */
	public static float getImagePixel(Context context, float size)
	{
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = size * (metrics.densityDpi / 160f);
	    return px;
	}

	
	
	/**
	 * Returns rotate animation that is set to refresh button to rotate refresh button.
	 *  
	 * @return RotateAnimation
	 */
	public static RotateAnimation getRefreshButtonAnimation()
	{
		rotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setDuration(800);
		rotateAnim.setInterpolator(new LinearInterpolator());
		rotateAnim.setInterpolator(new LinearInterpolator());
		rotateAnim.setRepeatCount(RotateAnimation.INFINITE);
		
		return rotateAnim;
	}
	
	
	/**
	 * Starts the refresh button rotate animation
	 */
	public static void startRefreshButtonAnimation()
	{
		if(rotateAnim != null)
		{
			rotateAnim.start();
		}
	}
	
	
	/**
	 * Stpos the refresh button rotate animation
	 */
	public static void stopRefreshButtonAnimation()
	{
		if(rotateAnim != null)
		{
			rotateAnim.cancel();
		}
	}
	
	
	/**
	 * Opens default Google map and show the direction route from current location to specified latitude and longitude.
	 * 
	 * @param context app context
	 * @param lat Latitude
	 * @param lng Longitude
	 */
	public static void gotoDirection(final Context context, final String lat,
			final String lng) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(context.getResources().getString(R.string.direction_title));
		alertDialog.setMessage(context.getResources().getString(R.string.out_of_app_alert));
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton(
				context.getResources().getString(R.string.dialog_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(MAP_DIRECTION_URI + lat + "," + lng));
							intent.setComponent(new ComponentName(
									MAP_PACKAGE_NAME,
									MAP_DIRECTION_AVTIVITY_NAME));
							context.startActivity(intent);
						} catch (ActivityNotFoundException ex) {
							AtnApp.showErrorDialog(AtnApp.getAppContext()
									.getResources()
									.getString(R.string.map_app_not_found));
						}
					}
				});

		alertDialog.setNegativeButton(
				context.getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING
					}
				});
		alertDialog.show();

	}
	
	
	/**
	 * Initiates a phone call to the specified phone number.
	 * 
	 * @param context app context
	 * @param phone_number number to call.
	 */
	public static void doCall(final Context context, final String phone_number)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(context.getResources().getString(R.string.call_title));
		alertDialog.setMessage(context.getResources().getString(R.string.out_of_app_alert));
		alertDialog.setCancelable(false);
		
		alertDialog.setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				try
				{
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse(VENUE_CALL + phone_number));
					context.startActivity(callIntent);
				}
				catch(ActivityNotFoundException ex)
				{
					AtnApp.showErrorDialog(AtnApp.getAppContext().getResources().getString(R.string.map_app_not_found));
				}
			}
		});
		
		
		alertDialog.setNegativeButton(context.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//DO NOTHING
			}
		});
		
		
		alertDialog.show();
	}
	
	
	
	
    /**
     * Returns the scaled Bitmap image from the specified image url.
     * 
     * @param imageUrl url of image.
     * @return Bitmap
     */
    public static Bitmap getBitmapFromURL(String imageUrl)
    {
    	try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            
            int imagePixel = (int)getImagePixel(AtnApp.getAppContext());
            
            myBitmap = Bitmap.createScaledBitmap(myBitmap, imagePixel, imagePixel, false);
            
            return myBitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    /**
     * Adds the white border to the specifed bitmap image using specified border size.
     * 
     * @param bmp to add border
     * @param borderSize
     * @return bitmap image with border.
     */
    public static Bitmap addWhiteBorder(Bitmap bmp, int borderSize,int imgSize) {
    	
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        
//        try {
//        	bmp.recycle();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        
        return bmpWithBorder;
    }
    
    
    
    /**
     * Returns Atn registered bar image with pin using specified bitmap
     * 
     * @param context app context
     * @param bmp bitmap image to get atn bar image with pin.
     * @return atn bar bitmap
     */
   public static Bitmap getAtnBitmap(Context context, Bitmap bmp) {
	   
 		DisplayMetrics displayMetrics = new DisplayMetrics();
 		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
 		
 		View view = View.inflate(context, R.layout.atn_bar_item, null);
 		
 		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
 		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
 		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
 		view.buildDrawingCache();
 		
 		//int imagePixel = (int)getImagePixel(AtnApp.getAppContext(), 30);
 		//bmp = Bitmap.createScaledBitmap(bmp, imagePixel, imagePixel, false);
 		//bmp = getRoundedShape(bmp);
 		
 		ImageView barImage = (ImageView)view.findViewById(R.id.img_atn_bar_image);
 		barImage.setImageBitmap(bmp);
 		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
 		Canvas canvas = new Canvas(bitmap);
 		view.draw(canvas);
 		
 		bmp.recycle();
 		
 		return bitmap;
   }
    
    
   
   /**
	 * Returns a rounded bitmap using specified bitmap image.
	 * 
	 * @param scaleBitmapImage bitmap to make round image.
	 * @return rounded bitmap
	 */
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) 
	{	
		if(scaleBitmapImage==null)
			return null;
		
		int targetWidth = (int) DP;
		int targetHeight = (int) DP;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
		                        targetHeight,Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		
		path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2,
		(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
		
		Path.Direction.CCW);
		
		Paint p = new Paint();
		p.setAntiAlias(true);

		canvas.clipPath(path);
		canvas.drawBitmap(scaleBitmapImage, new Rect(0, 0, scaleBitmapImage.getWidth(),
				scaleBitmapImage.getHeight()), new Rect(0, 0, targetWidth, targetHeight), p);
		
		p.setARGB(255, 16, 18, 16);
		
		scaleBitmapImage.recycle();
		
		return targetBitmap;
	}
    
	
    
   /**
    * Returns a bitmap with all the four corners as rounded using specified bitmap and bitmap pixels.
    * 
    * @param bitmap to make rounded bitmap
    * @param pixels size of bitmap
    * @return rounded courner bitmap.
    */
   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels)
   {
	   if(bitmap != null)
	   {
		   Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	       Canvas canvas = new Canvas(output);

	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = pixels;

	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	        bitmap.recycle();
	        return output;
	   }
	   else
	   {
		   return null;
	   }
        
   }
	
	
   
   
   /**
    * Return rounded bitmap with top left corner as rounded using specified bitmap and bitmap size in pixels.
    * 
    * @param bitmap to make rounded
    * @param pixels size of bitmap
    * @return slightly rounded bitmap.
    */
   public static Bitmap getRoundedTopLeftCornerBitmap(Bitmap bitmap, int pixels)
   {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	            .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;
	    final Rect topRightRect = new Rect(bitmap.getWidth()/2, 0, bitmap.getWidth(), bitmap.getHeight()/2);
	    final Rect bottomRect = new Rect(0, bitmap.getHeight()/2, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    // Fill in upper right corner
	    canvas.drawRect(topRightRect, paint);
	    // Fill in bottom corners
	    canvas.drawRect(bottomRect, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}
   
   
   
    
   /**
	 * Returns the device type on which app is running by calculating display martics.
	 * 
	 * @return device type.
	 */
	public static DeviceType getDeviceType() {
		float dpi = AtnApp.getAppContext().getResources().getDisplayMetrics().density;
		if (dpi == 0.75) {
			return DeviceType.LDPI;
		} else if (dpi == 1.0) {
			return DeviceType.MDPI;
		} else if (dpi == 1.5) {
			return DeviceType.HDPI;
		} else if (dpi == 2.0) {
			return DeviceType.XHDPI;
		} else {
			return DeviceType.XXHDPI;
		}
	}
    
    /**
     * Returns the forum post time using specified date/time.
     *  
     * @param dateToCompare
     * @return Formatted time with detail.
     */
   public static String getDaysDifference(String dateToCompare)
   {
	   try
	   {
		   SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		   Date lastDate  = dateFormat.parse(dateToCompare);
		   Calendar calendar = Calendar.getInstance();
		   Date currentDate = dateFormat.parse(dateFormat.format(calendar.getTime()));
		   
		   String result;
		   
		   long difference = currentDate.getTime() - lastDate.getTime();
		   
		   int days = (int) (difference / (1000*60*60*24));
		   if(days > 0)
		   {
			   result = days + " " + AtnApp.getAppContext().getResources().getString(R.string.comment_days_ago);
		   }
		   else
		   {
			   int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
			   if(hours > 0)
			   {
				   result = hours + " " + AtnApp.getAppContext().getResources().getString(R.string.comment_hrs_ago);
			   }
			   else
			   {
				   int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
				   if(min < 0 || (min > 0 && min < 2))
				   {
					   result = AtnApp.getAppContext().getResources().getString(R.string.comment_just_now);
				   }
				   else
				   {
					   result = min + " " + AtnApp.getAppContext().getResources().getString(R.string.comment_mins_ago);
				   }
			   }
		   }
		    
		   return result;
	   }
	   catch (ParseException e)
	   {
			e.printStackTrace();
	   }
	   
	   return null;
   }
   
   
   /**
    * Returns two days old date from the current date. This is used with forusquare web service to get the data
    * according to date.
    * 
    * @return date in string.
    */
   public static String getTwoDaysOldDate() {
	   
	   int yyyy = Calendar.getInstance().get(Calendar.YEAR);
	   int MM = Calendar.getInstance().get(Calendar.MONTH) + 1;
	   int dd = Calendar.getInstance().get(Calendar.DATE) - 2;
	   Calendar cal = Calendar.getInstance();
	   if(dd < 1) {
		   MM = MM - 1;
		   if(MM < 0){
			   MM = 0;
		   }   
	   }
	   
	   if(MM < 0){
		   yyyy = yyyy - 1;
	   }
	   cal.set(yyyy, MM, 1);
	   dd = cal.getActualMaximum(Calendar.DATE) - dd;
	   String oldDate = String.format("%04d%02d%02d", yyyy,MM,dd);
	   
	   return oldDate;
   }
   
   
   /**
    * Returns current date-time in format of yyyy-MM-dd hh:mm:ss
    * 
    * @return date time in string.
    */
   public static String getCurrentTime()
   {
	   int yyyy = Calendar.getInstance().get(Calendar.YEAR);
	   int MM = Calendar.getInstance().get(Calendar.MONTH) + 1;
	   int dd = Calendar.getInstance().get(Calendar.DATE);
	   int hh = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	   int mm = Calendar.getInstance().get(Calendar.MINUTE);
	   int ss = Calendar.getInstance().get(Calendar.SECOND);
	   String curDate = String.format("%04d-%02d-%02d %02d:%02d:%02d", yyyy,MM,dd,hh,mm,ss);
	   
	   return curDate;
	   
   }
   
   
   /**
	 * Returns absolute path of the file using specified Uri. This is used to get the path of image when user
	 * selects an image to post it in forum.
	 * 
	 * @param context current context.
	 * @param contentUri uri of the selected file.
	 * @return absolute path of the file.
	 */
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
    
	
   /**
	 * Returns the device registration id from google services if available, to get push notifications.
	 * 
	 * @param context base context of activity using it.
	 * @return Registration id if available, otherwise returns "" or empty value.
	 */
	public static String getDeviceId(Context context) 
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		
		String registrationId = prefs.getString(ATNConstants.PROPERTY_REG_ID, "");
		
		if (registrationId.isEmpty())
		{
		    return "";
		}
		   
		return registrationId;
	}
	
	
	
	/**
	 * Returns shared preferences for google services.
	 * 
	 * @param context base context of activity using it.
	 * @return shared preferences.
	 */
	public static SharedPreferences getGCMPreferences(Context context) 
	{
		return context.getSharedPreferences(SplashScreen.class.getSimpleName(), Context.MODE_PRIVATE);
	}
   
	
	
	
	
		
	/**
	 * Coverts the specified date/time value to specific milliseconds.
	 * 
	 * @param str to convert into specific date/time.
	 * @return converted milliseconds value.
	 */
	public static long getDateTimeinMilliSeconds(String str) {
			long milliseconds = 0;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date startdate = null;
			try {
				startdate = formatter.parse(str);
				milliseconds =  startdate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return milliseconds;
	}
	
	
	public static File getTempImageCaptureUri() {
		///
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ATN");
		if(dir.mkdirs()) {
			AtnUtils.log("mkdirs");
		}
		dir = new  File(dir, "atn_"+System.currentTimeMillis()+".jpeg");
		if (dir.exists()) {
			AtnUtils.log("exists");
		}
		return dir;
	}
	

	
	
	/**
	 * Hides the keyboard while switching among fragments.
	 * 
	 * @param context of activity.
	 */
	public static void hideKeyboard(Context context) {
		try {
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(((Activity) context)
					.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception ex) {
		}
	}

	public static void showKeyboard(Context context,View view) {
		try {
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
			view.requestFocus();
		} catch (Exception ex) {
		}
	}
	

	/**
	 * Returns semi-rounded bitmap. This is used for displaying atn promotion images.
	 * 
	 * @param context
	 * @param input
	 * @return
	 */
	public static Bitmap getSemiRoundedBitmap(Context context, Bitmap input) {
		Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    final float densityMultiplier = context.getResources().getDisplayMetrics().density;
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, input.getWidth(), input.getHeight());
	    final RectF rectF = new RectF(rect);
	    //make sure that our rounded corner is scaled appropriately
	    final float roundPx = densityMultiplier * 10;
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    //draw rectangles over the corners we want to be square
	    canvas.drawRect(input.getWidth()/2, 0, input.getWidth(), input.getHeight()/2, paint);
	    canvas.drawRect(input.getWidth()/2, input.getHeight()/2, input.getWidth(), input.getHeight(), paint);
	    paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(input, 0,0, paint);
	    input.recycle();

	    return output;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
	}
	
	

	
	/**
	 * Returns pixels from the specified dp value.
	 * 
	 * @param dp to calculate pixels.
	 * @return pixels.
	 */
	public static int getPixelFromDp(int dp) {
	    DisplayMetrics displayMetrics = AtnApp.getAppContext().getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	
	public static String getTweleveHourTime(String timeToConvert) {
		String convertedTime="";
		if(timeToConvert == null)
			return null;
		
		try {
			final Date dateObj = serverSideDateFormator.parse(timeToConvert);
			convertedTime = new SimpleDateFormat("K:mm a MM/dd/yyyy").format(dateObj);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return convertedTime;
	}
	
	public static String getTweleveHourTimeWith2DigtYear(String timeToConvert) {
		String convertedTime="";
		if(timeToConvert == null)
			return null;
		
		try {
			final Date dateObj = serverSideDateFormator.parse(timeToConvert);
			convertedTime = new SimpleDateFormat("K:mm a MM/dd/yy").format(dateObj);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return convertedTime;
	}
	
	public static void log(String str) {
		if(BuildConfig.DEBUG)
		   Log.d("ATN::", str+"");
	}
	
	public static void logE(String str){
		 //if(BuildConfig.DEBUG)
		    Log.e("ATN::", str+""); 
	}
	
	public static void logE(Throwable str) {
		//if(BuildConfig.DEBUG)
		   Log.e("ATN::", "Throwable",str);
	}
	
	public static boolean isConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	
	public enum ServiceCommand{
		RELOAD,
		REFRESH,
	}
	
	
	public static long getMinTimeStampForIgMedia() {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.DAY_OF_YEAR, -2);
		
		return rightNow.getTimeInMillis()/1000;
	}
	public static long getMaxTimeStampForIgMedia() {
		return System.currentTimeMillis()/1000;
	}

	
	public static void showToast(Object text){
		if(text!=null&&!TextUtils.isEmpty(text.toString())){
			Toast.makeText(AtnApp.getAppContext(), text.toString()+"", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * Convert String into int ,return 0 if blank string or 
	 * @str string to convert
	 * */
	public static int getInt(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		} else {
				try {
					return Integer.valueOf(str);
				} catch (Exception e) {
			}
			return 0;
		}
	}
	
	
	/**
	 * Convert TimeInMilliSeconds inot server side date format
	 * 
	 * @time TimeInMilliSeconds
	 * @return formated string date
	 * */
	public static String convertTimeInMilliSecondsToDateFormat(long time) {
		Date date = new Date(time);
		// Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return serverSideDateFormator.format(date).toString();
	}

	public static long convertInMillisecond(String dateStrServer) {

		//String orderDate = null;
		try {
			Date date = serverSideDateFormator.parse(dateStrServer);
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void logoutFacebook(FacebookSession fbSession) {
		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();

		if (fbSession != null) {
			fbSession.resetAccessToken();
		}
	}

	/**
	 * Convert server date into display date
	 * 
	 * @serverDateStr server date in string format
	 * @return display time (Like 1 second ago, 2 months ago etc.)
	 * */
	public static String getTimeToDisplay(String serverDateStr) {
		Date serverDate = null;
		try {
			serverDate = serverSideDateFormator.parse(serverDateStr);
		} catch (Exception e) {
			AtnUtils.logE("date formate ex" + e.getLocalizedMessage());
		}
		return convertDateToDisplayFormat(serverDate);
	}

	/**
	 * date into display date
	 * 
	 * @serverDateStr server date in string format
	 * @return display time (Like 1 second ago, 2 months ago etc.)
	 * */
	public static String convertDateToDisplayFormat(Date serverDate) {

		// current date
		Date endDate = new Date();

		long different = endDate.getTime() - serverDate.getTime();
		long seconds = TimeUnit.MILLISECONDS.toSeconds(different);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(different);
		long hrs = TimeUnit.MILLISECONDS.toHours(different);
		long days = TimeUnit.MILLISECONDS.toDays(different);

		int months = (int) Math.floor(days / 30);
		int years = (int) Math.floor(months / 12);
		
		if (years != 0) {
			if (years == 1) {
				return "1 year ago";
			} else {
				return years + " years ago";
			}
		} else if (months != 0) {
			if (months == 1) {
				return "1 month ago";
			} else {
				return months + " months ago";
			}
		} else if (days != 0) {
			if (days == 1) {
				return "Yesterday";
			} else {
				return days + " Days ago";
			}
		} else if (hrs != 0) {
			if (hrs == 1) {
				return hrs + " hour ago";
			} else {
				return hrs + " hours ago";
			}
		} else if (minutes != 0) {

			if (minutes == 1) {
				return minutes + " minute ago";
			} else {
				return minutes + " minutes ago";
			}
		} else {
			if (seconds == 0) {
				return "Now";
			} else if (seconds == 1) {
				return "1 sec ago";
			} else if(seconds > 0) {
				return seconds + " seconds ago";
			} else {
				return "Now";	
			}
		}
	}

	
	//check user in under valid location i.e within 500 meter for claiming deal
	//@businessLatLng promotion lat long
	public static boolean isUserInBusinessArea(LatLng businessLatLng){
		
		Location currentLoc = AtnLocationManager.getInstance().getCurrentLocation();
		if (currentLoc == null) {
			currentLoc = AtnLocationManager.getInstance().getLastLocation();
		}		
		if(currentLoc!=null&&businessLatLng!=null){

			Location businesLoc = new Location("");
			businesLoc.setLatitude(businessLatLng.latitude);
			businesLoc.setLongitude(businessLatLng.longitude);
			float disInMtr = Math.abs(businesLoc.distanceTo(currentLoc));
			if(disInMtr<=ATNConstants.CLAIM_LOCATION_DISTANCE) {
				return true;
			}
		}
		return false;
	}
	
	
	public static String getErrorDetailFromCode(int code) {

		String message = "UnKnow error code " + code;
		switch (code) {
		case 1:
			message = "MissingAccessToken";
			break;
		case 2:
			message = "InvalidAccessToken";
			break;
		case 3:
			message = "InvalidAccessToken";
			break;
		case 4:
			message = "MissingParam";
			break;
		case 5:
			message = "Forbidden";
			break;
		case 6:
			message = "ObjectNotFound";
			break;
		case 7:
			message = "NetworkError";
			break;
		default:
			break;
		}
		return message;
	}
	
	
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		//final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		return output;
	}
	
	
	
	
	/***
	 * create formted string start and end date for promotion
	 * @isStartText true if text for start otherwise false;
	 * @dateText date text
	 * @return formated String
	 * 
	 * */
	public static SpannableString getPromotionStartEndDateText(
			String startEnd, String dateText) {

		SpannableString spannableconten = new SpannableString(startEnd + "\n\n"+ AtnUtils.getTweleveHourTime(dateText));
		spannableconten.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				0, startEnd.length(), 0);

		return spannableconten;
	}
	
	
	public static Drawable convertIntoDrawable(Resources res,Bitmap bitmap){
		Drawable d = null;
		if(res!=null){
			 d = new BitmapDrawable(res,bitmap);
		}
		return d;
	} 
	
	
	
	public static boolean isMediaInDateRange(String mediaTimeStamp) {
		
		if(!TextUtils.isEmpty(mediaTimeStamp)){
			Date currentDate = new Date();
			long mediaDateLong = Long.valueOf(mediaTimeStamp)*1000L;
			long diff =  currentDate.getTime() - mediaDateLong;
			if(diff< IG_MEDIA_PHOTOS_RANGE){
				return true;
			}
		}
		return false;
	}
	
	
	public static Bitmap getAtnBarMapPin(View layout) {
		layout.invalidate();
		// Create a new bitmap and a new canvas using that bitmap
		 int w = AtnApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.atn_map_pin_width);
		 int H = AtnApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.atn_map_pin_height);
		 Bitmap bmp = Bitmap.createBitmap(w, H, Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(bmp);
		 layout.setDrawingCacheEnabled(true);
		 // Supply measurements
		 layout.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));
		 // Apply the measures so the layout would resize before drawing.
		 layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
		 // and now the bmp object will actually contain the requested layout
		 canvas.drawBitmap(layout.getDrawingCache(), 0, 0, new Paint());
		 return bmp;
	}
	
	
	 //working.
	 public static Bitmap getNonAtnBarMapPin(View layout) {
		 layout.invalidate();
		// Create a new bitmap and a new canvas using that bitmap
		 int wH = AtnApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.non_atn_bar_image_size);
		 Bitmap bmp = Bitmap.createBitmap(wH, wH, Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(bmp);
		 layout.setDrawingCacheEnabled(true);
		 // Supply measurements
		 layout.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));
		 // Apply the measures so the layout would resize before drawing.
		 layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
		 // and now the bmp object will actually contain the requested layout
		 canvas.drawBitmap(layout.getDrawingCache(), 0, 0, new Paint());
		 
		 return bmp;
	 }
	 
	 public static boolean hasKitKat() {
	        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
	    }
	 
	
	
	/**
	 * Google analytics for sending event data
	 * @atnApp app context
	 * @eventCatagory GA Event Catagory
	 * @eventAction GA Event Action 
	 * @eventLabel Event Label 
	 * */
	public  static void  gASendEvent(Context atnApp,String eventCatagory,String eventAction,String eventLabel) {
		//AtnUtils.log(eventCatagory);
		if (TextUtils.isEmpty(eventCatagory)) eventCatagory = "";
		if (TextUtils.isEmpty(eventAction))eventAction = "";
		if (TextUtils.isEmpty(eventLabel))eventLabel = "";
		
		Tracker t = ((AtnApp)atnApp.getApplicationContext()).getTracker(com.atn.app.AtnApp.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder()
		.setCategory(eventCatagory)
		.setAction(eventAction)
		.setLabel(eventLabel).build());
	}
	
	
	/**
	 * Google analytics for sending screen data
	 * @atnApp app context
	 * @screenName GA SCreen Name
	 * */
	public static void gASendView(Context atnApp,String screenName) {
		
		//AtnUtils.log(screenName);
		Tracker t = ((AtnApp)atnApp.getApplicationContext()).getTracker(com.atn.app.AtnApp.TrackerName.APP_TRACKER);
		t.setScreenName(screenName);
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
	
	public static void gASendView(Context atnApp,int screenNameResId) {
		try {
			if(screenNameResId!=0) {
				gASendView(atnApp, atnApp.getResources().getString(screenNameResId));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}	 
	
	public static ArrayList<String> getFsCategoryListIds(){
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("All");
		list.add("4d4b7105d754a06379d81259");//Travel & Transport
		list.add("4d4b7105d754a06377d81259");//Outdoors & Recreation
		list.add("4d4b7105d754a06376d81259");//Nightlife Spot
		list.add("4d4b7105d754a06374d81259");//food
		list.add("4d4b7105d754a06373d81259");//Event
		list.add("4d4b7104d754a06370d81259");//Arts & Entertainment
		
		return list;
	}
	
	
	
	
	
	/**
	 * Create drawable for action bar at run time with some text on it and with a background
	 * @txt txt that u want on drawable
	 * @bgResourceId background resourse id.
	 * 
	 * **/
	public static Drawable getActionBarDrawable(String txt,Context context,int bgResourceId){
		
		int wH = AtnApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.action_bar_icon_size);
		TextView txView = new TextView(context);
		txView.setTextColor(context.getResources().getColor(android.R.color.white));
		txView.setText(txt);
		txView.setWidth(wH);
		txView.setHeight(wH);
		txView.setGravity(Gravity.CENTER);
		txView.setBackgroundResource(bgResourceId);
		
		 Bitmap bmp = Bitmap.createBitmap(wH, wH, Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(bmp);
		 txView.setDrawingCacheEnabled(true);
		 // Supply measurements
		 txView.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));
		 // Apply the measures so the layout would resize before drawing.
		 txView.layout(0, 0, txView.getMeasuredWidth(), txView.getMeasuredHeight());
		 // and now the bmp object will actually contain the requested layout
		 canvas.drawBitmap(txView.getDrawingCache(), 0, 0, new Paint());
		
		return new BitmapDrawable(context.getResources(),bmp);
		 
	}
	
	
	//return true if distance is greater then zero;
	public static boolean willCameraMove(LatLng cameraLatLng,LatLng markerLatLng){
		Location cameraLoc = new Location("Camera");
		cameraLoc.setLatitude(cameraLatLng.latitude);
		cameraLoc.setLongitude(cameraLatLng.longitude);
		Location markerLoc = new Location("Marker");
		markerLoc.setLatitude(markerLatLng.latitude);
		markerLoc.setLongitude(markerLatLng.longitude);
		return ((int)cameraLoc.distanceTo(markerLoc))==0;
	}
	
	/*
	 * Start synch service .
	 * Load venue ,refresh venues, category  if required.
	 * **/
	public static void runSynchService(Context context,int command) {
		Intent intent = new Intent(context, SynchService.class);
		
		if(!SharedPrefUtils.fSVenueLoadStatus(context)) {
			intent.putExtra(SynchService.Command.COMMAND, SynchService.Command.RELOAD_VENUE);
		} else {
			intent.putExtra(SynchService.Command.COMMAND, command);
		}
		
		context.startService(intent);
	}
	//stop synch service.and media imagedownloader
	public static void stopSynchService(Context context) {
		context.stopService(new Intent(context, SynchService.class));
		InstagramImageLoader.loader.cancel();
	}
	
	//convert into small image url
	public static String createIconUrlFrmFsVenue(String url,int size) {
		if(TextUtils.isEmpty(url)) return null;
		return url.replace(SharedPrefUtils.getScreenWith(AtnApp.getAppContext())+"", String.valueOf(size));
	}
	
	// if all selected retrun null;
	public static String getFilterQueryString(Context context) {
		return SharedPrefUtils.isAllCategoriesSelected(context) ? null
				: SharedPrefUtils.getFilterCategories(context);

	}
	
	/**
	 * Get Venue distance from current location
	 * **/
	public static double distanceInMiles(String lat,String lng) {
		//get current location
		Location loc = AtnLocationManager.getInstance().getCurrentLocation();
		if(loc==null)
			loc = AtnLocationManager.getInstance().getLastLocation();
		///calculate distance if not null
		if(loc!=null) {
			Location mVenueLocation = new Location("");
			mVenueLocation.setLatitude(Double.valueOf(lat));
			mVenueLocation.setLongitude(Double.valueOf(lng));
			//calculate distance in mi
			return mVenueLocation.distanceTo(loc) / 1609.344;
		}
		return 0;
	}
	
}
