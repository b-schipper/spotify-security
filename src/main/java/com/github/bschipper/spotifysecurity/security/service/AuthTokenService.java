package com.github.bschipper.spotifysecurity.security.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthTokenService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    String generateTokenFromUsername(String username);

    boolean validateToken(String authToken);

    boolean isTokenValid(String token, UserDetails userDetails);
}
