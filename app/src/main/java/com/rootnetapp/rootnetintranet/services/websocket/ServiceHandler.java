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
        initWebSocket(protocol, port, token, domain);
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
    private void initWebSocket(String protocol, String port, String token, String domain) {
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
                    break;
                case REASON_GOODBYE:
                    return;
                default:
                    Log.d(TAG, "websocket disconnected: not managed leave resason");
            }

            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            String message = TAG + " - Service was destroyed. Reason: " + errorMessage;
            Crashlytics.logException(new ServiceDestroyedException(message));

            service.stopSelf(startId);

            initWebSocket(protocol, port, token, domain);
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
}
