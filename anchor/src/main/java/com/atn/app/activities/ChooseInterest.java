
/*
 * Copyright CopperMobile
 * */
package com.atn.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.atn.app.R;

/*
 * This screen show interest, 
 * **/
public class ChooseInterest extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_interest_activity);
		getSupportActionBar().hide();
	}

	@Override
	public void onBackPressed() {
	     setResult(Activity.RESULT_OK);
		super.onBackPressed();
	}
	
}
