package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;

@Repository
public interface AutomaticDebitPaymentRecordRepository extends JpaRepository<AutomaticDebitPaymentRecord, Integer> {
    boolean existsByUniqueId(String uniqueId);

    List<AutomaticDebitPaymentRecord> findByItemAutomaticDebit(ItemAutomaticDebit itemAutomaticDebit);
}
