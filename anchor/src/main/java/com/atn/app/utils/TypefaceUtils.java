
/**
 * @Copyright CopperMobile
 * */
package com.atn.app.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Utility Class for fonts
 * */
public class TypefaceUtils {

	public static final int ROBOTO_CONDENCED = 0;
	public static final int ROBOTO_CONDENCED_LIGHT = 1;
	public static final int ROBOTO_CONDENCED_BOLD_ITALIC = 2;
	public static final int ROBOTO_CONDENCED_BOLD = 3;
	//Roboto-Medium_0.ttf
	public static final int ROBOTO_MEDIUM = 4;

	/**
	 * @param context Copntext
	 * @param typeface type
	 * @return Typeface object;
	 * */
	public static Typeface getTypeface(Context context, int typeface) {

		
		switch (typeface) {
		case ROBOTO_CONDENCED_LIGHT:
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/RobotoCondensed-Light.ttf");
			
		case ROBOTO_CONDENCED_BOLD_ITALIC:
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/RobotoCondensed-BoldItalic_0.ttf");
			
		case ROBOTO_CONDENCED_BOLD:
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/RobotoCondensed-Bold.ttf");
			
		case ROBOTO_MEDIUM:
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Medium_0.ttf");
		
			
		case ROBOTO_CONDENCED:
		default:
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/RobotoCondensed-Regular.ttf");	
		}
	}

	/**
	 * Apply Typeface to given textview,button,edittext
	 * @param context Copntext
	 * @param textView TextView,Button,EditText
	 * @param typeface Typeface 
	 * */
	public static void applyTypeface(Context context, TextView textView,
			int typeface) {
		textView.setTypeface(getTypeface(context, typeface));
	}

	/**
	 * Apply Typeface to given textview,button,edittext with style
	 * @param context Copntext
	 * @param textView TextView,Button,EditText
	 * @param typeface Typeface 
	 * @param style bold,nomal,italic
	 * */
	public static void applyTypeface(Context context, TextView textView,
			int typeface, int style) {
		textView.setTypeface(getTypeface(context, typeface), style);
	}

}
