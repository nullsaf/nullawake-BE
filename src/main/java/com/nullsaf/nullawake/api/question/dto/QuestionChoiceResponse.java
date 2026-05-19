package com.nullsaf.nullawake.api.question.dto;

import com.nullsaf.nullawake.api.question.entity.QuestionChoice;

public record QuestionChoiceResponse(
        Long choiceId,
        Integer choiceOrder,
        String content
) {
    public static QuestionChoiceResponse from(QuestionChoice choice) {
        return new QuestionChoiceResponse(
                choice.getChoiceId(),
                choice.getChoiceOrder(),
                choice.getContent()
        );
    }
}
