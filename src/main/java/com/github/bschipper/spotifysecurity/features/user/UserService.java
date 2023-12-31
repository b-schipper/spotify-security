package com.github.bschipper.spotifysecurity.features.user;

import com.github.bschipper.spotifysecurity.features.musictrack.MusicTrack;
import com.github.bschipper.spotifysecurity.features.musictrack.MusicTrackRepository;
import com.github.bschipper.spotifysecurity.features.user.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MusicTrackRepository musicTrackRepository;

    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId).get();

        ProfileResponse response = new ProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        // Decides if the user gets a like badge
        Optional<List<MusicTrack>> likedMusicTracksOpt = musicTrackRepository.findMusicTracksByUserLikesContaining(user);
        response.setLikeBadge(false);
        if (likedMusicTracksOpt.isPresent() && likedMusicTracksOpt.get().size() >= 3) {
            response.setLikeBadge(true);
        }
        return response;
    }
}
