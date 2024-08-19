package com.cjhdev.cms.user.application;

import com.cjhdev.cms.user.service.CustomerService;
import com.cjhdev.cms.user.domain.SignInForm;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.exception.CustomerException;
import config.JwtAuthenticationProvider;
import domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cjhdev.cms.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form){
        // 1. 로그인 가능 여부 체크
        Customer customer = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomerException(LOGIN_CHECK_FAIL));

        // 2. 토큰 발행
        // 3. 토큰을 response
        return provider.createToken(customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }
}
