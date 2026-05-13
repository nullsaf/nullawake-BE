package com.nullsaf.nullawake.api.auth.service;

import com.nullsaf.nullawake.api.auth.dto.AuthLoginResponse;
import com.nullsaf.nullawake.api.auth.dto.TokenRefreshResponse;
import com.nullsaf.nullawake.api.auth.entity.OAuthAccount;
import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.api.auth.repository.OAuthAccountRepository;
import com.nullsaf.nullawake.api.user.repository.UserRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth 로그인 및 JWT 인증 관련 비즈니스 로직을 처리하는 서비스.
 *
 * 소셜 로그인, 회원가입, 토큰 재발급, 로그아웃 기능을 제공한다.
 */

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

    /**
     * 카카오 소셜 로그인을 수행
     * @param code 인가 코드를 포함한 로그인 요청 수행
     * @return 로그인 결과 및 JWT 정보
     */
    @Transactional
    public AuthLoginResponse kakaoLogin(String code) {
        return socialLogin(OAuthProvider.KAKAO, code);
    }

    /**
     * 구글 소셜 로그인을 수행한다.
     *
     * @param code OAuth 인가 코드
     * @return 로그인 결과 및 JWT 정보
     */
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

    /**
     * OAuth 로그인 또는 회원가입을 수행한다.
     *
     * 기존 회원이면 로그인 처리,
     * 존재하지 않으면 회원가입 후 로그인 처리한다.
     *
     * @param provider OAuth 제공자
     * @param userInfo 소셜 사용자 정보
     * @return 로그인 결과 및 JWT 정보
     */
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

    /**
     * 신규 사용자 및 OAuth 계정을 생성한다.
     *
     * @param provider OAuth 제공자
     * @param userInfo 소셜 사용자 정보
     * @return 생성된 OAuth 계정 정보
     */
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

    /**
     * Refresh Token을 검증하고 새로운 JWT를 재발급한다.
     *
     * @param refreshToken Refresh Token
     * @return 새롭게 발급된 JWT 정보
     */
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

    /**
     * 로그아웃을 수행하고 저장된 Refresh Token을 제거한다.
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(Long userId) {
        OAuthAccount oauthAccount = oauthAccountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        oauthAccount.updateRefreshTokenHash(null);
    }
}
