package com.github.bschipper.spotifysecurity.controller.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@RequiredArgsConstructor
@Getter
@Setter
public class JwtResponse {
    private final String token;
    private static final String type = "Bearer";
    private final String refreshToken;
    private final Long id;
    private final String username;
    private final String email;
    private final List<String> roles;
}
