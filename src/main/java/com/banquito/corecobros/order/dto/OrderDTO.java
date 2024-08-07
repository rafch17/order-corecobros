package com.banquito.corecobros.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;
@Value
@Builder
public class OrderDTO {
    private Integer orderId;
    private String uniqueId;
    private String serviceId;
    private String accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private String description;
    private String status;
    private String companyUid;
}
