package com.nullsaf.nullawake.api.question.dto;

public record QuestionSubmitRequest(
        Long submittedChoiceId,
        String submittedAnswer
) {
}
