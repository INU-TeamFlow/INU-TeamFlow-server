package com.inuteamflow.server.global.exception.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();

    Integer getCode();

    String getMessage();
}
