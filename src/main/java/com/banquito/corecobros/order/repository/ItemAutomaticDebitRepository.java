package com.banquito.corecobros.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.ItemAutomaticDebit;
@Repository
public interface ItemAutomaticDebitRepository extends JpaRepository<ItemAutomaticDebit, Integer> {

}
