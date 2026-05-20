package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자의 문제 풀이 기록 조회
 */
public interface QuestionHistoryRepository extends JpaRepository<QuestionHistory, Long> {

    // 풀이 기록 하나 조회. 이때 Question 정보도 같이 가져온다
    @EntityGraph(attributePaths = {"question"})
    Optional<QuestionHistory> findByHistoryId(Long historyId);
}
