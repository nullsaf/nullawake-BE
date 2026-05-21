package com.nullsaf.nullawake.api.question.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SolvedQuestionListResponse(
    List<SolvedQuestionInfo> questionList,
    Long nextCursor,
    Boolean hasNext
) {
    public record SolvedQuestionInfo(
        Long historyId,
        Long questionId,
        Long techCategoryId,
        String techCategoryName,
        Long techStackId,
        String techStackName,
        String title,
        LocalDateTime solvedAt,
        Boolean isCorrect,
        Boolean bookmarked
    ) {}
}
