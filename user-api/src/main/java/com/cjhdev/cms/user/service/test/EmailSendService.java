package com.cjhdev.cms.user.service.test;

import com.cjhdev.cms.user.client.MailgunClient;
import com.cjhdev.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    @Value("${mailgun.mail}")
    private String mailgunMail;

    private final MailgunClient mailgunClient;

    public String sendEmail(){
        SendMailForm form = SendMailForm.builder()
                .from(mailgunMail)
                .to("example@example.com") // 테스트 email의 경우 메일건에 설정 필요
                .subject("test")
                .text("my text")
                .build();

        return mailgunClient.sendEmail(form);
    }
}
