package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class WebSocketService extends Service {

    private static final String CHANNEL_ID = "web-socket";
    private static final CharSequence CHANNEL_NAME = "Notifications";
    private static final int NOTIFICATION_ID = 77;

    private volatile ServiceHandler serviceHandler;
    private volatile Looper looper;

    private static final String TAG = "INTRANET";

    private static String token, port, protocol, domain;

    private int startId;
    private int what;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(
                "WebSocketHandler",
                Process.THREAD_PRIORITY_BACKGROUND
        );
        thread.start();
        looper = thread.getLooper();
        serviceHandler = new ServiceHandler(looper, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;

        // TODO get from intent the token or some more parameters needed.
        // TODO send to handler a message with all the arguments that we need to share.
        if (intent == null) {
//            Toast.makeText(getApplicationContext(), "INTENT NULL " + startId, Toast.LENGTH_LONG).show();
            return START_REDELIVER_INTENT;
        } else {
            Log.d(TAG, "onStartCommand: WEBSCOKET STARTED WITH INTENT: start id " + startId);
//            Toast.makeText(getApplicationContext(), "onStartCommand " + startId, Toast.LENGTH_LONG).show();
        }

        token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

        Message message = serviceHandler.obtainMessage();
        what = message.what;
        message.arg1 = startId;
        message.obj = intent;

        boolean wasSentToThread = serviceHandler.sendMessage(message);
        if (!wasSentToThread) {
            String error = "onStartCommand: Can't send message with webSocket details" +
                    " to webSocket's service handler thread. Looper might be closed";
            Log.d(TAG, error);
        }

        //create a permanent notification to keep this service alive
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setShowWhen(false)
                .setSubText(getString(R.string.background_service_notification_text))
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setGroup("WebSocketService");

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        //todo GoogleAnalytics
//        Toast.makeText(getApplicationContext(), "onDestroy Service " + startId , Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
        serviceCleanup();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void serviceCleanup() {
        RestartWebsocketReceiver.resetReceiverRunningIndicator();
        serviceHandler.stopWebsocket();
        serviceHandler.removeMessages(what);
        stopSelf(startId);
        looper.quit();
//        sendBroadcastWebsocket();
    }

    private void sendBroadcastWebsocket() {
        Intent broadcastIntent = createIntent(RestartWebsocketReceiver.class, token, port, protocol,
                domain);
        broadcastIntent.setAction("restartservice");
        sendBroadcast(broadcastIntent);
    }

    private Intent createIntent(Class<?> className, String token, String port, String protocol,
                                String domain) {
        Intent intent = new Intent(getApplicationContext(), className);
        intent.putExtra(WebsocketSecureHandler.KEY_TOKEN, token);
        intent.putExtra(WebsocketSecureHandler.KEY_PORT, port);
        intent.putExtra(WebsocketSecureHandler.KEY_PROTOCOL, protocol);
        intent.putExtra(WebsocketSecureHandler.KEY_DOMAIN, domain);
        return intent;
    }

}
