package com.atn.app.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.atn.app.R;
import com.atn.app.activities.MainMenuActivity;
import com.atn.app.datamodels.PushNotificationData;
import com.atn.app.pool.UserDataPool;
import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * Creates a service to parse the push notification message received from server and notify the user.
 *
 */
public class GcmIntentService extends IntentService {
	
	private static final int NOTIFICATION_ID = 1;
	private final static String MESSAGE = "pushData";

	/**
	 * constructor
	 */
	public GcmIntentService() {
		super("GcmIntentService");
	}

	
	/**
	 * Handles push notification message.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
	
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()&&UserDataPool.getInstance().isUserLoggedIn()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				
				PushNotificationData pushData = new PushNotificationData(extras.getString(MESSAGE));
				sendNotification(pushData);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}   

	
	/**
	 * Shows a notification to the notification drawer using specified message.
	 * 
	 * @param msg to display in notification drawer.
	 */
	private void sendNotification(PushNotificationData pushData) {
		
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		Intent intent = new Intent(this, MainMenuActivity.class);
		intent.setAction(Long.toString(System.currentTimeMillis()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(PushNotificationData.PUSH_DATA, pushData);
		stackBuilder.addNextIntent(intent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
		
 		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
 		mBuilder.setSmallIcon(R.drawable.app_icon);
 		mBuilder.setContentTitle(pushData.getTitle());
 		mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(pushData.getDetail()));
 		mBuilder.setContentText(pushData.getDetail());
 		mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
 		mBuilder.setAutoCancel(true);
 		mBuilder.setContentIntent(resultPendingIntent);    
 		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	
}
