package com.banquito.corecobros.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "COLLECTION_PAYMENT_RECORD")
public class CollectionPaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_RECORD_ID", nullable = false)
    private Integer code;
    @Column(name = "ITEM_COLLECTION_ID")
    private Integer itemCollectionCode;
    @Column(name = "ITEM_COMMISSION_ID")
    private Integer itemCommissionCode;
    @Column(name = "COLLECTION_AMOUNT", precision = 17, scale = 2)
    private BigDecimal collectionAmount;
    @Column(name = "PAYMENT_TYPE", length = 3)
    private String paymentType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;
    @Column(name = "OUTSTANDING_BALANCE", precision = 17, scale = 2)
    private BigDecimal outstandingBalance;
    @Column(name = "CHANNEL", length = 3)
    private String channel;

    @ManyToOne
    @JoinColumn(name = "ITEM_COLLECTION_ID", referencedColumnName = "ITEM_COLLECTION_ID", insertable = false, updatable = false)
    private ItemCollection itemCollection;


    public CollectionPaymentRecord(Integer code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
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
        CollectionPaymentRecord other = (CollectionPaymentRecord) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

}
