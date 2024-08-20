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

    @Transactional(noRollbackFor = {CustomerException.class})
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomerException {
        // 회원의 예치금 조회, 잔액이 없는 경우 0을 반환
        CustomerBalanceHistory customerBalanceHistory
                = CustomerBalanceHistoryRepository.findFirstByCustomerId_IdOrderByIdDesc(customerId)
                .orElse(CustomerBalanceHistory.builder()
                        .changeMoney(0)
                        .currentMoney(0)
                        .customer(customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER)))
                        .build());

        if(customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0){
            throw new CustomerException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // 변경 잔액 저장
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
