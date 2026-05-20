package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 문제 테이블 조회
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 문제 한 개 조회
     * @param questionId questionId
     * @return 문제 하나의 정보, 이때 techStack, choice(객관식 보기)도 함께 조회
     */
    @EntityGraph(attributePaths = {"techStack", "choices"})
    Optional<Question> findByQuestionId(Long questionId);
}
