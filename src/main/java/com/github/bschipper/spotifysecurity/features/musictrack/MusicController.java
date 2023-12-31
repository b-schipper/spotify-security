package com.github.bschipper.spotifysecurity.features.musictrack;

import com.github.bschipper.spotifysecurity.features.musictrack.response.MusicTrackResponse;
import com.github.bschipper.spotifysecurity.security.CurrentUser;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/music")
@PreAuthorize("hasRole('ROLE_USER')")
public class MusicController {
    private final MusicTrackService musicTrackService;

    @GetMapping
    public ResponseEntity<List<MusicTrackResponse>> getAllExistingMusicTracks(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(musicTrackService.getAllExistingMusicTracks(userPrincipal.getId()));
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<MusicTrackResponse> getRequestedMusicTrack(@CurrentUser UserPrincipal userPrincipal,
                                                                     @PathVariable Long trackId) {
        return ResponseEntity.ok(musicTrackService.getRequestedMusicTrack(userPrincipal.getId(), trackId));
    }

    @PostMapping("/{trackId}/like")
    public ResponseEntity<MusicTrackResponse> likeMusicTrack(@CurrentUser UserPrincipal userPrincipal,
                                                             @PathVariable Long trackId) {
        return ResponseEntity.ok(musicTrackService.likeMusicTrack(userPrincipal.getId(), trackId));
    }

    @GetMapping ("/liked")
    public ResponseEntity<List<MusicTrackResponse>> getMusicTracksLikedByUser(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(musicTrackService.getMusicTracksLikedByUser(userPrincipal.getId()));
    }
}
