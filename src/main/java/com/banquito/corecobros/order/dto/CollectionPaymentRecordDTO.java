package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CollectionPaymentRecordDTO {
    private Integer code;
    private Integer itemCollectionCode;
    private Integer itemCommissionCode;
    private BigDecimal collectionAmount;
    private String paymentType;
    private LocalDateTime paymentDate;
    private BigDecimal outstandingBalance;
    private String channel;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getItemCollectionCode() {
        return itemCollectionCode;
    }

    public void setItemCollectionCode(Integer itemCollectionCode) {
        this.itemCollectionCode = itemCollectionCode;
    }

    public Integer getItemCommissionCode() {
        return itemCommissionCode;
    }

    public void setItemCommissionCode(Integer itemCommissionCode) {
        this.itemCommissionCode = itemCommissionCode;
    }

    public BigDecimal getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(BigDecimal collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
