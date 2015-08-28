/***
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.atn.app.R;
import com.atn.app.httprequester.FsHttpRequest;
import com.atn.app.utils.AtnTask;
import com.atn.app.utils.AtnUtils;


/**
 * Open FourSquare venue (Fetch url if not exist)Canonical url.
 * */
public class OpenFourSquareUrlActivity extends ActionBarActivity implements
		OnClickListener {

	//four square venue id key
	public static final String FOUR_SQUARE_ID = "FOUR_SQUARE_ID";
	//four anonical url key
	public static final String CANONICAL_URL = "CANONICAL_URL";
	
	
	private WebView mWebView = null;
	private ProgressBar mProgressBar = null;
	private ImageButton mRefreshImgBtn = null;
	//task for loading canonical url.
	private LoadFourSquareUrlTask loadUrlTask = null;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle arg0) {
		//hide action bar
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		getSupportActionBar().hide();
		setContentView(R.layout.open_four_square_layout);
		mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
		mRefreshImgBtn  =(ImageButton)findViewById(R.id.page_refresh_btn);
		mWebView = (WebView)findViewById(R.id.web_view);
		mWebView.setWebViewClient(new FourUrlLoadWebClient());
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		
		mRefreshImgBtn.setOnClickListener(this);
		findViewById(R.id.previous_page_img_btn).setOnClickListener(this);
		findViewById(R.id.next_page_img_btn).setOnClickListener(this);
		findViewById(R.id.done_btn).setOnClickListener(this);
		
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null) {
			String url = bundle.getString(CANONICAL_URL);
			if(!TextUtils.isEmpty(url)) {
				mWebView.loadUrl(url);
			} else {
				String fourSquareId  =bundle.getString(FOUR_SQUARE_ID);
				loadUrlTask = new LoadFourSquareUrlTask();
				loadUrlTask.execute(fourSquareId);
			}
		}
	}

	//web view client
	private class FourUrlLoadWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showProgressBar(true);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			showProgressBar(false);
		}
	}
	
	/**
	 * Show Hide Progress bar
	 * @param isShow true for show progress bar.
	 * */
	private void showProgressBar(boolean isShow) {
		mProgressBar.setVisibility(isShow?View.VISIBLE:View.INVISIBLE);
		mRefreshImgBtn.setVisibility(isShow?View.INVISIBLE:View.VISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		cancelTask();
	}
	
	private void cancelTask() {
		// cancel task if runnning
		if (loadUrlTask != null
				&& loadUrlTask.getStatus() != AtnTask.Status.FINISHED) {
			loadUrlTask.cancel(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.done_btn: {
			cancelTask();
			finish();
		}
			break;
		case R.id.previous_page_img_btn: {
			mWebView.goBack();
		}
			break;
		case R.id.next_page_img_btn: {
			mWebView.goForward();
		}
			break;
		case R.id.page_refresh_btn: {
			mWebView.reload();
		}
			break;
		default:
			break;
		}
	}
	
	//canonical url loader task fetch url and save into database
	private class LoadFourSquareUrlTask extends AtnTask<String, Void, String>{
		private String errorMessage = "";
		
		@Override
		public void onPreExecute() {
			showProgressBar(true);
			super.onPreExecute();
		}
		
		@Override
		public String doInBackground(String... params) {
			FsHttpRequest request = new FsHttpRequest(getApplicationContext());
			String uString = request.fetchCanonicalUrl(params[0]);;
			errorMessage = request.getError();
			return uString;
		}
		@Override
		public void onPostExecute(String result) {
			super.onPostExecute(result);
			if(TextUtils.isEmpty(result)){
				AtnUtils.showToast(errorMessage);
			} else {
				mWebView.loadUrl(result);
			}
		}
	}
}
