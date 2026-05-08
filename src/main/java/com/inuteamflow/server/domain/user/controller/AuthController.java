package com.inuteamflow.server.domain.user.controller;

import com.inuteamflow.server.domain.user.dto.request.LoginRequest;
import com.inuteamflow.server.domain.user.dto.request.SignupRequest;
import com.inuteamflow.server.domain.user.dto.request.VerifySchoolRequest;
import com.inuteamflow.server.domain.user.dto.response.MyInfoResponse;
import com.inuteamflow.server.domain.user.entity.UserDetailsImpl;
import com.inuteamflow.server.domain.user.service.AuthService;
import com.inuteamflow.server.global.jwt.TokenResponse;
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
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<MyInfoResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/verify-school")
    public ResponseEntity<MyInfoResponse> verifySchool(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody VerifySchoolRequest request
    ) {
        return ResponseEntity.ok(authService.verifySchool(userDetails, request));
    }
}
