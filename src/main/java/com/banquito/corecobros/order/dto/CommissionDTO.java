package com.banquito.corecobros.order.dto;

public class CommissionDTO {
    private String companyUniqueId;
    private String orderUniqueId;
    private String itemUniqueId;
    private String itemType;
    
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

    
}
