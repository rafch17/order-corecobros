package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
@Repository
public interface CollectionPaymentRecordRepository extends JpaRepository<CollectionPaymentRecord, Integer> {
    List<CollectionPaymentRecord> findByItemCollectionIdIn(List<Integer> itemCollectionIds);

    List<CollectionPaymentRecord> findByItemCollectionId(Integer itemCollectionId);

    boolean existsByUniqueId(String uniqueId);
}
