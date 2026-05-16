package com.nullsaf.nullawake.api.tech.controller;

import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.dto.TechStackSelectionRequest;
import com.nullsaf.nullawake.api.tech.dto.TechStackSelectionResponse;
import com.nullsaf.nullawake.api.tech.service.TechCategoryService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TechCategory Controller
 * 카테고리 관련 API 기능을 제공한다.
 */
@RestController
@Tag(name = "Tech Category", description = "기술 카테고리 관리 API")
@RequestMapping("/api/tech-categories")
@RequiredArgsConstructor
public class TechCategoryController {

    private final TechCategoryService techCategoryService;

    @GetMapping
    @Operation(
        summary = "기술 카테고리 목록 조회",
        description = "활성화된 기술 카테고리 목록과 각 카테고리에 포함된 활성 기술 스택 개수를 조회합니다."
    )
    public ResponseEntity<ApiResponse<TechCategoryResponse>> getTechCategories() {

        TechCategoryResponse response =
            techCategoryService.getTechCategories();

        return ResponseEntity.ok(
            ApiResponse.success(
                "기술 카테고리 목록 조회에 성공했습니다.",
                response
            )
        );
    }

    @GetMapping("/{techCategoryId}/tech-stacks")
    @Operation(
        summary = "카테고리별 기술 스택 목록 조회",
        description = "Path Variable로 전달된 categoryId와 연관된 활성 기술 스택 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<TechStackListResponse>> getTechStacksByCategory(
        @PathVariable Long techCategoryId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TechStackListResponse response =
            techCategoryService.getTechStacksByCategory(userDetails.getUserId(), techCategoryId);

        return ResponseEntity.ok(
            ApiResponse.success(
                "기술 스택 목록 조회에 성공했습니다.",
                response
            )
        );
    }

//    @GetMapping("/me")
//
    @PostMapping("/me/{techCategoryId}/tech-stacks")
    @Operation(
        summary = "기술 스택 선택 정보 저장",
        description = "사용자가 초기 화면에서 선택한 기술 스택을 저장합니다. "
    )
    public ResponseEntity<ApiResponse<TechStackSelectionResponse>> saveSelectedTechStacks(
        @PathVariable Long techCategoryId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody TechStackSelectionRequest request
    ) {
        Long userId = userDetails.getUserId();

        TechStackSelectionResponse response = techCategoryService.saveSelectedTechStacks(userId, techCategoryId, request);

        return ResponseEntity.ok(
            ApiResponse.success(
                "기술 스택 선택 정보 저장에 성공했습니다.",
                response
            )
        );
    }

    @PatchMapping("/me/{techCategoryId}/tech-stacks")
    @Operation(
        summary = "기술 스택 선택 정보 수정",
        description = "사용자가 기술 스택 수정 화면에서 선택한 기술 스택을 수정합니다."
    )
    public ResponseEntity<ApiResponse<TechStackSelectionResponse>> updateSelectedTechStacks(
        @PathVariable Long techCategoryId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody TechStackSelectionRequest request
    ) {
        Long userId = userDetails.getUserId();

        TechStackSelectionResponse response = techCategoryService.updateSelectedTechStacks(userId, techCategoryId, request);

        return ResponseEntity.ok(
            ApiResponse.success(
                "기술 스택 선택 정보 수정에 성공했습니다.",
                response
            )
        );
    }

}
