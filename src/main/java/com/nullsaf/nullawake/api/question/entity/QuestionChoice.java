package com.nullsaf.nullawake.api.question.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION_CHOICE")
public class QuestionChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "choice_order")
    private Integer choiceOrder;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "is_answer")
    private Boolean isAnswer;
}
