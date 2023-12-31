package com.github.bschipper.spotifysecurity.features.musictrack;

import com.github.bschipper.spotifysecurity.features.musictrack.request.MusicTrackRequest;
import com.github.bschipper.spotifysecurity.features.musictrack.response.MusicTrackResponse;
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
        Optional<MusicTrack> optMusicTrack = musicTrackRepository.findById(trackId);
        return optMusicTrack
                .map(track -> convertMusicTrackToResponse(track, userId))
                .orElseGet(MusicTrackResponse::new);
    }

    public MusicTrackResponse likeMusicTrack(Long userId, Long trackId) {
        Optional<MusicTrack> trackOpt = musicTrackRepository.findById(trackId);
        if (trackOpt.isPresent()) {
            MusicTrack track = trackOpt.get();
            Set<User> userLikes = track.getUserLikes();
            User user = userRepository.findById(userId).get();
            if (!userLikes.contains(user)) {
                userLikes.add(user);
                track.setUserLikes(userLikes);
                return convertMusicTrackToResponse(musicTrackRepository.save(track), userId);
            }
            userLikes.remove(user);
            track.setUserLikes(userLikes);
            musicTrackRepository.save(track);
        }
        return new MusicTrackResponse();
    }

    public List<MusicTrackResponse> getMusicTracksLikedByUser(Long userId) {
        User user = userRepository.findById(userId).get();

        List<MusicTrackResponse> response = new ArrayList<>();
        Optional<List<MusicTrack>> likedMusicTracksOpt = musicTrackRepository.findMusicTracksByUserLikesContaining(user);
        likedMusicTracksOpt.ifPresent(musicTracks -> musicTracks.forEach(track ->
                response.add(convertMusicTrackToResponse(track, userId))));
        return response;
    }

    public MusicTrackResponse uploadNewMusicTrack(Long artistId, MusicTrackRequest request) {
        return convertMusicTrackToResponse(musicTrackRepository
                .save(convertRequestToMusicTrack(request, artistId)), artistId);
    }

    public List<MusicTrackResponse> getAllArtistMusicTracks(Long artistId) {
        List<MusicTrackResponse> response = new ArrayList<>();
        Optional<List<MusicTrack>> artistMusicCollection = musicTrackRepository.findMusicTracksByArtist_Id(artistId);
        artistMusicCollection.ifPresent(musicTracks -> musicTracks.forEach(track ->
                response.add(convertMusicTrackToResponse(track, artistId))));
        return response;
    }

    public MusicTrackResponse editExistingMusicTrack(Long artistId, MusicTrackRequest request) {
        return convertMusicTrackToResponse(musicTrackRepository
                .save(convertRequestToMusicTrack(request, artistId)), artistId);
    }

    @Transactional
    public void deleteExistingMusicTrack(Long artistId, Long trackId) {
        User artist = userRepository.findById(artistId).get();

        musicTrackRepository.deleteMusicTrackByIdAndArtist(trackId, artist);
    }

    private MusicTrack convertRequestToMusicTrack(MusicTrackRequest request, Long artistId) {
        User artist = userRepository.findById(artistId).get();

        MusicTrack track = new MusicTrack();
        track.setId(request.getId());
        track.setTitle(request.getTitle());
        track.setDuration(request.getDuration());
        track.setArtist(artist);
        return track;
    }

    private MusicTrackResponse convertMusicTrackToResponse(MusicTrack track, Long userId) {
        User user = userRepository.findById(userId).get();
        boolean liked = musicTrackRepository.existsMusicTrackByIdAndUserLikesContaining(track.getId(), user);

        MusicTrackResponse response = new MusicTrackResponse();
        response.setId(track.getId());
        response.setTitle(track.getTitle());
        response.setDuration(track.getDuration());
        response.setArtistName(track.getArtist().getUsername());
        response.setArtistId(track.getArtist().getId());
        response.setLikeStatus(liked);
        return response;
    }
}
