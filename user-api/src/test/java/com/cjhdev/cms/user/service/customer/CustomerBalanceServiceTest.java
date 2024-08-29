package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.application.SignUpApplication;
import com.cjhdev.cms.user.domain.CustomerBalanceHistory;
import com.cjhdev.cms.user.domain.customer.ChangeBalanceForm;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerBalanceHistoryRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerBalanceServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

    @Autowired
    private CustomerBalanceService customerBalanceService;

    // 이메일인증시 key 값 생성
    private static String getRandomCode() {
        return RandomStringUtils.random(19, true, true); // 문자와 숫자 둘다 사용
    }

    @Test
    void changeBalance() {
        // given

        Customer customer = customerInfo("trueTest@gmail.com", "name", LocalDate.now(), "password", "phone");
        customerRepository.save(customer);

        // 잔액 조회
        CustomerBalanceHistory history = CustomerBalanceHistory.builder() // 값이 없을 경우
                .changeMoney(0)
                .currentMoney(0)
                .customer(customer) // 계정이 없는 경우
                .build();


        // 변경 정보 저장1//
        CustomerBalanceHistory customerBalanceHistory =
        customerBalanceService.changeBalance(customer.getId(), form(10000, "변경 상세 내용",customer.getEmail()));


        assertTrue(customerBalanceHistory.getCurrentMoney() > 0);

        // 첫번째 history 체크
        assertNotNull(customerBalanceHistory);
        assertTrue(customerBalanceHistory.getCurrentMoney().equals(10000));
        assertTrue(customerBalanceHistory.getChangeMoney().equals(10000));
        assertEquals(customerBalanceHistory.getCurrentMoney(), customer.getBalance());

        // 변경 정보 저장2
        customerBalanceHistory =
        customerBalanceService.changeBalance(customer.getId(), form(25000, "변경 상세 내용2",customer.getEmail()));

        assertTrue(customerBalanceHistory.getCurrentMoney() > 0);

        // 두번쨰 history 체크
        assertNotNull(customerBalanceHistory);
        assertTrue(customerBalanceHistory.getCurrentMoney().equals(35000));
        assertTrue(customerBalanceHistory.getChangeMoney().equals(25000));
        assertEquals(customerBalanceHistory.getCurrentMoney(), customer.getBalance());


        // 변경 정보 저장3
        CustomerException exception =
                Assertions.assertThrows(CustomerException.class,()
                -> customerBalanceService.changeBalance(customer.getId(), form(-45000, "변경 상세 내용3",customer.getEmail())));

        assertEquals(ErrorCode.NOT_ENOUGH_BALANCE, exception.getErrorCode());

    }

    private ChangeBalanceForm form (Integer changeMoney, String message, String from){
        return ChangeBalanceForm.builder()
                .money(changeMoney)
                .from(from)
                .message(message)
                .build();
    }

    private Customer customerInfo(String email, String name, LocalDate birth, String password, String phone){
        // 회원 정보 세팅
        return Customer.builder()
                .name(name)
                .birth(birth)
                .email(email)
                .password(password)
                .phone(phone)
                .verificationCode(getRandomCode())
                .verified(true)
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

    }














}