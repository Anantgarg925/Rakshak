package com.example.ambulancealert.config;

import com.example.ambulancealert.service.AmbulanceSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AmbulanceSocketHandler ambulanceSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Maps the handler to the "/tracking" endpoint
        registry.addHandler(ambulanceSocketHandler, "/tracking").setAllowedOrigins("*");
    }
}