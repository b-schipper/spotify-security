package com.github.bschipper.spotifysecurity.features.musictrack.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class MusicTrackResponse {
    private Long id;
    private String title;
    private float duration;
    private String artistName;
    private Long artistId;
    private boolean likeStatus;
}
