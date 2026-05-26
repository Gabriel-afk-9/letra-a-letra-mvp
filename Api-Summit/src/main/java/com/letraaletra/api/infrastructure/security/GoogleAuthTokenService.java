package com.letraaletra.api.infrastructure.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.letraaletra.api.application.context.GoogleAuthData;
import com.letraaletra.api.application.port.GoogleTokenService;
import com.letraaletra.api.domain.security.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class GoogleAuthTokenService implements GoogleTokenService {
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthTokenService(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public GoogleAuthData verify(String token) {
        try {
            GoogleIdToken idToken = verifier.verify(token);

            if (idToken == null) {
                throw new InvalidTokenException();
            }

            return new GoogleAuthData(
                    idToken.getPayload().getEmail(),
                    idToken.getPayload().getSubject()
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new InvalidTokenException();
        }
    }
}
