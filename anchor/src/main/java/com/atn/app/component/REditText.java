/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.component;

//custom EditText handle different type of type face
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.atn.app.R;
import com.atn.app.utils.TypefaceUtils;

public class REditText extends EditText {

	// The image we are going to use for the Clear button
	private Drawable imgCloseButton = null;

	public REditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs, defStyle);
	}

	public REditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs, 0);
	}

	public REditText(Context context) {
		super(context);
	}

	private void initView(Context context, AttributeSet attrs, int defStyle) {
		if (isInEditMode())
			return;

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomFontStyle, 0, 0);
		try {
			setTypeface(TypefaceUtils.getTypeface(context,
					a.getInteger(R.styleable.CustomFontStyle_customFont, 0)));
		} catch(Exception e){} finally {
			a.recycle();
		}
	}

	public void showClearButton() {
		init();
	}

	void init() {
		imgCloseButton = getResources().getDrawable(R.drawable.icn_close_comment);
		// Set bounds of the Clear button so it will look ok
		imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(),
				imgCloseButton.getIntrinsicHeight());

		// There may be initial text in the field, so we may need to display the
		// button
		handleClearButton();

		// if the Close image is displayed and the user remove his finger from
		// the button, clear it. Otherwise do nothing
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				REditText et = REditText.this;

				if (et.getCompoundDrawables()[2] == null)
					return false;

				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				if (event.getX() > et.getWidth() - et.getPaddingRight()
						- imgCloseButton.getIntrinsicWidth()) {
					et.setText("");
					REditText.this.handleClearButton();
					return true;
				}
				return false;
			}
		});

		// if text changes, take care of the button
		this.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				REditText.this.handleClearButton();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
	}

	void handleClearButton() {
		if (this.getText().toString().equals("")) {
			// add the clear button
			this.setCompoundDrawables(this.getCompoundDrawables()[0],
					this.getCompoundDrawables()[1], null,
					this.getCompoundDrawables()[3]);
		} else {
			this.setCompoundDrawables(this.getCompoundDrawables()[0],
					this.getCompoundDrawables()[1], imgCloseButton,
					this.getCompoundDrawables()[3]);
		}
	}
}
