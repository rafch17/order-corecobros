package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AutomaticDebitPaymentRecordDTO {
    private Integer id;
    private Integer itemAutomaticDebitId;
    private String uniqueId;
    private BigDecimal outstandingBalance;
    private String paymentType;
    private BigDecimal debitAmount;
    private LocalDateTime paymentDate;
    private String status;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getItemAutomaticDebitId() {
        return itemAutomaticDebitId;
    }
    public void setItemAutomaticDebitId(Integer itemAutomaticDebitId) {
        this.itemAutomaticDebitId = itemAutomaticDebitId;
    }
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
