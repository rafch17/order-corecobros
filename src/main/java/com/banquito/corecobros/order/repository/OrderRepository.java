package com.banquito.corecobros.order.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
        Order findByUniqueId(String uniqueId);

        List<Order> findByServiceIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        Integer serviceId, String status, LocalDate currentDate1, LocalDate currentDate2);

        List<Order> findByAccountId(Integer accountId);

        List<Order> findByEndDateBeforeAndStatus(LocalDate endDate, String status);

        List<Order> findByServiceIdAndAccountIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        Integer serviceId, Integer accountId, LocalDate startDate, LocalDate endDate);
}
