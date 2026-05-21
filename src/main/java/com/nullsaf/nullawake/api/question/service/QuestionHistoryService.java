package com.nullsaf.nullawake.api.question.service;

import com.nullsaf.nullawake.api.question.dto.ActiveTechCategoryResponse;
import com.nullsaf.nullawake.api.question.dto.ActiveTechCategoryResponse.ActiveTechCategory;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.AnswerInfo;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.ChoiceInfo;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.HistoryInfo;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.QuestionInfo;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.TechCategoryInfo;
import com.nullsaf.nullawake.api.question.dto.QuestionHistoryDetailResponse.TechStackInfo;
import com.nullsaf.nullawake.api.question.dto.SolvedQuestionListResponse;
import com.nullsaf.nullawake.api.question.entity.Question;
import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import com.nullsaf.nullawake.api.question.enums.QuestionType;
import com.nullsaf.nullawake.api.question.repository.QuestionHistoryRepository;
import com.nullsaf.nullawake.api.tech.entity.TechCategory;
import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.tech.repository.techcategory.TechCategoryRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionHistoryService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final TechCategoryRepository techCategoryRepository;
    private final QuestionHistoryRepository questionHistoryRepository;

    /**
     * 활성화된 기술 카테고리 조회
     * @return ActiveTechCategoryResponse
     */
    @Transactional(readOnly = true)
    public ActiveTechCategoryResponse getTechCategoryList() {
        try {
            List<ActiveTechCategory> techCategoryList = techCategoryRepository.findActiveCategories()
                .stream()
                .map(category -> new ActiveTechCategory(
                    category.getTechCategoryId(),
                    category.getTechCategoryName()
                ))
                .toList();

            return new ActiveTechCategoryResponse(techCategoryList);

        } catch (Exception e) {
            log.error("Error getting active tech categories - {}", e.getMessage());
            throw new CustomException(
                ErrorCode.TECH_CATEGORY_QUERY_FAILED
            );
        }
    }

    /**
     * 사용자가 푼 문제 목록 조회
     * @param userId 사용자 ID
     * @param categoryId 카테고리 ID (nullable)
     * @param bookmarked 북마크 여부 필터
     * @param cursor 마지막 조회 historyId
     * @param size 조회 개수
     * @return SolvedQuestionListResponse
     */
    public SolvedQuestionListResponse getSolvedQuestionList(
        Long userId,
        Long categoryId,
        Boolean bookmarked,
        Long cursor,
        Integer size
    ) {
        try {
            int pageSize = validateAndGetSize(size);
            int limit = pageSize + 1;

            List<SolvedQuestionListResponse.SolvedQuestionInfo> result =
                questionHistoryRepository.findSolvedQuestionList(
                    userId,
                    categoryId,
                    bookmarked,
                    cursor,
                    limit
                );

            boolean hasNext = result.size() > pageSize;

            List<SolvedQuestionListResponse.SolvedQuestionInfo> questionList =
                hasNext
                    ? result.subList(0, pageSize)
                    : result;

            Long nextCursor = hasNext
                ? questionList.getLast().historyId()
                : null;

            return new SolvedQuestionListResponse(
                questionList,
                nextCursor,
                hasNext
            );

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("[QuestionHistoryService] 사용자가 푼 문제 목록 조회 실패 - userId={}", userId, e);
            throw new CustomException(ErrorCode.SOLVED_QUESTION_LIST_READ_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public QuestionHistoryDetailResponse getQuestionHistoryDetail(
        Long userId,
        Long historyId
    ) {
        try {
            QuestionHistory history = questionHistoryRepository
                .findHistoryDetailByHistoryId(historyId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_HISTORY_NOT_FOUND));

            validateHistoryOwner(history, userId);

            Question question = history.getQuestion();
            TechStack techStack = question.getTechStack();
            TechCategory techCategory = techStack.getTechCategory();

            boolean bookmarked = questionHistoryRepository.existsBookmark(
                userId,
                question.getQuestionId()
            );

            List<ChoiceInfo> choices = getChoices(question);

            AnswerInfo answer = getAnswerInfo(history, question);

            return new QuestionHistoryDetailResponse(
                new HistoryInfo(
                    history.getHistoryId(),
                    history.getIsCorrect(),
                    history.getAttemptCount(),
                    history.getSolvedAt(),
                    bookmarked
                ),
                new QuestionInfo(
                    question.getQuestionId(),
                    question.getQuestionType().name(),
                    new TechCategoryInfo(
                        techCategory.getTechCategoryId(),
                        techCategory.getTechCategoryName()
                    ),
                    new TechStackInfo(
                        techStack.getTechStackId(),
                        techStack.getTechStackName()
                    ),
                    question.getDifficulty().name(),
                    question.getTitle(),
                    question.getContent(),
                    question.getExplanation(),
                    choices,
                    answer
                )
            );

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error(
                "[QuestionHistoryService] 문제 풀이 기록 상세 조회 실패 - userId={}, historyId={}",
                userId,
                historyId,
                e
            );

            throw new CustomException(ErrorCode.QUESTION_HISTORY_DETAIL_READ_FAILED);
        }
    }

    private void validateHistoryOwner(QuestionHistory history, Long userId) {
        if (!history.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.QUESTION_HISTORY_FORBIDDEN);
        }
    }

    private List<ChoiceInfo> getChoices(Question question) {
        if (question.getQuestionType() != QuestionType.MULTIPLE_CHOICE) {
            return List.of();
        }

        List<QuestionChoice> choices =
            questionHistoryRepository.findChoicesByQuestionId(
                question.getQuestionId()
            );

        return choices.stream()
            .map(choice -> new ChoiceInfo(
                choice.getChoiceId(),
                choice.getChoiceOrder(),
                choice.getContent()
            ))
            .toList();
    }

    private AnswerInfo getAnswerInfo(
        QuestionHistory history,
        Question question
    ) {
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            Long correctChoiceId = questionHistoryRepository
                .findCorrectChoiceIdByQuestionId(question.getQuestionId())
                .orElse(null);

            return new AnswerInfo(
                correctChoiceId,
                history.getSubmittedChoice().getChoiceId(),
                null,
                null
            );
        }

        return new AnswerInfo(
            null,
            null,
            question.getAnswer(),
            history.getSubmittedAnswer()
        );
    }

    /**
     * 조회 사이즈 검증 및 보정
     * @param size 요청 size
     * @return 검증 완료한 size
     */
    private int validateAndGetSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }

        if (size <= 0) {
            throw new CustomException(ErrorCode.INVALID_PAGE_SIZE);
        }
        return Math.min(size, MAX_SIZE);
    }
}
