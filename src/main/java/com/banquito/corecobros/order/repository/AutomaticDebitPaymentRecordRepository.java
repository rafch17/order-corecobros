package com.banquito.corecobros.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;

@Repository
public interface AutomaticDebitPaymentRecordRepository extends JpaRepository<AutomaticDebitPaymentRecord, Integer> {

}
