package com.cjhdev.cms.user.client;

import com.cjhdev.cms.user.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

//httpClient 대체
// HttpClient보다 간단, 높은 가독성, 테스트가 간편, 용이한 커스텀
@FeignClient(name="mailgun", url="https://api.mailgun.net/v3/")


// 사용할 의존 객체를 선택, bean객체 존재 필수
@Qualifier("mailgun")
public interface MailgunClient {
    @PostMapping("sandbox4d33d59d893d4643ba2cc921aaf23b5a.mailgun.org/messages")
    String sendEmail(@SpringQueryMap SendMailForm form);
}

