package com.inuteamflow.server.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                true,
                200,
                "요청이 성공했습니다.",
                data
        );
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(
                true,
                200,
                "요청이 성공했습니다.",
                null
        );
    }
}
