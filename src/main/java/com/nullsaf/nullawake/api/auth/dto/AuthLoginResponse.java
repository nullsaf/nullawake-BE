package com.nullsaf.nullawake.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String nickname;
    private String email;
}
