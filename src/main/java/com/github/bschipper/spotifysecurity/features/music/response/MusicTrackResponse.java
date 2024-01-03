package com.github.bschipper.spotifysecurity.features.music.response;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class MusicTrackResponse {
    private Long id;
    private String title;
    private float duration;
    private String artistName;
    private Long artistId;
    private boolean likeStatus;
}
