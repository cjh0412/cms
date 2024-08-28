package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    // 이메일인증시 key 값 생성
    private String getRandomCode() {
        return RandomStringUtils.random(19, true, true); // 문자와 숫자 둘다 사용
    }

    @Test
    void findByIdAndEmail() {
        //given
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(getRandomCode())
                .verified(true)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        //when
        customerRepository.save(customer);
        Optional<Customer>  trueResult = customerRepository.findById(customer.getId())
                .stream().filter(result -> result.getEmail().equals("trueEmail@gmail.com"))
                .findFirst();

        Optional<Customer>  falseResult = customerRepository.findById(customer.getId())
                .stream().filter(result -> result.getEmail().equals("falseEmail@gmail.com"))
                .findFirst();

        //then
        assertTrue(trueResult.isPresent());
        assertFalse(falseResult.isPresent());
    }

    @Test
    void findValidCustomer() {
        //given
        Customer customer = Customer.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("trueEmail@gmail.com")
                .password("1")
                .phone("01012341234")
                .verificationCode(getRandomCode())
                .verified(true)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        //when
        customerRepository.save(customer);
        Optional<Customer>  trueInfo = customerRepository.findByEmail("trueEmail@gmail.com").stream().filter(
                        result -> result.getPassword().equals("1") && result.isVerified())
                .findFirst();

        Optional<Customer>  emailFalse = customerRepository.findByEmail("falseEmail@gmail.com").stream().filter(
                        result -> result.getPassword().equals("1") && result.isVerified())
                .findFirst();

        Optional<Customer>  passwordFalse = customerRepository.findByEmail("trueEmail@gmail.com").stream().filter(
                        result -> result.getPassword().equals("1234") && result.isVerified())
                .findFirst();

        Optional<Customer>  totalFalse = customerRepository.findByEmail("falseEmail@gmail.com").stream().filter(
                        result -> result.getPassword().equals("1234") && result.isVerified())
                .findFirst();

    }
}