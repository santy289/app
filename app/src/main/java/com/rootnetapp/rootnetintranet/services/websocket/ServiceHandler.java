package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.NotificationManager;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.notifications.NotificationHandler;
import com.rootnetapp.rootnetintranet.notifications.NotificationIds;

import java.lang.ref.WeakReference;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Do not use it has a memory leak.
 */
public class ServiceHandler extends Handler {

    private int startId;
    private int counter = 0;

    private WebsocketSecureHandler webSocketHandler;

    private final WeakReference<Service> serviceWeakReference;

    ServiceHandler(Looper looper, Service service) {
        super(looper);
        serviceWeakReference = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        Service service = serviceWeakReference.get();
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        String token = (String) msg.obj;
        startId = msg.arg1;

        Toast.makeText(service.getApplicationContext(), "handleMessage " + startId, Toast.LENGTH_LONG).show();

        Bundle bundle = msg.getData();
        String protocol = bundle.getString(WebsocketSecureHandler.KEY_PROTOCOL);
        String port = bundle.getString(WebsocketSecureHandler.KEY_PORT);
        String domain = bundle.getString(WebsocketSecureHandler.KEY_DOMAIN);

        initWebsocket(protocol, port, token, domain);
        NotificationHandler.createNotificationChannel(notificationManager);
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
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        webSocketHandler = new WebsocketSecureHandler(protocol, port, token, domain);
        webSocketHandler.initNotificationsWithCallback(messageArray -> {
            counter += 1;
            NotificationHandler.prepareNotification(
                    messageArray[WebsocketSecureHandler.INDEX_ID],
                    messageArray[WebsocketSecureHandler.INDEX_TITLE],
                    messageArray[WebsocketSecureHandler.INDEX_MESSAGE],
                    service,
                    notificationManager,
                    NotificationIds.NOTIFICATION_ID + counter
            );
        }, errorMessage -> {
            // TODO if it disconnects try to connect again. Check if dosconnected message is given.
            if (webSocketHandler != null) {
                webSocketHandler.completeClient();
            }

            serviceWeakReference.get().stopSelf(startId);
        });
    }

    public void stopWebsocket() {
        if (webSocketHandler == null) {
            return;
        }
        webSocketHandler.cancelClient();
    }
}