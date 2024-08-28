package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.application.SignUpApplication;
import com.cjhdev.cms.user.domain.SignUpForm;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;

    @Autowired
    private CustomerRepository customerRepository;


    // 이메일인증시 key 값 생성
    private String getRandomCode() {
        return RandomStringUtils.random(19, true, true); // 문자와 숫자 둘다 사용
    }

    @Test
    void signUp() {

        // given
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("1")
                .phone("01012341234")
                .build();

        // when
        Customer customer = service.signUp(form);

        // then
        assertNotNull(customer);
    }

    @Test
    void isEmailExistTest(){
        //given
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(getRandomCode())
                .verified(false)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();
        //when
        customerRepository.save(customer);

        // 회원정보 존재할 경우
        Boolean emailTrue = service.isEmailExist(customer.getEmail());
        
        // 회원정보 존재하지 않을 경우
        Boolean emailFalse = service.isEmailExist("falseEmail@email.com");

        //then
        assertTrue(emailTrue); // 동일값인 경우
        assertFalse(emailFalse); // 동일값 x

    }

    @Test
    void verifyEmail_success(){
        String code = getRandomCode();
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(code)
                .verified(false)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        //when
        customerRepository.save(customer);
        service.verifyEmail(customer.getEmail(), customer.getVerificationCode());

        customer.verifiedYN(customer.getEmail(), true);
        customerRepository.save(customer);

        // then
        assertTrue(customer.isVerified());
        assertTrue("trueEmail@gmail.com".equals(customer.getEmail()));
        assertTrue(customer.getVerificationCode().equals(code));
    }


    @Test
    void verifyEmail_fail(){

        String code = getRandomCode();
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(code)
                .verified(false)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        //when
        customerRepository.save(customer);

        // then

        // 회원 정보 x
        CustomerException exception1 = Assertions.assertThrows(CustomerException.class,()
                -> service.verifyEmail("falseEmail@gmail.com", customer.getVerificationCode()));
        assertEquals(ErrorCode.NOT_FOUND_USER, exception1.getErrorCode());

        // 잘못된 인증키
        CustomerException exception2 = Assertions.assertThrows(CustomerException.class,()
                -> service.verifyEmail(customer.getEmail(), "123123123"));
        assertEquals(ErrorCode.WRONG_VERIFY, exception2.getErrorCode());

    }


    @Test
    void verifyEmail_fail_expiredAt(){
        String code = getRandomCode();
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(code)
                .verified(false)
                .verifyExpiredAt(LocalDateTime.now().minusDays(1))
                .build();

        //when
        customerRepository.save(customer);

        // 만료된 인증키
        CustomerException exception3 = Assertions.assertThrows(CustomerException.class,()
                -> service.verifyEmail(customer.getEmail(), customer.getVerificationCode()));
        assertEquals(ErrorCode.EXPIRE_CODE, exception3.getErrorCode());

    }


    @Test
    void verifyEmail_fail_verified(){
        String code = getRandomCode();
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(code)
                .verified(true)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        //when
        customerRepository.save(customer);

        // 회원 정보 존재
        CustomerException exception4 = Assertions.assertThrows(CustomerException.class,()
                -> service.verifyEmail(customer.getEmail(), customer.getVerificationCode()));
        assertEquals(ErrorCode.ALREADY_VERIFY, exception4.getErrorCode());

    }
}