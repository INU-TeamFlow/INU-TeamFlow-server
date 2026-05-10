package com.inuteamflow.server.global.s3;

import com.inuteamflow.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/users/me/profile/presigned-url")
    public ApiResponse<PresignedUrlResponse> getProfilePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        return ApiResponse.ok(s3Service.getProfilePresignedUrl(request));
    }

}
