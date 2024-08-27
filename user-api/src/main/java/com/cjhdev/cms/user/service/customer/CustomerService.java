package com.cjhdev.cms.user.service.customer;

import com.cjhdev.cms.user.domain.model.Customer;
import com.cjhdev.cms.user.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id, String email) {
        return customerRepository.findById(id)
                .stream().filter(customer -> customer.getEmail().equals(email))
                .findFirst();
    }

    public Optional<Customer> findValidCustomer(String email, String password) {
        // findByEmail에서 가져온 password와 입력값 동일 여부 및 verified가 true인지 체크
        return customerRepository.findByEmail(email).stream().filter(
                customer -> customer.getPassword().equals(password) && customer.isVerified())
                .findFirst();
    }

}
