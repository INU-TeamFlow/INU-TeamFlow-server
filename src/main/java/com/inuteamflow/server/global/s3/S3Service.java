package com.inuteamflow.server.global.s3;

import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {

    private final UserRepository userRepository;

    public PresignedUrlResponse getUserProfilePresignedUrl(
            UserDetailsImpl userDetails,
            PresignedUrlRequest request
    ) {
        // TODO: Config 작업 이후에 PresignedUrl을 발급하는 로직
        String uploadUrl = "";
        String imageKey = "";

        return PresignedUrlResponse.create(uploadUrl, imageKey);
    }
}
