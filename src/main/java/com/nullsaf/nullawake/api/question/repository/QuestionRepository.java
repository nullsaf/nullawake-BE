package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = {"techStack", "choices"})
    Optional<Question> findByQuestionId(Long questionId);

    default Optional<Question> findByIdWithTechStack(Long questionId) {
        return findByQuestionId(questionId);
    }
}
