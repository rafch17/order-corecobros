package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;

public class ResponseItemCommissionDTO {

    private Integer id; 
    private Integer commissionId;
    private String uniqueId; 
    private String companyUniqueId;
    private String orderUniqueId;
    private String itemUniqueId;
    private String itemType;
    private BigDecimal commissionValue;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCommissionId() {
        return commissionId;
    }
    public void setCommissionId(Integer commissionId) {
        this.commissionId = commissionId;
    }
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    public String getCompanyUniqueId() {
        return companyUniqueId;
    }
    public void setCompanyUniqueId(String companyUniqueId) {
        this.companyUniqueId = companyUniqueId;
    }
    public String getOrderUniqueId() {
        return orderUniqueId;
    }
    public void setOrderUniqueId(String orderUniqueId) {
        this.orderUniqueId = orderUniqueId;
    }
    public String getItemUniqueId() {
        return itemUniqueId;
    }
    public void setItemUniqueId(String itemUniqueId) {
        this.itemUniqueId = itemUniqueId;
    }
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public BigDecimal getCommissionValue() {
        return commissionValue;
    }
    public void setCommissionValue(BigDecimal commissionValue) {
        this.commissionValue = commissionValue;
    }
}