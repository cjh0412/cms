package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.application.SignUpApplication;
import com.cjhdev.cms.user.domain.CustomerBalanceHistory;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
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
class CustomerBalanceServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

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

        // 변경 정보 저장1
        history = changeHistory(10000, history.getCurrentMoney(), history.getCustomer(),  "변경 상세 내용 작성", "작성자1");

        customer.changeBalance(customer.getId(), history.getCurrentMoney());
        customerRepository.save(customer);
        customerBalanceHistoryRepository.save(history);

        // 첫번째 history 체크
        assertNotNull(history);
        assertTrue(history.getCurrentMoney().equals(10000));
        assertTrue(history.getChangeMoney().equals(10000));
        assertEquals(history.getCurrentMoney(), customer.getBalance());

        // 변경 정보 저장2
        history = changeHistory(25000, history.getCurrentMoney(), history.getCustomer(),  "변경 상세 내용 작성2", "작성자2");

        customer.changeBalance(customer.getId(), history.getCurrentMoney());
        customerRepository.save(customer);
        customerBalanceHistoryRepository.save(history);

        // 두번쨰 history 체크
        assertNotNull(history);
        assertTrue(history.getCurrentMoney().equals(35000));
        assertTrue(history.getChangeMoney().equals(25000));
        assertEquals(history.getCurrentMoney(), customer.getBalance());



        }

        private CustomerBalanceHistory changeHistory (Integer changeMoney, Integer currentMoney, Customer customer, String message, String from){
            return CustomerBalanceHistory.builder()
                    .changeMoney(changeMoney)
                    .currentMoney(currentMoney + changeMoney)
                    .description(message)
                    .fromMessage(from)
                    .customer(customer)
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