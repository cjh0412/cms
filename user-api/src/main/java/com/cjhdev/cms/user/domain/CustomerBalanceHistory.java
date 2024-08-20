package com.cjhdev.cms.user.domain;

import com.cjhdev.cms.user.domain.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceHistory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;
    //변경된 돈
    private Integer changeMoney;
    //현재 잔액
    private Integer currentMoney;
    private String fromMessage;
    private String description;
}
