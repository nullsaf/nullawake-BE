package com.nullsaf.nullawake.api.question.service;

import com.nullsaf.nullawake.api.question.dto.QuestionAnswerResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionResponse;
import com.nullsaf.nullawake.api.question.dto.QuestionSubmitRequest;
import com.nullsaf.nullawake.api.question.dto.QuestionSubmitResponse;
import com.nullsaf.nullawake.api.question.entity.Question;
import com.nullsaf.nullawake.api.question.entity.QuestionChoice;
import com.nullsaf.nullawake.api.question.entity.QuestionHistory;
import com.nullsaf.nullawake.api.question.entity.QuestionType;
import com.nullsaf.nullawake.api.question.repository.QuestionChoiceRepository;
import com.nullsaf.nullawake.api.question.repository.QuestionHistoryRepository;
import com.nullsaf.nullawake.api.question.repository.QuestionRepository;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.api.user.repository.UserRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문제 관련 서비스
 *
 * 문제 조회, 답안 제출, 정답 확인 기능을 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final QuestionHistoryRepository questionHistoryRepository;
    private final UserRepository userRepository;

    /**
     * 문제 ID를 기준으로 문제 정보, 기술 스택명, 객관식 선택지를 조회한다.
     *
     * @param questionId 문제 ID
     * @return 문제 상세 정보
     */
    public QuestionResponse getQuestion(Long questionId) {
        // 문제와 연관된 기술 스택, 선택지 정보를 함께 조회
        Question question = questionRepository.findByIdWithTechStack(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionResponse.from(question);
    }

    /**
     * 답안 제출
     *
     * 문제 유형에 따라 객관식 답안 또는 단답형 답안을 검증하고,
     * 사용자의 풀이 이력을 저장
     *
     * @param questionId 문제 ID
     * @param request 제출한 답안 정보
     * @return 답안 제출 결과
     */
    @Transactional
    public QuestionSubmitResponse submitAnswer(
            Long questionId,
            Long userId,
            QuestionSubmitRequest request
    ) {
        // 제출 대상 문제 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // JWT 인증에서 전달받은 userId 기준 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean isCorrect;
        QuestionChoice submittedChoice = null;
        String submittedAnswer = null;

        // 객관식 문제 채점
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            if (request.submittedChoiceId() == null) {
                throw new CustomException(ErrorCode.QUESTION_SUBMIT_INVALID_REQUEST);
            }

            // 사용자가 제출한 선택지 조회
            submittedChoice = questionChoiceRepository
                    .findByChoiceIdAndQuestionQuestionId(request.submittedChoiceId(), questionId)
                    .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_SUBMIT_INVALID_REQUEST));

            isCorrect = Boolean.TRUE.equals(submittedChoice.getIsAnswer());

            // 단답형 문제 채점
        } else {
            if (request.submittedAnswer() == null || request.submittedAnswer().isBlank()) {
                throw new CustomException(ErrorCode.QUESTION_SUBMIT_INVALID_REQUEST);
            }

            submittedAnswer = request.submittedAnswer().trim();

            // 대소문자 차이는 허용하여 정답 여부 판단
            isCorrect = question.getAnswer() != null
                    && question.getAnswer().trim().equalsIgnoreCase(submittedAnswer);
        }

        // 사용자의 문제 풀이 이력 생성
        QuestionHistory history = QuestionHistory.builder()
                .user(user)
                .question(question)
                .submittedChoice(submittedChoice)
                .submittedAnswer(submittedAnswer)
                .isCorrect(isCorrect)
                .attemptCount(1)
                .solvedAt(LocalDateTime.now())
                .build();

        QuestionHistory savedHistory = questionHistoryRepository.save(history);

        return QuestionSubmitResponse.from(savedHistory);
    }

    /**
     * 정답 확인
     *
     * 풀이 이력 ID를 기준으로 사용자가 제출했던 문제의 정답과 해설을 조회
     * 객관식 문제는 정답 선택지 ID를 반환하고, 단답형 문제는 정답 문자열을 반환
     *
     * @param historyId 문제 풀이 이력 ID
     * @return 정답 및 해설 정보
     */
    public QuestionAnswerResponse getAnswer(Long historyId, Long userId) {
        QuestionHistory history = questionHistoryRepository.findByIdWithQuestion(historyId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_ANSWER_QUERY_FAILED));

        if (!history.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.QUESTION_ANSWER_QUERY_FAILED);
        }

        Question question = history.getQuestion();

        Long correctChoiceId = null;

        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            correctChoiceId = questionChoiceRepository
                    .findByQuestionQuestionIdAndIsAnswerTrue(question.getQuestionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_ANSWER_QUERY_FAILED))
                    .getChoiceId();
        }

        return QuestionAnswerResponse.of(history, correctChoiceId);
    }
}
