package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionHistoryRepository extends JpaRepository<QuestionHistory, Long>, QuestionHistoryRepositoryCustom {

    @EntityGraph(attributePaths = {"question"})
    Optional<QuestionHistory> findByHistoryId(Long historyId);

    default Optional<QuestionHistory> findByIdWithQuestion(Long historyId) {
        return findByHistoryId(historyId);
    }

    /**
     * 사용자가 문제를 한번 이상 풀었는지 확인 여부
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @return boolean
     */
    boolean existsByUser_UserIdAndQuestion_QuestionId(Long userId, Long questionId);
}
