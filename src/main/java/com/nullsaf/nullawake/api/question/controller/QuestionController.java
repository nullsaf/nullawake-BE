package com.nullsaf.nullawake.api.question.controller;

import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.question.dto.QuestionAnswerResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionBookmarkResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionSubmitRequest;
import com.nullsaf.nullawake.api.question.dto.QuestionSubmitResponse;
import com.nullsaf.nullawake.api.question.service.QuestionService;
import com.nullsaf.nullawake.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 문제 조회/정답 제출/풀이조회 Controller
 *
 * 문제 조회, 정답 제출, 풀이조회 기능을 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
@Tag(name = "Question/Answer/Description", description = "문제 조회/정답 제출/풀이조회")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "문제 상세 조회")
    @GetMapping("/{questionId}")
    public ApiResponse<QuestionResponse> getQuestion(@PathVariable Long questionId) {
        return ApiResponse.success(
                "문제 조회에 성공했습니다.",
                questionService.getQuestion(questionId)
        );
    }

    @Operation(summary = "문제 답안 제출")
    @PostMapping("/{questionId}/submit")
    public ApiResponse<QuestionSubmitResponse> submitAnswer(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody QuestionSubmitRequest request
    ) {
        return ApiResponse.success(
                "답안 제출에 성공했습니다.",
                questionService.submitAnswer(questionId, userDetails.getUserId(), request)
        );
    }

    @Operation(summary = "유저가 푼 문제 상세 조회")
    @GetMapping("/{historyId}/answer")
    public ApiResponse<QuestionAnswerResponse> getAnswer(
            @PathVariable Long historyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
                "정답 조회에 성공했습니다.",
                questionService.getAnswer(historyId, userDetails.getUserId())
        );
    }

    @Operation(summary = "문제 북마크 추가")
    @PostMapping("/{questionId}/bookmark")
    public ApiResponse<QuestionBookmarkResponse> addBookmark(
        @PathVariable Long questionId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        QuestionBookmarkResponse response = questionService.addBookmark(questionId, userId);
        return ApiResponse.success(
            "문제 북마크 추가에 성공했습니다.",
            response
        );
    }
}
