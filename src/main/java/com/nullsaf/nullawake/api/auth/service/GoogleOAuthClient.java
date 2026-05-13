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
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient {

    private final WebClient webClient;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
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
        GoogleTokenResponse response = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block(Duration.ofSeconds(5));

        if (response == null || response.accessToken == null) {
            throw new CustomException(ErrorCode.INVALID_SOCIAL_CODE);
        }

        return response.accessToken;
    }

    private SocialUserInfo getUserInfoByAccessToken(String accessToken) {
        GoogleUserResponse response = webClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserResponse.class)
                .block(Duration.ofSeconds(5));

        if (response == null || response.sub == null) {
            throw new CustomException(ErrorCode.OAUTH_USER_INFO_FAILED);
        }

        return new SocialUserInfo(
                response.sub,
                response.name,
                response.email,
                response.picture
        );
    }

    @Getter
    private static class GoogleTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Getter
    private static class GoogleUserResponse {
        private String sub;
        private String name;
        private String email;
        private String picture;
    }
}
