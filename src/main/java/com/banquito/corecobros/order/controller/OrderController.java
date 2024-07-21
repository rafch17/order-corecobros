package com.banquito.corecobros.order.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.service.OrderService;


@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(this.orderService.obtainAllOrders());
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            this.orderService.createOrder(orderDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(this.orderService.obtainOrderById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/expire")
    public ResponseEntity<Void> expireOrders() {
        orderService.expireOrders();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<List<Order>> getOrdersByServiceCompanyAndDateRange(
            @RequestParam Integer serviceId,
            @RequestParam Integer companyId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Order> orders = orderService.getOrdersByServiceCompanyAndDateRange(serviceId, companyId, start, end);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer orderId, @RequestParam String status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    
}
