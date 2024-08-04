package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResponseTransactionDTO {
    private String transactionType;
    private String creditorAccount;
    private String debitorAccount;
    private LocalDateTime createDate;
    private BigDecimal pendiente;
    private String status;
    
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public String getCreditorAccount() {
        return creditorAccount;
    }
    public void setCreditorAccount(String creditorAccount) {
        this.creditorAccount = creditorAccount;
    }
    public String getDebitorAccount() {
        return debitorAccount;
    }
    public void setDebitorAccount(String debitorAccount) {
        this.debitorAccount = debitorAccount;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public BigDecimal getPendiente() {
        return pendiente;
    }
    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    
}
