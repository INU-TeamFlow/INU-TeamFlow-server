package com.inuteamflow.server.global.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresignedUrlRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private MediaType mediaType;

}
