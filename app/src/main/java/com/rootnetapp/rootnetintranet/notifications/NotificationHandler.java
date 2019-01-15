package com.rootnetapp.rootnetintranet.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;

import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

public class NotificationHandler {
    /**
     * Create a channel depending on the build version. We create one from Android O+.
     *
     * @param notificationManager
     */
    public static void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel notificationChannel = new NotificationChannel(
                NotificationChannels.WORKFLOW_COMMENTS_CHANNEL_ID,
                "Comments",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("Workflow Comments");

        notificationManager.createNotificationChannel(notificationChannel);
    }

    /**
     * This will create a notification.
     *
     * @param id
     * @param title
     * @param message
     */
    public static void prepareNotification(
            String id,
            String title,
            String message,
            Service service,
            NotificationManager notificationManager,
            int notificationId) {
        Resources resources = service.getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round);

        Person user = new Person.Builder()
                .setName("Intranet")
                .setIcon(IconCompat.createWithBitmap(logoBitmap))
                .build();
        Date date = new Date();
        NotificationCompat.MessagingStyle.Message messageStyle = new NotificationCompat.MessagingStyle.Message(
                message,
                date.getTime(),
                user
        );

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(user);
        messagingStyle.addMessage(messageStyle);
        messagingStyle.setConversationTitle(title);


        Intent detailIntent = new Intent(service.getApplicationContext(), WorkflowDetailActivity.class);
        detailIntent.putExtra(WorkflowDetailActivity.INTENT_EXTRA_ID, id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service.getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(detailIntent);
        PendingIntent detailPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
                service.getApplicationContext(),
                NotificationChannels.WORKFLOW_COMMENTS_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(title)
                .setContentIntent(detailPendingIntent)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setLargeIcon(logoBitmap)
                .setStyle(messagingStyle)
                .setAutoCancel(true)
                .setColor(resources.getColor(R.color.colorAccent, service.getApplicationContext().getTheme()))
                // priority and defaults need to be set together
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager.notify(
                notificationId,
                notifyBuilder.build()
        );
    }
}
