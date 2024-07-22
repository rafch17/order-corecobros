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
    private Integer serviceId;
    private Integer accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private String description;
    private String status;
}
