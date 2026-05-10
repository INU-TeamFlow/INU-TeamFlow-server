package com.inuteamflow.server.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode implements ErrorCode {

    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 401, ""),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, ""),
    USER_BANNED(HttpStatus.FORBIDDEN, 403, ""),
    USER_USERNAME_CONFLICT(HttpStatus.CONFLICT, 409, ""),
    USER_EMAIL_CONFLICT(HttpStatus.CONFLICT, 409, ""),

    // JWT 인증 에러
    JWT_INVALID(HttpStatus.UNAUTHORIZED, 401, ""),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, 401, ""),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, 401, ""),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, 401, ""),
    JWT_REFRESH_NOT_MATCH(HttpStatus.BAD_REQUEST, 400, ""),
    JWT_REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, 404, ""),

    // 요청 관련 에러
    COMMON_INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, ""),
    COMMON_HANDLER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
