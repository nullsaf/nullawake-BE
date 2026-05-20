package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionHistoryRepository extends JpaRepository<QuestionHistory, Long> {

    @EntityGraph(attributePaths = {"question"})
    Optional<QuestionHistory> findByHistoryId(Long historyId);

    default Optional<QuestionHistory> findByIdWithQuestion(Long historyId) {
        return findByHistoryId(historyId);
    }
}
