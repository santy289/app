package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.notifications.NotificationHandler;

import androidx.annotation.Nullable;

public class WebSocketIntentService extends IntentService {

    private WebsocketSecureHandler webSocketHandler;

    private int counter = 0;

    private static final String TAG = "WebsocketIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public WebSocketIntentService() {
        super("WebSocketIntentService");
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        String port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        String protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        String domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationHandler.createNotificationChannel(notificationManager);
        initWebsocket(protocol, port, token, domain);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(getApplicationContext(), "onTaskRemoved", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onTaskRemoved: onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelWebsocket();
        Log.d(TAG, "onDestroy: ");
//        Toast.makeText(getApplicationContext(), "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void testDebug(Service service) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            Log.i(TAG, "handleMessage: GOING TO SLEEP 30 SEC");
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
            Log.i(TAG, "handleMessage: Thread error " + e.getMessage());
            return;
        }
        Log.d(TAG, "testDebug: ");
        // Stop the service using the startId, so that we don't stop
        // the service in the middle of handling another job
//            prepareNotification("199", "alive", "aLIVE ALIVE service");
        NotificationHandler.prepareNotification("199", "TITLE Alive" , "ALIVE ALIve ", "test name", service, notificationManager, 0);
    }

    /**
     * If WebSocketHandler is not null this method will proceed and cancel the WebSocket.
     */
    private void cancelWebsocket() {
        if (webSocketHandler == null) {
            return;
        }
        webSocketHandler.cancelClient();
        webSocketHandler = null;
    }

    /**
     * This method will create a new WebsocketSecureHandler instance. Also it provides the
     * necessary callbacks for receiving messages from the web socket and any errors during the
     * connection.
     *
     * @param protocol
     * @param port
     * @param token
     */
    void initWebsocket(String protocol, String port, String token, String domain) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            counter += 1;
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    messageArray[WebsocketSecureHandler.INDEX_NAME],
                    this,
                    notificationManager,
                    counter

            );
        }, errorMessage -> {
            switch (errorMessage) {
                case "thruway.error.authentication_failure":
                    break;
                case "wamp.close.normal":
                    restartWebSocket(protocol, port, token, domain);
                    return;
                default:
                    Log.d(TAG, "websocket disconnected: not managed leave resason");
            }

            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            stopSelf();
        });
    }

    private void restartWebSocket(String protocol, String port, String token, String domain) {
        counter = 0;
        if (webSocketHandler != null) {
            webSocketHandler.completeClient();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            counter += 1;
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    messageArray[WebsocketSecureHandler.INDEX_NAME],
                    this,
                    notificationManager,
                    counter

            );
        }, errorMessage -> {
            switch (errorMessage) {
                case "thruway.error.authentication_failure":
                    break;
                case "wamp.close.normal":
                    break;
                default:

                    Log.d(TAG, "initNotifications: not managed leave resason");
            }

            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            stopSelf();
        });

    }

    private void showNotification(String[] messageArray) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

}
