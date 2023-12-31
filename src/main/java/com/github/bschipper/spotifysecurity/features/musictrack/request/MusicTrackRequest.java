package com.github.bschipper.spotifysecurity.features.musictrack.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicTrackRequest {
    private Long id;
    private String title;
    private float duration;

}
