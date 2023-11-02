package com.github.bschipper.spotifysecurity.security.services;

import com.github.bschipper.spotifysecurity.exception.TokenRefreshException;
import com.github.bschipper.spotifysecurity.controller.payload.response.TokenRefreshResponse;
import com.github.bschipper.spotifysecurity.models.RefreshToken;
import com.github.bschipper.spotifysecurity.repository.UserRepository;
import com.github.bschipper.spotifysecurity.repository.RefreshTokenRepository;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import com.github.bschipper.spotifysecurity.security.services.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    @Value("${token.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private final AuthTokenService authTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenRefreshResponse findByToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = authTokenService.generateToken(UserPrincipal.create(user));
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() ->
                        new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findById(userId).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
