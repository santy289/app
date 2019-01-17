package com.rootnetapp.rootnetintranet.services.websocket;

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
import io.crossbar.autobahn.wamp.types.TransportOptions;

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
    private String url;

    private CompletableFuture<ExitInfo> exitInfoCompletableFuture;

    private WebSocketSecureCallback callback;
    private WebSocketErrorCallback errorCallback;
    private Session session;

    private static final String TAG = "WebsocketHandler";
    public static final int INDEX_TITLE = 0;
    public static final int INDEX_MESSAGE = 1;
    public static final int INDEX_ID = 2;
    public static final int INDEX_NAME = 3;

    public static final int ERROR_AUTHENTICATION = 100;
    public static final int ERROR_SUBSCRIBING = 101;
    public static final int ERROR_DISCONNECT = 102;

    public static final String KEY_TOKEN = "intranet.token";
    public static final String KEY_PORT = "intranet.port";
    public static final String KEY_PROTOCOL = "intranet.protocol";
    public static final String KEY_DOMAIN = "intranet.domain";

    private final String realm = "master";

    public WebsocketSecureHandler(String protocol, String port, String token, String domain) {
        this.protocol = protocol;
        this.port = port;
        this.token = token;

        String domainName;
        try {
            domainName = getDomainName(domain);
        } catch (URISyntaxException e) {
            Log.d(TAG, "initNotifications: Missing websocket settings");
            return;
        }

        this.url = protocol + "://" + domainName + ":" + port + "/";
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
        session.leave();
        exitInfoCompletableFuture.complete(new ExitInfo(true));
    }

    /**
     * It will cancel the Future and cuts off any ongoing action.
     */
    public void cancelClient() {
        // mayInterruptRunning doesn't affect the method implementation.
        session.leave();
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
        session = new Session();
        // Add all onJoin listeners
        session.addOnJoinListener(this::subscribeToWebsocket);
        session.addOnReadyListener(readySession -> {
            Log.d(TAG, "3 initNotifications: On Ready Session");
        });
        session.addOnDisconnectListener((sessionDisconnect, clean) -> {
            Log.d(TAG, "initNotifications: On Disconnect");
            if (errorCallback == null) {
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
            if (errorCallback == null) {
                return;
            }

            switch (details.reason) {
                case "thruway.error.authentication_failure":
                    errorCallback.onError(details.reason);
                    break;
                case "wamp.close.normal":
                    errorCallback.onError(details.reason);
                    break;
                default:
                    errorCallback.onError(details.reason);
                    Log.d(TAG, "initNotifications: not managed leave resason");
            }
        });

//         finally, provide everything to a Client and connect
        connect(session, url, realm, token);
    }

    private void connect(Session session, String url, String realm, String token) {
        IAuthenticator authenticator = new ChallengeResponseAuth(token);
        Client client = new Client(session, url, realm, authenticator);
        TransportOptions transportOptions = new TransportOptions();
        exitInfoCompletableFuture = client.connect(transportOptions);
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
        String keyProperty = "properties";
        String keyWorkflowId = "id";
        String keyName = "author";

        LinkedHashMap properties = (LinkedHashMap) incomingMessage.get(keyProperty);

        String message = (String) incomingMessage.get(keyMessage);
        String title = (String) incomingMessage.get(keyTitle);
        String name = (String) properties.get(keyName);
        Integer id = (Integer) properties.get(keyWorkflowId);

        String[] notificationMessage = new String[4];
        notificationMessage[INDEX_TITLE] = title;
        notificationMessage[INDEX_MESSAGE] = message;
        notificationMessage[INDEX_ID] = String.valueOf(id);
        notificationMessage[INDEX_NAME] = name;

        if (callback == null) {
            Log.d(TAG, "initNotifications: Needs a callback, callback can't be null");
        } else {
            callback.onMessageRecieved(notificationMessage);
        }
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
