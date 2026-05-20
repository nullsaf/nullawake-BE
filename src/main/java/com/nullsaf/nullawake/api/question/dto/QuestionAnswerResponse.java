package com.nullsaf.nullawake.api.question.dto;

import com.nullsaf.nullawake.api.question.entity.Question;
import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import com.nullsaf.nullawake.api.question.enums.QuestionType;

public record QuestionAnswerResponse(
        Long questionId,
        QuestionType questionType,
        Long submittedChoiceId,
        String submittedAnswer,
        Long correctChoiceId,
        String answer,
        String explanation,
        Boolean isCorrect
) {
    public static QuestionAnswerResponse of(
            QuestionHistory history,
            Long correctChoiceId
    ) {
        Question question = history.getQuestion();

        Long submittedChoiceId = history.getSubmittedChoice() != null
                ? history.getSubmittedChoice().getChoiceId()
                : null;

        return new QuestionAnswerResponse(
                question.getQuestionId(),
                question.getQuestionType(),
                submittedChoiceId,
                history.getSubmittedAnswer(),
                correctChoiceId,
                question.getQuestionType() == QuestionType.SHORT_ANSWER ? question.getAnswer() : null,
                question.getExplanation(),
                history.getIsCorrect()
        );
    }
}
