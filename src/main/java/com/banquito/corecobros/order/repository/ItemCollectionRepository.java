package com.banquito.corecobros.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.ItemCollection;

@Repository
public interface ItemCollectionRepository extends JpaRepository<ItemCollection, Integer> {

    List<ItemCollection> findByCounterpartAndStatus(String counterpart, String status);

    List<ItemCollection> findByStatus(String status);

    List<ItemCollection> findByOrderId(Integer id);

    Optional<ItemCollection> findByCounterpart(String counterpart);

    List<ItemCollection> findByOrderIdIn(List<Integer> orderIds);

    


}
