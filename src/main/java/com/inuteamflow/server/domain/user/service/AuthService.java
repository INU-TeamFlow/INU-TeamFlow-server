package com.inuteamflow.server.domain.user.service;

import com.inuteamflow.server.domain.user.dto.request.LoginRequest;
import com.inuteamflow.server.domain.user.dto.request.SignupRequest;
import com.inuteamflow.server.domain.user.dto.request.VerifySchoolRequest;
import com.inuteamflow.server.domain.user.dto.response.MyInfoResponse;
import com.inuteamflow.server.domain.user.entity.User;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.user.repository.UserRepository;
import com.inuteamflow.server.global.exception.error.RestApiException;
import com.inuteamflow.server.global.exception.error.CustomErrorCode;
import com.inuteamflow.server.global.jwt.JwtTokenProvider;
import com.inuteamflow.server.global.jwt.TokenResponse;
import com.inuteamflow.server.global.jwt.refresh.RefreshToken;
import com.inuteamflow.server.global.jwt.refresh.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MyInfoResponse signUp(
            SignupRequest request
    ) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RestApiException(CustomErrorCode.USER_USERNAME_CONFLICT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RestApiException(CustomErrorCode.USER_EMAIL_CONFLICT);
        }

        User user = User.create(request, bCryptPasswordEncoder.encode(request.getPassword()));

        // TODO: CloudFront 생성 후, 이미지 조회용 URL 생성
        String imageUrl = "";

        return MyInfoResponse.create(userRepository.save(user), imageUrl);
    }

    @Transactional
    public TokenResponse login(
            LoginRequest request
    ) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    public TokenResponse reissue(
            String refreshToken
    ) {
        RefreshToken savedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.JWT_REFRESH_NOT_FOUND));

        if (!savedRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new RestApiException(CustomErrorCode.JWT_REFRESH_NOT_MATCH);
        }

        User user = userRepository.findById(savedRefreshToken.getUserId())
                .orElseThrow(() -> new RestApiException(CustomErrorCode.USER_NOT_FOUND));
        return jwtTokenProvider.generateTokenByUsername(user.getEmail());
    }

    public MyInfoResponse verifySchool(
            UserDetailsImpl userDetails,
            VerifySchoolRequest request
    ) {
        User user = userDetails.getUser();

        // TODO: 학교 서버에 접근할 수 있게 설정 후, 학생 인증 로직 구현

        // TODO: CloudFront 생성 후, 이미지 조회용 URL 생성
        String imageUrl = "";

        return MyInfoResponse.create(user, imageUrl);
    }
}
