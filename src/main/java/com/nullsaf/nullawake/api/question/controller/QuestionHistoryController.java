package com.nullsaf.nullawake.api.question.controller;

import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.question.dto.ActiveTechCategoryResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse;
import com.nullsaf.nullawake.api.question.dto.SolvedQuestionListResponse;
import com.nullsaf.nullawake.api.question.service.QuestionHistoryService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question-histories")
@RequiredArgsConstructor
@Tag(name = "Question History", description = "사용자의 문제 풀이 이력 관련 API")
public class QuestionHistoryController {

    private final QuestionHistoryService questionHistoryService;

    @GetMapping()
    @Operation(summary = "사용자가 푼 문제 리스트 조회")
    public ResponseEntity<ApiResponse<SolvedQuestionListResponse>> getSolvedQuestionList(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false, defaultValue = "false") Boolean bookmarked,
        @RequestParam(required = false) Long cursor,
        @RequestParam(required = false) Integer size,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        SolvedQuestionListResponse response = questionHistoryService.getSolvedQuestionList(userId, categoryId, bookmarked, cursor, size);

        return ResponseEntity.ok(
            ApiResponse.success(
                "활성화된 기술 카테고리 조회에 성공했습니다.",
                response
            )
        );
    }

    @GetMapping("/category")
    @Operation(summary = "활성화된 기술 카테고리 조회")
    public ResponseEntity<ApiResponse<ActiveTechCategoryResponse>> getTechCategoryList(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ActiveTechCategoryResponse response = questionHistoryService.getTechCategoryList();

        return ResponseEntity.ok(
            ApiResponse.success(
                "활성화된 기술 카테고리 조회에 성공했습니다.",
                response
            )
        );
    }

    @GetMapping("/{historyId}")
    @Operation(summary = "문제 풀이 기록 상세 조회")
    public ResponseEntity<ApiResponse<QuestionHistoryDetailResponse>> getQuestionHistoryDetail(
        @PathVariable Long historyId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        QuestionHistoryDetailResponse response =
            questionHistoryService.getQuestionHistoryDetail(
                userId,
                historyId
            );

        return ResponseEntity.ok(
            ApiResponse.success(
                "문제 풀이 기록 상세 조회에 성공했습니다.",
                response
            )
        );
    }
}
