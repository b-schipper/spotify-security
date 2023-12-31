package com.github.bschipper.spotifysecurity.features.musictrack;

import com.github.bschipper.spotifysecurity.features.musictrack.request.MusicTrackRequest;
import com.github.bschipper.spotifysecurity.features.musictrack.response.MusicTrackResponse;
import com.github.bschipper.spotifysecurity.security.CurrentUser;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/artist/music")
@PreAuthorize("hasRole('ROLE_ARTIST')")
public class ArtistMusicController {
    private final MusicTrackService musicTrackService;

    @PostMapping
    public ResponseEntity<MusicTrackResponse> uploadNewMusicTrack(@CurrentUser UserPrincipal userPrincipal,
                                                                  @Valid @RequestBody MusicTrackRequest request) {
        return ResponseEntity.ok(musicTrackService.uploadNewMusicTrack(userPrincipal.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<MusicTrackResponse>> getAllArtistMusicTracks(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(musicTrackService.getAllArtistMusicTracks(userPrincipal.getId()));
    }

    @PutMapping
    public ResponseEntity<MusicTrackResponse> editExistingMusicTrack(@CurrentUser UserPrincipal userPrincipal,
                                                                     @Valid @RequestBody MusicTrackRequest request) {
        return ResponseEntity.ok(musicTrackService.editExistingMusicTrack(userPrincipal.getId(), request));
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Boolean> deleteExistingMusicTrack(@CurrentUser UserPrincipal userPrincipal,
                                                            @PathVariable Long trackId) {
        musicTrackService.deleteExistingMusicTrack(userPrincipal.getId(), trackId);
        return ResponseEntity.ok(true);
    }
}
