package com.github.bschipper.spotifysecurity.payload.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class MessageResponse {
    private final String message;
}
