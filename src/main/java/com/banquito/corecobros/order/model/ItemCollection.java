package com.banquito.corecobros.order.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "ITEM_COLLECTION")
public class ItemCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_COLLECTION_ID", nullable = false)
    private Integer id;
    @Column(name = "ORDER_ID")
    private Integer orderId;
    @Column(name = "UNIQUE_ID", length = 16, nullable = false)
    private String uniqueId;
    @Column(name = "DEBTOR_NAME", length = 100, nullable = false)
    private String debtorName;
    @Column(name = "COUNTERPART", length = 13, nullable = false)
    private String counterpart;
    @Column(name = "COLLECTION_AMOUNT", precision = 17, scale = 2, nullable = false)
    private BigDecimal collectionAmount;
    @Column(name = "STATUS", length = 3, nullable = false)
    private String status;

    @OneToMany(mappedBy = "itemCollection")
    private List<CollectionPaymentRecord> collectionPaymentRecords;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID", insertable = false, updatable = false)
    private Order order;

    public ItemCollection(Integer id) {
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
        ItemCollection other = (ItemCollection) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
