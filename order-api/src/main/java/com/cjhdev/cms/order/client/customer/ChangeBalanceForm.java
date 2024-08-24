package com.cjhdev.cms.order.client.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
