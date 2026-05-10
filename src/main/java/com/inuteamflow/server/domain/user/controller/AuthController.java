package com.inuteamflow.server.domain.user.controller;

import com.inuteamflow.server.domain.user.dto.request.LoginRequest;
import com.inuteamflow.server.domain.user.dto.request.SignupRequest;
import com.inuteamflow.server.domain.user.dto.request.VerifySchoolRequest;
import com.inuteamflow.server.domain.user.dto.response.MyInfoResponse;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.user.service.AuthService;
import com.inuteamflow.server.global.jwt.TokenResponse;
import com.inuteamflow.server.global.jwt.JwtTokenProvider;
import com.inuteamflow.server.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        addRefreshTokenCookie(response, tokenResponse.getRefreshToken());
        return ApiResponse.ok(tokenResponse);
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(
            @CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.reissue(refreshToken);
        addRefreshTokenCookie(response, tokenResponse.getRefreshToken());
        return ApiResponse.ok(tokenResponse);
    }

    @PostMapping("/signup")
    public ApiResponse<MyInfoResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return ApiResponse.ok(authService.signUp(request));
    }

    @PostMapping("/verify-school")
    public ApiResponse<MyInfoResponse> verifySchool(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody VerifySchoolRequest request
    ) {
        return ApiResponse.ok(authService.verifySchool(userDetails, request));
    }

    private void addRefreshTokenCookie(
            HttpServletResponse response,
            String refreshToken
    ) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(JwtTokenProvider.REFRESH_TOKEN_VALID_TIME))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
