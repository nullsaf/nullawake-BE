package com.nullsaf.nullawake.api.question.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionHistoryDetailResponse(
    HistoryInfo history,
    QuestionInfo question
) {
    public record HistoryInfo(
        Long historyId,
        Boolean isCorrect,
        Integer attemptCount,
        LocalDateTime solvedAt,
        Boolean bookmarked
    ) {}

    public record QuestionInfo(
        Long questionId,
        String questionType,
        TechCategoryInfo techCategory,
        TechStackInfo techStack,
        String difficulty,
        String title,
        String content,
        String explanation,
        List<ChoiceInfo> choices,
        AnswerInfo answer
    ) {}

    public record TechCategoryInfo(
        Long techCategoryId,
        String techCategoryName
    ) {}

    public record TechStackInfo(
        Long techStackId,
        String techStackName
    ) {}

    public record ChoiceInfo(
        Long choiceId,
        Integer choiceOrder,
        String content
    ) {}

    public record AnswerInfo(
        Long correctChoiceId,
        Long submittedChoiceId,
        String correctAnswer,
        String submittedAnswer
    ) {}
}