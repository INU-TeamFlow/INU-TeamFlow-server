package com.inuteamflow.server.domain.user.dto.request;

import com.inuteamflow.server.domain.user.enums.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequest {

    private String password;

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotNull
    private Department department;

    private String imageKey;

}
