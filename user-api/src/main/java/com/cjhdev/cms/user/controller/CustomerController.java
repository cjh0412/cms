package com.cjhdev.cms.user.controller;

import com.cjhdev.cms.user.domain.customer.ChangeBalanceForm;
import com.cjhdev.cms.user.domain.customer.CustomerDto;
import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import com.cjhdev.cms.user.service.customer.CustomerBalanceService;
import com.cjhdev.cms.user.service.customer.CustomerService;
import config.JwtAuthenticationProvider;
import domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name="X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Customer customer = customerService.findByIdAndEmail(vo.getId(),vo.getEmail())
                .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDto.from(customer));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> chanBalance(@RequestHeader(name="X-AUTH-TOKEN") String token
                                                , @RequestBody ChangeBalanceForm form) {

        UserVo vo = provider.getUserVo(token);
        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }



}
