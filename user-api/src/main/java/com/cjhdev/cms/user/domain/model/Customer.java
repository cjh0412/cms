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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class) // 생성, 수정 일자 자동 변경
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


}
