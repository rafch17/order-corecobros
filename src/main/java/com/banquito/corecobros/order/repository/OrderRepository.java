package com.banquito.corecobros.order.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByUniqueId(String uniqueId);
    @Query("SELECT o FROM Order o WHERE o.serviceId = :serviceId AND o.accountId = :accountId AND o.startDate >= :startDate AND o.endDate <= :endDate")
    List<Order> findByServiceIdAndAccountIdAndDateRange(
            @Param("serviceId") Integer serviceId,
            @Param("accountId") Integer accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);;
}
