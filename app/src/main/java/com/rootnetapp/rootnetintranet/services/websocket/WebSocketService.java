package com.rootnetapp.rootnetintranet.services.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class WebSocketService extends Service {

    private ServiceHandler serviceHandler;
    private HandlerThread thread;

    private static final String TAG = "INTRANET";


    @Override
    public void onCreate() {
        thread = new HandlerThread(
                "WebSocketHandler",
                Process.THREAD_PRIORITY_BACKGROUND
        );
        thread.start();
        serviceHandler = new ServiceHandler(thread.getLooper(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: WEBSCOKET STARTED WITH INTENT: start id " + startId);

        Toast.makeText(getApplicationContext(), "onStartCommand " + startId, Toast.LENGTH_LONG).show();

        // TODO get from intent the token or some more parameters needed.
        // TODO send to handler a message with all the arguments that we need to share.

        String token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        String port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        String protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        String domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

        Bundle bundle = new Bundle();
        bundle.putString(WebsocketSecureHandler.KEY_PORT, port);
        bundle.putString(WebsocketSecureHandler.KEY_PROTOCOL, protocol);
        bundle.putString(WebsocketSecureHandler.KEY_DOMAIN, domain);

        Message message = serviceHandler.obtainMessage();

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
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy Service", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
        thread.quit();
//        thread.interrupt();
        thread = null;
        super.onDestroy();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
