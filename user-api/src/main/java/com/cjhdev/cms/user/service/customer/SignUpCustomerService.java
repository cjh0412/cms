package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.domain.SignUpForm;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    // 회원가입
    public Customer signUp(SignUpForm form){
        return customerRepository.save(Customer.from(form));
    };

    public boolean isEmailExist(String email){
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).
                isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER));

        if(customer.isVerified()){
            throw new CustomerException(ErrorCode.ALREADY_VERIFY);
        }else if(!customer.getVerificationCode().equals(code)){
            throw new CustomerException(ErrorCode.WRONG_VERIFY);
        }else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomerException(ErrorCode.EXPIRE_CODE);
        }

        customer.setVerified(true);
    }

    // 인증키를 전송한 회원정보(이메일)
    @Transactional
    public LocalDateTime ChangeCustomerValidateEmail(Long customerId, String verificationCode){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1)); // 인증키 만료일(now + 1일)
            return customer.getVerifyExpiredAt();
        }
        throw new CustomerException(ErrorCode.NOT_FOUND_USER);

    }
}
