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

    private NotificationManager notificationManager;

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

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        initWebsocket(protocol, port, token, domain);
        NotificationHandler.createNotificationChannel(notificationManager);


        testDebug(this);

        stopSelf();

        testDebug(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        Toast.makeText(getApplicationContext(), "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void testDebug(Service service) {
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
        NotificationHandler.prepareNotification("199", "TITLE Alive" , "ALIVE ALIve ", service, notificationManager);
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
        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    this,
                    notificationManager
            );
        }, errorMessage -> {

            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            //stopSelf(startId);
            stopSelf();

        });
    }

}
