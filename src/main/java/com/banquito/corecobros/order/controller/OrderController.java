package com.banquito.corecobros.order.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.banquito.corecobros.order.service.OrderService;


@RestController
@RequestMapping("/api/v1/orders")
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

    @PutMapping("/{uniqueId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String uniqueId, @RequestParam String status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(uniqueId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderDTO>> getOrdersByCriteria(
            @RequestParam Integer serviceId,
            @RequestParam Integer accountId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<OrderDTO> orders = orderService.getOrdersByServiceIdAndAccountIdAndDateRange(
                serviceId, accountId, startDate, endDate);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    
}
