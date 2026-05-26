package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.presentation.websocket.AuthHandshakeInterceptor;
import com.letraaletra.api.presentation.websocket.GlobalWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GlobalWebSocketHandler handler;
    private final AuthHandshakeInterceptor interceptor;

    public WebSocketConfig(GlobalWebSocketHandler handler, AuthHandshakeInterceptor interceptor) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/game")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*");
    }
}