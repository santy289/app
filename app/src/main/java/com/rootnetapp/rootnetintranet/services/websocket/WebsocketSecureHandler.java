package com.rootnetapp.rootnetintranet.services.websocket;

import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;
import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.auth.ChallengeResponseAuth;
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class WebsocketSecureHandler {

    public interface WebSocketSecureCallback {
        void onMessageRecieved(String[] messageArray);
    }

    public interface WebSocketErrorCallback {
        void onError(String errorMessage);
    }

    private String protocol;
    private String port;
    private String token;
    private String domain;

    private CompletableFuture<ExitInfo> exitInfoCompletableFuture;

    private WebSocketSecureCallback callback;
    private WebSocketErrorCallback errorCallback;

    private static final String TAG = "WebsocketHandler";
    public static final int INDEX_TITLE = 0;
    public static final int INDEX_MESSAGE = 1;
    public static final int INDEX_ID = 2;

    public static final int ERROR_AUTHENTICATION = 100;
    public static final int ERROR_SUBSCRIBING = 101;
    public static final int ERROR_DISCONNECT = 102;

    public static final String KEY_TOKEN = "intranet.token";
    public static final String KEY_PORT = "intranet.port";
    public static final String KEY_PROTOCOL = "intranet.protocol";
    public static final String KEY_DOMAIN = "intranet.domain";

    public WebsocketSecureHandler(String protocol, String port, String token, String domain) {
        this.protocol = protocol;
        this.port = port;
        this.token = token;
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }

    public String getToken() {
        return token;
    }

    /**
     * Calls complete(ExitInfo(true)) for the current exitINfoCompletableFuture created.
     */
    public void completeClient() {
        exitInfoCompletableFuture.complete(new ExitInfo(true));
    }

    /**
     * It will cancel the Future and cuts off any ongoing action.
     */
    public void cancelClient() {
        // mayInterruptRunning doesn't affect the method implementation.
        exitInfoCompletableFuture.cancel(true);
    }

    /**
     * This method will take callbacks instead of relying on observables. Passing callbacks will
     * set them as default and any attempt to use LiveData will be ignored and callbacks will be
     * used instead.
     *
     * @param callback
     * @param errorCallback
     */
    public void initNotificationsWithCallback(@NonNull WebSocketSecureCallback callback, @NonNull WebSocketErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        initNotifications();
    }

    /**
     * This method will initiate a new session and this session will listen for new actions coming
     * fom the webSocket.
     */
    private void initNotifications() {
        // Create a session object
        Session session = new Session();
        // Add all onJoin listeners
        session.addOnJoinListener(this::subscribeToWebsocket);
        session.addOnReadyListener(readySession -> {
            Log.d(TAG, "3 initNotifications: On Ready Session");
        });
        session.addOnDisconnectListener((sessionDisconnect, clean) -> {
            Log.d(TAG, "initNotifications: On Disconnect");
            if (callback == null) {
                Log.d(TAG, "initNotifications: Needs a callback, callback can't be null");
            } else {
                errorCallback.onError("Error Disconnect");
            }
        });

        session.addOnConnectListener(sessionConnect -> {
            Log.d(TAG, "1 initNotifications: on Connect");
        });
        session.addOnJoinListener((sessionJoin, details) -> {
            Log.d(TAG, "2 initNotifications: on Join");
        });
        session.addOnLeaveListener((sessionLeave, details) -> {
            if (details.reason.equals("thruway.error.authentication_failure")) {
                Log.d(TAG, "initNotifications: failure");
                if (errorCallback == null) {
                    Log.d(TAG, "initNotifications: Needs a errorCallback, callback can't be null");
                } else {
                    errorCallback.onError("thruway.error.authentication_failure");
                }
                // TODO log to analytics tool and send to server
            } else {
                Log.d(TAG, "initNotifications: something else");
            }
        });

        String domainName;
        try {
            domainName = getDomainName(domain);
        } catch (URISyntaxException e) {
            Log.d(TAG, "initNotifications: Missing websocket settings");
            return;
        }

        String url = protocol + "://" + domainName + ":" + port + "/";
        String realm = "master";

//         finally, provide everything to a Client and connect
        IAuthenticator authenticator = new ChallengeResponseAuth(token);
        Client client = new Client(session, url, realm, authenticator);
        exitInfoCompletableFuture = client.connect();
    }

    /**
     * Subscription to some topic in the web server.
     * @param session
     * @param details
     */
    private void subscribeToWebsocket(Session session, SessionDetails details) {
//         Subscribe to topic to receive its events.
        CompletableFuture<Subscription> subFuture = session.subscribe("master.notification",
                this::onEvent);
        subFuture.whenComplete((subscription, throwable) -> {
            if (throwable == null) {
                // We have successfully subscribed.
                System.out.println("Subscribed to topic " + subscription.topic);
            } else {
                // Something went bad.
                throwable.printStackTrace();

                if (errorCallback == null) {
                    Log.d(TAG, "initNotifications: Needs a errorCallback, callback can't be null");
                } else {
                    errorCallback.onError(throwable.getMessage());
                }
            }
        });
    }

    /**
     * On coming messages will be listen by onEvent. It will only listen to master.notifications.
     * @param args
     * @param kwargs
     * @param details
     */
    private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
        String topic = details.topic;
        if (!topic.equals("master.notification")) {
            return;
        }

        int indexMessage = 1;
        LinkedHashMap incomingMessage = (LinkedHashMap) args.get(indexMessage);
        if (incomingMessage == null) {
            return;
        }

        String keyMessage = "message";
        String keyTitle = "title";
        String keyId = "url";
        String message = (String) incomingMessage.get(keyMessage);
        String title = (String) incomingMessage.get(keyTitle);
        String id = (String) incomingMessage.get(keyId);

        id = getIdFromUrl(id);

        String[] notificationMessage = new String[3];
        notificationMessage[INDEX_TITLE] = title;
        notificationMessage[INDEX_MESSAGE] = message;
        notificationMessage[INDEX_ID] = id;

        if (callback == null) {
            Log.d(TAG, "initNotifications: Needs a callback, callback can't be null");
        } else {
            callback.onMessageRecieved(notificationMessage);
        }
    }

    /**
     * Helper method to get the last segment on some uri path.
     *
     * @param url
     * @return
     */
    private String getIdFromUrl(String url) {
        Uri uri = Uri.parse(url);
        return uri.getLastPathSegment();
    }

    /**
     * Gets a domain from some URL string.
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    private static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
