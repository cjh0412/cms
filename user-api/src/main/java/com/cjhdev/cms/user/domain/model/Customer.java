package com.cjhdev.cms.user.domain.model;

import com.cjhdev.cms.user.domain.BaseEntity;
import com.cjhdev.cms.user.domain.SignUpForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Setter
// 생성, 수정 일자 자동 변경
@AuditOverride(forClass = BaseEntity.class)
public class Customer extends BaseEntity {
    @Id
    @Column(name="id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String phone;
    private String password;
    private LocalDate birth;

    private LocalDateTime verifyExpiredAt ;
    private String verificationCode ;
    private boolean verified ;

    // 고객 잔액
    @Column(columnDefinition = "int default 0")
    private Integer balance;


    //customer 객체 반환
    public static Customer from(SignUpForm form){
        return Customer.builder()
                .email(form.getEmail().toLowerCase((Locale.ROOT)))
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone())
                .verified(false)
                .build();
    }

    // 잔액 변경
    public void changeBalance(Long customerId, Integer balance){
        this.id = customerId;
        this.balance = balance;
    }

    // 인증키 세팅
    public void verifiedInfo(Long id, String verificationCode, LocalDateTime verifyExpiredAt){

        this.id = id;
        this.verifyExpiredAt = verifyExpiredAt;
        this.verificationCode = verificationCode;

    }

    // 인증 완료여부
    public void verifiedYN(String email , boolean verified){
       this.verified = verified;


    }


}
