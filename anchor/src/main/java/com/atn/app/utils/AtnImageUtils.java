package com.atn.app.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class AtnImageUtils {

	
	 public static boolean rorateIfNeededAndSave(String filePath) {

			try {
				
				ExifInterface exif = new ExifInterface(filePath);
				float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
				System.out.println(rotation);
				int rotationInDegrees = 0;

				if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
					rotationInDegrees = 90;
		        }
		        else if (rotation  == ExifInterface.ORIENTATION_ROTATE_180) {
		        	rotationInDegrees = 180;
		        }
		        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
		        	rotationInDegrees = 270;
		        }
				
				if(rotationInDegrees==0){
					AtnUtils.log("No Need to rotate");
					return new File(filePath).exists();
				}
				
				Bitmap rotatedBitmap = getBitmapFromPathAndRotate(filePath,rotationInDegrees,1);
				if(rotatedBitmap!=null){
					FileOutputStream fos = new FileOutputStream(filePath);
					rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
					fos.flush();
					fos.close();
					return true;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	 
	 
	 private static Bitmap getBitmapFromPathAndRotate(String path,float rotation,int inSampleSize){
		 
		 Bitmap rotatedBitmap = null;
		 try {
			 Bitmap cameraBitmap = decodeSampledBitmapFromFile(path,inSampleSize);
			 Matrix matrix = new Matrix();
			 matrix.postRotate(rotation); 
			 rotatedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(),cameraBitmap.getHeight(), matrix, true);
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
			return getBitmapFromPathAndRotate(path,rotation,inSampleSize*2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return rotatedBitmap;
	 }
	 	 
	 public static String getBitmapFromUri(Uri uri,Context mCOntext)  {
		 
		 String path = null;
		 try {
			    ParcelFileDescriptor parcelFileDescriptor = mCOntext.getContentResolver().openFileDescriptor(uri, "r");
			    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			    parcelFileDescriptor.close();
			    path = AtnUtils.getTempImageCaptureUri().getAbsolutePath();
			    FileOutputStream fos = new FileOutputStream(path);
			    image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return resizeAndSaveIfOutOfMemory(uri,mCOntext,2);
		}catch(Exception ex) {
			ex.printStackTrace();
		}		   
	    return path;
	}
	 
	 
	 private static String resizeAndSaveIfOutOfMemory(Uri uri,Context mCOntext,int inSampleSize){
		 String path = null;
		 try {
			    ParcelFileDescriptor parcelFileDescriptor = mCOntext.getContentResolver().openFileDescriptor(uri, "r");
			    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			    Bitmap image = decodeSampledBitmapFromDescriptor(fileDescriptor, inSampleSize);
			    parcelFileDescriptor.close();
			    path = AtnUtils.getTempImageCaptureUri().getAbsolutePath();
			    FileOutputStream fos = new FileOutputStream(path);
			    image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return resizeAndSaveIfOutOfMemory(uri,mCOntext,inSampleSize*2);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return path; 
	 }
	 
	 
	 public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor,int inSampleSize ) {
	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	        // Calculate inSampleSize
	        options.inSampleSize = inSampleSize;
	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        // If we're running on Honeycomb or newer, try to use inBitmap
	        return  BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	    }
	
	 public static Bitmap decodeSampledBitmapFromFile(String filename,int inSampleSize) {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(filename, options);
	        // Calculate inSampleSize
	        options.inSampleSize = inSampleSize;
	        // If we're running on Honeycomb or newer, try to use inBitmap
	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        return BitmapFactory.decodeFile(filename, options);
	    }
	 
	 //return round bitmap
	 public static Bitmap getRoundBitmap(Bitmap bitmap) {

		 Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		 BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
		 Paint paint = new Paint();
		         paint.setShader(shader);
		 paint.setAntiAlias(true);
		 Canvas c = new Canvas(circleBitmap);
		 c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
		 return circleBitmap;
	 }
	
	 
	 
	 public static Bitmap getMapRoundBitmap(Bitmap bmp, int radius) {
		   Bitmap bitmap;
			if (bmp.getWidth() != radius || bmp.getHeight() != radius)
				bitmap = Bitmap.createScaledBitmap(bmp, radius*2, radius*2, true);
			else
				bitmap = bmp;
		  return getRoundBitmap(bitmap);			
		}
	 
	 
	 
	 
	 ///blur image using renderscript
	 public static Bitmap blurBitmap(Bitmap bitmap,Context context){
		   //Let's create an empty bitmap with the same size of the bitmap we want to blur
		 	Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			//Instantiate a new Renderscript
			RenderScript rs = RenderScript.create(context);
			//Create an Intrinsic Blur Script using the Renderscript
			ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			//Create the in/out Allocations with the Renderscript and the in/out bitmaps
			Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
			Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
			//Set the radius of the blur
			blurScript.setRadius(25.f);
			//Perform the Renderscript
			blurScript.setInput(allIn);
			blurScript.forEach(allOut);
			//Copy the final bitmap created by the out Allocation to the outBitmap
			allOut.copyTo(outBitmap);
			//recycle the original bitmap
			bitmap.recycle();
			//After finishing everything, we destroy the Renderscript.
			rs.destroy();
			return outBitmap;
		}
	 
	 
	 /***
	  * Covert Drawable into bitmap
	  * */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
	 
	 
	 
	 
}
