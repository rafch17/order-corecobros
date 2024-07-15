package com.banquito.corecobros.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
@Repository
public interface CollectionPaymentRecordRepository extends JpaRepository<CollectionPaymentRecord, Integer> {

}
