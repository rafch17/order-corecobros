package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AutomaticDebitPaymentRecordDTO {
    private Integer code;
    private Integer itemAutomaticDebitCode;
    private Integer itemCommissionCode;
    private BigDecimal owedPayment;
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

    public BigDecimal getOwedPayment() {
        return owedPayment;
    }

    public void setOwedPayment(BigDecimal owedPayment) {
        this.owedPayment = owedPayment;
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
