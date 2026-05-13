package com.nullsaf.nullawake.api.auth.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class RefreshTokenEncoder {

    public String encode(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Refresh Token 해싱에 실패했습니다.", e);
        }
    }

    public boolean matches(String rawRefreshToken, String encodedRefreshToken) {
        return encode(rawRefreshToken).equals(encodedRefreshToken);
    }
}
