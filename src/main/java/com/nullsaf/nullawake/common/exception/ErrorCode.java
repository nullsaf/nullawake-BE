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
    TECH_STACK_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "선택된 기술 스택 정보가 비어있습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
