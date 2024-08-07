package com.banquito.corecobros.order.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.corecobros.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
        Order findByUniqueId(String uniqueId);

        boolean existsByUniqueId(String uniqueId);

        List<Order> findByServiceIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        String serviceId, String status, LocalDate currentDate1, LocalDate currentDate2);

        List<Order> findByAccountId(String accountId);

        List<Order> findByEndDateBeforeAndStatus(LocalDate endDate, String status);

        List<Order> findByServiceIdAndAccountIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        String serviceId, String accountId, LocalDate startDate, LocalDate endDate);

        List<Order> findByServiceIdAndStatus(String serviceId, String status);

        List<Order> findByCompanyUid(String companyUid);

        List<Order> findByStatus(String status);
        
        Order findByOrderIdAndUniqueId(Integer orderId, String uniqueId);
}
