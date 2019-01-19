package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

public class WebSocketService extends Service {

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
            Toast.makeText(getApplicationContext(), "INTENT NULL " + startId, Toast.LENGTH_LONG).show();
            return START_REDELIVER_INTENT;
        } else {
            Log.d(TAG, "onStartCommand: WEBSCOKET STARTED WITH INTENT: start id " + startId);
            Toast.makeText(getApplicationContext(), "onStartCommand " + startId, Toast.LENGTH_LONG).show();
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

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy Service " + startId , Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
        serviceHandler.stopWebsocket();
        serviceHandler.removeMessages(what);
        stopSelf(startId);
        looper.quit();
        sendBroadcastWebsocket();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendBroadcastWebsocket() {
        Intent broadcastIntent = createIntent(RestartWebsocketReceiver.class, token, port, protocol, domain);
        broadcastIntent.setAction("restartservice");
        sendBroadcast(broadcastIntent);
    }

    private Intent createIntent(Class<?> className, String token, String port, String protocol, String domain) {
        Intent intent = new Intent(getApplicationContext(), className);
        intent.putExtra(WebsocketSecureHandler.KEY_TOKEN, token);
        intent.putExtra(WebsocketSecureHandler.KEY_PORT, port);
        intent.putExtra(WebsocketSecureHandler.KEY_PROTOCOL, protocol);
        intent.putExtra(WebsocketSecureHandler.KEY_DOMAIN, domain);
        return intent;
    }

}
