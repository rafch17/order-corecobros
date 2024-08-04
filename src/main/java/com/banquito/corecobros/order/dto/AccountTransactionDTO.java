package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;
@Value
@Builder
public class AccountTransactionDTO {
    private Integer accountId;
    private String uniqueId;
    private String codeChannel;
    private String uniqueKey;
    private String transactionType;
    private String transactionSubtype;
    private String reference;
    private BigDecimal amount;
    private String creditorAccount;
    private String debitorAccount;
    private LocalDateTime createDate;
    private Boolean applyTax;
    private String parentTransactionKey;
    private String status;
}
