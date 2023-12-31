package com.github.bschipper.spotifysecurity.features.authentication.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TokenRefreshResponse {
    private final String accessToken;
    private final String refreshToken;
    private static final String tokenType = "Bearer";
}