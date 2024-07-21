package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AutomaticDebitPaymentRecordDTO {
    private Integer code;
    private Integer itemAutomaticDebitCode;
    private Integer itemCommissionCode;
    private BigDecimal outstandingBalance;
    private String paymentType;
    private BigDecimal debitAmount;
    private LocalDateTime paymentDate;
    private String status;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getItemAutomaticDebitCode() {
        return itemAutomaticDebitCode;
    }

    public void setItemAutomaticDebitCode(Integer itemAutomaticDebitCode) {
        this.itemAutomaticDebitCode = itemAutomaticDebitCode;
    }

    public Integer getItemCommissionCode() {
        return itemCommissionCode;
    }

    public void setItemCommissionCode(Integer itemCommissionCode) {
        this.itemCommissionCode = itemCommissionCode;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
