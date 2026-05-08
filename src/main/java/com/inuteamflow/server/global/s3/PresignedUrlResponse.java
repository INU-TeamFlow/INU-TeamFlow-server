package com.inuteamflow.server.global.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PresignedUrlResponse {

    @NotBlank
    private String uploadUrl;

    @NotBlank
    private String imageKey;

    public static PresignedUrlResponse create(
            String uploadUrl,
            String imageKey
    ) {
        return new PresignedUrlResponse(
                uploadUrl,
                imageKey
        );
    }

}
