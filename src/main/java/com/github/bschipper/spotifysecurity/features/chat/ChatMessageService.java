package com.github.bschipper.spotifysecurity.features.chat;

import com.github.bschipper.spotifysecurity.exception.custom.ResourceNotFoundException;
import com.github.bschipper.spotifysecurity.features.chat.request.ChatMessageRequest;
import com.github.bschipper.spotifysecurity.features.chat.response.ChatMessageResponse;
import com.github.bschipper.spotifysecurity.features.music.MusicTrackRepository;
import com.github.bschipper.spotifysecurity.features.user.User;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MusicTrackRepository musicTrackRepository;

    public ChatMessageResponse saveMessage(ChatMessageRequest messageRequest) {
        User sender = userRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .content(messageRequest.getContent())
                .chatId(messageRequest.getChatId())
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(message);

        return convertChatMessageToResponse(message);
    }

    private ChatMessageResponse convertChatMessageToResponse(ChatMessage message) {
        // Decide if the user gets a like badge
        AtomicBoolean likeBadge = new AtomicBoolean(false);
        musicTrackRepository.findMusicTracksByUserLikesContaining(message.getSender()).ifPresent(musicTracks -> {
            if (musicTracks.size() >= 3)
                likeBadge.set(true);
        });

        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .chatId(message.getChatId())
                .createdAt(message.getCreatedAt().toString())
                .likeBadge(likeBadge.get())
                .build();
    }
}
