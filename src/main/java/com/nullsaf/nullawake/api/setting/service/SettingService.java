package com.nullsaf.nullawake.api.setting.service;

import com.nullsaf.nullawake.api.setting.dto.InquiryRequest;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingService {

    private final JavaMailSender mailSender;

    @Value("${app.inquiry.receiver-email}")
    private String receiverEmail;

    /**
     * 문의 내용 개발자 이메일로 전달
     * @param request 문의 내용
     */
    public void sendInquiry(InquiryRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(receiverEmail);
            message.setReplyTo(request.replyEmail());
            message.setSubject("[Null Awake 문의] 사용자 문의가 도착했습니다.");
            message.setText(createMailContent(request));

            mailSender.send(message);

        } catch (MailException e) {
            log.error("[InquiryService] 문의 메일 전송 실패 - replyEmail={}",
                request.replyEmail(), e);

            throw new CustomException(ErrorCode.INQUIRY_MAIL_SEND_FAILED);
        }
    }

    /**
     * 메일 본문 생성용 메서드
     * @param request 사용자가 보낸 본문
     * @return 메일 본문
     */
    private String createMailContent(InquiryRequest request) {
        return """
            [사용자 문의]

            답변 받을 이메일:
            %s

            문의 내용:
            %s
            """.formatted(
            request.replyEmail(),
            request.content()
        );
    }
}
