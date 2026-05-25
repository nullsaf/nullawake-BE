package com.nullsaf.nullawake.api.question.repository;

import static com.nullsaf.nullawake.api.question.entity.QQuestion.question;
import static com.nullsaf.nullawake.api.question.entity.QQuestionChoice.questionChoice;
import static com.nullsaf.nullawake.api.question.entity.QQuestionHistory.questionHistory;
import static com.nullsaf.nullawake.api.question.entity.QUserQuestionBookmark.userQuestionBookmark;
import static com.nullsaf.nullawake.api.tech.entity.QTechCategory.techCategory;
import static com.nullsaf.nullawake.api.tech.entity.QTechStack.techStack;

import com.nullsaf.nullawake.api.question.dto.SolvedQuestionListResponse;
import com.nullsaf.nullawake.api.question.dto.SolvedQuestionListResponse.SolvedQuestionInfo;
import com.nullsaf.nullawake.api.question.entity.QQuestionHistory;
import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionHistoryRepositoryImpl implements QuestionHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<SolvedQuestionInfo> findSolvedQuestionList(
        Long userId,
        Long categoryId,
        Boolean bookmarked,
        Long cursor,
        int limit
    ) {
        QQuestionHistory latestHistory =
            new QQuestionHistory("latestHistory");

        BooleanBuilder condition = new BooleanBuilder();

        // 사용자 조건
        condition.and(questionHistory.user.userId.eq(userId));

        // 문제별 최신 풀이 기록만 조회
        condition.and(
            questionHistory.historyId.eq(
                JPAExpressions
                    .select(latestHistory.historyId.max())
                    .from(latestHistory)
                    .where(
                        latestHistory.user.userId.eq(userId),
                        latestHistory.question.questionId.eq(
                            question.questionId
                        )
                    )
            )
        );

        // 카테고리 필터
        if (categoryId != null) {
            condition.and(
                techCategory.techCategoryId.eq(categoryId)
            );
        }

        // 무한 스크롤 cursor
        if (cursor != null) {
            condition.and(
                questionHistory.historyId.lt(cursor)
            );
        }

        // 북마크 필터
        if (Boolean.TRUE.equals(bookmarked)) {
            condition.and(
                userQuestionBookmark.bookmarkId.isNotNull()
            );
        }

        return queryFactory
            .select(
                Projections.constructor(
                    SolvedQuestionListResponse.SolvedQuestionInfo.class,

                    questionHistory.historyId,
                    question.questionId,

                    techCategory.techCategoryId,
                    techCategory.techCategoryName,

                    techStack.techStackId,
                    techStack.techStackName,

                    question.title,

                    questionHistory.solvedAt,
                    questionHistory.isCorrect,

                    userQuestionBookmark.bookmarkId.isNotNull()
                )
            )
            .from(questionHistory)

            .join(questionHistory.question, question)

            .join(question.techStack, techStack)

            .join(techStack.techCategory, techCategory)

            .leftJoin(userQuestionBookmark)
            .on(
                userQuestionBookmark.user.userId.eq(userId),
                userQuestionBookmark.question.questionId.eq(
                    question.questionId
                )
            )

            .where(condition)

            .orderBy(questionHistory.historyId.desc())

            .limit(limit)

            .fetch();
    }

    @Override
    public Optional<QuestionHistory> findHistoryDetailByHistoryId(Long historyId) {
        QuestionHistory result = queryFactory
            .selectFrom(questionHistory)
            .join(questionHistory.question, question).fetchJoin()
            .join(question.techStack, techStack).fetchJoin()
            .join(techStack.techCategory, techCategory).fetchJoin()
            .where(
                questionHistory.historyId.eq(historyId),
                question.devActive.isTrue(),
                techStack.devActive.isTrue(),
                techCategory.devActive.isTrue()
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<QuestionChoice> findChoicesByQuestionId(Long questionId) {
        return queryFactory
            .selectFrom(questionChoice)
            .where(questionChoice.question.questionId.eq(questionId))
            .orderBy(questionChoice.choiceOrder.asc())
            .fetch();
    }

    @Override
    public Optional<Long> findCorrectChoiceIdByQuestionId(Long questionId) {
        Long result = queryFactory
            .select(questionChoice.choiceId)
            .from(questionChoice)
            .where(
                questionChoice.question.questionId.eq(questionId),
                questionChoice.isAnswer.isTrue()
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsBookmark(Long userId, Long questionId) {
        Integer result = queryFactory
            .selectOne()
            .from(userQuestionBookmark)
            .where(
                userQuestionBookmark.user.userId.eq(userId),
                userQuestionBookmark.question.questionId.eq(questionId)
            )
            .fetchFirst();

        return result != null;
    }
}
