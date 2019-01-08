package com.rootnetapp.rootnetintranet.services.websocket;

import android.util.Log;

import androidx.annotation.Nullable;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RootnetWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private static final String TAG = "WebSocketListener";

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("test websocket");
        webSocket.send("second test");
        webSocket.close(NORMAL_CLOSURE_STATUS, "Closing test");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "onMessage: ");
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d(TAG, "onClosing: ");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "onClosed: ");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        Log.d(TAG, "onFailure: ");
    }
}
