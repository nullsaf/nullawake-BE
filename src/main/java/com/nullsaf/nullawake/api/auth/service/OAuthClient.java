package com.nullsaf.nullawake.api.auth.service;

import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;

/**
 * OAuth 제공자별 인증 로직을 추상화한 인터페이스.
 *
 * Kakao, Google 등 OAuth 제공자별 구현체에서 사용된다.
 */
public interface OAuthClient {

    /**
     * OAuth 제공자 정보를 반환한다.
     *
     * @return OAuth 제공자
     */
    OAuthProvider getProvider();

    /**
     * 인가 코드를 기반으로 사용자 정보를 조회한다.
     *
     * @param code OAuth 인가 코드
     * @return 소셜 사용자 정보
     */
    SocialUserInfo getUserInfo(String code);
}
