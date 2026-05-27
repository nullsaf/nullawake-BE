package com.nullsaf.nullawake.api.setting.controller;

import com.nullsaf.nullawake.api.setting.dto.InquiryRequest;
import com.nullsaf.nullawake.api.setting.service.SettingService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setting")
@Tag(name = "Setting", description = "설정화면 기능 API")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @PostMapping("/inquiries")
    @Operation(summary = "문의 메일 전송")
    public ResponseEntity<ApiResponse<Void>> sendInquiry(
        @Valid @RequestBody InquiryRequest request
    ) {
        settingService.sendInquiry(request);

        return ResponseEntity.ok(
            ApiResponse.success(
                "문의 메일 전송에 성공했습니다.",
                null
            )
        );
    }
}
