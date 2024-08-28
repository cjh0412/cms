package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.domain.CustomerBalanceHistory;
import com.cjhdev.cms.user.domain.customer.ChangeBalanceForm;
import com.cjhdev.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import com.cjhdev.cms.user.exception.CustomerException;

import com.cjhdev.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

    private final CustomerRepository customerRepository;
    private final CustomerBalanceHistoryRepository CustomerBalanceHistoryRepository;
    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

    // noRollbackFor : {CustomerException.class}이 발생할 경우 롤백하지 x
    @Transactional(noRollbackFor = {CustomerException.class})
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomerException {
        // 회원의 예치금 조회, 잔액이 없는 경우 0을 반환
        CustomerBalanceHistory customerBalanceHistory
                = CustomerBalanceHistoryRepository.findFirstByCustomerId_IdOrderByIdDesc(customerId) // 가장 최신 잔액 값을 가져옴
                .orElse(CustomerBalanceHistory.builder() // 값이 없을 경우
                        .changeMoney(0)
                        .currentMoney(0)
                        .customer(customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER))) // 계정이 없는 경우
                        .build());

        // 현재 잔액 + 변경 금액
        if(customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0){
            throw new CustomerException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // 변경 정보(잔액) 저장
        customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(form.getMoney())
                .currentMoney(customerBalanceHistory.getCurrentMoney()+ form.getMoney())
                .description(form.getMessage())
                .fromMessage(form.getFrom())
                .customer(customerBalanceHistory.getCustomer())
                .build();

        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());
        customerBalanceHistoryRepository.save(customerBalanceHistory);

        return customerBalanceHistory;
    }
}
