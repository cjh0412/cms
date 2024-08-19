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
                .to("example@example.com")
                .subject("test")
                .text("my text")
                .build();

        return mailgunClient.sendEmail(form);
    }
}
