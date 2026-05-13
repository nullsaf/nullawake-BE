package com.nullsaf.nullawake.api.auth.service;

import com.nullsaf.nullawake.api.auth.dto.AuthLoginResponse;
import com.nullsaf.nullawake.api.auth.dto.TokenRefreshResponse;
import com.nullsaf.nullawake.api.auth.entity.OAuthAccount;
import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;
import com.nullsaf.nullawake.api.user.dto.Users;
import com.nullsaf.nullawake.api.auth.repository.OAuthAccountRepository;
import com.nullsaf.nullawake.api.user.repository.UserRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AuthService {


    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenEncoder refreshTokenEncoder;
    private final Map<OAuthProvider, OAuthClient> oauthClients;

    public AuthService(
            UserRepository userRepository,
            OAuthAccountRepository oauthAccountRepository,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenEncoder refreshTokenEncoder,
            List<OAuthClient> oauthClientList
    ) {
        this.userRepository = userRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenEncoder = refreshTokenEncoder;
        this.oauthClients = new EnumMap<>(OAuthProvider.class);

        for (OAuthClient oauthClient : oauthClientList) {
            this.oauthClients.put(oauthClient.getProvider(), oauthClient);
        }
    }

    @Transactional
    public AuthLoginResponse kakaoLogin(String code) {
        return socialLogin(OAuthProvider.KAKAO, code);
    }

    @Transactional
    public AuthLoginResponse googleLogin(String code) {
        return socialLogin(OAuthProvider.GOOGLE, code);
    }

    private AuthLoginResponse socialLogin(OAuthProvider provider, String code) {
        OAuthClient oauthClient = oauthClients.get(provider);

        if (oauthClient == null) {
            throw new CustomException(ErrorCode.OAUTH_USER_INFO_FAILED);
        }

        SocialUserInfo userInfo = oauthClient.getUserInfo(code);
        return loginOrSignUp(provider, userInfo);
    }

    private AuthLoginResponse loginOrSignUp(
            OAuthProvider provider,
            SocialUserInfo userInfo
    ) {
        OAuthAccount oauthAccount = oauthAccountRepository
                .findByProviderAndProviderUserId(provider, userInfo.providerUserId())
                .orElseGet(() -> createUserAndOAuthAccount(provider, userInfo));

        Users user = oauthAccount.getUser();

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        String refreshTokenHash = refreshTokenEncoder.encode(refreshToken);

        oauthAccount.updateRefreshTokenHash(refreshTokenHash);

        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    private OAuthAccount createUserAndOAuthAccount(
            OAuthProvider provider,
            SocialUserInfo userInfo
    ) {
        Users user = Users.create(
                userInfo.nickname(),
                userInfo.email(),
                userInfo.profileImage()
        );

        Users savedUser = userRepository.save(user);

        String refreshToken = jwtTokenProvider.createRefreshToken(savedUser.getUserId());
        String refreshTokenHash = refreshTokenEncoder.encode(refreshToken);

        OAuthAccount oauthAccount = OAuthAccount.create(
                savedUser,
                provider,
                userInfo.providerUserId(),
                refreshTokenHash
        );

        return oauthAccountRepository.save(oauthAccount);
    }

    @Transactional
    public TokenRefreshResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        OAuthAccount oauthAccount = oauthAccountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!refreshTokenEncoder.matches(refreshToken, oauthAccount.getRefreshTokenHash())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
        String newRefreshTokenHash = refreshTokenEncoder.encode(newRefreshToken);

        oauthAccount.updateRefreshTokenHash(newRefreshTokenHash);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(token);

        OAuthAccount oauthAccount = oauthAccountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        oauthAccount.updateRefreshTokenHash(null);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return authorizationHeader.substring(7);
    }
}
