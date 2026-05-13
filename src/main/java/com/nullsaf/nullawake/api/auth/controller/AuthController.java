package com.nullsaf.nullawake.api.auth.controller;

import com.nullsaf.nullawake.api.auth.dto.*;
import com.nullsaf.nullawake.api.auth.service.AuthService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 소셜 로그인을 수행
     * @param request 인가 코드를 포함한 로그인 요청 수행
     * @return JWT 토큰 및 사용자 정보
     */
    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 ", description= "카카오 계정으로 소셜로그인을 합니다.")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> kakaoLogin(
            @Valid @RequestBody SocialLoginRequest request
    ) {
        AuthLoginResponse response = authService.kakaoLogin(request.getCode());

        return ResponseEntity.ok(
                ApiResponse.success("카카오 로그인에 성공했습니다.", response)
        );
    }

    /**
     * 구글 소셜 로그인을 수행
     * @param request 인가 코드를 포함한 로그인 요청 수행
     * @return JWT 토큰 및 사용자 정보
     */
    @PostMapping("/google/login")
    @Operation(summary = "구글 로그인 ", description= "구글 계정으로 소셜로그인을 합니다.")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> googleLogin(
            @Valid @RequestBody SocialLoginRequest request
    ) {
        AuthLoginResponse response = authService.googleLogin(request.getCode());

        return ResponseEntity.ok(
                ApiResponse.success("구글 로그인에 성공했습니다.", response)
        );
    }

    /**
     * 리프레시 토큰을 검증하여 새로운 Access Token과 Refresh Token을 발급
     * @param request 리프레시 토큰 재발급 요청 정보
     * @return 새롭게 발급된 JWT 토큰 정보
     */
    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰 재발급 ", description= "리프레시 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenRefreshResponse response = authService.refresh(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success("토큰 재발급에 성공했습니다.", response)
        );
    }


    /**
     *
     * @param authorizationHeader
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description= "사용자가 로그아웃을 합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        authService.logout(authorizationHeader);

        return ResponseEntity.ok(
                ApiResponse.success("로그아웃에 성공했습니다.", null)
        );
    }
}
