/**
 * @Copyright coppermobile 2014.
 * 
 * */
package com.atn.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.atn.app.R;


/**
 * Show terms and condition screen
 * */
public class TermsAndCondition extends AtnActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_contion_activity); 
        final ActionBar bar = getSupportActionBar();
		 bar.setDisplayHomeAsUpEnabled(true);
		 bar.setDisplayShowHomeEnabled(false);
    }
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
}
