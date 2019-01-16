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

/**
 * Not full tested and might have a memory leak.
 */
public class WebSocketService extends Service {

    private volatile ServiceHandler serviceHandler;
    private volatile Looper looper;

    private static final String TAG = "INTRANET";

    private int startId;
    private int what;

    @Override
    public void onCreate() {
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

        String token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        String port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        String protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        String domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

        Bundle bundle = new Bundle();
        bundle.putString(WebsocketSecureHandler.KEY_PORT, port);
        bundle.putString(WebsocketSecureHandler.KEY_PROTOCOL, protocol);
        bundle.putString(WebsocketSecureHandler.KEY_DOMAIN, domain);

        Message message = serviceHandler.obtainMessage();

        what = message.what;

        message.arg1 = startId;
        message.obj = token;
        message.setData(bundle);

        boolean wasSentToThread = serviceHandler.sendMessage(message);
        if (!wasSentToThread) {
            String error = "onStartCommand: Can't send message with webSocket details" +
                    " to webSocket's service handler thread. Looper might be closed";
            Log.d(TAG, error);
        }

        return START_REDELIVER_INTENT;
//        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy Service " + startId , Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
        serviceHandler.stopWebsocket();
        serviceHandler.removeMessages(what);
        looper.quit();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
