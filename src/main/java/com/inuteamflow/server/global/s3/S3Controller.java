package com.inuteamflow.server.global.s3;

import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<PresignedUrlResponse> getUserProfilePresignedUrl(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        return ResponseEntity.ok(s3Service.getUserProfilePresignedUrl(userDetails, request));
    }

}
