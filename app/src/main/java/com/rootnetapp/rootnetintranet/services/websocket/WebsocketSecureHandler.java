package com.rootnetapp.rootnetintranet.services.websocket;

import android.util.Log;

import com.rootnetapp.rootnetintranet.commons.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.auth.ChallengeResponseAuth;
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class WebsocketSecureHandler {

    private String protocol;
    private String port;
    private String token;

    private CompletableFuture<ExitInfo> exitInfoCompletableFuture;

    private MutableLiveData<String[]> incomingNotification;

    private static final String TAG = "WebsocketHandler";
    public static int INDEX_TITLE = 0;
    public static int INDEX_MESSAGE = 1;

    public WebsocketSecureHandler(String protocol, String port, String token) {
        this.protocol = protocol;
        this.port = port;
        this.token = token;
        this.incomingNotification = new MutableLiveData<>();
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

    public void completeClient() {
        exitInfoCompletableFuture.complete(new ExitInfo(true));
    }

    public void cancelClient() {
        // mayInterruptRunning doesn't affect the method implementation.
        exitInfoCompletableFuture.cancel(true);
    }

    public void initNotifications() {
        // Create a session object
        Session session = new Session();
        // Add all onJoin listeners
        session.addOnJoinListener(this::subscribeToWebsocket);
        session.addOnReadyListener(readySession -> {
            Log.d(TAG, "3 initNotifications: On Ready Session");
        });
        session.addOnDisconnectListener((sessionDisconnect, clean) -> {
            Log.d(TAG, "initNotifications: On Disconnect");
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
            } else {
                Log.d(TAG, "initNotifications: something else");
            }
        });

        String domain;
        try {
            domain = getDomainName(Utils.domain);
        } catch (URISyntaxException e) {
            Log.d(TAG, "initNotifications: Missing websocket settings");
            return;
        }

        String url = protocol + "://" + domain + ":" + port + "/";
        String realm = "master";

//         finally, provide everything to a Client and connect
        IAuthenticator authenticator = new ChallengeResponseAuth(token);
        Client client = new Client(session, url, realm, authenticator);
        exitInfoCompletableFuture = client.connect();
    }

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
            }
        });
    }

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
        String message = (String) incomingMessage.get(keyMessage);
        String title = (String) incomingMessage.get(keyTitle);

        String[] notificationMessage = new String[]{title, message};
        incomingNotification.postValue(notificationMessage);
    }

    private static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public LiveData<String[]> getObservableIncomingNotification() {
        return incomingNotification;
    }
}
