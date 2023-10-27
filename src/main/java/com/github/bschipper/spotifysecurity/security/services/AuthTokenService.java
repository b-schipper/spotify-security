package com.github.bschipper.spotifysecurity.security.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthTokenService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateTokenFromUsername(String username);

    boolean isTokenValid(String token, UserDetails userDetails);
}
