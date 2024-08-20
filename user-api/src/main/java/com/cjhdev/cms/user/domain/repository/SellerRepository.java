package com.cjhdev.cms.user.domain.repository;

import com.cjhdev.cms.user.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByIdAndEmail(Long id, String email);
    Optional<Seller> findByEmailAndPasswordAndVerifiedIsTrue(String email, String password);
    Optional<Seller> findByEmail(String email);

}
