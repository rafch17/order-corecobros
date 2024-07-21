package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.ItemAutomaticDebit;
@Repository
public interface ItemAutomaticDebitRepository extends JpaRepository<ItemAutomaticDebit, Integer> {
    List<ItemAutomaticDebit> findByOrderId(Integer orderId);
    List<ItemAutomaticDebit> findByStatus(String status);
}
