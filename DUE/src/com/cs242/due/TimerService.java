package com.cs242.due;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class TimerService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public TimerService() {
		super("TimerService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Object[] extras = (Object[]) intent.getSerializableExtra("list");
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.

		if (extras.length!=0) { // has effect of unparcelling Bundle
			SimpleDateFormat df = new SimpleDateFormat("dd:HH:mm:ss");
			try {
				while (true) {

					Thread.sleep(1000);
					long curTime = (new Date()).getTime();
					for(Object duedate: extras){
							String datedue = (String) duedate;
							Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
									Locale.ENGLISH).parse(datedue);
						

						long milliseconds = date.getTime();
						long millisecondsFromNow = milliseconds- curTime;
						
						if(millisecondsFromNow < 1800000 && millisecondsFromNow > 1790000/* || millisecondsFromNow == 900000 || millisecondsFromNow == 300000*/){
							sendNotification("You have an assignment is about to due in "+ df.format(millisecondsFromNow));
						}
					}
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
			// Post notification of received message.
			//sendNotification(extras);
			// Log.i(TAG, "Received: " + extras.toString());

		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Login.class), 0);
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.impo)
				.setContentTitle("DUE!")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg).setSound(soundUri);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
