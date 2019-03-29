package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.rootnetapp.rootnetintranet.notifications.NotificationHandler;
import com.rootnetapp.rootnetintranet.notifications.NotificationIds;

import java.lang.ref.WeakReference;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.rootnetapp.rootnetintranet.services.websocket.WebsocketSecureHandler.REASON_AUTH_FAILURE;
import static com.rootnetapp.rootnetintranet.services.websocket.WebsocketSecureHandler.REASON_CLOSE;
import static com.rootnetapp.rootnetintranet.services.websocket.WebsocketSecureHandler.REASON_GOODBYE;

public class ServiceHandler extends Handler {

    private int startId;
    private int counter = 0;

    private static final String TAG = "ServiceHandler";

    private WebsocketSecureHandler webSocketHandler;

    private final WeakReference<Service> serviceWeakReference;

    ServiceHandler(Looper looper, Service service) {
        super(looper);
        serviceWeakReference = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        Service service = serviceWeakReference.get();
        if (service == null) {
            return;
        }

        Intent intent = (Intent) msg.obj;

        startId = msg.arg1;

        String token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        String port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        String protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        String domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        NotificationHandler.createNotificationChannel(notificationManager);
        initWebsocket(protocol, port, token, domain);
    }

    public void testDebug(Service service) {
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        String TAG = "ServiceHandler";
        try {
            Log.d(TAG, "handleMessage: GOING TO SLEEP 30 SEC");
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
            Log.d(TAG, "handleMessage: Thread error " + e.getMessage());
            return;
        }
        Log.d(TAG, "testDebug: ");
        counter += 1;
        NotificationHandler.prepareNotification(
                "199",
                "TITLE Alive" ,
                "ALIVE ALIve " + startId,
                "Name test",
                service,
                notificationManager,
                counter
        );
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
        Service service = serviceWeakReference.get();
        if (service == null) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            counter += 1;
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    messageArray[WebsocketSecureHandler.INDEX_NAME],
                    service,
                    notificationManager,
                    NotificationIds.NOTIFICATION_ID + counter
            );
        }, errorMessage -> {
            switch (errorMessage) {
                case REASON_AUTH_FAILURE:
                    break;
                case REASON_CLOSE:
                    restartWebSocket(protocol, port, token, domain);
                    return;
                case REASON_GOODBYE:
                    return;
                default:
                    Log.d(TAG, "websocket disconnected: not managed leave resason");
            }

            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            String message = TAG + " - Service was destroyed. Reason: " + errorMessage;
            Crashlytics.logException(new ServiceDestroyedException(message));

            service.stopSelf(startId);
        });
    }

    /**
     * Stops the websocket handler if it is not null.
     */
    public void stopWebsocket() {
        if (webSocketHandler == null) {
            return;
        }
        webSocketHandler.cancelClient();
    }

    /**
     * Restarts the websocket by creating a new NotificationManager and new WebsocketHandler.
     *
     * @param protocol
     * @param port
     * @param token
     * @param domain
     */
    private void restartWebSocket(String protocol, String port, String token, String domain) {
        Service service = serviceWeakReference.get();

        counter = 0;
        if (webSocketHandler != null) {
            webSocketHandler.completeClient();
        }

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            counter += 1;
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    messageArray[WebsocketSecureHandler.INDEX_NAME],
                    service,
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

//                    Log.d(TAG, "initNotifications: not managed leave resason");
            }

            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

//            stopSelf();
        });

    }
}
