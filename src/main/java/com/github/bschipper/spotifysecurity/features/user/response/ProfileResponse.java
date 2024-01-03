package com.github.bschipper.spotifysecurity.features.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private boolean likeBadge;
}
