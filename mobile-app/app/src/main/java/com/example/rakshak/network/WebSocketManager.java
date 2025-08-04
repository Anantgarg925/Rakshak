package com.example.rakshak.network;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    // Remember: 10.0.2.2 is the address for your computer's localhost from the Android emulator
    private static final String WEBSOCKET_URL = "ws://192.168.29.140:8081/tracking";

    private OkHttpClient client;
    private WebSocket webSocket;

    public interface MessageListener {
        void onMessageReceived(String message);
        void onConnectionChange(boolean isConnected);
    }

    public void connect(final MessageListener listener) {
        if (client == null) {
            client = new OkHttpClient();
        }

        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.i(TAG, "WebSocket connection opened!");
                listener.onConnectionChange(true);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.i(TAG, "New message received: " + text);
                listener.onMessageReceived(text);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.i(TAG, "WebSocket connection closed: " + reason);
                listener.onConnectionChange(false);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e(TAG, "WebSocket connection failure: " + t.getMessage());
                listener.onConnectionChange(false);
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User disconnected");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}