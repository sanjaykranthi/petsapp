package com.petsapp.android.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.petsapp.android.ChatActivity;
import com.petsapp.android.ChatListActivity;
import com.petsapp.android.LostFoundNotification;
import com.petsapp.android.Notification_count_session;
import com.petsapp.android.app.Config;

import java.util.HashMap;

/**
 * Created by WEB DESIGNING on 25-05-2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    String message1 = "", message, msgby, msgto, id, by_name, url, message2;
    Notification_count_session Notificationcountsession;
    String idString;
    int i, j;
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(String from, Bundle bundle) {

        String title = bundle.getString("title");
        message = bundle.getString("message");
        msgby = bundle.getString("msgby");
        msgto = bundle.getString("msgto");
        message1 = bundle.getString("message1");
        message2 = bundle.getString("message2");
        by_name = bundle.getString("by_name");
        url = bundle.getString("url");
        id = bundle.getString("id");
        System.out.println("Message in console" + title);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("msgby", msgby);
            pushNotification.putExtra("msgto", msgto);
            pushNotification.putExtra("message1", message1);
            pushNotification.putExtra("message2", message2);
            pushNotification.putExtra("nameVal", by_name);
            pushNotification.putExtra("phturl", url);
            pushNotification.putExtra("id", id);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils();
            notificationUtils.playNotificationSound();
            if (message1 == null && message2 == null) {
                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message1", message1);
                resultIntent.putExtra("nameVal", by_name);
                resultIntent.putExtra("phturl", url);
                resultIntent.putExtra("id", id);
                Notificationcountsession = new Notification_count_session(getApplicationContext());
                HashMap<String, String> user = Notificationcountsession.getUserDetails();

                idString = user.get(Notification_count_session.KEY_NAME);
                i = Integer.parseInt(idString);
                j = i + 1;
                Notificationcountsession.createLoginSession(String.valueOf(j));
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, msgby, msgto, id, resultIntent);
            } else if (message == null && message2 == null) {
                Intent resultIntent = new Intent(getApplicationContext(), LostFoundNotification.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message1", message1);
                resultIntent.putExtra("id", id);
                showNotificationMessageWithBigImage(getApplicationContext(), title, message1, msgby, msgto, id, resultIntent);
            } else if (message1 == null && message == null) {
                Intent resultIntent = new Intent(getApplicationContext(), ChatListActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message2", message2);
                pushNotification.putExtra("nameVal", by_name);
                pushNotification.putExtra("phturl", url);
                resultIntent.putExtra("id", id);

                showNotificationMessageWithBigImage(getApplicationContext(), title, message2, msgby, msgto, id, resultIntent);
            }
        } else {

            if (message1 == null && message2 == null) {
                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message1", message1);
                resultIntent.putExtra("nameVal", by_name);
                resultIntent.putExtra("phturl", url);
                resultIntent.putExtra("id", id);
                Notificationcountsession = new Notification_count_session(getApplicationContext());
                HashMap<String, String> user = Notificationcountsession.getUserDetails();

                idString = user.get(Notification_count_session.KEY_NAME);
                i = Integer.parseInt(idString);
                j = i + 1;
                Notificationcountsession.createLoginSession(String.valueOf(j));
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, msgby, msgto, id, resultIntent);

            } else if (message == null && message1 == null) {
                Intent resultIntent = new Intent(getApplicationContext(), ChatListActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message2", message2);
                resultIntent.putExtra("nameVal", by_name);
                resultIntent.putExtra("phturl", url);
                resultIntent.putExtra("id", id);
                showNotificationMessageWithBigImage(getApplicationContext(), title, message2, msgby, msgto, id, resultIntent);
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), LostFoundNotification.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("msgby", msgby);
                resultIntent.putExtra("msgto", msgto);
                resultIntent.putExtra("message1", message1);
                resultIntent.putExtra("id", id);
                showNotificationMessageWithBigImage(getApplicationContext(), title, message1, msgby, msgto, id, resultIntent);

            }

        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String msgby, String msgto, String id, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, msgby, msgto, id, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String msgby, String msgto, String id, Intent intent) {
      /*  Notificationcountsession = new Notification_count_session(getApplicationContext());
        HashMap<String, String> user = Notificationcountsession.getUserDetails();

        idString = user.get(Notificationcountsession.KEY_NAME);
        i = Integer.parseInt(idString);
        j = i+1;
        Notificationcountsession.createLoginSession(String.valueOf(j));*/
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, msgby, msgto, id, intent);

    }
}