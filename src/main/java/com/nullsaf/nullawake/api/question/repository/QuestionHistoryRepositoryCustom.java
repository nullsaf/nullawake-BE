package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.dto.SolvedQuestionListResponse.SolvedQuestionInfo;
import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import java.util.List;
import java.util.Optional;

public interface QuestionHistoryRepositoryCustom {

    List<SolvedQuestionInfo> findSolvedQuestionList(
        Long userId,
        Long categoryId,
        Boolean bookmarked,
        Long cursor,
        int limit
    );


    Optional<QuestionHistory> findHistoryDetailByHistoryId(Long historyId);

    List<QuestionChoice> findChoicesByQuestionId(Long questionId);

    Optional<Long> findCorrectChoiceIdByQuestionId(Long questionId);

    boolean existsBookmark(Long userId, Long questionId);
}
