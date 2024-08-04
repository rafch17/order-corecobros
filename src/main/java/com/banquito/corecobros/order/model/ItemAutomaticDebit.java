package com.banquito.corecobros.order.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "ITEM_AUTOMATIC_DEBIT")
public class ItemAutomaticDebit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_AUTOMATIC_DEBIT_ID", nullable = false)
    private Integer id;
    @Column(name = "ORDER_ID")
    private Integer orderId;
    @Column(name = "IDENTIFICATION", length = 13, nullable = false)
    private String identification;
    @Column(name = "DEBTOR_NAME", length = 100, nullable = false)
    private String debtorName;
    @Column(name = "DEBIT_ACCOUNT", length = 13, nullable = false)
    private String debitAccount;
    @Column(name = "DEBIT_AMOUNT", precision = 17, scale = 2, nullable = false)
    private BigDecimal debitAmount;
    @Column(name = "STATUS", length = 3, nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID",insertable = false, updatable = false)
    private Order order;

    public ItemAutomaticDebit(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        ItemAutomaticDebit other = (ItemAutomaticDebit) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
