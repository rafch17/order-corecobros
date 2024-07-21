package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.google.common.base.Optional;

@Repository
public interface ItemCollectionRepository extends JpaRepository<ItemCollection, Integer> {

    List<ItemCollection> findByCounterpartAndStatus(String counterpart, String status);

    List<ItemCollection> findByStatus(String status);

    List<CollectionPaymentRecord> findByAccountId(Integer accountId);

    List<ItemCollection> findByOrderId(Integer orderId);

    Optional<ItemCollection> findByCounterpart(String counterpart);
}
