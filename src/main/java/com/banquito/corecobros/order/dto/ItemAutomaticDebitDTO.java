package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;

public class ItemAutomaticDebitDTO {
    private Integer id;
    private Integer orderId;
    private String identification;
    private String debtorName;
    private String debitAccount;
    private BigDecimal debitAmount;
    private String status;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public String getIdentification() {
        return identification;
    }
    public void setIdentification(String identification) {
        this.identification = identification;
    }
    public String getDebtorName() {
        return debtorName;
    }
    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }
    public String getDebitAccount() {
        return debitAccount;
    }
    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }
    public BigDecimal getDebitAmount() {
        return debitAmount;
    }
    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    
}
