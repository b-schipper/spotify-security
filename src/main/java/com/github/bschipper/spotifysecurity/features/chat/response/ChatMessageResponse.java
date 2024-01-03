package com.github.bschipper.spotifysecurity.features.chat.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String content;
    private Long senderId;
    private String senderUsername;
    private Long chatId;
    private String createdAt;
    private boolean likeBadge;
}
