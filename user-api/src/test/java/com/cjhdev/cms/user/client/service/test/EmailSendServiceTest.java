package com.cjhdev.cms.user.client.service.test;

import com.cjhdev.cms.user.service.test.EmailSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailSendServiceTest {

    @Autowired
    private EmailSendService emailSendService;

    @Test
    void sendEmail() {
        String response = emailSendService.sendEmail();
        System.out.println(response);
    }
}