package com.atn.app.instagram;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.atn.app.R;

/**
 * Display Instagram authentication dialog.
 * 
 */

public class InstagramDialog extends DialogFragment {
	
	public interface InstagramLoginListener {
		public void onLoggedIn(boolean isSuccess,String message);
	}

	private InstagramLoginListener mListener;
	private ProgressBar mProgressBar = null;
	private WebView mWebView;

	
	public void setOAuthDialogListener(InstagramLoginListener mListener) {
		this.mListener = mListener;
	}

	
	public static InstagramDialog newInstance() {
		InstagramDialog f = new InstagramDialog();
        return f;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0,R.style.CommentDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.instgram_login_dialog, container, false);
        v.findViewById(R.id.back_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener!=null){
					mListener.onLoggedIn(false, null);
				}
				InstagramDialog.this.dismiss();
			}
		});
        
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mWebView = (WebView) v.findViewById(R.id.web_view);
        mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.setWebViewClient(new OAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.loadUrl(InstagramSession.getInstagramLoginUrl());
        CookieSyncManager.createInstance(getActivity());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		
        return v;
    }

	private class OAuthWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			if (url.startsWith(InstagramSession.CALLBACK_URL)) {
				String urls[] = url.split("\\#");
				String message = "";
				boolean isSuccess = false;
				if(urls.length==2){
					String[] keyValue =  urls[1].split("\\&");
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < keyValue.length; i++) {
						String string = keyValue[i];
						String[] keyV = string.split("\\=");
						if(keyV.length==2){
							isSuccess = keyV[0].equals("access_token");
							builder.append(keyV[1]+" ");
						}
					}
					message = builder.toString();
				}else{
					message = url;
				}
				
				if(isSuccess){
					InstagramSession.storeAccessToken(getActivity(), message.trim());
				}
				InstagramDialog.this.dismiss();
				if(mListener!=null){
				  mListener.onLoggedIn(isSuccess, message);
				}
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			InstagramDialog.this.dismiss();
			if(mListener!=null){
				mListener.onLoggedIn(false, description);
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
	        ///show prgress bar
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}	
	
}