package com.nullsaf.nullawake.api.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient implements OAuthClient {

    private final WebClient webClient;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public SocialUserInfo getUserInfo(String code) {
        if (code == null || code.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SOCIAL_CODE);
        }

        String accessToken = getAccessToken(code);
        return getUserInfoByAccessToken(accessToken);
    }

    private String getAccessToken(String code) {
        try {
            KakaoTokenResponse response = webClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", clientId)
                            .with("redirect_uri", redirectUri)
                            .with("code", code))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .block();

            if (response == null || response.getAccessToken() == null) {
                throw new CustomException(ErrorCode.INVALID_SOCIAL_CODE);
            }

            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            System.out.println("Kakao OAuth Error: " + e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.INVALID_SOCIAL_CODE);
        }
    }

    private SocialUserInfo getUserInfoByAccessToken(String accessToken) {
        KakaoUserResponse response = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();

        if (response == null || response.id == null) {
            throw new CustomException(ErrorCode.OAUTH_USER_INFO_FAILED);
        }

        KakaoAccount kakaoAccount = response.kakaoAccount;
        KakaoProfile profile = kakaoAccount == null ? null : kakaoAccount.profile;

        String nickname = profile == null || profile.nickname == null
                ? "카카오사용자"
                : profile.nickname;

        return new SocialUserInfo(
                response.id.toString(),
                nickname,
                kakaoAccount == null ? null : kakaoAccount.email,
                profile == null ? null : profile.profileImageUrl
        );
    }

    @Getter
    private static class KakaoTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Getter
    private static class KakaoUserResponse {
        private Long id;

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;
    }

    @Getter
    private static class KakaoAccount {
        private String email;
        private KakaoProfile profile;
    }

    @Getter
    private static class KakaoProfile {
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}
