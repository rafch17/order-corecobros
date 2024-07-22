package com.banquito.corecobros.order.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "\"ORDER\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID", nullable = false)
    private Integer orderId;
    @Column(name = "UNIQUE_ID", length = 16)
    private String uniqueId;
    @Column(name = "SERVICE_ID", nullable = false)
    private Integer serviceId;
    @Column(name = "ACCOUNT_ID")
    private Integer accountId;
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;
    @Column(name = "TOTAL_AMOUNT", precision = 17, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "DESCRIPTION", length = 100, nullable = false)
    private String description;
    @Column(name = "STATUS", length = 3, nullable = false)
    private String status;
    
    public Order(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (orderId == null) {
            if (other.orderId != null)
                return false;
        } else if (!orderId.equals(other.orderId))
            return false;
        return true;
    }

    

}
