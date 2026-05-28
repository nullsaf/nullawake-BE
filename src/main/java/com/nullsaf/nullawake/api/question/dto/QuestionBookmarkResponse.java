package com.nullsaf.nullawake.api.question.dto;

public record QuestionBookmarkResponse(
    Long questionId,
    boolean bookmarked
) {

}
