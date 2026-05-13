package com.nullsaf.nullawake.api.auth.controller;

import com.nullsaf.nullawake.api.auth.dto.*;
import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.auth.service.AuthService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인", description= "카카오 계정으로 소셜로그인을 합니다.")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> kakaoLogin(
            @Valid @RequestBody SocialLoginRequest request
    ) {
        AuthLoginResponse response = authService.kakaoLogin(request.getCode());

        return ResponseEntity.ok(
                ApiResponse.success("카카오 로그인에 성공했습니다.", response)
        );
    }

    @PostMapping("/google/login")
    @Operation(summary = "구글 로그인", description= "구글 계정으로 소셜로그인을 합니다.")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> googleLogin(
            @Valid @RequestBody SocialLoginRequest request
    ) {
        AuthLoginResponse response = authService.googleLogin(request.getCode());

        return ResponseEntity.ok(
                ApiResponse.success("구글 로그인에 성공했습니다.", response)
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰 재발급", description= "리프레시 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenRefreshResponse response = authService.refresh(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success("토큰 재발급에 성공했습니다.", response)
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description= "사용자가 로그아웃을 합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.logout(userDetails.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success("로그아웃에 성공했습니다.", null)
        );
    }
}
