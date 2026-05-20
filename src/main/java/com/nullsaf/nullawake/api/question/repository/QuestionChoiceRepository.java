package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {

    Optional<QuestionChoice> findByQuestionQuestionIdAndIsAnswerTrue(Long questionId);

    Optional<QuestionChoice> findByChoiceIdAndQuestionQuestionId(Long choiceId, Long questionId);
}
