package com.github.bschipper.spotifysecurity.features.portal.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricsResponse {
    private int userAmount;
    private int artistAmount;
    private int adminAmount;
    private long musicAmount;
}
