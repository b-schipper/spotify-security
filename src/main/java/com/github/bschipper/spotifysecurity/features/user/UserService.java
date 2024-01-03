package com.github.bschipper.spotifysecurity.features.user;

import com.github.bschipper.spotifysecurity.exception.custom.ResourceNotFoundException;
import com.github.bschipper.spotifysecurity.features.music.MusicTrack;
import com.github.bschipper.spotifysecurity.features.music.MusicTrackRepository;
import com.github.bschipper.spotifysecurity.features.user.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MusicTrackRepository musicTrackRepository;

    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Decide if the user gets a like badge
        AtomicBoolean likeBadge = new AtomicBoolean(false);
        musicTrackRepository.findMusicTracksByUserLikesContaining(user).ifPresent(musicTracks -> {
            if (musicTracks.size() >= 3)
                likeBadge.set(true);
        });

        return ProfileResponse.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .likeBadge(likeBadge.get())
                .build();
    }
}
