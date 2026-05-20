package com.nullsaf.nullawake.api.question.dto;

import com.nullsaf.nullawake.api.question.entity.Question;
import com.nullsaf.nullawake.api.question.enums.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long questionId,
        String techStackName,
        QuestionType questionType,
        String title,
        String content,
        List<QuestionChoiceResponse> choices
) {
    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getQuestionId(),
                question.getTechStack().getTechStackName(),
                question.getQuestionType(),
                question.getTitle(),
                question.getContent(),
                question.getChoices().stream()
                        .map(QuestionChoiceResponse::from)
                        .toList()
        );
    }
}
