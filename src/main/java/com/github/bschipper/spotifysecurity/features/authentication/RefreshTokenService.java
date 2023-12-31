package com.github.bschipper.spotifysecurity.features.authentication;

import com.github.bschipper.spotifysecurity.exception.custom.TokenRefreshException;
import com.github.bschipper.spotifysecurity.features.authentication.response.TokenRefreshResponse;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import com.github.bschipper.spotifysecurity.security.services.JwtUtil;
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

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenRefreshResponse findByToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtil.generateToken(UserPrincipal.create(user));
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() ->
                        new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    public Boolean existsByUserId(Long userId) {
        return refreshTokenRepository.existsRefreshTokenByUserId(userId);
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
