/**
 * @Copyright coppermobile 2014.
 * */
package com.atn.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.atn.app.R;


//Create profile screen
public class CreateProfileActivity extends AtnActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_profile_layout);
		final ActionBar bar = getSupportActionBar();
		//enable back button
	    bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		
		setActionBarAlpha(0);
	}	
	
	//finish on back press.
	@Override
	public boolean onSupportNavigateUp() {
			finish();
		return super.onSupportNavigateUp();
	}

}
