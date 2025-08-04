package com.example.ambulancealert.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AmbulanceSocketHandler extends TextWebSocketHandler {

    // A thread-safe list to hold all client sessions
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Add new session to the list when a client connects
        sessions.add(session);
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    /**
     * This public method can be called from other services to send a message
     * to all connected clients.
     * @param message The message to broadcast.
     */
    public void broadcast(String message) {
        System.out.println("Broadcasting message: " + message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
            }
        }
    }
}