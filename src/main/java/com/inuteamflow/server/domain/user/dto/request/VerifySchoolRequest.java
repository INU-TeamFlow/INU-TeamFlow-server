package com.inuteamflow.server.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifySchoolRequest {

    @NotBlank
    private String studentNumber;

    @NotBlank
    private String portalPassword;

}
