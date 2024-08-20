package com.cjhdev.cms.user.domain.repository;

import com.cjhdev.cms.user.domain.CustomerBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory, Long> {

    Optional<CustomerBalanceHistory> findFirstByCustomerId_IdOrderByIdDesc(@RequestParam("customer_id") Long customerId);
}

