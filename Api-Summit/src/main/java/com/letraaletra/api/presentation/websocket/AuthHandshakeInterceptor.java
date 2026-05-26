package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.domain.security.TokenService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) {
        UriComponents uri = UriComponentsBuilder.fromUri(request.getURI()).build();
        String token = uri.getQueryParams().getFirst("token");

        String userId = tokenService.getTokenContent(token);

        if (userId == null) {
            return false;
        }

        attributes.put("userId", userId);

        return true;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {}
}