package com.nullsaf.nullawake.api.alarm.controller;

import com.nullsaf.nullawake.api.alarm.dto.request.AlarmRequest;
import com.nullsaf.nullawake.api.alarm.dto.request.AlarmSelectedRequest;
import com.nullsaf.nullawake.api.alarm.dto.response.AlarmCategoryStackResponse;
import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.alarm.dto.response.AlarmResponse;
import com.nullsaf.nullawake.api.alarm.service.AlarmCommandService;
import com.nullsaf.nullawake.api.alarm.service.AlarmQueryService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alarm", description = "알람 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmQueryService alarmQueryService;
    private final AlarmCommandService alarmCommandService;

    @Operation(summary = "알람 목록 조회")
    @GetMapping
    public ApiResponse<AlarmResponse.ListResponse> getAlarms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        return ApiResponse.success(
                "알람 목록 조회에 성공했습니다",
                alarmQueryService.getAlarms(userId)
        );
    }

    @Operation(summary = "알람 생성")
    @PostMapping
    public ApiResponse<AlarmResponse.CreateResponse> createAlarm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AlarmRequest request
    ) {
        Long userId = userDetails.getUserId();

        Long alarmGroupId = alarmCommandService.createAlarm(userId, request);

        return ApiResponse.created(
                "알람 생성에 성공했습니다.",
                new AlarmResponse.CreateResponse(alarmGroupId)
        );
    }

    @Operation(summary = "알람 상세 조회")
    @GetMapping("/{alarmGroupId}")
    public ApiResponse<AlarmResponse.DetailResponse> getAlarmDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmGroupId
    ) {
        Long userId = userDetails.getUserId();

        return ApiResponse.success(
                "알람 조회에 성공했습니다.",
                alarmQueryService.getAlarmDetail(userId, alarmGroupId)
        );
    }

    @Operation(summary = "알람 수정")
    @PutMapping("/{alarmGroupId}")
    public ApiResponse<Void> updateAlarm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmGroupId,
            @Valid @RequestBody AlarmRequest request
    ) {
        Long userId = userDetails.getUserId();

        alarmCommandService.updateAlarm(userId, alarmGroupId, request);

        return ApiResponse.success("알람 수정에 성공했습니다.");
    }

    @Operation(summary = "알람 삭제")
    @DeleteMapping("/{alarmGroupId}")
    public ApiResponse<Void> deleteAlarm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmGroupId
    ) {
        Long userId = userDetails.getUserId();

        alarmCommandService.deleteAlarm(userId, alarmGroupId);

        return ApiResponse.success("알람 삭제에 성공했습니다.");
    }

    @Operation(summary = "알람 활성화/비활성화")
    @PatchMapping("/{alarmGroupId}/selected")
    public ApiResponse<AlarmResponse.SelectedResponse> updateSelected(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmGroupId,
            @Valid @RequestBody AlarmSelectedRequest request
    ) {
        Long userId = userDetails.getUserId();

        Boolean selected = alarmCommandService.updateSelected(userId, alarmGroupId, request);

        return ApiResponse.success(
                "알람 상태 변경에 성공했습니다.",
                new AlarmResponse.SelectedResponse(alarmGroupId, selected)
        );
    }

    @Operation(summary = "알람 생성용 카테고리/스택 조회")
    @GetMapping("/category_stacks")
    public ApiResponse<AlarmCategoryStackResponse> getCategoryStacks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        AlarmCategoryStackResponse response =
                alarmQueryService.getCategoryStacks(userId);

        return ApiResponse.success(
                "활성화된 카테고리/스택 조회에 성공했습니다.",
                response
        );
    }
}
