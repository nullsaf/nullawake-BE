package com.nullsaf.nullawake.common.exception;

import com.nullsaf.nullawake.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역 예외 처리를 담당하는 Handler.
 *
 * CustomException과 Validation 예외를 공통 응답 형식으로 변환한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(
            CustomException e
    ) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                        ApiResponse.fail(
                                errorCode.getStatus().value(),
                                errorCode.getMessage()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(400, message));
    }
}
