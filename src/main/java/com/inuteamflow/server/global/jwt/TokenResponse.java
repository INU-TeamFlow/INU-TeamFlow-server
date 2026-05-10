package com.inuteamflow.server.global.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String grantType;
    private String accessToken;

    @JsonIgnore
    private String refreshToken;
}
