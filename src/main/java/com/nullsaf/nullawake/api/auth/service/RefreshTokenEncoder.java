package com.nullsaf.nullawake.api.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String refreshToken) {
        return encoder.encode(refreshToken);
    }

    public boolean matches(String rawRefreshToken, String encodedRefreshToken) {
        return encoder.matches(rawRefreshToken, encodedRefreshToken);
    }
}
