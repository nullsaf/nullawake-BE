package com.nullsaf.nullawake.api.question.repository;

import com.nullsaf.nullawake.api.question.entity.UserQuestionBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBookmarkRepository extends JpaRepository<UserQuestionBookmark, Long> {
    boolean existsByUser_UserIdAndQuestion_QuestionId(Long userId, Long questionId);
}
