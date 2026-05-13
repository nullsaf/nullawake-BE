package com.nullsaf.nullawake.api.tech.controller;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.service.TechCategoryService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
