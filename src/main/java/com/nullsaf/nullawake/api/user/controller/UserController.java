package com.nullsaf.nullawake.api.user.controller;

import com.nullsaf.nullawake.api.user.dto.UserInfoResponse;
import com.nullsaf.nullawake.api.user.dto.UserUpdateRequest;
import com.nullsaf.nullawake.api.user.service.UserService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 정보를 조회합니다")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserInfoResponse response = userService.getMyInfo(authorizationHeader);

        return ResponseEntity.ok(
                ApiResponse.success("유저 정보 조회에 성공했습니다.", response)
        );
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 유저의 정보를 수정합니다")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateMyInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserInfoResponse response = userService.updateMyInfo(authorizationHeader, request);

        return ResponseEntity.ok(
                ApiResponse.success("유저 정보 수정에 성공했습니다.", response)
        );
    }

    @DeleteMapping("/me")
    @Operation(summary = "계정 탈퇴", description = "현재 로그인한 계정을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteMyInfo(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        userService.deleteMyInfo(authorizationHeader);

        return ResponseEntity.ok(
                ApiResponse.success("유저 탈퇴에 성공했습니다.", null)
        );
    }
}
