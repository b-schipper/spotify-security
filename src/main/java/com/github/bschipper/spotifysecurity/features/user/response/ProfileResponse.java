package com.github.bschipper.spotifysecurity.features.user.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private String username;
    private String email;
    private boolean likeBadge;
}
