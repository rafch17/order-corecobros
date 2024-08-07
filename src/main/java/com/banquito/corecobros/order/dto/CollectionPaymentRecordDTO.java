package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CollectionPaymentRecordDTO {
    private Integer id;
    private Integer itemCollectionId;
    private Integer itemCommissionId;
    private String uniqueId;
    private BigDecimal collectionAmount;
    private String paymentType;
    private LocalDateTime paymentDate;
    private BigDecimal outstandingBalance;
    private String channel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemCollectionId() {
        return itemCollectionId;
    }

    public void setItemCollectionId(Integer itemCollectionId) {
        this.itemCollectionId = itemCollectionId;
    }

    public Integer getItemCommissionId() {
        return itemCommissionId;
    }

    public void setItemCommissionId(Integer itemCommissionId) {
        this.itemCommissionId = itemCommissionId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
