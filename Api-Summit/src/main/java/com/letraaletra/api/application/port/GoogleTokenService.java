package com.letraaletra.api.application.port;

import com.letraaletra.api.application.context.GoogleAuthData;

public interface GoogleTokenService {
    GoogleAuthData verify(String token);
}
