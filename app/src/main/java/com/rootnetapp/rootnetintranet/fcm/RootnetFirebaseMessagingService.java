package com.rootnetapp.rootnetintranet.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.splash.SplashActivity;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;

import java.util.Map;

public class RootnetFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = data.get(NotificationDataKeys.KEY_TITLE);
            String message = data.get(NotificationDataKeys.KEY_MESSAGE);
            String workflowId = data.get(NotificationDataKeys.KEY_WORKFLOW_ID);
            String author = data.get(NotificationDataKeys.KEY_AUTHOR);
            int notificationId = 0;

            if (title != null && message != null && workflowId != null) {
                prepareNotification(title, message, workflowId, notificationId);
                return;
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification: " + remoteMessage.getNotification());

            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            int notificationId = 0;

            if (title != null && message != null) {
                prepareNotification(title, message, null, notificationId);
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of the previous token
     * had been compromised. Note that this is called when the InstanceID token is initially
     * generated so this is where you would retrieve the token.
     * <p>
     * We don't use this method to send the token to the server, because we fetch the current
     * instance id token when logging in, and this is done every time the app is opened.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    /**
     * Create and show a notification containing the received FCM message.
     * <p>
     * This notification creates a pending intent to a related workflow by using the payload's
     * workflow ID. If there is no workflow associated with it, simply opens the app to the launcher
     * activity.
     * <p>
     * This method should be called when a notification is received in {@link
     * #onMessageReceived(RemoteMessage)}, that is, when the app is on the foreground. Then, we
     * should display the notification manually.
     *
     * @param workflowId     ID of the workflow to be opened by the pending intent.
     * @param title          notification title
     * @param message        notification message
     * @param notificationId notifications with the same IDs overlap the previous one. Different IDs
     *                       make them all appear on the notification tray.
     */
    public void prepareNotification(
            @NonNull String title,
            @NonNull String message,
            @Nullable String workflowId,
            int notificationId) {

        Resources resources = getResources();

        Intent intent = new Intent(this, SplashActivity.class);

        if (workflowId != null && !workflowId.isEmpty()) {
            intent = new Intent(this, WorkflowDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(WorkflowDetailActivity.INTENT_EXTRA_ID, workflowId);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(true)
                        .setColor(resources
                                .getColor(R.color.colorAccent, getApplicationContext().getTheme()))
                        // priority and defaults need to be set together
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
