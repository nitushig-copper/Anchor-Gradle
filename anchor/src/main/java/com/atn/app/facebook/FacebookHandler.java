package com.atn.app.facebook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;

public class FacebookHandler {

	public interface FacebookSessionListener {
		void onSessionStateChange(Session session, SessionState state,
				Exception exception);
	}

	private FacebookSessionListener listener;

	private UiLifecycleHelper uiHelper;
	private Activity mActivity;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (listener != null) {
				listener.onSessionStateChange(session, state, exception);
			}
		}
	};

	public void setFacebookSessionListener(FacebookSessionListener listener) {
		this.listener = listener;
	}

	public FacebookHandler(Activity context) {
		mActivity = context;
		uiHelper = new UiLifecycleHelper(context, callback);
	}

	public void onCreate(Bundle savedInstanceState) {
		uiHelper.onCreate(savedInstanceState);
	}

	public void onPause() {
		uiHelper.onPause();
	}

	public void onResume() {
		uiHelper.onResume();
	}

	public void onDestroy() {
		uiHelper.onDestroy();
	}

	public void onSaveInstanceState(Bundle outState) {
		uiHelper.onSaveInstanceState(outState);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	public void validateUserLogin() {
		Session.openActiveSession(mActivity, true, callback);
	}

	public static void postToWall(Activity activity) {

		createFacebookConnection(activity);
	}

	public static void createFacebookConnection(final Activity activity) {
		Session session = new Session(activity);
		Session.setActiveSession(session);

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session.StatusCallback statusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// String message = "Facebook session status changed - " +
				// session.getState() + " - Exception: " + exception;
				if (session.isOpened()
						|| session.getPermissions().contains("publish_actions")) {
					// publishToWall(activity);
				} else if (session.isOpened()) {
					OpenRequest open = new OpenRequest(activity)
							.setCallback(this);
					List<String> permission = new ArrayList<String>();
					permission.add("publish_actions");
					open.setPermissions(permission);
					session.openForPublish(open);
				}
			}
		};

		if (!session.isOpened() && !session.isClosed()
				&& session.getState() != SessionState.OPENING) {
			session.openForRead(new Session.OpenRequest(activity)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(activity, true, statusCallback);
		}
	}
}
