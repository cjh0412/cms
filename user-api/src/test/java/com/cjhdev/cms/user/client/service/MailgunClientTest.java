package com.cjhdev.cms.user.client.service;

import com.cjhdev.cms.user.client.MailgunClient;
import com.cjhdev.cms.user.client.mailgun.SendMailForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MailgunClientTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Value("${mailgun.mail}")
    private String mailgunMail;


    @Test
    void emailTest() {
        SendMailForm form = SendMailForm.builder()
                .from(mailgunMail)
                .to("jyedev04@gmail.com") // 테스트 email의 경우 메일건에 설정 필요
                .subject("test")
                .text("my text")
                .build();

        mailgunClient.sendEmail(form);

        assertNotNull(form.getFrom());
        assertNotNull(form.getTo());
        assertNotNull(form.getSubject());
        assertNotNull(form.getText());

    }
}