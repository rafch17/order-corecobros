package com.banquito.corecobros.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByType(String type);
    List<Order> findByStatusAndType(String status, String type);
}
