package com.rootnetapp.rootnetintranet.services.websocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class RestartWebsocketReceiver extends BroadcastReceiver {

    private static boolean running = false;

    private static final String TAG = "RestartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (running || intent == null) {
            Log.d(TAG, "onReceive: Websocket still running or intent is null.");
            return;
        }
        running = true;

        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        String token = intent.getStringExtra(WebsocketSecureHandler.KEY_TOKEN);
        String protocol = intent.getStringExtra(WebsocketSecureHandler.KEY_PROTOCOL);
        Toast.makeText(context, protocol, Toast.LENGTH_LONG).show();
        String port = intent.getStringExtra(WebsocketSecureHandler.KEY_PORT);
        String domain = intent.getStringExtra(WebsocketSecureHandler.KEY_DOMAIN);

//        Intent reloadedIntent = new Intent(context, WebSocketIntentService.class);

        Intent reloadedIntent = new Intent(context, WebSocketService.class);

        reloadedIntent.putExtra(WebsocketSecureHandler.KEY_TOKEN, token);
        reloadedIntent.putExtra(WebsocketSecureHandler.KEY_PORT, port);
        reloadedIntent.putExtra(WebsocketSecureHandler.KEY_PROTOCOL, protocol);
        reloadedIntent.putExtra(WebsocketSecureHandler.KEY_DOMAIN, domain);
        reloadedIntent.putExtra(WebsocketSecureHandler.KEY_BACKGROUND, true);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, WebSocketIntentService.class));
//        } else {


            context.startService(reloadedIntent);
//        }
    }

    public static void resetReceiverRunningIndicator() {
        running = false;
    }
}
