/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.atn.app.R;

/**
 * Terms and conditions screen
 * Load terms and condition from raw html file 
 * */
public class TAndCFragment extends AtnBaseFragment {

	public static AtnBaseFragment newInstance() {
		TAndCFragment fragment = new  TAndCFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setTitle(R.string.terms_and_condition);
		View view = inflater.inflate(R.layout.terms_conditions, container,false);
		
		WebView mWebView = (WebView)view.findViewById(R.id.webView);
		mWebView.setBackgroundColor(0);
		mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
		mWebView.loadUrl("file:///android_res/raw/terms_conditions.html");
		return view;
	}

	
}
