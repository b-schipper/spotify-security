package com.github.bschipper.spotifysecurity.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {

    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date timestamp;
    private String message;
    private String description;
    private Optional<List<String>> errors;
}
