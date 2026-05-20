package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 객관식 문제의 선지 조회
 */
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {

    // 정답인 선지 조회
    Optional<QuestionChoice> findByQuestionQuestionIdAndIsAnswerTrue(Long questionId);

    // 문제 풀이 제출 시, 사용자가 제출한 선지가 해당 문제에 속하는지 검증
    Optional<QuestionChoice> findByChoiceIdAndQuestionQuestionId(Long choiceId, Long questionId);
}
