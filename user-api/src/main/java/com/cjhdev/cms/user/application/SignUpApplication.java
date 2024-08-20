package com.cjhdev.cms.user.application;

import com.cjhdev.cms.user.client.MailgunClient;
import com.cjhdev.cms.user.client.mailgun.SendMailForm;
import com.cjhdev.cms.user.domain.model.Seller;
import com.cjhdev.cms.user.service.customer.SignUpCustomerService;
import com.cjhdev.cms.user.domain.SignUpForm;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import com.cjhdev.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpApplication {
    @Value("${mailgun.mail}")
    private String mailgunMail;

    //각기 다른 역할을 하는 서비스를 관리
    //회원가입시 할일 1. 회원계정 만들기 2.메일보내기,
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SellerService sellerService;

    public void customerVerify(String email, String code){
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        // 메일 정보가 존재할 경우
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            //exception
            throw new CustomerException(ErrorCode.ALREADY_REGISTER_USER);
        } else {
            Customer customer = signUpCustomerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();
            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from(mailgunMail)
                    .to(form.getEmail())
                    .subject("verfication Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), "customer", code))
                    .build();
            mailgunClient.sendEmail(sendMailForm);
            signUpCustomerService.ChangeCustomerValidateEmail(customer.getId(), code);
        }
        return "회원가입에 성공하셨습니다.";
    }

    public void sellerVerify(String email, String code){
        sellerService.verifyEmail(email, code);
    }

    public String sellerSignUp(SignUpForm form) {
        // 메일 정보가 존재할 경우
        if (sellerService.isEmailExist(form.getEmail())) {
            //exception
            throw new CustomerException(ErrorCode.ALREADY_REGISTER_USER);
        } else {
            Seller seller = sellerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();
            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from(mailgunMail)
                    .to(form.getEmail())
                    .subject("verfication Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), "seller", code))
                    .build();
            mailgunClient.sendEmail(sendMailForm);
            sellerService.ChangeSellerValidateEmail(seller.getId(), code);
        }
        return "회원가입에 성공하셨습니다.";
    }

    // 이메일인증시 key 값 설정
    private String getRandomCode() {
        return RandomStringUtils.random(19, true, true); // 문자와 숫자 둘다 사용
    }

    // 전송 내용
    private String getVerificationEmailBody(String email, String name, String type, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello")
                .append(name + "님")
                .append("'이메일 인증'을 클릭해주세요")
                .append("http://localhost:8081/signup/"+type+"/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();

    }


}
