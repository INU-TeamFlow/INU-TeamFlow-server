package com.inuteamflow.server.domain.user.service;

import com.inuteamflow.server.domain.user.dto.request.UserUpdateRequest;
import com.inuteamflow.server.domain.user.dto.response.MyInfoResponse;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MyInfoResponse getMyInfo(
            UserDetailsImpl userDetails
    ) {
        // TODO: CloudFront 생성 후, 이미지 조회용 URL 생성
        String imageUrl = "";
        return MyInfoResponse.create(userDetails.getUser(), imageUrl);
    }

    @Transactional
    public MyInfoResponse updateMyInfo(
            UserDetailsImpl userDetails,
            UserUpdateRequest request
    ) {
        User user = userDetails.getUser();
        String encodedPassword = StringUtils.hasText(request.getPassword())
                ? bCryptPasswordEncoder.encode(request.getPassword())
                : null;

        user.update(request, encodedPassword);

        // TODO: CloudFront 생성 후, 이미지 조회용 URL 생성
        String imageUrl = "";
        return MyInfoResponse.create(user, imageUrl);
    }
}
