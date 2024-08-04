package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;

public class ItemCollectionDTO {
    private Integer id;
    private Integer orderId;
    private String uniqueId;
    private String debtorName;
    private String counterpart;
    private BigDecimal collectionAmount;
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
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    public String getDebtorName() {
        return debtorName;
    }
    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }
    public String getCounterpart() {
        return counterpart;
    }
    public void setCounterpart(String counterpart) {
        this.counterpart = counterpart;
    }
    public BigDecimal getCollectionAmount() {
        return collectionAmount;
    }
    public void setCollectionAmount(BigDecimal collectionAmount) {
        this.collectionAmount = collectionAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    

}
