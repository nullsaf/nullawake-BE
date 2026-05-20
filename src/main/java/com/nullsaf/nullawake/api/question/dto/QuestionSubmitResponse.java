package com.nullsaf.nullawake.api.question.dto;

import com.nullsaf.nullawake.api.question.entity.QuestionHistory;

import java.time.LocalDateTime;

public record QuestionSubmitResponse(
        Long historyId,
        Boolean isCorrect,
        LocalDateTime solvedAt
) {
    public static QuestionSubmitResponse from(QuestionHistory history) {
        return new QuestionSubmitResponse(
                history.getHistoryId(),
                history.getIsCorrect(),
                history.getSolvedAt()
        );
    }
}
