package com.nullsaf.nullawake.api.auth.service;

public record SocialUserInfo(
        String providerUserId,
        String nickname,
        String email,
        String profileImage
) {
}
