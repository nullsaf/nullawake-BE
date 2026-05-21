package com.nullsaf.nullawake.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // OAuth
    INVALID_SOCIAL_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인가 코드입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    OAUTH_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "소셜 사용자 정보를 가져오지 못했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),

    // TECH CATEGORY
    TECH_CATEGORY_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기술 카테고리 조회 중 오류가 발생했습니다."),
    TECH_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 기술 카테고리입니다."),

    // TECH_STACK
    TECH_STACK_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기술 스택 목록 조회 중 오류가 발생했습니다."),
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 해당 카테고리에 속하지 않는 기술 스택이 포함되어 있습니다."),
    TECH_STACK_SELECTION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기술 스택 선택 정보 저장 중 오류가 발생했습니다."),
    TECH_STACK_SELECTION_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기술 스택 선택 정보 수정 중 오류가 발생했습니다."),
    TECH_STACK_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "선택된 기술 스택 정보가 비어있습니다."),
    SELECTED_TECH_STACK_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "선택한 기술 스택 조회 중 오류가 발생했습니다."),

    // Alarm
    ALARM_LIST_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 알람 목록 조회 요청입니다."),
    ALARM_CREATE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "기술 스택, 요일, 알람 시간 설정은 필수입니다."),
    ALARM_UPDATE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "알람 수정 요청 값이 올바르지 않습니다."),
    ALARM_DELETE_FAILED(HttpStatus.BAD_REQUEST, "알람 삭제에 실패했습니다."),
    ALARM_SELECTED_BAD_REQUEST(HttpStatus.BAD_REQUEST, "알람 활성화 여부 값이 올바르지 않습니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."),
    ALARM_CATEGORY_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "활성화된 카테고리/스택을 찾을 수 없습니다."),
    ALARM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),
    ALARM_INACTIVE_STACK(HttpStatus.BAD_REQUEST, "활성화하지 않은 카테고리/스택으로는 알람을 생성할 수 없습니다."),

    // Question
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 문제입니다."),
    QUESTION_QUERY_FAILED(HttpStatus.BAD_REQUEST, "문제 조회에 실패했습니다."),
    QUESTION_SUBMIT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "제출한 답안 형식이 올바르지 않습니다."),
    QUESTION_ANSWER_QUERY_FAILED(HttpStatus.BAD_REQUEST, "정답 확인에 실패했습니다."),
    QUESTION_CHOICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 선택지입니다."),

    // Question History
    SOLVED_QUESTION_LIST_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사용자가 푼 문제 목록 조회 중 오류가 발생했습니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "조회 개수는 1 이상이어야 합니다."),
    QUESTION_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "문제 풀이 기록이 존재하지 않습니다."),
    QUESTION_HISTORY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 문제 풀이 기록에 접근할 수 없습니다."),
    QUESTION_HISTORY_DETAIL_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "문제 풀이 기록 상세 조회 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
