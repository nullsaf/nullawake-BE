package com.nullsaf.nullawake.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private LocalDateTime responseAt;
    private T data;
    private boolean success;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .responseAt(LocalDateTime.now())
                .data(data)
                .success(true)
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .code(200)
                .message(message)
                .responseAt(LocalDateTime.now())
                .success(true)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .code(201)
                .message(message)
                .responseAt(LocalDateTime.now())
                .data(data)
                .success(true)
                .build();
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .responseAt(LocalDateTime.now())
                .success(false)
                .build();
    }
}
