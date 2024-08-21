package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountTransactionPaymentDTO {
    //TRANSACTIONS
    private Integer accountId;
    private String codeChannel;
    private BigDecimal amount;
    private String debitorAccount;
    private String creditorAccount;
    private String transactionType;
    private String reference;
    private BigDecimal comission;
    private String parentTransactionKey;
    
    //EXTRA COLLECTION
    private BigDecimal amountCollected;

}
