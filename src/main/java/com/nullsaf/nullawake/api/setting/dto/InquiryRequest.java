package com.nullsaf.nullawake.api.setting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InquiryRequest(
    @NotBlank(message = "문의 내용은 필수입니다.")
    String content,

    @NotBlank(message = "답변받을 이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String replyEmail
) {

}
