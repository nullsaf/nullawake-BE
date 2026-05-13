package com.nullsaf.nullawake.common.exception;

import lombok.Getter;

/**
 * 서비스 전역에서 사용하는 커스텀 예외 클래스.
 *
 * ErrorCode를 기반으로 예외 상태와 메시지를 관리한다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
