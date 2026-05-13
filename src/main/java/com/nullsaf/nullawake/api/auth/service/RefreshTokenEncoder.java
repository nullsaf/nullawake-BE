package com.nullsaf.nullawake.api.auth.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Refresh Token 해싱 및 검증을 담당하는 클래스.
 *
 * Refresh Token 원문 저장을 방지하기 위해
 * SHA-256 기반 해시 값을 저장한다.
 */
@Component
public class RefreshTokenEncoder {

    /**
     * Refresh Token을 SHA-256 기반 해시 값으로 변환한다.
     *
     * @param refreshToken 원본 Refresh Token
     * @return 해시된 Refresh Token
     */
    public String encode(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Refresh Token 해싱에 실패했습니다.", e);
        }
    }

    /**
     * Refresh Token과 저장된 해시 값을 비교한다.
     *
     * @param rawRefreshToken 원본 Refresh Token
     * @param encodedRefreshToken 저장된 해시 값
     * @return 일치 여부
     */
    public boolean matches(String rawRefreshToken, String encodedRefreshToken) {
        return encode(rawRefreshToken).equals(encodedRefreshToken);
    }
}
