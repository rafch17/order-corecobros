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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "AUTOMATIC_DEBIT_PAYMENT_RECORD")
public class AutomaticDebitPaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_RECORD_ID", nullable = false)
    private Integer code;
    @Column(name = "ITEM_AUTOMATIC_DEBIT_ID")
    private Integer itemAutomaticDebitCode;
    @Column(name = "ITEM_COMMISSION_ID")
    private Integer itemCommissionCode;
    @Column(name = "OWED_PAYMENT", precision = 17, scale = 2)
    private BigDecimal owedPayment;
    @Column(name = "PAYMENT_TYPE", length = 3)
    private LocalDateTime paymentDate;
    @Column(name = "STATUS", length = 3)
    private String status;

    @ManyToOne
    @JoinColumn(name = "ITEM_AUTOMATIC_DEBIT_ID", referencedColumnName = "ITEM_AUTOMATIC_DEBIT_ID", insertable = false, updatable = false)
    private ItemAutomaticDebit itemAutomaticDebit;

    public AutomaticDebitPaymentRecord(Integer code) {
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
        AutomaticDebitPaymentRecord other = (AutomaticDebitPaymentRecord) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

}
