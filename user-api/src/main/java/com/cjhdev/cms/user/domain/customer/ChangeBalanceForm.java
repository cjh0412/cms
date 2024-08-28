package com.cjhdev.cms.user.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
