package com.github.bschipper.spotifysecurity.features.chat.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
    private Long chatId;
    private Long senderId;
    private String content;
}
