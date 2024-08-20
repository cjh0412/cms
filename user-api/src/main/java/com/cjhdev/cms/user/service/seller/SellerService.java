package com.cjhdev.cms.user.service.seller;

import com.cjhdev.cms.user.domain.SignUpForm;
import com.cjhdev.cms.user.domain.model.Seller;
import com.cjhdev.cms.user.domain.repository.SellerRepository;
import com.cjhdev.cms.user.exception.CustomerException;
import com.cjhdev.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByIdAndEmail(Long id, String email) {
        return sellerRepository.findByIdAndEmail(id, email);
    }

    public Optional<Seller> findValidSeller(String email, String password) {
        return sellerRepository.findByEmailAndPasswordAndVerifiedIsTrue(email, password);
    }

    public Seller signUp(SignUpForm form) {
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email) {
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER));

        if(seller.isVerified()){
            throw new CustomerException(ErrorCode.ALREADY_VERIFY);
        }else if(!seller.getVerificationCode().equals(code)){
            throw new CustomerException(ErrorCode.WRONG_VERIFY);
        }else if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomerException(ErrorCode.EXPIRE_CODE);
        }

        seller.setVerified(true);
    }

    // 인증키를 전송한 회원정보(이메일)
    @Transactional
    public LocalDateTime ChangeSellerValidateEmail(Long sellerId, String verificationCode){
        Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);

        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            seller.setVerificationCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1)); // 인증키 만료일(now + 1일)
            return seller.getVerifyExpiredAt();
        }
        throw new CustomerException(ErrorCode.NOT_FOUND_USER);

    }

}
