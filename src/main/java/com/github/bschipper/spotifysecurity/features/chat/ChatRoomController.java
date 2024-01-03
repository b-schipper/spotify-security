package com.github.bschipper.spotifysecurity.features.chat;

import com.github.bschipper.spotifysecurity.features.chat.request.ChatMessageRequest;
import com.github.bschipper.spotifysecurity.features.chat.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessageRequest messageRequest) {
        ChatMessageResponse messageResponse = chatMessageService.saveMessage(messageRequest);

        messagingTemplate.convertAndSend("/queue/" + messageRequest.getChatId(), messageResponse);
    }
}
