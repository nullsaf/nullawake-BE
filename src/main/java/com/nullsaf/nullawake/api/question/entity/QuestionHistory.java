package com.nullsaf.nullawake.api.question.entity;

import com.nullsaf.nullawake.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_QUESTION_HISTORY")
public class QuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_choice_id")
    private QuestionChoice submittedChoice;

    @Lob
    @Column(name = "submitted_answer")
    private String submittedAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;
}
