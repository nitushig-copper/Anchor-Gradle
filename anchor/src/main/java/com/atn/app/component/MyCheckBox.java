/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.component;

import com.atn.app.R;
import com.atn.app.utils.TypefaceUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;

//custom CheckBox handle different type of type face
public class MyCheckBox extends CheckBox {

	public MyCheckBox(Context context) {
		super(context);
	}

	public MyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs, 0);
	}

	public MyCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs, defStyle);
	}

	private void initView(Context context, AttributeSet attrs, int defStyle) {

		//for eclipse preview xml
		if(isInEditMode()) return;
		//get type face and apply
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomFontStyle, 0, 0);
		try {
			setTypeface(TypefaceUtils.getTypeface(context,
					a.getInteger(R.styleable.CustomFontStyle_customFont, 0)));
		}  catch(Exception e){}finally {
			a.recycle();
		}
	}

}
