package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
@Repository
public interface CollectionPaymentRecordRepository extends JpaRepository<CollectionPaymentRecord, Integer> {
    List<CollectionPaymentRecord> findByItemCollectionIdIn(List<Integer> itemCollectionIds);
}
