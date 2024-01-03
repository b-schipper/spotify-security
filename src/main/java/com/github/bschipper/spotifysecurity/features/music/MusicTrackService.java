package com.github.bschipper.spotifysecurity.features.music;

import com.github.bschipper.spotifysecurity.exception.custom.ResourceNotFoundException;
import com.github.bschipper.spotifysecurity.features.music.request.MusicTrackRequest;
import com.github.bschipper.spotifysecurity.features.music.response.MusicTrackResponse;
import com.github.bschipper.spotifysecurity.features.user.User;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class MusicTrackService {
    private final MusicTrackRepository musicTrackRepository;
    private final UserRepository userRepository;

    public List<MusicTrackResponse> getAllExistingMusicTracks(Long userId) {
        List<MusicTrackResponse> response = new ArrayList<>();
        musicTrackRepository.findAll().forEach(track -> {
            response.add(convertMusicTrackToResponse(track, userId));
        });
        return response;
    }

    public MusicTrackResponse getRequestedMusicTrack(Long userId, Long trackId) {
        return musicTrackRepository.findById(trackId)
                .map(track -> convertMusicTrackToResponse(track, userId))
                .orElseThrow(() -> new ResourceNotFoundException("Music track not found"));
    }

    public MusicTrackResponse likeMusicTrack(Long userId, Long trackId) {
        MusicTrack track = musicTrackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Music track not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<User> userLikes = track.getUserLikes();
        if (!userLikes.contains(user)) {
            userLikes.add(user);
            track.setUserLikes(userLikes);
            return convertMusicTrackToResponse(musicTrackRepository.save(track), userId);
        }
        userLikes.remove(user);
        track.setUserLikes(userLikes);
        return convertMusicTrackToResponse(musicTrackRepository.save(track), userId);
    }

    public List<MusicTrackResponse> getMusicTracksLikedByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<MusicTrackResponse> response = new ArrayList<>();
        musicTrackRepository.findMusicTracksByUserLikesContaining(user).ifPresent(musicTracks -> musicTracks
                .forEach(track -> response.add(convertMusicTrackToResponse(track, userId))));
        return response;
    }

    public MusicTrackResponse uploadNewMusicTrack(Long artistId, MusicTrackRequest request) {
        return convertMusicTrackToResponse(musicTrackRepository
                .save(convertRequestToMusicTrack(request, artistId)), artistId);
    }

    public List<MusicTrackResponse> getAllArtistMusicTracks(Long artistId) {
        List<MusicTrackResponse> response = new ArrayList<>();
        musicTrackRepository.findMusicTracksByArtist_Id(artistId).ifPresent(musicTracks -> musicTracks
                .forEach(track -> response.add(convertMusicTrackToResponse(track, artistId))));
        return response;
    }

    public MusicTrackResponse editExistingMusicTrack(Long artistId, MusicTrackRequest request) {
        return convertMusicTrackToResponse(musicTrackRepository
                .save(convertRequestToMusicTrack(request, artistId)), artistId);
    }

    @Transactional
    public void deleteExistingMusicTrack(Long artistId, Long trackId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        musicTrackRepository.deleteMusicTrackByIdAndArtist(trackId, artist);
    }

    private MusicTrack convertRequestToMusicTrack(MusicTrackRequest request, Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        return MusicTrack.builder()
                .id(request.getId())
                .title(request.getTitle())
                .duration(request.getDuration())
                .artist(artist)
                .build();
    }

    private MusicTrackResponse convertMusicTrackToResponse(MusicTrack track, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean liked = musicTrackRepository.existsMusicTrackByIdAndUserLikesContaining(track.getId(), user);
        return MusicTrackResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .duration(track.getDuration())
                .artistId(track.getArtist().getId())
                .artistName(track.getArtist().getUsername())
                .likeStatus(liked)
                .build();
    }
}
