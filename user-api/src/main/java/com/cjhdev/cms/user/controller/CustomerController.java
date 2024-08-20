package com.cjhdev.cms.user.controller;

import com.cjhdev.cms.user.domain.customer.CustomerDto;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import com.cjhdev.cms.user.service.customer.CustomerService;
import config.JwtAuthenticationProvider;
import domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name="X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Customer customer = customerService.findByIdAndEmail(vo.getId(),vo.getEmail())
                .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDto.from(customer));
    }


}
